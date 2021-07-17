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
         * Complete initialization by fetching a list of admins and list of all groupings.
         */
        $scope.init = function () {
            // Adds the loading spinner.
            $scope.loading = true;
            groupingsService.getAdminLists(function (res) {
                $scope.adminsList = _.sortBy(res.adminGroup.members, "name");
                $scope.filter($scope.adminsList, "pagedItemsAdmins", "currentPageAdmins", $scope.adminsQuery, true);

                $scope.groupingsList = _.sortBy(res.allGroupingPaths, "name");
                $scope.filter($scope.groupingsList, "pagedItemsGroupings", "currentPageGroupings", $scope.groupingsQuery, true);
                $scope.loading = false;

            }, function (res) {
                $scope.createApiErrorModal();
            });
        };

        /**
         * Fetch a list of memberships pertaining to $scope.personToLookUp.
         */
        $scope.searchForUserGroupingInformation = function () {
            if ($scope.personToLookup.length === 0) {
                $scope.emptyInput = true;
            } else {
                $scope.loading = true;
                groupingsService.getMembershipAssignmentForUser(function (res) {
                    $scope.personList = _.sortBy(res, "name");
                    $scope.filter($scope.personList, "pagedItemsPerson", "currentPagePerson", $scope.personQuery, true);
                    $scope.user = $scope.personToLookup;
                    $scope.loading = false;
                }, function (res) {
                    $scope.loading = false;
                    $scope.resStatus = res.status;
                    $scope.user = $scope.personToLookup;
                }, $scope.personToLookup);
            }
        };

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
         * Removes selected user from a list of groupings.
         */
        $scope.removeFromGroups = function () {
            // Array of names to be displayed in the modal.
            $scope.selectedGroupingsNames = [];
            // Array of grouping paths that will be passed to the API for deletion.
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
                    if (attributes === "") {
                        return;
                    }
                    let userToRemove = {
                        username: attributes.uid,
                        name: attributes.cn,
                        uhUuid: attributes.uhUuid
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
                });
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

        /**
         * Copy grouping path to clipboard and toggle 'copied!' popover.
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

    /**
     * Saves the current tab on refresh.
     */
    jQuery.noConflict();
    $(document).ready(function(){
        $("[data-toggle='tab']").on("show.bs.tab", function(e) {
            localStorage.setItem("activeTab", $(e.target).attr("href"));
        });
        var activeTab = localStorage.getItem("activeTab");
        if(activeTab){
            $("#adminTab a[href='" + activeTab + "']").tab("show");
        }
    });

    UHGroupingsApp.controller("AdminJsController", AdminJsController);
}());
