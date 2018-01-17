(function () {

    /**
     * Admin controller for the entire admin page.
     *
     * @param $scope - binding between controller and HTML page
     * @param $window - reference to the browser's window
     * @param dataProvider - service function that provides GET and POST requests for getting or updating data
     */
    function AdminJsController($scope, $window, $uibModal, $filter, dataProvider) {

        $scope.filteredItems = [];
        $scope.basis = [];

        $scope.adminsList = [];
        $scope.groupingsList = [];

        $scope.groupingMembers = [];
        $scope.groupingBasis = [];
        $scope.groupingInclude = [];
        $scope.groupingExclude = [];
        $scope.groupingOwners = [];

        $scope.symbol = [];

        $scope.pagedItemsAdmins = [];
        $scope.currentPageAdmins = 0;

        $scope.pagedItemsGroupings = [];
        $scope.currentPageGroupings = 0;

        $scope.pagedItemsBasis = [];
        $scope.currentPageBasis = 0;

        $scope.pagedItemsMembers = [];
        $scope.currentPageMembers = 0;

        $scope.pagedItemsInclude = [];
        $scope.currentPageInclude = 0;

        $scope.pagedItemsExclude = [];
        $scope.currentPageExclude = 0;

        $scope.pagedItemsOwners = [];
        $scope.currentPageOwners = 0;

        $scope.allowOptIn = false;
        $scope.allowOptOut = false;
        $scope.listserv = false;
        $scope.LDAP = false;

        $scope.gap = 2;
        $scope.itemsPerPage = 20;

        $scope.showGrouping = false;

        /**
         * Initializing function for the admin page.
         * Calls dataProvider service to retrieve a list of admins.
         * @returns returns
         *                1 for ascending
         *                -1 for descending
         *                0 for failed attempt
         */
        $scope.init = function () {
            // Adds the loading spinner.
            $scope.loading = true;

            var url = "api/groupings/adminLists";

            dataProvider.loadData(function (d) {
                console.log(d.allGroupings.length);
                if (d.allGroupings.length == 0) {
                    $scope.errorModal();
                } else {
                    $scope.adminsList = d.adminGroup.members;
                    $scope.groupingsList = d.allGroupings;
                    $scope.groupingsList = _.sortBy($scope.groupingsList, 'name');
                    $scope.modify($scope.adminsList);
                    $scope.symbol.name = '\u21c5';
                    $scope.pagedItemsAdmins = $scope.groupToPages($scope.adminsList, $scope.pagedItemsAdmins);
                    $scope.pagedItemsGroupings = $scope.groupToPages($scope.groupingsList, $scope.pagedItemsGroupings);
                    console.log($scope.groupingsList);
                }
                $scope.loading = false;
            }, url);
        };

        $scope.change = function () {
            $scope.showGrouping = false;
            $scope.resetGroupingInformation();
        };

        $scope.errorModal = function () {
            $scope.errorModalInstance = $uibModal.open({
                templateUrl: 'modal/apiError.html',
                windowClass: 'center-modal',
                scope: $scope
            });
        };

        $scope.errorDismiss = function () {
            $scope.errorModalInstance.dismiss();
        };

        $scope.sortCol = function (list, col, listPaged, symbol) {
            $scope.symbol = {'name': '', 'path': '', 'uuid': '', 'username': ''};

            if ($scope[symbol] === 'ascend' || typeof $scope[symbol] == 'undefined') {
                list = _.sortBy(list, col);
                $scope[listPaged] = $scope.groupToPages(list, $scope[listPaged]);
                $scope[symbol] = 'descend';
            } else {
                list = _.sortBy(list, col).reverse();
                $scope[listPaged] = $scope.groupToPages(list, $scope[listPaged]);
                $scope[symbol] = 'ascend';
            }
            switch (col) {
                case 'name':
                    $scope.symbol.name = '\u21c5';
                    break;
                case 'path':
                    $scope.symbol.path = '\u21c5';
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
         * Retrieves information about the grouping.
         */
        $scope.getData = function () {
            $scope.loading = true;
            var groupingDataUrl = "api/groupings/" + $scope.selectedGrouping.path + "/grouping";

            dataProvider.loadData(function (d) {
                console.log(d);
                if (d.path.length == 0) {
                    $scope.errorModal();
                } else {
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
                    $scope.showGrouping = true;
                }
            }, groupingDataUrl);
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
                            for (var k = 0; k < $scope.groupingInclude.length; k++) {
                                if ($scope.groupingInclude[k].uuid === grouping[m].uuid) {
                                    grouping[m].basis = "in Basis / in Include";
                                }
                            }
                        }
                        else grouping[m].basis = "Yes";
                    }
                }
            }

            //sorts data in alphabetic order
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

        // TODO: Find a way to make the 3 adds into a more singular function.

        /**
         * Adds a user to the admin list.
         */
        $scope.addAdmin = function () {
            var addUrl = "api/groupings/" + $scope.adminToAdd + "/addAdmin";
            dataProvider.updateData(function (d) {
                var successful = false;
                console.log(d);
                if (d.statusCode != null) {
                    console.log("Error, Status Code: " + d.statusCode);
                } else if (d.resultCode === 'SUCCESS') {
                    successful = true;
                    console.log("Success In Adding");
                    $scope.addModalAlert('admin', 'success');
                    // Clear the input field
                }
                $scope.createAddModal($scope.adminToAdd, successful);
                $scope.adminToAdd = '';
            }, addUrl);
        };

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
                    $scope.createAddModal($scope.ownerUser, 'owner', successful);
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
         * @param {string?} path - the path to the grouping (if adding the user to a grouping)
         */
        $scope.createAddModal = function (user, wasSuccessful, path) {
            $scope.user = user;
            $scope.wasSuccessful = wasSuccessful;

            $scope.addModalInstance = $uibModal.open({
                templateUrl: 'modal/addModal.html',
                scope: $scope,
            });

            $scope.addModalInstance.result.finally(function() {
                if (wasSuccessful) {
                    $scope.loading = true;
                    // If no path was specified, then refresh the admin list. Otherwise, refresh the grouping
                    if (path === undefined) {
                        $scope.init();
                    } else {
                        $scope.getData(path);
                    }
                }
            });
        };

        /**
         * Closes the add user modal.
         */
        $scope.closeAddModal = function () {
            $scope.addModalInstance.close();
        };

        // TODO: Find a way to make the 3 removes into a more singular function.

        /**
         * Removes an admin from the admin list. There must be at least one admin remaining.
         * @param {number} index - the index of the admin to delete, with the current page and items per page taken into
         * account
         */
        $scope.removeAdmin = function (index) {
            var deleteUser = $scope.adminsList[index].username;
            var deleteUrl = "api/groupings/" + deleteUser + "/deleteAdmin";

            $scope.createRemoveModal(deleteUser, deleteUrl);
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
         * @param {string?} path - the path to the grouping (if deleting a user from a grouping)
         */
        $scope.createRemoveModal = function (user, url, path) {
            $scope.userToDelete = user;

            $scope.removeModalInstance = $uibModal.open({
                templateUrl: 'modal/removeModal.html',
                scope: $scope
            });

            $scope.removeModalInstance.result.then(function () {
                $scope.loading = true;
                // Remove the user, then reload either the admin list or grouping
                dataProvider.updateData(function () {
                    if (path === undefined) {
                        $scope.init();
                    } else {
                        $scope.getData(path);
                    }
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
         * Closes the modal for deleting a user. This does not delete the user from the grouping/admin list.
         */
        $scope.cancelRemoveUser = function () {
            $scope.removeModalInstance.dismiss();
        };

        $scope.infoModal = function (preference, group) {
            $scope.info = '';

            if (preference === 'opt')
                $scope.info = "or not members can " + group + " themselves to the grouping";
            else if (preference === 'publication')
                $scope.info = "the publication destination is active or not";


            $scope.infoModalInstance = $uibModal.open({
                templateUrl: 'modal/infoModal.html',
                scope: $scope,
                resolve: {
                    items: function () {
                        return $scope.info;
                    }
                }
            });
        };

        $scope.infoDismiss = function () {
            $scope.infoModalInstance.dismiss();
        };

        $scope.updateAllowOptOut = function () {
            var url = "api/groupings/" + $scope.selectedGrouping.path + "/"  + $scope.allowOptOut + "/setOptOut";
            dataProvider.updateData(function (d) {
                console.log(d);
                if (d.statusCode != null) {
                    console.log("Error, Status Code: " + d.statusCode);
                    $scope.preferenceErrorModal();
                } else if (d[0].resultCode === "SUCCESS_ALLOWED" || d[0].resultCode === "SUCCESS_NOT_ALLOWED") {
                    console.log("success");
                }
            }, url);
            console.log(url);

        };

        $scope.updateAllowOptIn = function () {
            var url = "api/groupings/" + $scope.selectedGrouping.path + "/" + $scope.allowOptIn + "/setOptIn";
            dataProvider.updateData(function (d) {
                if (d.statusCode != null) {
                    console.log("Error, Status Code: " + d.statusCode);
                    $scope.preferenceErrorModal();
                } else if (d[0].resultCode === "SUCCESS_ALLOWED" || d[0].resultCode === "SUCCESS_NOT_ALLOWED") {
                    console.log("success");
                }
            }, url);
            console.log(url);
        };

        $scope.updateListserv = function () {
            var url = "api/groupings/" + $scope.selectedGrouping.path + "/" + $scope.listserv + "/setListserv";
            dataProvider.updateData(function (d) {
                console.log(d);
                if (d.statusCode != null) {
                    console.log("Error, Status Code: " + d.statusCode);
                    $scope.preferenceErrorModal();
                } else if (d.resultCode === "SUCCESS") {
                    console.log("success");
                }
            }, url);

            console.log(url);
        };

        $scope.checkLDAP = function () {
            console.log($scope.LDAP);
        };

        $scope.preferenceErrorModal = function () {
            $scope.preferenceErrorModalInstance = $uibModal.open({
                templateUrl: 'modal/preferenceErrorModal.html',
                windowClass: 'center-modal',
                scope: $scope
            });
        };

        $scope.preferenceErrorDismiss = function () {
            $scope.preferenceErrorModalInstance.dismiss();
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
         * @param end - all the param are self explanatory
         * @return ret
         *     everything within the range of start,
         *       end, and making sure it's that size
         **/
        $scope.range = function (size, start, end) {
            var ret = [];
            if (size < end) {
                end = size;
                //start = size-$scope.gap;
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
         * Goes back to the list of groupings available for administration.
         */
        $scope.showGroups = function () {
            if (!$scope.showGrouping) {
                $scope.showGrouping = true;
            } else {
                $scope.showGrouping = false;
                $scope.resetGroupingInformation();
                $scope.resetSelectedGroup();
            }
        };

        /**
         * Gets information about the grouping clicked by the user on the Manage Groupings tab
         * @param {number} row - the row clicked on by the user (zero-indexed)
         */
        $scope.showData = function (row) {
            $scope.selectedGrouping = $scope.pagedItemsGroupings[$scope.currentPageGroupings][row];
            if (!$scope.showGrouping) {
                $scope.showGrouping = true;
                $scope.getData();
            } else {
                $scope.showGrouping = false;
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
            var content = $('#pill-content')[0].children
            for (var i = 0; i < pills.length && i < content.length; i++) {
                if (i === 0 && !$(content[i]).hasClass('active')) {
                    $(pills[i]).addClass('active');
                    $(content[i]).addClass('in active');
                } else if (i !== 0 && $(pills[i]).hasClass('active')) {
                    $(pills[i]).removeClass('active');
                    $(content[i]).removeClass('in active');
                }
            }
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

    UHGroupingsApp.controller("AdminJsController", AdminJsController);

})();
