(function () {

    /**
     * Owner controller for the groupings page
     *
     * @param $scope        - A Binding variable between controller and html page.
     * @param dataProvider  - service function that acts as the AJAX get.
     * @param dataUpdater   - service function that acts as AJAX post, used mainly for adding or updating
     * @param dataDeleter   - service function that acts as AJAX psst, use function mainly for delete function.
     * @constructor
     */
    function GeneralJSController($scope, $uibModal, $window, $filter, dataProvider) {

        ////This was to test out how making this a parent class works//////
        // var vm =this;
        // vm.data = {'skeet':"quick maffs"};
        //
        // vm.funnyVideo = funnyVideo;
        //
        // function funnyVideo(){
        //     return "i went viral";
        // };
        ///////////

        $scope.errorModal = function () {
            $scope.errorModalInstance = $uibModal.open({
                templateUrl: 'apiError.html',
                windowClass: 'center-modal',
                scope: $scope
            });
        };

        $scope.errorDismiss = function () {
            $scope.errorModalInstance.dismiss();
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

        $scope.groupToPages = function (list, pagedList) {
            var pagedList = [];
            if (list == null) {
                console.log("Null");
            }
            if (list != null) {
                for (var i = 0; i < list.length; i++) {
                    if (i % $scope.itemsPerPage === 0) {
                        pagedList[Math.floor(i / $scope.itemsPerPage)] = [list[i]];
                    } else {
                        pagedList[Math.floor(i / $scope.itemsPerPage)].push(list[i]);
                    }
                }
            }
            return pagedList;
        };

        $scope.range = function (size, start, end) {
            var ret = [];

            if (size < end) {
                end = size;
                // start = size - $scope.gap;
            }
            if (start < 0) {
                start = 0;
            }
            for (var i = start; i < end; i++) {
                ret.push(i);
            }
            return ret;
        };

        $scope.currentPage = function (pages, whatPage, whatList) {
            switch (pages) {
                case 'Next':
                    if ($scope[whatPage] < $scope[whatList].length - 1) {
                        $scope[whatPage] = $scope[whatPage] + 1;
                    }
                    break;
                case 'Set':
                    $scope[whatPage] = this.n;
                    break;
                case 'Prev':
                    if ($scope[whatPage] > 0) {
                        $scope[whatPage]--;
                    }
                    break;
                case 'First':
                    $scope[whatPage] = 0;
                    break;
                case 'Last':
                    if ($scope[whatPage] >= 0) {
                        $scope[whatPage] = $scope[whatList].length - 1;
                    }
                    break;
            }
        };

        /**
         * Resets the arrays containing the members of each grouping and their page numbers.
         */
        $scope.resetGroupingInformation = function () {
            // Reset grouping member data for next load
            $scope.groupingMembers = [];
            $scope.groupingBasis = [];
            $scope.groupingInclude = [];
            $scope.groupingExclude = [];
            $scope.groupingOwners = [];
            // Reset paged items
            $scope.pagedItemsMembers = [];
            $scope.pagedItemsBasis = [];
            $scope.pagedItemsInclude = [];
            $scope.pagedItemsExclude = [];
            $scope.pagedItemsOwners = [];
            // Reset page numbers
            $scope.currentPageMembers = 0;
            $scope.currentPageBasis = 0;
            $scope.currentPageInclude = 0;
            $scope.currentPageExclude = 0;
            $scope.currentPageOwners = 0;
        };

        /**
         * Resets the selected group to the list of all members.
         */
        $scope.resetSelectedGroup = function() {
            var pills = $('#group-pills')[0].children;
            var tabContents = $('#pill-content')[0].children
            for (var i = 0; i < pills.length; i++) {
                if (i === 0 && !$(pills[i]).hasClass('active')) {
                    $(pills[i]).addClass('active');
                    $(tabContents[i]).addClass('in active');
                } else if (i !== 0 && $(pills[i]).hasClass('active')) {
                    $(pills[i]).removeClass('active');
                    $(tabContents[i]).removeClass('in active');
                }
            }
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
        $scope.sortCol = function (list, col, listPaged, symbol) {
            $scope.symbol = {'name': '', 'url': '', 'uuid': '', 'username': ''};

            if ($scope[symbol] === '\u25B2' || typeof $scope[symbol] == 'undefined') {
                list = _.sortBy(list, col);
                $scope[listPaged] = $scope.groupToPages(list, $scope[listPaged]);
                $scope[symbol] = '\u25BC';
            }
            else {
                list = _.sortBy(list, col).reverse();
                $scope[listPaged] = $scope.groupToPages(list, $scope[listPaged]);
                $scope[symbol] = '\u25B2';
            }
            switch (col) {
                case 'name':
                    $scope.symbol.name = '\u21c5';
                    break;
                case 'url':
                    $scope.symbol.url = '\u21c5';
                    break;
                case 'uuid':
                    $scope.symbol.uuid = '\u21c5';
                    break;
                case 'username':
                    $scope.symbol.username = '\u21c5';
                    break;
                case 'basis':
                    $scope.symbol.basis = '\u21c5';
            }
        };

        /**gives you a true or false if it finds the match
         **@param haystack - the thing to be checked
         **@param needle - the check against
         **
         **/
        var searchMatch = function (haystack, needle) {
            if (!needle) {
                return true;
            }
            return haystack.toLowerCase().indexOf(needle.toLowerCase()) !== -1;
        };

        /**searches through the array to find matches and then fixes the list
         **@param list - gives the whole list to sort out
         **@param whatList - it gives you the list you need to search through
         **@param whatQuery - it gives the search bar its seperate search function.
         **/
        $scope.search = function (list, whatList, whatQuery) {
            var query = "";
            query = $scope[whatQuery];
            //console.log($scope[whatList]);
            $scope.filteredItems = [];
            $scope.filteredItems = $filter('filter')(list, function (item) {
                for (var key in item) {
                    if (item.hasOwnProperty(key) && key !== 'basis' && key !== '$$hashKey' && searchMatch(item[key], query)) {
                        return true;
                    }
                }
            });
            // console.log($scope.filteredItems);
            page = 0;
            // now group by pages
            var emptyList = [];
            $scope[whatList] = $scope.groupToPagesChanged(emptyList);
        };
    }

    UHGroupingsApp.controller("GeneralJSController", GeneralJSController);
})();
