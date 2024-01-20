/* global _, angular, UHGroupingsApp */

(() => {
    /**
     * This controller contains functions specific to the announcements seen in the home page.
     * @param $scope - binding between controller and HTML page
     * @param $controller - service for instantiating controllers
     * @param groupingsService - service for creating requests to the groupings API
     */
    function AnnouncementsJsController($scope, $controller, groupingsService) {
        angular.extend(this, $controller("GeneralJsController", { $scope }));
        /**
         * Load the valid outage messages into outageMessage,
         * otherwise display an API error modal.
         */
        $scope.init = () => {
            groupingsService.getAnnouncements((res) => {
                    console.log(res);
                    $scope.activeAnnouncements = $scope.handleActiveAnnouncements(res.announcements);
                }, () =>{
                    $scope.displayApiErrorModal();
                }
            );
        };

        $scope.handleActiveAnnouncements = (announcements) =>
            announcements
                .filter((announcement) => announcement.state === "Active")
                .map((announcement) => announcement.message);

    }

    UHGroupingsApp.controller("AnnouncementsJsController", AnnouncementsJsController);
})();