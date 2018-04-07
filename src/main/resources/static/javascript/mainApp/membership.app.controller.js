(function () {

    // BIG QUESTIONS: why is there so much output for the URL?

    /**
     * Membership controller for the whole memberships page
     *
     * @param $scope - binding between controller and HTML page
     * @param dataProvider - service function that provides GET and POST requests for getting or updating data
     */
    function MembershipJsController($scope, $uibModal, $window, $controller, dataProvider) {

        $scope.currentUsername = "";
        $scope.membersList = [];
        $scope.optInList = [];
        $scope.optOutList = [];
        $scope.loading = true;

        //these will be place holders for now
        $scope.pagedItemsMembersList = [];
        $scope.pagedItemsOptInList = [];
        $scope.gap = 2;

        $scope.itemsPerPage = 20;
        $scope.currentPageOptIn = 0;
        $scope.currentPageOptOut = 0;

        angular.extend(this, $controller("TableJsController", { $scope: $scope }));

        /**init is something that is usually called at the start of something
         * so calling init would be called at the start
         **/
        $scope.init = function () {
            var groupingURL = "api/groupings/groupingAssignment";
            /**Loads Data into a membersList
             *                  optOutList
             *                  optInList
             *                  optedIn
             *                  optedOut
             *takes all of that data and puts them into pages as called by "groupToPages"
             **/
            dataProvider.loadData(function (d) {
                console.log(d);
                if (typeof d.groupingsIn === "undefined") {
                    $scope.loading = false;
                    $scope.errorModal();
                }
                else {
                    $scope.membersList = d.groupingsIn;
                    $scope.optOutList = d.groupingsToOptOutOf;
                    $scope.optInList = d.groupingsToOptInTo;

                    $scope.membersList = $scope.sortOrder($scope.membersList, "name");
                    $scope.optInList = $scope.sortOrder($scope.optInList, "name");

                    if ($scope.optInList.length === 0) {
                        $scope.optInList.push({ "name": "NO GROUPINGS TO OPT IN TO" });
                    }

                    $scope.pagedItemsMembersList = $scope.groupToPages($scope.membersList);
                    $scope.pagedItemsOptInList = $scope.groupToPages($scope.optInList);

                    $scope.loading = false;
                }
            }, function (d) {
                dataProvider.handleException({ exceptionError: d.string }, "feedback/error", "feedback");
            }, groupingURL);
        };

        $scope.errorModal = function () {
            $scope.errorModalInstance = $uibModal.open({
                templateUrl: "modal/apiError.html",
                windowClass: "center-modal",
                scope: $scope
            });
        };

        $scope.errorDismiss = function () {
            $scope.errorModalInstance.dismiss();
        };

        /**
         * Function that calls the underscore library function sortBy.
         * Standalone function in order to call fake for testing purposes.
         * @param list - The data list to which will be sorted
         * @param col - The object to name to determine how it will be sorted by.
         * @returns the list sorted.
         */
        $scope.sortOrder = function (list, col) {
            return _.sortBy(list, col);
        };

        /**
         * Adds the user to the exclude group of the grouping selected. Sends back an alert saying if it failed.
         * @param {number} index - the index of the grouping clicked by the user
         *
         */
        $scope.optOut = function (index) {
            console.log(index);
            var optOutURL = "api/groupings/" + $scope.pagedItemsMembersList[$scope.currentPageOptOut][index].path + "/optOut";
            $scope.loading = true;
            dataProvider.updateData(function (d) {
                console.log(d);
                if (d[0].resultCode.indexOf("FAILURE") > -1) {
                    console.log("Failed to opt out");
                    alert("Failed to opt out");
                    $scope.loading = false;
                } else {
                    $scope.init();
                }
            }, optOutURL);
        };

        /**
         * Adds the user to the include group of the grouping selected.
         * @param {number} index - the index of the grouping clicked by the user
         */
        $scope.optIn = function (index) {
            var optInURL = "api/groupings/" + $scope.pagedItemsOptInList[$scope.currentPageOptIn][index].path + "/optIn";
            console.log(optInURL);
            $scope.loading = true;
            dataProvider.updateData(function (d) {
                $scope.init();
            }, optInURL);
        };

        //Disables opt in button if there are no groupings to opt into.
        $scope.disableOptIn = function (index) {
            for(grouping in $scope.membersList) {
                if (grouping.name === $scope.optInList[index].name) {
                    return true;
                }
            }
        };

        //Disable button if list is empty
        $scope.disableButton = function (type, index) {
            var list = type[index];
            return list.name.indexOf("NO GROUPINGS TO") > -1;
        };

        /**
         * Function that will show opt out button if true otherwise will not show opt out button
         * @param index - table row
         * @returns {boolean} - if there is a match then return true inorder enable button.
         */
        $scope.required = function (index) {
            for (var i = 0; i < $scope.optOutList.length; i++) {
                if ($scope.pagedItemsMembersList[$scope.currentPageOptOut][index].name === $scope.optOutList[i].name) {
                    return false;
                }
            }
            return true;
        };

    }

    UHGroupingsApp.controller("MembershipJsController", MembershipJsController);

})();
