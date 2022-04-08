(function () {

    /**
     * This controller contains functions specific to the admin page.
     * @param $scope - binding between controller and HTML page
     * @param $window - the browser window object
     * @param $controller - service for instantiating controllers
     * @param $uibModal - the UI Bootstrap service for creating modals
     * @param dataProvider - service function that provides GET and POST requests for getting or updating data
     */
    function AdminJsController($scope, $window, $uibModal, $controller, dataProvider, groupingsService) {

        $scope.adminsList = [];
        $scope.pagedItemsAdmins = [];
        $scope.currentPageAdmins = 0;
        $scope.personList = [];
        $scope.pagedItemsPerson = [];
        $scope.currentPagePerson = 0;
        $scope.selectedGroupingsPaths = [];
        $scope.emptySelect = false;
        $scope.selectedOwnedGroupingsNames = [];

        let totalCheckBoxCount = 0;
        let currentCheckBoxCount = 0;
        let PAGE_SIZE = 20;

        angular.extend(this, $controller("GeneralJsController", { $scope: $scope }));


        $scope.createRoleErrorModal = function () {
            $scope.loading = false;
            $scope.RoleErrorModalInstance = $uibModal.open({
                templateUrl: "modal/roleErrorModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false
            });
        };

        /**
         * Callback which takes the admin tab data and moves it into adminList and groupingsList, each of these objects
         * is then paginated.
         */
        $scope.getAdminListsCallbackOnSuccess = function (res) {
            $scope.adminsList = _.sortBy(res.adminGroup.members, "name");
            $scope.pagedItemsAdmins = $scope.objToPageArray($scope.adminsList, PAGE_SIZE);

            $scope.groupingsList = _.sortBy(res.allGroupingPaths, "name");
            $scope.pagedItemsGroupings = $scope.objToPageArray($scope.groupingsList, PAGE_SIZE);
            $scope.loading = false;
        };
        /**
         * Complete initialization by fetching a list of admins and list of all groupings.
         */
        $scope.init = function () {
            $scope.loading = true;
            groupingsService.getAdminLists($scope.getAdminListsCallbackOnSuccess, $scope.createApiErrorModal);
        };

        $scope.searchForUserGroupingInformationOnSuccessCallback = function (res) {
            $scope.personList = _.sortBy(res, "name");
            $scope.filter($scope.personList, "pagedItemsPerson", "currentPagePerson", $scope.personQuery, true);
            $scope.user = $scope.personToLookup;
            $scope.loading = false;
        };
        $scope.searchForUserGroupingInformationOnErrorCallback = function (res) {
            $scope.loading = false;
            $scope.resStatus = res.status;
            $scope.user = $scope.personToLookup;
        };
        /**
         * Fetch a list of memberships pertaining to $scope.personToLookUp.
         */
        $scope.searchForUserGroupingInformation = function () {
            if ($scope.personToLookup.length === 0) {
                $scope.emptyInput = true;
            } else {
                $scope.loading = true;
                groupingsService.getMembershipAssignmentForUser(
                    $scope.searchForUserGroupingInformationOnSuccessCallback,
                    $scope.searchForUserGroupingInformationOnErrorCallback,
                    $scope.personToLookup);
            }
        };

        /**
         * Checks if the user being removed is a sole owner of any grouping before proceeding 
         */
        $scope.checkSoleOwner = function (res) {
            if (res === "") {
                return;
            }
            let userToRemove = {
                username: res.username,
                name: res.name,
                uhUuid: res.uhUuid
            };

            $scope.soleOwnerGroupingNames = [];

            _.forEach($scope.selectedOwnedGroupings, function (grouping) {
                    groupingsService.isSoleOwner(grouping.path, userToRemove.username, (res) => {
                        if (res) {
                            $scope.soleOwnerGroupingNames.push(grouping.name);
                        }
                        if (grouping === $scope.selectedOwnedGroupings[$scope.selectedOwnedGroupings.length - 1]) {
                            $scope.removeFromGroupsCallbackOnSuccess(userToRemove);
                        }
                    }, () => $scope.createApiErrorModal());
                }
            );
        };

        $scope.removeFromGroupsCallbackOnSuccess = function (userToRemove) {
            if (_.isEmpty($scope.selectedGroupingsPaths)) {
                $scope.emptySelect = true;
            } else if ($scope.soleOwnerGroupingNames.length >= 1) {
                $scope.createRemoveErrorModal("owner");
            } else {
                $scope.createRemoveFromGroupsModal({
                    user: userToRemove,
                    groupPaths: $scope.selectedGroupingsPaths,
                    listName: $scope.selectedGroupingsNames
                });
            }
        };

        /**
         * Removes selected user from a list of groupings.
         */
        $scope.removeFromGroups = function () {
            $scope.selectedGroupingsNames = [];
            $scope.selectedGroupingsPaths = [];
            $scope.selectedOwnedGroupings = [];
            $scope.selectedOwnedGroupingsNames = [];
            $scope.createGroupPathsAndNames($scope.pagedItemsPerson[$scope.currentPagePerson], $scope.selectedGroupingsNames, $scope.selectedGroupingsPaths, $scope.selectedOwnedGroupingsNames, $scope.selectedOwnedGroupings);

            if ($scope.personToLookup != null) {
                groupingsService.getMemberAttributes($scope.personToLookup, $scope.checkSoleOwner);
            }
        };

        /*
         * Pluck selectedGroupingsNames and selectedGroupingsPaths from currentPage
         */
        $scope.createGroupPathsAndNames = function (currentPage, selectedGroupingsNames, selectedGroupingsPaths, selectedOwnedGroupingsNames, selectedOwnedGroupings) {
            _.forEach(currentPage, function (grouping) {
                if (grouping.isSelected) {
                    let basePath = grouping.path;
                    let groupName = grouping.name;
                    selectedGroupingsNames.push(groupName);
                    if (grouping.inOwner) {
                        let temp = basePath + ":owners";
                        selectedGroupingsPaths.push(temp);
                        selectedOwnedGroupingsNames.push(groupName);
                        selectedOwnedGroupings.push(grouping);
                    }
                    if (grouping.inInclude) {
                        let temp = basePath + ":include";
                        selectedGroupingsPaths.push(temp);
                    }
                    if (grouping.inExclude) {
                        let temp = basePath + ":exclude";
                        selectedGroupingsPaths.push(temp);
                    }
                }
            });
        };

        $scope.updateCheckBoxes = function () {
            $scope.checkAll = !$scope.checkAll;
            _.forEach($scope.pagedItemsPerson[$scope.currentPagePerson], function (grouping) {
                if (grouping.inOwner || grouping.inInclude || grouping.inExclude) {
                    grouping.isSelected = $scope.checkAll;
                }
            });
        };

        $scope.updateCheckAll = function (grouping) {
            totalCheckBoxCount = $scope.personList.length;

            if (grouping.isSelected) {
                currentCheckBoxCount = currentCheckBoxCount + 1;
            } else {
                currentCheckBoxCount = currentCheckBoxCount - 1;
            }
            $scope.checkAll = (currentCheckBoxCount === totalCheckBoxCount);
        };

        /**
         * Checks if the user is already an admin
         * @param {string} user - the user you are checking to see if they are already in the list being added to
         * @returns {boolean} true if the user is already in the list being added to, otherwise returns false
         */
        function inAdminList(user) {
            return _.some($scope.adminsList, { username: user }) ||
                _.some($scope.adminsList, { uhUuid: user });
        };

        /**
         * Adds a user to the admin list.
         */
        $scope.addAdmin = function () {
            $scope.waitingForImportResponse = true;
            const adminToAdd = $scope.adminToAdd;
            if (_.isEmpty(adminToAdd)) {
                // Todo : Error message pop up needs implementation.
                $scope.emptyInput = true;
            } else {
                if (inAdminList(adminToAdd)) {
                    // Todo : Error message pop up needs implementation.
                    $scope.user = adminToAdd;
                    $scope.listName = "admins";
                    $scope.swap = false;
                } else {
                    $scope.createConfirmAddModal({
                        userToAdd: adminToAdd,
                        listName: "admins"
                    });
                }
            }
            $scope.waitingForImportResponse = false;
        };

        /**
         * Remove an admin from the admin list. There must be at least one admin remaining.
         * @param {number} currentPage - the current page in the admins list
         * @param {number} index - the index of the admin to delete, with the current page and items per page taken into
         * account
         */
        $scope.removeAdmin = function (currentPage, index) {
            const adminToRemove = $scope.pagedItemsAdmins[currentPage][index];

            if ($scope.adminsList.length > 1) {
                $scope.createRemoveModal({
                    user: adminToRemove,
                    listName: "admins"
                });
            } else {
                const userType = "admin";
                $scope.createRemoveErrorModal(userType);
            }
        };

        /**
         * Create a modal that prompts the user whether they want to delete the user or not. If 'Yes' is pressed, then
         * a request is made to delete the user.
         * @param {object} options - the options object
         * @param {object} options.user - the user being removed
         * @param {string} options.groupPaths - groups the user is being removed from
         * @param {string} options.listName - groups the user is being removed from
         */
        $scope.createRemoveFromGroupsModal = function (options) {
            const userToRemove = options.user.uhUuid;
            $scope.userToRemove = options.user;
            $scope.groupPaths = options.groupPaths.join(", ");
            $scope.listName = options.listName.join(", ");
            $scope.ownerOfListName = $scope.selectedOwnedGroupingsNames.join(", ");

            const windowClass = $scope.showWarningRemovingSelfFromList() ? "modal-danger" : "";

            groupingsService.getMemberAttributes(userToRemove, function (person) {
                if (person === "") {
                    return;
                } else {
                    $scope.initMemberDisplayName(person);
                }
                $scope.removeModalInstance = $uibModal.open({
                    templateUrl: "modal/removeModal",
                    windowClass: windowClass,
                    scope: $scope,
                    backdrop: "static",
                    keyboard: false
                });

                $scope.removeModalInstance.result.then(function () {
                    $scope.loading = true;
                    let userToRemove = options.user.uhUuid;
                    let groupingPath = $scope.groupPaths;
                    groupingsService.removeFromGroups(groupingPath, userToRemove, handleRemoveFromGroupsOnSuccess, handleRemoveFromGroupsOnError);
                });
            }, function (res) {
                $scope.user = userToRemove;
                $scope.resStatus = res.status;
            });
        };

        function handleRemoveFromGroupsOnSuccess() {
            $scope.loading = false;
            $scope.searchForUserGroupingInformation();
        }

        function handleRemoveFromGroupsOnError() {
            $scope.loading = false;
        }
    }

    UHGroupingsApp.controller("AdminJsController", AdminJsController);
}());