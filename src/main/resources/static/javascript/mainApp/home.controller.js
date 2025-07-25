/* global _, angular, UHGroupingsApp */

(() => {

    /**
     * This controller contains functions specific to the home page.
     * @param $scope - binding between controller and HTML page
     * @param $controller - service for instantiating controllers
     * @param groupingsService - service for creating requests to the groupings API
     * @param Message - display messages
     */
    function HomeJsController($scope, $controller, groupingsService, Message) {

        angular.extend(this, $controller('GeneralJsController', {$scope}));

        $scope.init = () => {

            /**
             * Get the number of memberships that the current user is associated with.
             */
            groupingsService.getNumberOfMemberships((res) => {
                    $scope.numberOfMemberships = res;
                }
            );

            /**
             * Get the number of groupings that the current user is associated with.
             */
            groupingsService.getNumberOfGroupings((res) => {
                    $scope.numberOfGroupings = res;
                }
            );
        };
    }

    UHGroupingsApp.controller("HomeJsController", HomeJsController);
})();
