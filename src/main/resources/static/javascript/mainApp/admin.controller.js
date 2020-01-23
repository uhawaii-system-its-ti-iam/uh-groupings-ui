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

        // Allow this controller to use functions from the General Controller
        angular.extend(this, $controller("GeneralJsController", { $scope: $scope }));


        $scope.createRoleErrorModal = function () {
            $scope.loading = false;
            $scope.RoleErrorModalInstance = $uibModal.open({
                templateUrl: "modal/roleErrorModal",
                scope: $scope,
                backdrop: 'static',
                keyboard: false
            });
        };


        /**
         * Initializes the page, displaying the list of groupings to administer and the list of admins to manage.
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

        $scope.displayAdmins = function () {
            $scope.resetGroupingInformation();
            $scope.filter($scope.adminsList, "pagedItemsAdmins", "currentPageAdmins", $scope.adminsQuery, true);
            $scope.pagedItemsGroupings = $scope.groupToPages($scope.groupingsList);
            $scope.showGrouping = false;
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
         * Removes an admin from the admin list. There must be at least one admin remaining.
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
         * Copies grouping path to clipboard.
         */
        $scope.copyPath = function (grouping) {
            var copyText = document.getElementById(grouping.path);
            copyText.select();
            document.execCommand("copy");
        };
    }

    UHGroupingsApp.controller("AdminJsController", AdminJsController);

})();
