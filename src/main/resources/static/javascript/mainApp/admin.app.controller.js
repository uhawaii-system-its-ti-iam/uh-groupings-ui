(function () {

    /**
     * This controller contains functions specific to the admin page.
     * @param $scope - binding between controller and HTML page
     * @param $controller - service for instantiating controllers
     * @param $uibModal - the UI Bootstrap service for creating modals
     * @param dataProvider - service function that provides GET and POST requests for getting or updating data
     */
    function AdminJsController($scope, $window, $controller, $uibModal, dataProvider) {

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

            var url = "api/groupings/adminLists";
            $scope.currentUser = $window.document.getElementById("name").innerHTML;

            dataProvider.loadData(function (d) {
                if (d.allGroupings.length == 0) {
                    $scope.createApiErrorModal();
                } else {
                    $scope.adminsList = d.adminGroup.members;
                    $scope.groupingsList = d.allGroupings;
                    $scope.groupingsList = _.sortBy($scope.groupingsList, "name");
                    $scope.modify($scope.adminsList);
                    $scope.pagedItemsAdmins = $scope.groupToPages($scope.adminsList);
                    $scope.pagedItemsGroupings = $scope.groupToPages($scope.groupingsList);
                }
                $scope.loading = false;
            }, function (d) {
                dataProvider.handleException({ exceptionMessage: d.exceptionMessage }, "feedback/error", "feedback");
            }, url);
        };

        $scope.change = function () {
            $scope.showGrouping = false;
            $scope.resetGroupingInformation();
        };

        // TODO: Find a way to make the 3 adds into a more singular function.

        /**
         * Adds a user to the admin list.
         */
        $scope.addAdmin = function () {
            var adminToAdd = $scope.adminToAdd;
            var addUrl = "api/groupings/" + adminToAdd + "/addAdmin";
            dataProvider.updateData(function (d) {
                var successful = false;
                if (d.statusCode != null) {
                    console.log("Error, Status Code: " + d.statusCode);
                } else if (d.resultCode.indexOf("SUCCESS") === 0) {
                    successful = true;
                }
                var listName = "admins list";
                $scope.createAddModal(adminToAdd, successful, listName);
                $scope.adminToAdd = "";
            }, addUrl);
        };

        /**
         * Creates a modal telling the user whether or not the user was successfully added into the grouping/admin list.
         * @param {string} user - the user being added
         * @param {boolean} wasSuccessful - whether or not the user was successfully added
         * @param {string} listName - where the user is being added to
         * @param {string?} path - the path to the grouping (if adding the user to a grouping)
         */
        $scope.createAddModal = function (user, wasSuccessful, listName, path) {
            $scope.user = user;
            $scope.wasSuccessful = wasSuccessful;
            $scope.listName = listName;

            $scope.addModalInstance = $uibModal.open({
                templateUrl: "modal/addModal.html",
                scope: $scope
            });

            $scope.addModalInstance.result.finally(function () {
                if (wasSuccessful) {
                    $scope.loading = true;
                    // If no path was specified, then refresh the admin list. Otherwise, refresh the grouping
                    if (path === undefined) {
                        $scope.init();
                    } else {
                        $scope.getData(path);
                    }
                }
            });
        };

        /**
         * Removes an admin from the admin list. There must be at least one admin remaining.
         * @param {number} index - the index of the admin to delete, with the current page and items per page taken into
         * account
         */
        $scope.removeAdmin = function (index) {
            var deleteUser = $scope.adminsList[index].username;
            var deleteUrl = "api/groupings/" + deleteUser + "/deleteAdmin";
            var listName = "admins list";
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
                // Remove the user, then reload either the admin list or grouping
                if($scope.currentUser === $scope.userToDelete)
                {
                    dataProvider.updateData(function () {
                        $window.location.href = "/uhgroupings/";
                    }, url);
                }
                else
                {
                    dataProvider.updateData(function () {
                        if (path === undefined) {
                            $scope.init();
                        } else {
                            $scope.getData(path);
                        }
                    }, url);
                }

            });
        };

    }

    UHGroupingsApp.controller("AdminJsController", AdminJsController);

})();
