(function () {

    /**
     * This controller contains functions specific to the groupings page.
     * @param $scope - binding between controller and HTML page
     * @param $controller - service for instantiating controllers
     * @param $uibModal - the UI Bootstrap service for creating modals
     * @param dataProvider - service function that provides GET and POST requests for getting or updating data
     */
    function OwnerJsController($scope, $controller, $window, $uibModal, dataProvider) {

        $scope.gap = 2;
        $scope.itemsPerPage = 20;

        // Allow this controller to use functions from the General Controller
        angular.extend(this, $controller("GeneralJsController", { $scope: $scope }));

        /**
         * Initialize function that retrieves the groupings you own.
         */
        $scope.init = function () {
            $scope.loading = true;
            var groupingsOwned = "api/groupings/groupingAssignment";

            dataProvider.loadData(function (d) {
                $scope.groupingsList = (_.sortBy((d.groupingsOwned),'name'));
                $scope.pagedItemsGroupings = $scope.groupToPages($scope.groupingsList);
                $scope.loading = false;
            }, function (d) {
                dataProvider.handleException({ exceptionMessage: d.exceptionMessage }, "feedback/error", "feedback");
            }, groupingsOwned);
        };

        /**
         * Creates a modal that prompts the user whether they want to delete the user or not. If 'Yes' is pressed, then
         * a request is made to delete the user.
         * @param {string} user - the user to delete
         * @param {string} url - the URL used to make the request
         * @param {string} listName - where the user is being removed from
         * @param {string} path - the path to the grouping
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
                if ($scope.currentUser === $scope.userToDelete && listName === 'owners') {
                    if ($scope.groupingsList.length === 1) {
                        dataProvider.updateData(function () {
                            $window.location.href = "home";
                        }, url);
                    } else {
                        dataProvider.updateData(function () {
                            $window.location.href = "groupings";
                        }, url);
                    }
                } else {
                    // Reload the grouping
                    dataProvider.updateData(function () {
                        $scope.getData(path);
                    }, url);
                }
            });
        };

    }

    UHGroupingsApp.controller("OwnerJsController", OwnerJsController);

})();
