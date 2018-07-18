(function () {

    /**
     * This controller contains functions specific to the groupings page.
     * @param $scope - binding between controller and HTML page
     * @param $controller - service for instantiating controllers
     * @param $window - the browser window object
     * @param $uibModal - the UI Bootstrap service for creating modals
     * @param dataProvider - service function that provides GET and POST requests for getting or updating data
     * @param BASE_URL - the constant base URL for endpoints
     */
    function OwnerJsController($scope, $controller, $window, $uibModal, dataProvider, BASE_URL) {

        $scope.gap = 2;
        $scope.itemsPerPage = 20;

        // Allow this controller to use functions from the General Controller
        angular.extend(this, $controller("GeneralJsController", { $scope: $scope }));

        /**
         * Initialize function that retrieves the groupings you own.
         */
        $scope.init = function () {
            $scope.loading = true;
            var endpoint = BASE_URL + "groupingAssignment";

            dataProvider.loadData(function (res) {
                if (_.isNull(res)) {
                    $scope.createApiErrorModal();
                } else {
                    $scope.groupingsList = _.sortBy(res.groupingsOwned, "name");
                    $scope.pagedItemsGroupings = $scope.groupToPages($scope.groupingsList);
                }
                $scope.loading = false;
            }, function (res) {
                dataProvider.handleException({ exceptionMessage: res.exceptionMessage }, "feedback/error", "feedback");
            }, endpoint);
        };

        /**
         * Creates a modal that prompts the user whether they want to delete the user or not. If 'Yes' is pressed, then
         * a request is made to delete the user.
         * @param {object} options - the options object
         * @param {string} options.user - the user being removed
         * @param {string} options.endpoint - the endpoint used to make the request
         * @param {string} options.listName - where the user is being removed from
         */
        $scope.createRemoveModal = function (options) {
            $scope.userToRemove = options.user;
            $scope.listName = options.listName;

            $scope.removeModalInstance = $uibModal.open({
                templateUrl: "modal/removeModal.html",
                scope: $scope
            });

            $scope.removeModalInstance.result.then(function () {
                $scope.loading = true;

                dataProvider.updateData(function () {
                    if ($scope.currentUser === $scope.userToRemove && $scope.listName === "owners") {
                        if ($scope.groupingsList.length === 1) {
                            $window.location.href = "home";
                        } else {
                            $window.location.href = "groupings";
                        }
                    } else {
                        $scope.getGroupingInformation();
                    }
                }, function (res) {
                    console.log("Error, Status Code: " + res.statusCode);
                }, options.endpoint);
            });
        };

    }

    UHGroupingsApp.controller("OwnerJsController", OwnerJsController);

})();
