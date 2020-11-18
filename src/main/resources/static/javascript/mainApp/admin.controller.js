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

        let totalCheckBoxCount = 0;
        let currentCheckBoxCount = 0;

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
         * Complete initialization by fetching a list of admins and a list of all existing groupings.
         */
        $scope.init = function () {
            // Adds the loading spinner.
            $scope.loading = true;
            groupingsService.getAdminLists(function (res) {
                $scope.adminsList = _.sortBy(res.adminGroup.members, "name");
                $scope.filter($scope.adminsList, "pagedItemsAdmins", "currentPageAdmins", $scope.adminsQuery, true);

                $scope.groupingsList = _.sortBy(res.allGroupings, "name");
                $scope.filter($scope.groupingsList, "pagedItemsGroupings", "currentPageGroupings", $scope.groupingsQuery, true);
                $scope.loading = false;

            }, function (res) {
                if (res.status === 403) {
                    $scope.createRoleErrorModal();
                }
            });
        };

        /**
         * Fetch a list of memberships pertaining to $scope.personToLookUp.
         */
        $scope.searchForUserGroupingInformation = function () {
            $scope.loading = true;
            groupingsService.getMembershipAssignmentForUser(function (res) {
                $scope.personList = _.sortBy(res, "name");
                $scope.personList = mergeManagePersonDuplicateValues($scope.personList);
                $scope.filter($scope.personList, "pagedItemsPerson", "currentPagePerson", $scope.personQuery, true);
                $scope.loading = false;
            }, function (res) {
                dataProvider.handleException({ exceptionMessage: JSON.stringify(res, null, 4) },
                    "feedback/error", "feedback");
            }, $scope.personToLookup);
        };

        /**
         * With the coupled array created from coupleDuplicatePaths, merge all duplicates into one object and preserve
         * all values that each duplicate contained. (Changed return values to meet Manager Person tab needs)
         */
        function mergeManagePersonDuplicateValues(dups) {
            let result = [];
            dups.forEach((membership, index) => {
                dups.forEach((m, index2) => {
                    if (membership.name === m.name && index !== index2) {
                        membership.inInclude |= m.inInclude;
                        membership.inExclude |= m.inExclude;
                        membership.inBasis |= m.inBasis;
                        membership.inOwner |= m.inOwner;
                        membership.inBasisAndInclude |= m.inBasisAndInclude;
                        dups.splice(index2, 1);
                    }
                });
                result.push({
                    "name": membership.name,
                    "path": membership.path,
                    "inInclude": membership.inInclude,
                    "inExclude": membership.inExclude,
                    "inBasis": membership.inBasis,
                    "inOwner": membership.inOwner,
                    "inBasisAndInclude": membership.inBasisAndInclude

                });
            });
            return result;
        }

        /**
         * Separate the list of Admins into pages.
         */
        $scope.displayAdmins = function () {
            $scope.resetGroupingInformation();
            $scope.filter($scope.adminsList, "pagedItemsAdmins", "currentPageAdmins", $scope.adminsQuery, true);
            $scope.pagedItemsGroupings = $scope.groupToPages($scope.groupingsList);
            $scope.showGrouping = false;
        };


        /**
         * Separate the list of persons into pages.
         */
        $scope.displayPerson = function () {
            $scope.resetGroupingInformation();
            $scope.filter($scope.personList, "pagedItemsPerson", "currentPagePerson", $scope.personQuery, true);
            $scope.pagedItemsPerson = $scope.groupToPages($scope.personList);
            $scope.showGrouping = false;
            $scope.personToLookup = "";
        };

        /**
         * Check for checked check boxes.
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
                groupingsService.getMemberAttributes($scope.personToLookup, function (attributes) {
                    let userToRemove = {
                        username: attributes.uid,
                        name: attributes.cn,
                        uhUuid: attributes.uhUuid
                    };
                    if (_.isEmpty($scope.selectedGroupingsNames)) {
                        $scope.createOwnerErrorModal($scope.selectedGroupingsNames);
                    } else {
                        $scope.createRemoveFromGroupsModal({
                            user: userToRemove,
                            listName: $scope.selectedGroupingsNames
                        });
                    }
                });
            }
        };

        $scope.updateCheckBoxes = function () {
            $scope.checkAll = !$scope.checkAll;
            _.forEach($scope.pagedItemsPerson[$scope.currentPagePerson], function (grouping) {
                if (grouping.inInclude || grouping.inOwner) {
                    grouping.isSelected = $scope.checkAll;
                }
            });
            if ($scope.checkAll) {
                currentCheckBoxCount = totalCheckBoxCount;
            } else {
                currentCheckBoxCount = 0;
            }
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
         * Adds a user to the admin list.
         */
        $scope.addAdmin = function () {
            groupingsService.getAdminLists(function () {
                const adminToAdd = $scope.adminToAdd;

                if (_.isEmpty(adminToAdd)) {
                    $scope.createAddErrorModal(adminToAdd);
                } else {
                    $scope.createConfirmAddModal({
                        userToAdd: adminToAdd,
                        listName: "admins"
                    });
                }
            }, function (res) {
                if (res.statusCode === 403) {
                    $scope.createRoleErrorModal();
                }
            });
        };

        /**
         * Remove an admin from the admin list. There must be at least one admin remaining.
         * @param {number} currentPage - the current page in the admins list
         * @param {number} index - the index of the admin to delete, with the current page and items per page taken into
         * account
         */
        $scope.removeAdmin = function (currentPage, index) {
            groupingsService.getAdminLists(function () {
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
            }, function (res) {
                if (res.statusCode === 403) {
                    $scope.createRoleErrorModal();
                }
            });
        };

        /**
         * Copy grouping path to clipboard.
         */
        $scope.copyPath = function (grouping) {
            let copyText = document.getElementById(grouping.path);
            copyText.select();
            document.execCommand("copy");
        };
    }

    UHGroupingsApp.controller("AdminJsController", AdminJsController);
}());
