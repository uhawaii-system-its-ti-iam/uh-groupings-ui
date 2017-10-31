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
    function OwnerJsController($scope, $uibModal, $window, $filter, dataProvider, dataUpdater, dataDeleter) {
        $scope.currentUsername = "";
        $scope.initCurrentUsername = function () {
            $scope.currentUsername = $window.document.getElementById("name").innerHTML;
        };

        $scope.getCurrentUsername = function () {
            return $scope.currentUsername;
        };

        var groupingsOwned;
        var getUrl;

        $scope.ownedList = [];
        $scope.groupingsList = [];
        $scope.groupingsBasis = [];
        $scope.groupingInclude = [];
        $scope.groupingExclude = [];
        $scope.ownerList = [];
        $scope.pref = [];
        $scope.allowOptIn = [];
        $scope.allowOptOut = [];
        $scope.showGrouping = false;
        $scope.loading = true;
        $scope.groupingName = '';

        /*
         *pagination variables
         */
        $scope.gap = 2;
        $scope.itemsPerPage = 25;
        //figure out how much pages to paginate. so far lets do one
        $scope.pagedItemsOwned = [];
        $scope.currentPageOwned = 0;

        $scope.pagedItemsInclude = [];
        $scope.currentPageInclude = 0;

        $scope.pagedItemsGroupings = [];
        $scope.currentPageGroupings = 0;

        $scope.pagedItemsBasis = [];
        $scope.currentPageBasis = 0;

        $scope.pagedItemsExclude = [];
        $scope.currentPageExclude = 0;

        $scope.pagedItemsOwners = [];
        $scope.currentPageOwners = 0;

        $scope.pagedItemsList = [];
        $scope.currentPageList = 0;

        /**
         * Initialize function that retrieves the groupings you own.
         */
        $scope.init = function () {
            $scope.initCurrentUsername();

            groupingsOwned = "api/groupings/" + $scope.getCurrentUsername() + "/groupingAssignment";
            dataProvider.loadData(function (d) {
                var temp = [];
                console.log(d);
                //Assigns grouping name, folder directories and url used for api call.
                for (var i = 0; i < d.groupingsOwned.length; i++) {
                    temp[i] = d.groupingsOwned[i].path.split(':');
                    var folder = '';
                    for (var j = 0; j < temp[i].length - 1; j++) {
                        folder += temp[i][j];
                        if (j != temp[i].length - 1) {
                            folder += "/";
                        }
                    }
                    $scope.ownedList.push({
                        'name': d.groupingsOwned[i].name,
                        'folder': folder,
                        'url': d.groupingsOwned[i].path
                    });
                }
                $scope.pagedItemsOwned = $scope.groupToPages($scope.ownedList, $scope.pagedItemsOwned);
                $scope.loading = false;
            }, groupingsOwned);
        };

        /**
         * Switches from showing that data of the grouping you own to the information about the grouping selected.
         *
         * @param row - row of the grouping with relation to the table.
         */
        $scope.showData = function (row) {
            $scope.groupingName = $scope.ownedList[row];
            //URLS being used in the api calls.
            if ($scope.showGrouping == false) {
                $scope.showGrouping = true;
                $scope.getData();
            }
            else {
                $scope.showGrouping = false;
            }
        };

        /**
         *  Retrieves the information about grouping selected.
         *  Assigns basis group, basisPlusIncludeMinusExclude group, include group, exclude group,
         *  owners list and grouping privileges.
         */
        $scope.getData = function () {
            getUrl = "api/groupings/" + $scope.groupingName.url + "/" + $scope.getCurrentUsername() + "/grouping";
            $scope.loading = true;
            dataProvider.loadData(function (d) {
                console.log(d);
                $scope.basis = d.basis.members;

                //Gets members in grouping
                $scope.groupingsList = d.composite.members;
                $scope.modify($scope.groupingsList);
                $scope.pagedItemsList = $scope.groupToPages($scope.groupingsList, $scope.pagedItemsList);

                //Gets members in the basis group
                $scope.groupingsBasis = d.basis.members;
                $scope.modify($scope.groupingsBasis);
                $scope.pagedItemsBasis = $scope.groupToPages($scope.groupingsBasis, $scope.pagedItemsBasis);

                //Gets members in the include group
                $scope.groupingInclude = d.include.members;
                $scope.modify($scope.groupingInclude);
                $scope.pagedItemsInclude = $scope.groupToPages($scope.groupingInclude, $scope.pagedItemsInclude);

                //Gets members in the exclude group
                $scope.groupingExclude = d.exclude.members;
                $scope.modify($scope.groupingExclude);
                $scope.pagedItemsExclude = $scope.groupToPages($scope.groupingExclude, $scope.pagedItemsExclude);

                //Gets owners of the grouping
                $scope.ownerList = d.owners.members;
                $scope.modify($scope.ownerList);
                $scope.pagedItemsOwners = $scope.groupToPages($scope.ownerList, $scope.pagedItemsOwners);

                $scope.preference = {
                    optIn: d.optInOn,
                    optOut: d.optOutOn,
                    listserv: d.listservOn
                };


                //Stop loading spinner
                $scope.loading = false;
            }, getUrl);
        };

        /**
         * Modify the data from the grouping to be sorted, filter out hawaii.edu
         * and determines if a user is in the basis group or not.
         *
         * @param grouping - The name of the grouping of which its data will be modified.
         *
         * @returns returns
         *                1 for ascending
         *                -1 for descending
         *                0 for failed attempt
         */
        $scope.modify = function (grouping) {
            //Filter out names with hawaii.edu and adds basis object.
            for (var i = 0; i < grouping.length; i++) {
                grouping[i].basis = "No";
                if (grouping[i].name.includes("hawaii.edu")) {
                    grouping.splice(i, 1);
                    i--;
                }
            }

            //Determines if member is in the basis or not
            for (var l = 0; l < $scope.basis.length; l++) {
                for (var m = 0; m < grouping.length; m++) {
                    if ($scope.basis[l].uuid === grouping[m].uuid) {
                        grouping[m].basis = "Yes";
                    }
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
            $scope.symbol = {'name': '', 'folder': '', 'uuid': '', 'username': ''};

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
                case 'folder':
                    $scope.symbol.folder = '\u21c5';
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

        /**
         *  Function that switches from the view of a single grouping
         *  to the list of groupings that you own.
         */
        $scope.showGroups = function () {
            if ($scope.showGrouping == false) {
                $scope.showGrouping = true;
            }
            else {
                $scope.showGrouping = false;
                $scope.groupingName = '';
                $scope.groupingsList = [];
                $scope.groupingsBasis = [];
                $scope.groupingInclude = [];
                $scope.groupingExclude = [];
                $scope.ownerList = [];
            }
        };

        // TODO: Make add and remove for members and owners into a more singular function for add and remove.
        /**
         * Adds member to the include or exclude group.
         * If user is successful in adding, then alerts success.
         * Otherwise alert that the user does not exist
         *
         * @param type - the type of group that the user is being added into. Include or exclude.
         */
        $scope.addMember = function (type) {
            var addUrl = "api/groupings/" + $scope.groupingName.url + "/" + $scope.getCurrentUsername() + "/" + $scope.addUser + "/addMemberTo" + type + "Group";
            dataUpdater.addData(function (d) {
                if (d.resultCode === "SUCCESS") {
                    $scope.addModalAlert('success');
                }
                else if (typeof d.resultsCode === 'undefined') {
                    console.log($scope.addUser + " this user does not exist.");
                    $scope.addModalAlert();
                }
            }, addUrl);
            $scope.addUser = '';
        };

        /**
         * Gives a user ownership of a grouping.
         * If the user is successfully assigned ownership, then alerts success.
         * Otherwise alerts that the user does not exist.
         */
        $scope.addOwner = function () {
            var addOwnerUrl = "api/groupings/" + $scope.groupingName.url + "/" + $scope.getCurrentUsername() + "/" + $scope.ownerUser + "/assignOwnership";
            dataUpdater.addData(function (d) {
                if (d.resultCode === "SUCCESS") {
                    console.log("Assigned " + $scope.ownerUser + " as an owner");
                    $scope.addModalAlert('success');
                }
                else if (typeof d.resultsCode === 'undefined') {
                    console.log($scope.ownerUser + " this user does not exist.");
                    $scope.addModalAlert();
                }
            }, addOwnerUrl);
            $scope.ownerUser = '';
        };

        /**
         *
         * @param user - user being added
         * @param success - whether if the addData service returned a success in adding.
         */
        $scope.addModalAlert = function (success) {
            if (success === 'success') var message = "User has been added";
            else var message = "Error: User is not a valid username";

            var modalHtml = '<div class="modal-body">' + message + '</div>';
            modalHtml += '<div class="modal-footer"><button class="btn btn-primary" ng-click="continue()">OK</button></div>';

            $scope.addModalInstance = $uibModal.open({
                template: modalHtml,
                scope: $scope
            });

            $scope.addModalInstance.result.then(function () {
                $scope.loading = true;
                if (success === 'success') $scope.getData();
            });
        };

        /**
         * Function that closes modal and proceeds with the modal result.
         */
        $scope.continue = function () {
            $scope.addModalInstance.close();
        };

        /**
         * Removes member from the include group or exclude groups. Completely removes them from the group.
         *
         * @param type - type of group that the user is being added into. Include or exclude.
         * @param row  - index of the user in the respected group array.
         */
        $scope.removeMember = function (type, row) {
            var user;
            if (type === 'Include') {
                user = $scope.groupingInclude[row].username;
            }
            if (type === 'Exclude') {
                user = $scope.groupingExclude[row].username;
            }

            var URL = "api/groupings/" + $scope.groupingName.url + "/" + $scope.getCurrentUsername() + "/" + user + "/deleteMemberFrom" + type + "Group";

            $scope.deleteModal(user, URL, null, $scope.groupingPath);
        };

        /**
         * Removes ownership of a grouping from an user
         *
         * @param index - The index of the member in the ownerList array.
         */
        $scope.removeOwner = function (index) {
            var removeOwner = $scope.ownerList[index].username;
            var removeOwnerUrl = "api/groupings/" + $scope.groupingName.url + "/" + $scope.getCurrentUsername() + "/" + removeOwner + "/removeOwnership";
            if ($scope.ownerList.length > 1) {
                $scope.deleteModal(removeOwner, removeOwnerUrl, null, $scope.groupingPath);
            }
        };

        $scope.deleteModal = function (user, url, location, type) {
            var message = "Are you sure you want to delete " + user;
            var modalHtml = '<div class="modal-body">' + message + '</div>';
            modalHtml += '<div class="modal-footer"><button class="btn btn-primary" ng-click="ok()">OK</button><button class="btn btn-warning" ng-click="cancel()" data-dismiss="modal">Cancel</button></div>';

            $scope.deleteModalInstance = $uibModal.open({
                template: modalHtml,
                scope: $scope
            });

            $scope.deleteModalInstance.result.then(function () {
                if (type === 'admin' && $scope.list.length > 1) {
                    dataDeleter.deleteData(function (d) {
                        $scope.list.splice(location, 1);
                        $scope.init();
                    }, url);
                }
                else {
                    dataDeleter.deleteData(function (d) {
                        console.log(d);
                        $scope.loading = true;
                        $scope.getData(type);
                    }, url);
                }
            });
        };

        /**
         * Function that closes modal and proceeds with the modal result.
         */
        $scope.ok = function () {
            $scope.deleteModalInstance.close();
        };

        /**
         * Function that closes modal.
         */
        $scope.cancel = function () {
            $scope.deleteModalInstance.dismiss();
        };

        /**
         * Saves changes made to grouping privileges
         */
        $scope.savePref = function () {
            var prefUrls = [];

            prefUrls.push({
                "url": "api/groupings/" + $scope.groupingName.url + "/" + $scope.getCurrentUsername() + "/" + $scope.preference.listserv + "/setListserv",
                "name": "Listserv"
            });
            prefUrls.push({
                "url": "api/groupings/" + $scope.groupingName.url + "/" + $scope.getCurrentUsername() + "/" + $scope.preference.optIn + "/setOptIn",
                "name": "optInOption"
            });
            prefUrls.push({
                "url": "api/groupings/" + $scope.groupingName.url + "/" + $scope.getCurrentUsername() + "/" + $scope.preference.optOut + "/setOptOut",
                "name": "optOutOption"
            });

            for (var i = 0; i < prefUrls.length; i++) {
                dataUpdater.addData(function (d) {
                    var success = 0;
                    console.log(d);
                    if (d.resultCode === "SUCCESS") {
                        console.log("LISTSERV preference successfully updated");
                        alert("LISTSERV preference successfully updated");
                        success = 1;
                    }
                    else if (typeof d.resultsCode === 'undefined') {
                        if (typeof d[0] != 'undefined' && (d[0].resultCode === "SUCCESS_ALLOWED" || d[0].resultCode === "SUCCESS_NOT_ALLOWED" )) {
                            console.log("OptIn/OptOut preference successfully updated");
                            alert("OptIn/OptOut preference successfully updated");
                            success = 1;
                        }
                        else {
                            console.log("Preference did not change");
                            alert("Preference did not change");
                        }
                    }
                    if (success == 1) {
                        $scope.getData($scope.groupingPath);
                    }
                }, prefUrls[i].url);
            }
        };

        $scope.infoModal = function (preference, group) {
            if(preference === 'opt')
                var modalHtml = '<div class="text-center modal-body">This option allows owners to set whether or not members can ' + group + ' themselves from the grouping</div>';
            else if(preference === 'publication')
                var modalHtml = '<div class="text-center modal-body">This option allows owners to set whether or not the publication destination is active or not</div>';

            $scope.deleteModalInstance = $uibModal.open({
                template: modalHtml,
                windowClass: 'center-modal',
                scope: $scope
            });
        };

        /**
         * Export data in table to a CSV file
         *
         * @param type - type of group being exported
         * @param name - name of the group. i.e. include or exclude
         */
        $scope.export = function (type, name) {
            var data, filename, link;

            var csv = $scope.convertArrayOfObjectsToCSV(type);
            if (csv == null) return;

            filename = name + '_export.csv';

            if (!csv.match(/^data:text\/csv/i)) {
                csv = 'data:text/csv;charset=utf-8,' + csv;
            }
            data = encodeURI(csv);

            link = document.createElement('a');
            link.setAttribute('href', data);
            link.setAttribute('download', filename);
            link.click();
        };

        /**
         * Converts the data in the table into data that is usable for a csv file.
         *
         * @param type - type of group to retrieve data.
         * @returns a string of converted array to be usable for the csv file.
         */
        $scope.convertArrayOfObjectsToCSV = function (type) {
            var str = "Name, Username, Email \r\n";

            for (var i = 0; i < type.length; i++) {
                var line = '';
                //for (var index in type[i]) {
                if (line != '')
                    line += ',';
                line += type[i].name + ', ' + type[i].username + ', ' + type[i].username + "@hawaii.edu,";
                //}
                str += line + '\r\n';
            }
            return str;
        };
        //Pagination code


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
            console.log(query);
            //console.log($scope[whatList]);
            $scope.filteredItems = [];
            $scope.filteredItems = $filter('filter')(list, function (item) {
                if (searchMatch(item.name, query)) {
                    return true;
                }
            });
            // console.log($scope.filteredItems);
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


        /**groups all the items to pages
         have separate arrays
         **/
        $scope.groupToPages = function (theList, pagedList) {
            var pagedList = [];
            if (theList == null) {
                console.log("I AM NULL ... WHY?!");
            }
            if (theList != null) {
                for (var i = 0; i < theList.length; i++) {
                    if (i % $scope.itemsPerPage === 0) {
                        pagedList[Math.floor(i / $scope.itemsPerPage)] = [theList[i]];
                    } else {
                        pagedList[Math.floor(i / $scope.itemsPerPage)].push(theList[i]);
                    }
                }
            }
            return pagedList;
        };

        /**shows the range between the start and end
         *checks for negative numbers
         *
         * @param size
         * @param start
         * @param end
         *  all the param are self explanitory
         * @return ret
         *     everything within the range of start,
         *       end, and making sure it's that size
         **/
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

    }

    ownerApp.controller("OwnerJsController", OwnerJsController);
})();
