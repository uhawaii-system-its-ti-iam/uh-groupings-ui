(function () {

    /**
     * Controller for the memberships page.
     * @param $scope - binding between controller and HTML page
     * @param $window - the browser window object
     * @param $controller - the service for instantiating controllers
     * @param dataProvider - service that handles redirection to the feedback page upon error
     * @param groupingsService - service for creating requests to the groupings API
     */
    function MembershipJsController($scope, $window, $controller, groupingsService, dataProvider, Message) {

        $scope.membershipsList = [];
        $scope.pagedItemsMemberships = [];
        $scope.currentPageMemberships = 0;

        $scope.optInList = [];
        $scope.pagedItemsOptInList = [];
        $scope.currentPageOptIn = 0;

        $scope.loading = false;

        angular.extend(this, $controller("GeneralJsController", { $scope: $scope }));

        /**
         * Loads the groups the user is a member in, the groups the user is able to opt in to, and the groups the user
         * is able to opt out of.
         */
        $scope.init = function () {
            $scope.loading = true;

            groupingsService.getMembershipAssignment(function (res) {
                $scope.membershipsList = _.sortBy(res.groupingsIn, "name");
                $scope.filter($scope.membershipsList, "pagedItemsMemberships", "currentPageMemberships", $scope.membersQuery, true);

                $scope.optInList = _.sortBy(res.groupingsToOptInTo, "name");
                $scope.filter($scope.optInList, "pagedItemsOptInList", "currentPageOptIn", $scope.optInQuery, true);

                $scope.loading = false;
            }, function (res) {
                dataProvider.handleException({ exceptionMessage: JSON.stringify(res, null, 4) }, "feedback/error", "feedback");
            });
        };

        $scope.memberFilterReset = function () {
            $scope.membersQuery = "";
            $scope.optInQuery = "";
            $scope.filter($scope.membershipsList, "pagedItemsMemberships", "currentPageMemberships", $scope.membersQuery, true);
            $scope.filter($scope.optInList, "pagedItemsOptInList", "currentPageOptIn", $scope.optInQuery, true);
        };

        /**
         * Handles responses for opting into or out of a grouping.
         * @param {object} res - the response from opting into/out of a grouping
         */
        function handleSuccessfulOpt(res) {
            if (_.startsWith(res[0].resultCode, "SUCCESS")) {
                $scope.init();
            }
        }

        /**
         * Adds the user to the exclude group of the grouping selected. Sends back an alert saying if it failed.
         * @param {number} currentPage - the current page within the table
         * @param {number} indexClicked - the index of the grouping clicked by the user
         */
        $scope.optOut = function (currentPage, indexClicked) {
            const groupingPath = $scope.pagedItemsMemberships[currentPage][indexClicked].path;
            $scope.loading = true;
            groupingsService.optOut(groupingPath, handleSuccessfulOpt, handleUnsuccessfulOpt);
        };

        /**
         * Adds the user to the include group of the grouping selected.
         * @param {number} currentPage - the current page within the table
         * @param {number} indexClicked - the index of the grouping clicked by the user
         */
        $scope.optIn = function (currentPage, indexClicked) {
            const groupingPath = $scope.pagedItemsOptInList[currentPage][indexClicked].path;
            $scope.loading = true;
            groupingsService.optIn(groupingPath, handleSuccessfulOpt, handleUnsuccessfulOpt);
        };

        /**
         * Copies grouping path to clipboard.
         */
        $scope.copyPath = function (grouping) {
            var copyText = document.getElementById(grouping.path);
            copyText.select();
            document.execCommand("copy");
        };
    }

    UHGroupingsApp.controller("MembershipJsController", MembershipJsController);

}());
