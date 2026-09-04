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
                // A proxy or error page can answer with HTTP 200 and a non-JSON body. Guard against
                // that rather than throwing inside the success handler, where the exception would be
                // swallowed by $q and leave no visible trace of the failure.
                if (!res || !Array.isArray(res.announcements)) {
                    console.error("Unexpected announcements response; expected {announcements: []} but received:", res);
                    $scope.activeAnnouncements = [];
                    return;
                }
                $scope.activeAnnouncements = res.announcements.map((announcement) => announcement.message);
            }, (res) => {
                console.error(`Failed to load announcements. status: ${res && res.status}`, res);
                $scope.displayApiErrorModal();
            });
        };

    }

    UHGroupingsApp.controller("AnnouncementsJsController", AnnouncementsJsController);
})();