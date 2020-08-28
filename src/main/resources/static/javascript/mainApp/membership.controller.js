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

        function extendDuplicatePaths(memberships) {
            let dups = [];
            if (undefined === memberships)
                return dups;
            _.forEach(memberships, (membership) => {
                dups.push(memberships.filter(ms => {
                    if (ms.name === membership.name) {
                        return ms;
                    }
                }));
            });
            return dups;
        }

        function mergeDuplicateValues(dups) {
            let result = [];
            _.forEach(dups, (membership) => {
                if (membership.length > 1) {
                    _.forEach(membership, m => {
                        membership[0].inInclude |= m.inInclude;
                        membership[0].inExclude |= m.inExclude;
                        membership[0].inBasis |= m.inBasis;
                        membership[0].inOwner |= m.inOwner;
                    });
                }
                result.push(membership[0]);
            });
            return result;
        }

        /**
         * Loads the groups the user is a member in, the groups the user is able to opt in to, and the groups the user
         * is able to opt out of.
         */
        $scope.init = function () {
            $scope.loading = true;

            groupingsService.getMembershipResults((res) => {
                let data = [];
                _.forEach(res, (membership) => {
                    data.push(membership);
                });

                let dups = extendDuplicatePaths(data);
                let result = mergeDuplicateValues(dups);

                $scope.membershipsList = _.sortBy(_.uniq(result), "name");
                // Chunk array to pages
                let i = 0;
                const pageSize = 20;
                while (i < $scope.membershipsList.length) {
                    $scope.pagedItemsMemberships.push($scope.membershipsList.slice(i, pageSize + i));
                    i += pageSize;
                }
                $scope.loading = false;
            }, (res) => console.log(res));

            groupingsService.getOptInGroups((res) => {
                _.forEach(res, (path) => {
                    $scope.optInList.push({
                        "name": path.split(":").pop(),
                        "path": path
                    });
                });
                $scope.optInList = _.sortBy($scope.optInList, "name");
                $scope.filter($scope.optInList, "pagedItemsOptInList", "currentPageOptIn", $scope.optInQuery, true);
            }, (res) => console.log(res));
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

        function handleUnsuccessfulOpt(res) {
            console.log(res);
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
