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
         * If the request fails, display the API error modal only when the user is authenticated.
         */
        $scope.init = () => {
            groupingsService.getAnnouncements((res) => {
                // A proxy or error page can answer with HTTP 200 and a non-JSON body. Guard against
                // that rather than throwing inside the success handler, where the exception would be
                // swallowed by $q.
                if (!res || !Array.isArray(res.announcements)) {
                    $scope.activeAnnouncements = [];
                    return;
                }
                $scope.activeAnnouncements = res.announcements.map((announcement) => announcement.message);
            }, () => {
                $scope.activeAnnouncements = [];
                // Do not display the API error modal before the user has authenticated.
                // The modal can lead to the feedback route, which requires authentication.
                if ($scope.currentUser?.uid && $scope.currentUser?.uhUuid) {
                    $scope.displayApiErrorModal();
                }
            });
        };

    }

    UHGroupingsApp.controller("AnnouncementsJsController", AnnouncementsJsController);
})();