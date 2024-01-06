/* global _, angular, UHGroupingsApp */

(() => {

    /**
     * Controller for the memberships page.
     * @param $scope - binding between controller and HTML page
     * @param $controller - the service for instantiating controllers
     * @param $window - the browser window object
     * @param $uibModal - the UI Bootstrap service for creating modals
     * @param dataProvider - service that handles redirection to the feedback page upon error
     * @param groupingsService - service for creating requests to the groupings API
     */
    function MembershipJsController($scope, $controller, $window, $uibModal, groupingsService, dataProvider) {

        $scope.membershipsList = [];
        $scope.pagedItemsMemberships = [];
        $scope.currentPageMemberships = 0;

        $scope.optInList = [];
        $scope.pagedItemsOptInList = [];
        $scope.currentPageOptIn = 0;
        $scope.resStatus = 0;

        $scope.loadingMemberships = false;
        $scope.loadingOptIn = false;

        angular.extend(this, $controller("GeneralJsController", { $scope }));

        /**
         *  Load the groups a user is a member in, the groups the user is able to opt in to, and the groups the user
         *  is able to opt out of.
         */
        $scope.init =  () => {
            $scope.loadingMemberships = true;
            $scope.loadingOptIn = true;

            // Request a list of membership objects from the API.
            groupingsService.getMembershipResults((res) => {
                    // Codacy throws an error regarding the '_' in the uniqBy function. This error will be ignored until a solution is found.
                    $scope.membershipsList = _.sortBy(_.uniqBy(res.results, "name"), "name");
                    $scope.pagedItemsMemberships = $scope.objToPageArray($scope.membershipsList, 20);
                    $scope.loadingMemberships = false;
                },
                () => {
                    $scope.displayApiErrorModal();
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
                  $scope.loadingOptIn = false;
                },
                () => {
                    $scope.displayApiErrorModal();
                }
            );
        };

        $scope.clearFilterQueryStrings = () => {
            $scope.membersQuery = "";
            $scope.optInQuery = "";
        };

        /**
         * Filter member list with respect to membersQuery.
         */
        $scope.memberFilterReset = () => {
            $scope.clearFilterQueryStrings();
            $scope.filter($scope.membershipsList, "pagedItemsMemberships", "currentPageMemberships", $scope.membersQuery, true);
            $scope.filter($scope.optInList, "pagedItemsOptInList", "currentPageOptIn", $scope.optInQuery, true);
        };

        /**
         * Handle responses for opting into or out of a grouping.
         */
        const handleSuccessfulOpt = (res) => {
            if (res.resultCode === "SUCCESS") {
                $scope.init();
            } else {
                $scope.displayOptErrorModal();
            }
            $scope.clearFilterQueryStrings();
        };

        const handleUnsuccessfulOpt = (res) => {
            $scope.clearFilterQueryStrings();
            $scope.displayOptErrorModal(res);
        };

        $scope.displayOptErrorModal = () => {
            $scope.loadingMemberships = false;
            $scope.loadingOptIn = false;
            
            $scope.optErrorModalInstance = $uibModal.open({
                templateUrl: "modal/optErrorModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false,
                ariaLabelledBy: "opt-error-modal"
            });
        };

        $scope.closeOptErrorModal = () => {
            $scope.optErrorModalInstance.close();
        };

        /**
         * Add the user to the exclude group of the grouping selected. Sends back an alert saying if it failed.
         * @param {number} currentPage - the current page within the table
         * @param {number} indexClicked - the index of the grouping clicked by the user
         */
        $scope.optOut = (currentPage, indexClicked) => {
            const groupingPath = $scope.pagedItemsMemberships[currentPage][indexClicked].path;
            $scope.loadingMemberships = true;
            $scope.loadingOptIn = true;
            groupingsService.optOut(groupingPath, handleSuccessfulOpt, handleUnsuccessfulOpt);
        };

        /**
         * Add the user to the include group of the grouping selected.
         * @param {number} currentPage - the current page within the table
         * @param {number} indexClicked - the index of the grouping clicked by the user
         */
        $scope.optIn = (currentPage, indexClicked) => {
            const groupingPath = $scope.pagedItemsOptInList[currentPage][indexClicked].path;
            $scope.loadingMemberships = true;
            $scope.loadingOptIn = true;
            groupingsService.optIn(groupingPath, handleSuccessfulOpt, handleUnsuccessfulOpt);
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
})();
