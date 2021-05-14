(function () {

    /**
     * Controller for the memberships page.
     * @param $scope - binding between controller and HTML page
     * @param $window - the browser window object
     * @param $controller - the service for instantiating controllers
     * @param dataProvider - service that handles redirection to the feedback page upon error
     * @param groupingsService - service for creating requests to the groupings API
     */
    function MembershipJsController($scope, $uibModal, $window, $controller, groupingsService, dataProvider, Message) {

        $scope.membershipsList = [];
        $scope.pagedItemsMemberships = [];
        $scope.currentPageMemberships = 0;

        $scope.optInList = [];
        $scope.pagedItemsOptInList = [];
        $scope.currentPageOptIn = 0;
        $scope.resStatus = 0;

        $scope.loading = false;
        $scope.membersLoaded = false;

        angular.extend(this, $controller("GeneralJsController", { $scope: $scope }));

        /**
         * Couple an array of objects into an array of object arrays which contain duplicate paths.
         */
        function coupleDuplicatePaths(memberships) {
            let dups = [];
            _.forEach(memberships, (membership) => {
                dups.push(memberships.filter(ms => {
                    if (ms.name === membership.name) {
                        return ms;
                    }
                }));
            });
            return dups;
        }

        /**
         * Take the coupled array created from coupleDuplicatePaths, merge all duplicates into one object and preserve
         * all values that each duplicate contained.
         */
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
                let path = membership[0].path.substring(0, membership[0].path.lastIndexOf(":"));
                result.push({
                    "name": membership[0].name,
                    "path": path,
                    "optOutEnabled": membership[0].optOutEnabled
                });
            });
            return result;
        }

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
         *  Load the groups a user is a member in, the groups the user is able to opt in to, and the groups the user
         *  is able to opt out of.
         */
        $scope.init = function () {
            $scope.loading = true;

            // Request a list of membership objects from the API.
            groupingsService.getMembershipResults((res) => {
                    let data = [];

                    _.forEach(res, (membership) => {
                        if (membership.inInclude) {
                            data.push(membership);
                        }
                    });

                    let dups = coupleDuplicatePaths(data);
                    let result = mergeDuplicateValues(dups);

                    $scope.membershipsList = _.sortBy(_.uniqBy(result, "name"), "name");
                    $scope.pagedItemsMemberships = objToPageArray($scope.membershipsList, 20);
                    $scope.loading = false;
                },
                (res) => {
                    dataProvider.handleException({
                        exceptionMessage: JSON.stringify(res, null, 4)
                    }, "feedback/error", "feedback");
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
                (res) => {
                    dataProvider.handleException({
                        exceptionMessage: JSON.stringify(res, null, 4)
                    }, "feedback/error", "feedback");
                }
            );
            $scope.membersLoaded = true;
        };


        /**
         * Filter member list with respect to membersQuery.
         */
        $scope.memberFilterReset = function () {
            $scope.membersQuery = "";
            $scope.optInQuery = "";
            $scope.filter($scope.membershipsList, "pagedItemsMemberships", "currentPageMemberships", $scope.membersQuery, true);
            $scope.filter($scope.optInList, "pagedItemsOptInList", "currentPageOptIn", $scope.optInQuery, true);
        };

        /**
         * Handle responses for opting into or out of a grouping.
         */
        function handleSuccessfulOpt(res) {
            if (res[0].result === "SUCCESS") {
                $scope.init();
            } else {
                $scope.createOptErrorModal(res.status);
            }
        }

        function handleUnsuccessfulOpt(res) {
            $scope.createOptErrorModal(res);
        }

        $scope.createOptErrorModal = function (resStatus) {
            $scope.loading = false;
            $scope.optErrorModalInstance = $uibModal.open({
                templateUrl: "modal/optErrorModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false
            });
        };

        $scope.closeOptErrorModal = function () {
            $scope.optErrorModalInstance.close();
        };

        /**
         * Redirect the user to the groupings page.
         */
        $scope.proceedRedirect = function () {
            $scope.optErrorModalInstance.close();
            $window.location.href = "/uhgroupings/feedback";
        };

        /**
         * Add the user to the exclude group of the grouping selected. Sends back an alert saying if it failed.
         * @param {number} currentPage - the current page within the table
         * @param {number} indexClicked - the index of the grouping clicked by the user
         */
        $scope.optOut = function (currentPage, indexClicked) {
            const groupingPath = $scope.pagedItemsMemberships[currentPage][indexClicked].path;
            $scope.loading = true;
            groupingsService.optOut(groupingPath, handleSuccessfulOpt, handleUnsuccessfulOpt);
        };

        /**
         * Add the user to the include group of the grouping selected.
         * @param {number} currentPage - the current page within the table
         * @param {number} indexClicked - the index of the grouping clicked by the user
         */
        $scope.optIn = function (currentPage, indexClicked) {
            const groupingPath = $scope.pagedItemsOptInList[currentPage][indexClicked].path;
            $scope.loading = true;
            groupingsService.optIn(groupingPath, handleSuccessfulOpt, handleUnsuccessfulOpt);
        };

        /**
         * Copy grouping path to clipboard and toggle 'copied!' popover.
         */
        $scope.copyPath = function (grouping) {
            $("[data-content='copy']").popover("hide");

            $("[data-content='copied!']").popover();
            setTimeout(function () {
                $("[data-content='copied!']").popover("hide");
            }, 1000);

            let copyText = document.getElementById(grouping.path);
            copyText.select();
            document.execCommand("copy");
        };
        /**
         * Toggle 'copy' popover when clipboard is being hovered.
         */
        $scope.hoverCopy = function () {
            $("[data-content='copy']").popover();
        };
    }

    /**
     * Saves the current tab on refresh.
     */
    jQuery.noConflict();
    $(document).ready(function () {
        $("[data-toggle='tab']").on("show.bs.tab", function (e) {
            localStorage.setItem("activeTab", $(e.target).attr("href"));
        });
        var activeTab = localStorage.getItem("activeTab");
        if (activeTab) {
            $("#memberTab a[href='" + activeTab + "']").tab("show");
        }
    });

    UHGroupingsApp.controller("MembershipJsController", MembershipJsController);

}());
