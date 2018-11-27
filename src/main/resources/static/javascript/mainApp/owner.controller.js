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

        // Allow this controller to use functions from the General Controller
        angular.extend(this, $controller("GeneralJsController", { $scope: $scope }));
        angular.extend(this, $controller("TimeoutJsController", { $scope: $scope }));

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

    }

    UHGroupingsApp.controller("OwnerJsController", OwnerJsController);

})();
