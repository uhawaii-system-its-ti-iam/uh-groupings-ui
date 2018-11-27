(function () {

    /**
     * This controller contains functions specific to the admin page.
     * @param $scope - binding between controller and HTML page
     * @param $window - the browser window object
     * @param $controller - service for instantiating controllers
     * @param $uibModal - the UI Bootstrap service for creating modals
     * @param dataProvider - service function that provides GET and POST requests for getting or updating data
     * @param BASE_URL - the constant base URL for endpoints
     */
    function AdminJsController($scope, $window, $controller, $uibModal, dataProvider, BASE_URL) {

        $scope.adminsList = [];
        $scope.pagedItemsAdmins = [];
        $scope.currentPageAdmins = 0;

        // Allow this controller to use functions from the General Controller
        angular.extend(this, $controller("GeneralJsController", { $scope: $scope }));
        angular.extend(this, $controller("TimeoutJsController", { $scope: $scope }));

        /**
         * Initializes the page, displaying the list of groupings to administer and the list of admins to manage.
         */
        $scope.init = function () {
            // Adds the loading spinner.
            $scope.loading = true;
            var endpoint = BASE_URL + "adminLists";

            dataProvider.loadData(function (res) {
                if (_.isNull(res)) {
                    $scope.createApiErrorModal();
                } else {
                    $scope.adminsList = _.sortBy(res.adminGroup.members, "name");
                    $scope.filter($scope.adminsList, "pagedItemsAdmins", "currentPageAdmins", $scope.adminsQuery);

                    $scope.groupingsList = _.sortBy(res.allGroupings, "name");
                    $scope.filter($scope.groupingsList, "pagedItemsGroupings", "currentPageGroupings", $scope.groupingsQuery);

                    $scope.loading = false;
                }
            }, function (res) {
                dataProvider.handleException({ exceptionMessage: res.exceptionMessage }, "feedback/error", "feedback");
            }, endpoint);
        };

        $scope.displayAdmins = function () {
            $scope.resetGroupingInformation();
            $scope.pagedItemsGroupings = $scope.groupToPages($scope.groupingsList);
            $scope.showGrouping = false;
        };

        // TODO: Find a way to make the 3 adds into a more singular function.

        /**
         * Adds a user to the admin list.
         */
        $scope.addAdmin = function () {
            var adminToAdd = $scope.adminToAdd;
            var endpoint = BASE_URL + adminToAdd + "/addAdmin";

            if (_.isEmpty(adminToAdd)) {
                $scope.createAddModal({ user: adminToAdd });
            } else {
                $scope.createConfirmAddModal({
                    userToAdd: adminToAdd,
                    listName: "admins",
                    endpoint: endpoint
                });
            }
        };

        /**
         * Removes an admin from the admin list. There must be at least one admin remaining.
         * @param {number} currentPage - the current page in the admins list
         * @param {number} index - the index of the admin to delete, with the current page and items per page taken into
         * account
         */
        $scope.removeAdmin = function (currentPage, index) {
            var adminToRemove = $scope.pagedItemsAdmins[currentPage][index];
            var endpoint = BASE_URL + adminToRemove.username + "/deleteAdmin";

            if ($scope.adminsList.length > 1) {
                $scope.createRemoveModal({
                    user: adminToRemove,
                    endpoint: endpoint,
                    listName: "admins"
                });
            } else {
                $scope.createRemoveErrorModal({
                    userType: "admin"
                });
            }
        };

    }

    UHGroupingsApp.controller("AdminJsController", AdminJsController);

})();
