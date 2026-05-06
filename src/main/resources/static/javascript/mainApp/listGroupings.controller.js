/* global _, angular, UHGroupingsApp */

(() => {

    /**
     * This controller contains functions specific to the groupings page.
     * @param $scope - binding between controller and HTML page
     * @param $controller - service for instantiating controllers
     * @param groupingsService - service for creating requests to the groupings API
     */
    function GroupingsJsController($scope, $controller, groupingsService) {

        angular.extend(this, $controller("GroupingJsController", { $scope }));
        /**
         * Initialize function that retrieves the groupings you own.
         */
        $scope.init = () => {
            const selectedOwnerGrouping = JSON.parse(sessionStorage.getItem("selectedOwnerGrouping"));
            if (!_.isEmpty(selectedOwnerGrouping)) {
                $scope.displayOwnerGrouping(selectedOwnerGrouping);
                sessionStorage.removeItem("selectedOwnerGrouping");
            }
            $scope.loading = true;

            groupingsService.getGroupingsOwned((res) => {
                $scope.groupingsList = _.sortBy(res.groupingPaths, "name");
                $scope.pagedItemsGroupings = $scope.groupToPages($scope.groupingsList);
                $scope.loading = false;
            }, () => {
                $scope.displayApiErrorModal();
            });
        };
    }

    UHGroupingsApp.controller("GroupingsJsController", GroupingsJsController);
})();
