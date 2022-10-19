/* global _, angular, UHGroupingsApp */

(function () {

    /**
     * Controller for the memberships page.
     * @param $scope - binding between controller and HTML page
     * @param $window - the browser window object
     * @param $controller - the service for instantiating controllers
     * @param dataProvider - service that handles redirection to the feedback page upon error
     * @param groupingsService - service for creating requests to the groupings API
     */
    function MembershipJsController($scope, $uibModal, $window, $controller, groupingsService, dataProvider) {

        $scope.membershipsList = [];
        $scope.pagedItemsMemberships = [];
        $scope.currentPageMemberships = 0;

        $scope.optInList = [];
        $scope.pagedItemsOptInList = [];
        $scope.currentPageOptIn = 0;
        $scope.resStatus = 0;

        $scope.loading = false;

        angular.extend(this, $controller("GeneralJsController", { $scope }));

        /**
         *  Load the groups a user is a member in, the groups the user is able to opt in to, and the groups the user
         *  is able to opt out of.
         */
        $scope.init = function () {
            $scope.loading = true;

            // Request a list of membership objects from the API.
            groupingsService.getMembershipResults((res) => {
                    // Codacy throws an error regarding the '_' in the uniqBy function. This error will be ignored until a solution is found.
                    $scope.membershipsList = _.filter(_.sortBy(_.uniqBy(res, "name"), "name"), (membership) => {
                        return membership.inInclude;
                    });
                    $scope.pagedItemsMemberships = $scope.objToPageArray($scope.membershipsList, 20);
                    $scope.loading = false;
                },
                (res) => {
                    $scope.createApiErrorModal();
                }
            );

            // Request a list of opt-in-able paths from the API.
            $scope.optInList = [];
            groupingsService.getOptInGroups((res) => {
                    _.forEach(res, (path) => {
                        $scope.optInList.push({
                            "name": path.name,
                            "path": path.path,
                            "description": path.description
                        });
                    });
                    $scope.optInList = _.sortBy($scope.optInList, "name");
                    $scope.filter($scope.optInList, "pagedItemsOptInList", "currentPageOptIn", $scope.optInQuery, true);
                },
                (res) => {
                    $scope.createApiErrorModal();
                }
            );
        };

        $scope.clearFilterQueryStrings = function () {
            $scope.membersQuery = "";
            $scope.optInQuery = "";
        };

        /**
         * Filter member list with respect to membersQuery.
         */
        $scope.memberFilterReset = function () {
            $scope.clearFilterQueryStrings();
            $scope.filter($scope.membershipsList, "pagedItemsMemberships", "currentPageMemberships", $scope.membersQuery, true);
            $scope.filter($scope.optInList, "pagedItemsOptInList", "currentPageOptIn", $scope.optInQuery, true);
        };

        /**
         * Handle responses for opting into or out of a grouping.
         */
        function handleSuccessfulOpt(res) {
            if (res.result === "SUCCESS") {
                $scope.init();
            } else {
                $scope.createOptErrorModal(res.status);
            }
            $scope.clearFilterQueryStrings();
        }

        function handleUnsuccessfulOpt(res) {
            $scope.clearFilterQueryStrings();
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
     */

    UHGroupingsApp.controller("MembershipJsController", MembershipJsController);

}());