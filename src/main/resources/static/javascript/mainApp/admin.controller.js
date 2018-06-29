(function () {

    /**
     * This controller contains functions specific to the admin page.
     * @param $scope - binding between controller and HTML page
     * @param $controller - service for instantiating controllers
     * @param $uibModal - the UI Bootstrap service for creating modals
     * @param dataProvider - service function that provides GET and POST requests for getting or updating data
     */
    function AdminJsController($scope, $window, $controller, $uibModal, dataProvider, BASE_URL) {

        $scope.adminsList = [];
        $scope.pagedItemsAdmins = [];
        $scope.currentPageAdmins = 0;

        $scope.gap = 2;
        $scope.itemsPerPage = 20;

        // Allow this controller to use functions from the General Controller
        angular.extend(this, $controller("GeneralJsController", { $scope: $scope }));

        /**
         * Initializes the page, displaying the list of groupings to administer and the list of admins to manage.
         */
        $scope.init = function () {
            // Adds the loading spinner.
            $scope.loading = true;
            var endpoint = BASE_URL + "adminLists";

            dataProvider.loadData(function (res) {
                $scope.adminsList = _.sortBy(res.adminGroup.members, "name");
                $scope.pagedItemsAdmins = $scope.groupToPages($scope.adminsList);

                $scope.groupingsList = _.sortBy(res.allGroupings, "name");
                $scope.pagedItemsGroupings = $scope.groupToPages($scope.groupingsList);

                $scope.loading = false;
            }, function (res) {
                dataProvider.handleException({ exceptionMessage: res.exceptionMessage }, "feedback/error", "feedback");
            }, endpoint);
        };

        $scope.displayAdmins = function () {
            $scope.resetGroupingInformation();
            $scope.showGrouping = false;
        };

        // TODO: Find a way to make the 3 adds into a more singular function.

        /**
         * Adds a user to the admin list.
         */
        $scope.addAdmin = function () {
            var adminToAdd = $scope.adminToAdd;
            var endpoint = "api/groupings/" + adminToAdd + "/addAdmin";

            dataProvider.updateData(function (res) {
                $scope.createAddModal({
                    user: adminToAdd,
                    response: res,
                    listName: "admins"
                });
                $scope.adminToAdd = "";
            }, endpoint);
        };

        /**
         * Removes an admin from the admin list. There must be at least one admin remaining.
         * @param {number} index - the index of the admin to delete, with the current page and items per page taken into
         * account
         */
        $scope.removeAdmin = function (index) {
            var deleteUser = $scope.adminsList[index].username;
            var deleteUrl = "api/groupings/" + deleteUser + "/deleteAdmin";
            var listName = "admins";
            $scope.createRemoveModal(deleteUser, deleteUrl, listName);

        };

        /**
         * Creates a modal that prompts the user whether they want to delete the user or not. If 'Yes' is pressed, then
         * a request is made to delete the user.
         * @param {string} user - the user to delete
         * @param {string} url - the URL used to make the request
         * @param {string} listName - where the user is being removed from
         * @param {string?} path - the path to the grouping (if deleting a user from a grouping)
         */
        $scope.createRemoveModal = function (user, url, listName, path) {
            $scope.userToDelete = user;
            $scope.listName = listName;

            $scope.removeModalInstance = $uibModal.open({
                templateUrl: "modal/removeModal.html",
                scope: $scope
            });

            $scope.removeModalInstance.result.then(function () {
                $scope.loading = true;
                dataProvider.updateData(function () {
                    if (path === undefined) {
                        if ($scope.currentUser === $scope.userToDelete) {
                            $window.location.href = "home";
                        } else {
                            $scope.init();
                        }
                    } else {
                        $scope.getGroupingInformation();
                    }
                }, url);

            });
        };

    }

    UHGroupingsApp.controller("AdminJsController", AdminJsController);

})();
