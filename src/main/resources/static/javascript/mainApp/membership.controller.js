(function () {

    /**
     * Controller for the memberships page.
     * @param $scope - binding between controller and HTML page
     * @param $window - the browser window object
     * @param $controller - the service for instantiating controllers
     * @param dataProvider - service that handles redirection to the feedback page upon error
     * @param groupingsService - service for creating requests to the groupings API
     */
    function MembershipJsController($scope, $window, $controller, groupingsService, dataProvider) {

        $scope.membershipsList = [];
        $scope.pagedItemsMemberships = [];
        $scope.currentPageMemberships = 0;

        $scope.optInList = [];
        $scope.pagedItemsOptInList = [];
        $scope.currentPageOptIn = 0;

        $scope.loading = false;

        angular.extend(this, $controller("GeneralJsController", { $scope: $scope }));

        /**
         * Chunk an array of objects into an array of paged object arrays.
         */
        function objToPageArray(obj, size) {
            let i = 0;
            let arr = [];
            while (i < obj.length) {
                arr.push(obj.slice(i, size + i));
                i += size;
            }
            return arr;
        }

        /**
         * Load the groups the user is a member in, the groups the user is able to opt in to, and the groups the user
         * is able to opt out of.
         */
        $scope.init = function () {
            $scope.loading = true;

            // Request a list of membership objects from the API.
            groupingsService.getMembershipAssignment((res) => {
                    console.log(res);
                    $scope.membershipsList = _.sortBy(_.uniqBy(res, "name"), "name");
                    $scope.pagedItemsMemberships = objToPageArray($scope.membershipsList, 20);
                    $scope.loading = false;
                },
                () => {
                    $scope.createApiErrorModal();
                }
            );

            // Request a list of opt-in-able paths from the API.
            $scope.optInList = [];
            groupingsService.getOptInGroups((res) => {
                    _.forEach(res, (path) => {
                        $scope.optInList.push({
                            "name": path.split(":").pop(),
                            "path": path
                        });
                    });
                    $scope.optInList = _.sortBy($scope.optInList, "name");
                    $scope.filter($scope.optInList, "pagedItemsOptInList", "currentPageOptIn", $scope.optInQuery, true);
                },
                () => {
                    $scope.createApiErrorModal();
                }
            );
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
         * Generic handler for an unsuccessful opt into/out of a grouping.
         * @param {object} res - the response from the request
         */
        function handleUnsuccessfulOpt(res) {
            console.log("Error opting into grouping: " + res.statusCode);
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

})();
