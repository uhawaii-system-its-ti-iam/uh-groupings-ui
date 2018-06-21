(function () {

    /**
     * Controller for the memberships page.
     * @param $scope - binding between controller and HTML page
     * @param $window - the browser window object
     * @param $controller - the service for instantiating controllers
     * @param dataProvider - the service that provides GET and POST requests for getting or updating data
     * @param BASE_URL - the constant base URL for endpoints
     */
    function MembershipJsController($scope, $window, $controller, dataProvider, BASE_URL) {

        $scope.membershipsList = [];
        $scope.pagedItemsMemberships = [];
        $scope.currentPageMemberships = 0;

        $scope.optInList = [];
        $scope.pagedItemsOptIn = [];
        $scope.currentPageOptIn = 0;

        $scope.loading = true;

        $scope.gap = 2;
        $scope.itemsPerPage = 20;

        $scope.optOutList = [];

        angular.extend(this, $controller("TableJsController", { $scope: $scope }));

        /**
         * Loads the groups the user is a member in, the groups the user is able to opt in to, and the groups the user
         * is able to opt out of.
         */
        $scope.init = function () {
            var endpoint = BASE_URL + "groupingAssignment";

            dataProvider.loadData(function (res) {
                $scope.membershipsList = _.sortBy(res.groupingsIn, "name");
                $scope.pagedItemsMemberships = $scope.groupToPages($scope.membershipsList);

                $scope.optInList = _.sortBy(res.groupingsToOptInTo, "name");
                $scope.pagedItemsOptInList = $scope.groupToPages($scope.optInList);

                $scope.optOutList = res.groupingsToOptOutOf;

                $scope.loading = false;
            }, function (res) {
                dataProvider.handleException({ exceptionMessage: res.exceptionMessage }, "feedback/error", "feedback");
            }, endpoint);
        };

        /**
         * Adds the user to the exclude group of the grouping selected. Sends back an alert saying if it failed.
         * @param {number} currentPage - the current page within the table
         * @param {number} indexClicked - the index of the grouping clicked by the user
         */
        $scope.optOut = function (currentPage, indexClicked) {
            var groupingPath = $scope.pagedItemsMemberships[currentPage][indexClicked].path;
            var endpoint = BASE_URL + groupingPath + "/optOut";

            $scope.loading = true;

            dataProvider.updateData(function (res) {
                if (_.startsWith(res[0].resultCode, "FAILURE")) {
                    alert("Failed to opt out");
                    $scope.loading = false;
                } else {
                    $scope.init();
                }
            }, endpoint);
        };

        /**
         * Adds the user to the include group of the grouping selected.
         * @param {number} currentPage - the current page within the table
         * @param {number} indexClicked - the index of the grouping clicked by the user
         */
        $scope.optIn = function (currentPage, indexClicked) {
            var groupingPath = $scope.pagedItemsOptInList[currentPage][indexClicked].path;
            var endpoint = BASE_URL + groupingPath + "/optIn";

            $scope.loading = true;

            dataProvider.updateData(function (res) {
                if (_.startsWith(res[0].resultCode, "FAILURE")) {
                    alert("Failed to opt in");
                    $scope.loading = false;
                } else {
                    $scope.init();
                }
            }, endpoint);
        };

        /**
         * Checks if membership is required in the grouping.
         * @param index - the index of the grouping in the table
         * @returns {boolean} true if membership is required, otherwise returns false
         */
        $scope.membershipRequired = function (currentPage, indexClicked) {
            var groupingPath = $scope.pagedItemsMemberships[currentPage][indexClicked].path;
            return !_.some($scope.optOutList, { path: groupingPath });
        };

    }

    UHGroupingsApp.controller("MembershipJsController", MembershipJsController);

})();