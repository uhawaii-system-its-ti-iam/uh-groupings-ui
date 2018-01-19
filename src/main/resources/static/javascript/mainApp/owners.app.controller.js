(function () {

    /**
     * Owner controller for the groupings page
     *
     * @param $scope - binding between controller and HTML page
     * @param dataProvider  - service function that provides GET and POST requests for getting or updating data
     */
    function OwnerJsController($scope, $uibModal, $window, $filter, dataProvider) {

        var groupingsOwned;
        var getUrl;

        $scope.groupingsList = [];
        $scope.groupingsBasis = [];
        $scope.groupingInclude = [];
        $scope.groupingExclude = [];
        $scope.groupingOwners = [];
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
        $scope.itemsPerPage = 20;
        //figure out how much pages to paginate. so far lets do one
        $scope.pagedItemsGroupings = [];
        $scope.currentPageGroupings = 0;

        $scope.pagedItemsInclude = [];
        $scope.currentPageInclude = 0;

        $scope.pagedItemsBasis = [];
        $scope.currentPageBasis = 0;

        $scope.pagedItemsExclude = [];
        $scope.currentPageExclude = 0;

        $scope.pagedItemsOwners = [];
        $scope.currentPageOwners = 0;

        $scope.pagedItemsMembers = [];
        $scope.currentPageMembers = 0;

        /**
         * Initialize function that retrieves the groupings you own.
         */
        $scope.init = function () {

            groupingsOwned = "api/groupings/groupingAssignment";
            dataProvider.loadData(function (d) {
                var temp = [];
                console.log(d);
                if (typeof d.groupingsIn === 'undefined') {
                    $scope.createApiErrorModal();
                } else {
                    // Assigns grouping name and url used for api call.
                    for (var i = 0; i < d.groupingsOwned.length; i++) {
                        $scope.groupingsList.push({
                            'name': d.groupingsOwned[i].name,
                            'path': d.groupingsOwned[i].path
                        });
                    }
                    $scope.pagedItemsGroupings = $scope.groupToPages($scope.groupingsList, $scope.pagedItemsGroupings);
                }
                $scope.loading = false;
            }, groupingsOwned);
        };

        /**
         * Creates a modal for errors in loading data from the API.
         */
        $scope.createApiErrorModal = function () {
            $scope.apiErrorModalInstance = $uibModal.open({
                templateUrl: 'modal/apiError.html',
                scope: $scope
            });
        };

        /**
         * Closes the API error modal.
         */
        $scope.closeApiError = function () {
            $scope.apiErrorModalInstance.close();
        };

        /**
         * Gets information about the grouping clicked by the user.
         * @param {number} index - the index of the grouping selected, with the current page and items per page taken
         * into account
         */
        $scope.showData = function (index) {
            $scope.selectedGrouping = $scope.groupingsList[index];
            if (!$scope.showGrouping) {
                $scope.showGrouping = true;
                $scope.getData();
            } else {
                $scope.showGrouping = false;
            }
        };

        /**
         *  Retrieves the information about grouping selected.
         *  Assigns basis group, basisPlusIncludeMinusExclude group, include group, exclude group,
         *  owners list and grouping privileges.
         */
        $scope.getData = function () {
            getUrl = "api/groupings/" + $scope.selectedGrouping.path + "/grouping";
            $scope.loading = true;
            dataProvider.loadData(function (d) {
                console.log(d);
                $scope.basis = d.basis.members;

                //Gets members in grouping
                $scope.groupingMembers = d.composite.members;
                $scope.modify($scope.groupingMembers, 'members');
                $scope.pagedItemsMembers = $scope.groupToPages($scope.groupingMembers, $scope.pagedItemsMembers);

                //Gets members in the basis group
                $scope.groupingBasis = d.basis.members;
                $scope.modify($scope.groupingBasis);
                $scope.pagedItemsBasis = $scope.groupToPages($scope.groupingBasis, $scope.pagedItemsBasis);

                //Gets members in the include group
                $scope.groupingInclude = d.include.members;
                $scope.modify($scope.groupingInclude);
                $scope.pagedItemsInclude = $scope.groupToPages($scope.groupingInclude, $scope.pagedItemsInclude);

                //Gets members in the exclude group
                $scope.groupingExclude = d.exclude.members;
                $scope.modify($scope.groupingExclude);
                $scope.pagedItemsExclude = $scope.groupToPages($scope.groupingExclude, $scope.pagedItemsExclude);

                //Gets owners of the grouping
                $scope.groupingOwners = d.owners.members;
                $scope.modify($scope.groupingOwners);
                $scope.pagedItemsOwners = $scope.groupToPages($scope.groupingOwners, $scope.pagedItemsOwners);

                $scope.allowOptIn = d.optInOn;
                $scope.allowOptOut = d.optOutOn;
                $scope.listserv = d.listservOn;

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
        $scope.modify = function (grouping, list) {
            //Filter out names with hawaii.edu and adds basis object.
            for (var i = 0; i < grouping.length; i++) {
                if (list === 'members') grouping[i].basis = "in Include";
                else grouping[i].basis = "No";

                if (grouping[i].name.indexOf("hawaii.edu") > -1) {
                    grouping.splice(i, 1);
                    i--;
                }
            }

            //Determines if member is in the basis or not
            for (var l = 0; l < $scope.basis.length; l++) {
                for (var m = 0; m < grouping.length; m++) {
                    if ($scope.basis[l].uuid === grouping[m].uuid) {
                        if (list === 'members') {
                            grouping[m].basis = "in Basis";
                            for (var k = 0; k <  $scope.groupingInclude.length;k++) {
                                if ($scope.groupingInclude[k].uuid === grouping[m].uuid){
                                    grouping[m].basis = "in Basis / in Include";
                                }
                            }
                        }
                        else grouping[m].basis = "Yes";
                    }
                }
            }

            grouping.sort(function (a, b) {
                var nameA = a.name.toLowerCase(), nameB = b.name.toLowerCase();
                if (nameA < nameB) //sort string ascending
                    return -1;
                if (nameA > nameB)
                    return 1;
                return 0
            });

            $scope.replaceBlankUsernames(grouping);
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
            } else {
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

        /**
         *  Function that switches from the view of a single grouping
         *  to the list of groupings that you own.
         */
        $scope.showGroups = function () {
            if ($scope.showGrouping == false) {
                $scope.showGrouping = true;
            } else {
                $scope.showGrouping = false;
                $scope.resetGroupingInformation();
                $scope.resetSelectedGroup();
            }
        };

        // TODO: Make add and remove for members and owners into a more singular function for add and remove.

        /**
         * Adds a user to a group.
         * @param {string} type - the type of group to add the user to (either Include or Exclude)
         */
        $scope.addMember = function (type) {
            var addUrl = "api/groupings/" + $scope.selectedGrouping.path + "/" + $scope.addUser + "/addMemberTo" + type + "Group";
            dataProvider.updateData(function (d) {
                var successful = false;
                if (d.statusCode != null) {
                    console.log("Error, Status Code: " + d.statusCode);
                } else if (d.resultCode === "SUCCESS") {
                    successful = true;
                }
                $scope.createAddModal($scope.addUser, successful, $scope.selectedGrouping.path);
                $scope.addUser = '';
            }, addUrl);
        };

        /**
         * Gives a user ownership of a grouping.
         */
        $scope.addOwner = function () {
            var addOwnerUrl = "api/groupings/" + $scope.selectedGrouping.path + "/" + $scope.ownerUser + "/assignOwnership";
            dataProvider.updateData(function (d) {
                var successful = false;
                if (d.statusCode != null) {
                    console.log("Error, Status Code: " + d.statusCode);
                } else if (d.resultCode === "SUCCESS") {
                    successful = true;
                    console.log("Assigned " + $scope.ownerUser + " as an owner");
                }
                $scope.createAddModal($scope.ownerUser, successful, $scope.selectedGrouping.path);
                $scope.ownerUser = '';
            }, addOwnerUrl);
        };

        /**
         * Creates a modal telling the user whether or not the user was successfully added into the grouping/admin list.
         * @param {string} user - the user being added
         * @param {boolean} wasSuccessful - whether or not the user was successfully added
         * @param {string} path - the path to the grouping
         */
        $scope.createAddModal = function (user, wasSuccessful, path) {
            $scope.user = user;
            $scope.wasSuccessful = wasSuccessful;
            console.log(wasSuccessful);

            $scope.addModalInstance = $uibModal.open({
                templateUrl: 'modal/addModal.html',
                scope: $scope,
            });

            $scope.addModalInstance.result.finally(function() {
                if (wasSuccessful) {
                    $scope.loading = true;
                    $scope.getData(path);
                }
            });
        };

        /**
         * Closes the add user modal.
         */
        $scope.closeAddModal = function () {
            $scope.addModalInstance.close();
        };

        /**
         * Removes a user from the include or exclude group.
         * @param {string} type - the type of group the user will be removed from (either Include or Exclude)
         * @param {number} index - the index of the user to delete, with the current page and items per page taken into
         * account
         */
        $scope.removeMember = function (type, index) {
            var user;
            if (type === 'Include') {
                user = $scope.groupingInclude[index].username;
            } else if (type === 'Exclude') {
                user = $scope.groupingExclude[index].username;
            }
            var url = "api/groupings/" + $scope.selectedGrouping.path + "/" + user + "/deleteMemberFrom" + type + "Group";
            $scope.createRemoveModal(user, url, $scope.selectedGrouping.path);
        };

        /**
         * Removes a grouping owner. There must be at least one grouping owner remaining.
         * @param {number} index - the index of the owner to delete, with the current page and items per page taken into
         * account
         */
        $scope.removeOwner = function (index) {
            var removeOwner = $scope.groupingOwners[index].username;
            var removeOwnerUrl = "api/groupings/" + $scope.selectedGrouping.path + "/" + removeOwner + "/removeOwnership";
            if ($scope.groupingOwners.length > 1) {
                $scope.createRemoveModal(removeOwner, removeOwnerUrl, $scope.selectedGrouping.path);
            }
        };

        /**
         * Creates a modal that prompts the user whether they want to delete the user or not. If 'Yes' is pressed, then
         * a request is made to delete the user.
         * @param {string} user - the user to delete
         * @param {string} url - the URL used to make the request
         * @param {string} path - the path to the grouping
         */
        $scope.createRemoveModal = function (user, url, path) {
            $scope.userToDelete = user;
            $scope.removeModalInstance = $uibModal.open({
                templateUrl: 'modal/removeModal.html',
                scope: $scope
            });

            $scope.removeModalInstance.result.then(function () {
                $scope.loading = true;
                // Remove the user, then reload the grouping
                dataProvider.updateData(function () {
                    $scope.getData(path);
                }, url);
            });
        };

        /**
         * Closes the modal, then proceeds with deleting a user from a grouping.
         */
        $scope.proceedRemoveUser = function () {
            $scope.removeModalInstance.close();
        };

        /**
         * Closes the modal for deleting a user. This does not delete the user from the grouping.
         */
        $scope.cancelRemoveUser = function () {
            $scope.removeModalInstance.dismiss();
        };

        /**
         * Toggles the grouping preference which allows users to opt out of a grouping.
         */
        $scope.updateAllowOptOut = function () {
            var url = "api/groupings/" + $scope.selectedGrouping.path + "/" + $scope.allowOptOut + "/setOptOut";
            dataProvider.updateData(function (d) {
                if (d.statusCode != null) {
                    console.log("Error, Status Code: " + d.statusCode);
                    $scope.createPreferenceErrorModal();
                } else if (d[0].resultCode === "SUCCESS_ALLOWED" || d[0].resultCode === "SUCCESS_NOT_ALLOWED") {
                    console.log("success");
                }
            }, url);
        };

        /**
         * Toggles the grouping preference which allows users to discover the grouping and opt into it.
         */
        $scope.updateAllowOptIn = function () {
            var url = "api/groupings/" + $scope.selectedGrouping.path + "/" + $scope.allowOptIn + "/setOptIn";
            dataProvider.updateData(function (d) {
                if (d.statusCode != null) {
                    console.log("Error, Status Code: " + d.statusCode);
                    $scope.createPreferenceErrorModal();
                } else if (d[0].resultCode === "SUCCESS_ALLOWED" || d[0].resultCode === "SUCCESS_NOT_ALLOWED") {
                    console.log("success");
                }
            }, url);
        };

        /**
         * Toggles the grouping preference which creates a LISTSERV email list based off the grouping.
         */
        $scope.updateListserv = function () {
            var url = "api/groupings/" + $scope.selectedGrouping.path + "/" + $scope.listserv + "/setListserv";
            dataProvider.updateData(function (d) {
                console.log(d);
                if (d.statusCode != null) {
                    console.log("Error, Status Code: " + d.statusCode);
                    $scope.createPreferenceErrorModal();
                } else if (d.resultCode === "SUCCESS") {
                    console.log("success");
                }
            }, url);
        };

        $scope.checkLDAP = function () {
            console.log($scope.LDAP);
        };

        /**
         * Creates a modal indicating an error in saving the grouping's preferences.
         */
        $scope.createPreferenceErrorModal = function () {
            $scope.preferenceErrorModalInstance = $uibModal.open({
                templateUrl: 'modal/preferenceErrorModal.html',
                scope: $scope
            });
        };

        /**
         * Closes the preference error modal.
         */
        $scope.closePreferenceError = function () {
            $scope.preferenceErrorModalInstance.close();
        };

        /**
         * Creates a modal with a description of the preference selected.
         * @param {string} desc - the description of the preference
         */
        $scope.createPreferenceInfoModal = function (desc) {
            $scope.preferenceInfo = desc;

            $scope.infoModalInstance = $uibModal.open({
                templateUrl: 'modal/infoModal.html',
                scope: $scope,
            });
        };

        /**
         * Closes the preference information modal.
         */
        $scope.closePreferenceInfo = function () {
            $scope.infoModalInstance.close();
        };

        /**
         * Exports data in a table to a CSV file
         * @param {object[]} table - the table to export
         * @param name - the name of the group (i.e. include or exclude)
         */
        $scope.export = function (table, name) {
            var data, filename, link;

            var csv = $scope.convertArrayOfObjectsToCSV(table);
            if (csv == null) return;

            filename = name + '_export.csv';

            if (!csv.match(/^data:text\/csv/i)) {
                csv = 'data:text/csv;charset=utf-8,' + csv;
            }
            data = encodeURI(csv);

            link = document.createElement('a');
            link.setAttribute('href', data);
            link.setAttribute('download', filename);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        };

        /**
         * Converts the data in the table into comma-separated values.
         * @param {object[]} table - the table to convert
         * @returns the table in CSV format
         */
        $scope.convertArrayOfObjectsToCSV = function (table) {
            var str = "Name, Username, Email \r\n";
            for (var i = 0; i < table.length; i++) {
                var line = '';
                if (line != '')
                    line += ',';
                line += table[i].name + ', ' + table[i].username + ', ' + table[i].username + "@hawaii.edu,";
                str += line + '\r\n';
            }
            return str;
        };

        /**
         * Checks if a string contains a substring (case insensitive).
         * @param {string} str - the string to check
         * @param {string} substr - the substring to find
         * @returns {boolean} true if the string contains the substring. Otherwise returns false.
         */
        var searchMatch = function (str, substr) {
            if (!substr) return true;
            return str.toLowerCase().indexOf(substr.toLowerCase()) !== -1;
        };

        /**
         * Filters through a list given a user's query.
         * @param {object[]} list - the list to filter
         * @param {string} pagedListVar - the name of the variable containing the paginated list
         * @param {string} pageVar - the name of the variable containing the current page of the list
         * @param {string} queryVar - the name of the variable containing the user's query
         */
        $scope.filter = function (list, pagedListVar, pageVar, queryVar) {
            var query = $scope[queryVar];
            // Filters for items that match the user's query
            var filteredItems = $filter('filter')(list, function (item) {
                for (var key in item) {
                    // Ignore the 'basis' and '$$hashKey' properties, as well as non-string items
                    if (item.hasOwnProperty(key) && key !== 'basis' && key !== '$$hashKey' && typeof(item[key]) === 'string') {
                        if (searchMatch(item[key], query)) return true;
                    }
                }
            });
            // Resets the page number
            $scope[pageVar] = 0;
            // Paginates the filtered items
            $scope[pagedListVar] = $scope.groupToPages(filteredItems, []);
        };

        /**
         * Paginates a list of items.
         * @param {object[]} list - the unpaginated list
         * @param {object[]} pagedList - the paginated list
         * @returns {object[]} list (the first parameter), paginated
         */
        $scope.groupToPages = function (list, pagedList) {
            var pagedList = [];
            if (list == null) {
                console.log("I AM NULL ... WHY?!");
            } else {
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
        $scope.resetSelectedGroup = function () {
            var pills = $('#group-pills')[0].children;
            var contents = $('#pill-content')[0].children
            for (var i = 0; i < pills.length && i < contents.length; i++) {
                if (i === 0 && !$(pills[i]).hasClass('active')) {
                    $(pills[i]).addClass('active');
                    $(contents[i]).addClass('in active');
                } else if (i !== 0 && $(pills[i]).hasClass('active')) {
                    $(pills[i]).removeClass('active');
                    $(contents[i]).removeClass('in active');
                }
            }
        };

        /**
         * Checks if the UH usernames in a group are blank or not. If it is blank, it will be replaced with N/A.
         * @param {object[]} group - the group to check
         */
        $scope.replaceBlankUsernames = function (group) {
            for (var i = 0; i < group.length; i++) {
                if (group[i].username === '') {
                    group[i].username = 'N/A';
                }
            }
        };

    }

    UHGroupingsApp.controller("OwnerJsController", OwnerJsController);
})();
