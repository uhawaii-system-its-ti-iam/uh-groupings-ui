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

        $scope.removeFromGroupsCallbackOnSuccess = function (res) {
            if (res === "") {
                return;
            }
            let userToRemove = {
                username: res.uid,
                name: res.cn,
                uhUuid: res.uhUuid
            };
            if (_.isEmpty($scope.selectedGroupingsPaths)) {
                $scope.emptySelect = true;
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

            let i = 0;
            _.forEach($scope.pagedItemsPerson[$scope.currentPagePerson], function (grouping) {
                if (grouping.isSelected) {
                    let basePath = grouping.path.substring(0, grouping.path.lastIndexOf(":") + 1);
                    basePath = basePath.slice(0, -1);
                    let groupName = basePath;
                    groupName = groupName.split(":").pop();
                    $scope.selectedGroupingsNames.push(groupName);
                    if (grouping.inOwner) {
                        let temp = basePath + ":owners";
                        $scope.selectedGroupingsPaths.push(temp);
                    }
                    if (grouping.inInclude) {
                        let temp = basePath + ":include";
                        $scope.selectedGroupingsPaths.push(temp);
                    }
                    if (grouping.inExclude) {
                        let temp = basePath + ":exclude";
                        $scope.selectedGroupingsPaths.push(temp);
                    }
                }
                i++;
            });

            if ($scope.personToLookup != null) {
                groupingsService.getMemberAttributes($scope.personToLookup, $scope.removeFromGroupsCallbackOnSuccess);
            }
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
    }

    UHGroupingsApp.controller("AdminJsController", AdminJsController);
}());
