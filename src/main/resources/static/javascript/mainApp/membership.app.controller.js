(function () {
    /**
     * Membership controller for the whole memberships page
     *
     * @param $scope - binding between controller and HTML page
     * @param $window - reference to the browser's window
     * @param dataProvider - service function that provides GET and POST requests for getting or updating data
     */
    function MembershipJsController($scope, $window, $uibModal, $filter, dataProvider) {

        $scope.currentUsername = "";
        $scope.membersList = [];
        $scope.optInList = [];
        $scope.optOutList = [];
        $scope.loading = true;

        $scope.symbol = {'member': '', 'optInName': '', 'optInPath': ''};

        //these will be place holders for now
        $scope.pagedItemsMembersList = [];
        $scope.pagedItemsOptInList = [];
        $scope.gap = 2;

        $scope.itemsPerPage = 20;
        $scope.currentPageOptIn = 0;
        $scope.currentPageOptOut = 0;

        $scope.initCurrentUsername = function () {
            $scope.currentUsername = $window.document.getElementById("name").innerHTML;
        };

        $scope.getCurrentUsername = function () {
            return $scope.currentUsername;
        };

        /**init is something that is usually called at the start of something
         * so calling init would be called at the start
         **/
        $scope.init = function () {
            $scope.initCurrentUsername();
            var groupingURL = "api/groupings/groupingAssignment";
            /**Loads Data into a membersList
             *                  optOutList
             *                  optInList
             *                  optedIn
             *                  optedOut
             *takes all of that data and puts them into pages as called by "groupToPages"
             **/
            dataProvider.loadData(function (d) {
                if(typeof d.groupingsIn === 'undefined') {
                    $scope.loading = false;
                    $scope.errorModal();
                }
                else{
                    $scope.membersList = d.groupingsIn;
                    $scope.optOutList = d.groupingsToOptOutOf;
                    $scope.optInList = d.groupingsToOptInTo;

                    $scope.membersList = $scope.sortOrder($scope.membersList, 'name');
                    $scope.optInList = $scope.sortOrder($scope.optInList, 'name');

                    //Sorts tables by name
                    $scope.symbol.member = '\u21c5';
                    $scope.symbolList = '\u25BC';
                    $scope.symbol.optInName = '\u21c5';
                    $scope.symbolOptIn = '\u25BC';

                    if ($scope.optInList.length === 0) {
                        $scope.optInList.push({'name': "NO GROUPINGS TO OPT IN TO"});
                    }

                    $scope.pagedItemsMembersList = $scope.groupToPages($scope.membersList, $scope.pagedItemsMembersList);
                    $scope.pagedItemsOptInList = $scope.groupToPages($scope.optInList, $scope.pagedItemsOptInList);

                    $scope.loading = false;
                }
            }, groupingURL);
        };

        $scope.errorModal = function () {
            $scope.errorModalInstance = $uibModal.open({
                templateUrl: 'modal/apiError.html',
                windowClass: 'center-modal',
                scope: $scope
            });
        };

        $scope.errorDismiss = function() {
            $scope.errorModalInstance.dismiss();
        };

        /**
         *  Sorts the data in the table in ascending or descending order based on
         *  the list and column being sorted.
         *
         * @param list - The data list to which will be sorted
         * @param col - The object to name to determine how it will be sorted by.
         * @param listPaged - The paged data list to which the sorted list will go into.
         * @param symbol - The symbol to tell user if they are sorting in ascending or descending order.
         */
        $scope.sort = function (list, col, listPaged, symbol) {
            $scope.symbol = {'member': '', 'optInName': '', 'optInPath': ''};
            if ($scope[symbol] === '\u25B2' || typeof $scope[symbol] == 'undefined') {
                list = $scope.sortOrder(list, col);
                $scope[listPaged] = $scope.groupToPages(list, $scope[listPaged]);
                $scope[symbol] = '\u25BC';
            }
            else {
                list = $scope.sortOrder(list, col).reverse();
                $scope[listPaged] = $scope.groupToPages(list, $scope[listPaged]);
                $scope[symbol] = '\u25B2';
            }
            switch (listPaged) {
                case 'pagedItemsMembersList' :
                    $scope.symbol.member = '\u21c5';
                    break;
                case 'pagedItemsOptInList' :
                    if(col == "name") $scope.symbol.optInName = '\u21c5';
                    else $scope.symbol.optInPath = '\u21c5';
                    break;
            }
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
            var optOutURL = "api/groupings/" + $scope.pagedItemsMembersList[$scope.currentPageOptOut][index].path + "/optOut";
            $scope.loading = true;
            dataProvider.updateData(function (d) {
                if (d[0].resultCode.indexOf("FAILURE") > -1) {
                    alert("Failed to opt out");
                    $scope.loading = false;
                }
                else {
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
            $scope.loading = true;
            dataProvider.updateData(function (d) {
                $scope.init();
            }, optInURL);
        };

        /**searches through the array to find matches and then fixes the list
         **@param list - gives the whole list to sort out
         **@param whatList - it gives you the list you need to search through
         **@param whatQuery - it gives the search bar its seperate search function.
         **/
        $scope.search = function (list, whatList, whatQuery) {
            var query = "";
            query = $scope[whatQuery];
            $scope.filteredItems = [];
            $scope.filteredItems = $filter('filter')(list, function (item) {
                // Filter by path name for groups in Available Groupings tab
                if (list === $scope.optInList) {
                    if (searchMatch(item.path, query)) {
                        return true;
                    }
                } else {
                    // Otherwise filter by name of grouping for My Grouping Memberships tab
                    if (searchMatch(item.name, query)) {
                        return true;
                    }
                }
            });
            page = 0;
            // now group by pages
            var emptyList = [];
            $scope[whatList] = $scope.groupToPagesChanged(emptyList);
        };


        $scope.groupToPagesChanged = function (pagedList) {
            var pagedList = [];
            for (var i = 0; i < $scope.filteredItems.length; i++) {
                if (i % $scope.itemsPerPage === 0) {
                    pagedList[Math.floor(i / $scope.itemsPerPage)] = [$scope.filteredItems[i]];
                } else {
                    pagedList[Math.floor(i / $scope.itemsPerPage)].push($scope.filteredItems[i]);
                }
            }
            return pagedList;
        };
        //Disables opt in button if there are no groupings to opt into.
        $scope.disableOptIn = function (index) {
            for (var i = 0; i < $scope.membersList.length; i++) {
                if ($scope.membersList[i].name === $scope.optInList[index].name) {
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

        $scope.required = function(index)
        {
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
