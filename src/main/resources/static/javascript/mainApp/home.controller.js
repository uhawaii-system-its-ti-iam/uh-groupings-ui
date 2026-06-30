/* global _, angular, UHGroupingsApp */

(() => {

    /**
     * This controller contains functions specific to the home page.
     * @param $scope - binding between controller and HTML page
     * @param $controller - service for instantiating controllers
     * @param userService - service for accessing logged-in user's data.
     * @param groupingsService - service for creating requests to the groupings API
     */
    function HomeJsController($scope, $controller, userService, groupingsService) {

        angular.extend(this, $controller('GeneralJsController', {$scope}));

        const countFallback = "--";

        const handleCountError = (setFallbackCount) => (response) => {
            if (response && response.data && response.data.resultCode === "BACKEND_UNAVAILABLE") {
                setFallbackCount();
            }
        };

        $scope.init = () => {

            /**
             * Refresh current user info.
             */
            userService.refresh();

            /**
             * Get the number of memberships that the current user is associated with.
             */
            groupingsService.getNumberOfMemberships((res) => {
                    $scope.numberOfMemberships = res;
                },
                handleCountError(() => {
                    $scope.numberOfMemberships = countFallback;
                })
            );

            /**
             * Get the number of groupings that the current user is associated with.
             */
            groupingsService.getNumberOfGroupings((res) => {
                    $scope.numberOfGroupings = res;
                },
                handleCountError(() => {
                    $scope.numberOfGroupings = countFallback;
                })
            );
        };
    }

    UHGroupingsApp.controller("HomeJsController", HomeJsController);
})();
