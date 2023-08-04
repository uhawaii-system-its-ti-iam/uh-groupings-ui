/* global _, angular, UHGroupingsApp */

(() => {
    /**
     * This controller contains functions specific to the announcements seen in the home page.
     * @param $scope - binding between controller and HTML page
     * @param $controller - service for instantiating controllers
     * @param groupingsService - service for creating requests to the groupings API
     */
    function OutageMessageJsController($scope, $controller, groupingsService) {
        angular.extend(this, $controller("GeneralJsController", { $scope }));
        /**
         * Load the valid outage messages into outageMessage,
         * otherwise display an API error modal.
         */
        $scope.init = () => {
            groupingsService.getOutageMessages((res) =>{
                $scope.outageMessage = res;
            }, () =>{
                    $scope.displayApiErrorModal();
                }
            );
        };
    }

    UHGroupingsApp.controller("OutageMessageJsController", OutageMessageJsController);
})();
