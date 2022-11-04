/* global _, angular, UHGroupingsApp */

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
        $scope.personToLookup = "";
        $scope.currentManagePerson = "";
        $scope.selectedGroupingsPaths = [];
        $scope.emptySelect = false;
        $scope.selectedOwnedGroupingsNames = [];

        let PAGE_SIZE = 20;

        angular.extend(this, $controller("GeneralJsController", { $scope }));

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
            $scope.loading = true;
            const validUser = $scope.sanitizer($scope.personToLookup);
            if (validUser !== "") {
                $scope.getMemberDetails(validUser);
            } else {
                if (!$scope.personToLookup) {
                    $scope.emptyInput = true;
                } else {
                    $scope.resStatus = 500;
                }
                $scope.loading = false;
                $scope.user = $scope.personToLookup;
                $scope.personList = [];
                $scope.filter($scope.personList, "pagedItemsPerson", "currentPagePerson", $scope.personQuery, true);
                $scope.currentManagePerson = "";
            }
            $scope.checkAll = false;
        };

        /**
         * Helper - searchForUserGroupingInformation
         * @param validUser
         */
        $scope.getMemberDetails = function (validUser) {
            groupingsService.getMembershipAssignmentForUser(
                $scope.searchForUserGroupingInformationOnSuccessCallback,
                $scope.searchForUserGroupingInformationOnErrorCallback,
                $scope.personToLookup);
            groupingsService.getMemberAttributes(validUser, function (person) {
                $scope.initMemberDisplayName(person);
                if ($scope.user != null) {
                    $scope.currentManagePerson = "(" + $scope.fullName + ", " + $scope.uid + ", " + $scope.uhUuid + ")";
                } else {
                    $scope.currentManagePerson = "";
                    $scope.resStatus = 500;
                }
            }, () => {});
        };

        /**
         * Helper - removeFromGroups
         * Checks if the user being removed is a sole owner of any grouping before proceeding
         */
        $scope.checkSoleOwner = function (res) {
            if (res === "") {
                return;
            }
            let memberToRemove = {
                username: res.username,
                name: res.name,
                uhUuid: res.uhUuid
            };

            $scope.soleOwnerGroupingNames = [];

            if ($scope.selectedOwnedGroupings.length === 0) {
                $scope.removeFromGroupsCallbackOnSuccess(memberToRemove);
            }
            _.forEach($scope.selectedOwnedGroupings, function (grouping) {
                    groupingsService.isSoleOwner(grouping.path, memberToRemove.username, (res) => {
                        if (res) {
                            $scope.soleOwnerGroupingNames.push(grouping.name);
                        }
                        if (grouping === $scope.selectedOwnedGroupings[$scope.selectedOwnedGroupings.length - 1]) {
                            $scope.removeFromGroupsCallbackOnSuccess(memberToRemove);
                        }
                    }, () => $scope.createApiErrorModal());
                }
            );
        };

        $scope.removeFromGroupsCallbackOnSuccess = function (memberToRemove) {
            if (_.isEmpty($scope.selectedGroupingsPaths)) {
                $scope.emptySelect = true;
            } else if ($scope.soleOwnerGroupingNames.length >= 1) {
                $scope.createRemoveErrorModal("owner");
            } else {
                $scope.createRemoveFromGroupsModal({
                    member: memberToRemove,
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

        /**
         * Helper - removeFromGroups
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

        /**
         * Helper - addAdmin
         * Checks if the user is already an admin
         * @param {string} user - the user you are checking to see if they are already in the list being added to
         * @returns {boolean} true if the user is already in the list being added to, otherwise returns false
         */
        function inAdminList(user) {
            return _.some($scope.adminsList, { username: user }) ||
                _.some($scope.adminsList, { uhUuid: user });
        }

        /**
         * Adds a user to the admin list.
         */
        $scope.addAdmin = function () {
            $scope.waitingForImportResponse = true;
            const sanitizedAdmin = $scope.sanitizer($scope.adminToAdd);
            if (_.isEmpty(sanitizedAdmin)) {
                // Todo : Error message pop up needs implementation.
                $scope.emptyInput = true;
            } else {
                if (inAdminList(sanitizedAdmin)) {
                    // Todo : Error message pop up needs implementation.
                    $scope.user = sanitizedAdmin;
                    $scope.listName = "admins";
                    $scope.swap = false;
                } else {
                    $scope.createConfirmAddModal({
                        userToAdd: sanitizedAdmin,
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
                    members: adminToRemove,
                    listName: "admins"
                });
            } else {
                const userType = "admin";
                $scope.createRemoveErrorModal(userType);
            }
        };

        function handleRemoveFromGroupsOnSuccess() {
            $scope.loading = false;
            $scope.searchForUserGroupingInformation();
        }

        function handleRemoveFromGroupsOnError() {
            $scope.loading = false;
        }

        /**
         * Create a modal that prompts the user whether they want to delete the user or not. If 'Yes' is pressed, then
         * a request is made to delete the user.
         * @param {object} options - the options object
         * @param {object} options.member - the user being removed
         * @param {string} options.groupPaths - groups the user is being removed from
         * @param {string} options.listName - groups the user is being removed from
         */
        $scope.createRemoveFromGroupsModal = function (options) {
            const memberToRemove = options.member.uhUuid;
            const sanitizedUser = $scope.sanitizer(memberToRemove);
            $scope.memberToRemove = options.member;
            $scope.groupPaths = options.groupPaths;
            $scope.listName = options.listName;
            $scope.ownerOfListName = $scope.selectedOwnedGroupingsNames.join(", ");

            const windowClass = $scope.showWarningRemovingSelfFromList() ? "modal-danger" : "";

            groupingsService.getMemberAttributes(sanitizedUser, function (person) {
                if (person === "") {
                    return;
                } else {
                    $scope.initMemberDisplayName(person);
                }
                $scope.removeModalInstance = $uibModal.open({
                    templateUrl: "modal/removeModal",
                    windowClass,
                    scope: $scope,
                    backdrop: "static",
                    keyboard: false
                });

                $scope.removeModalInstance.result.then(function () {
                    $scope.loading = true;
                    let memberToRemove = options.member.uhUuid;
                    let groupingPath = $scope.groupPaths;
                    groupingsService.removeFromGroups(groupingPath, memberToRemove, handleRemoveFromGroupsOnSuccess, handleRemoveFromGroupsOnError);
                });
            }, function (res) {
                $scope.user = memberToRemove;
                $scope.resStatus = res.status;
            });
        };

        /**
         * Copy the path of a grouping through the clipboard button
         * @param {Object} grouping - the current selected grouping
         */
        $scope.copyPath = function (grouping) {
            $("[data-content='copy']").popover("hide");

            $("[data-content='copied!']").popover();
            setTimeout(function () {
                $("[data-content='copied!']").popover("hide");
            }, 1000);

            let copyText = document.getElementById(grouping.path);
            copyText.select();
            document.execCommand("copy");
        };

        /**
         * Toggle 'copy' popover when clipboard is being hovered.
         */
        $scope.hoverCopy = function () {
            $("[data-content='copy']").popover();
        };
    }

    UHGroupingsApp.controller("AdminJsController", AdminJsController);
}());