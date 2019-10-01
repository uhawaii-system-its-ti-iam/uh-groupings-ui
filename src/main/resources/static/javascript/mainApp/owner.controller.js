(function () {

    /**
     * This controller contains functions specific to the groupings page.
     * @param $scope - binding between controller and HTML page
     * @param $controller - service for instantiating controllers
     * @param groupingsService - service for creating requests to the groupings API
     */
    function OwnerJsController($scope, $controller, groupingsService, dataProvider) {

        // Allow this controller to use functions from the General Controller
        angular.extend(this, $controller("GeneralJsController", { $scope: $scope }));

        /**
         * Initialize function that retrieves the groupings you own.
         */
        $scope.init = function () {
            $scope.loading = true;

            groupingsService.getGroupingsOwned(function (res) {
                $scope.groupingsList = _.sortBy(res, "name");
                $scope.pagedItemsGroupings = $scope.groupToPages($scope.groupingsList);

                $scope.loading = false;
            }, function (res) {
                dataProvider.handleException({ exceptionMessage: JSON.stringify(res, null, 4)}, "feedback/error", "feedback");
            });
        };

        /**
         * Copies the path of a grouping through the clipboard button
         *
         * @param {Object} grouping - the current selected grouping
         */

        $scope.copyPath = function (grouping) {
            var copyText = document.getElementById(grouping.path);

            copyText.select();
            document.execCommand("copy");

        };

    }

    UHGroupingsApp.controller("OwnerJsController", OwnerJsController);

})();
