(function () {
    /**
     * Admin controller for the entire admin page.
     *
     * @param $scope       - Binding variable between controller and html page.
     * @param $window      - Reference to the browser's window
     * @param dataProvider - service function that acts as the AJAX get.
     * @param dataUpdater  - service function that acts as AJAX post, used mainly for adding or updating
     * @param dataDelete   - service function that acts as AJAX psst, use function mainly for delete function.
     * @constructor
     */
    function AdminJsController($scope, $window, $uibModal, $filter, dataProvider, dataUpdater, dataDelete) {

        $scope.currentUsername = "";
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

            $scope.initCurrentUsername();

            var url = "api/groupings/" + $scope.getCurrentUsername() + "/adminLists";

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

        $scope.initCurrentUsername = function () {
            $scope.currentUsername = $window.document.getElementById("name").innerHTML;
        };

        $scope.getCurrentUsername = function () {
            return $scope.currentUsername;
        };

        $scope.sortCol = function (list, col, listPaged, symbol) {
            $scope.symbol = {'name': '', 'path': '', 'uuid': '', 'username': ''};

            if ($scope[symbol] === 'ascend' || typeof $scope[symbol] == 'undefined') {
                list = _.sortBy(list, col);
                $scope[listPaged] = $scope.groupToPages(list, $scope[listPaged]);
                $scope[symbol] = 'descend';
            }
            else {
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
         * @param {string} path - the grouping's path
         */
        $scope.getData = function(path) {
            $scope.groupingPath = path;
            $scope.loading = true;
            var groupingDataUrl = "api/groupings/" + $scope.groupingPath + "/" + $scope.getCurrentUsername() + "/grouping";
            console.log(groupingDataUrl);

            dataProvider.loadData(function (d) {
                console.log(d);
                if (d.path.length == 0) {
                    $scope.errorModal();
                } else {
                    $scope.basis = d.basis.members;

                    //Gets members in grouping
                    $scope.groupingMembers = d.composite.members;
                    $scope.modify($scope.groupingMembers);
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
        $scope.modify = function (grouping) {
            //Filter out names with hawaii.edu and adds basis object.
            for (var i = 0; i < grouping.length; i++) {
                grouping[i].basis = "No";
                if (grouping[i].name.indexOf("hawaii.edu") > -1) {
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

            //sorts data in alphabetic order


            grouping.sort(function (a, b) {
                var nameA = a.name.toLowerCase(), nameB = b.name.toLowerCase();
                if (nameA < nameB) //sort string ascending
                    return -1;
                if (nameA > nameB)
                    return 1;
                return 0
            });
        };

        // TODO: Find a way to make the 3 adds into a more singular function.

        /**
         * Adds function that adds an member to the admin grouping.
         * Uses dataUpdater service to post the user that is being added.
         * If user is successfully added, dataProvider will return a Success in result Code.
         * Else if resultCode is undefined, then user was not successfully added.
         */
        $scope.addAdmin = function () {
            var addUrl = "api/groupings/" + $scope.getCurrentUsername() + "/" + $scope.username + "/addAdmin";
            $scope.testdata = [];

            dataUpdater.updateData(function (d) {
                if (d.resultCode === 'SUCCESS') {
                    console.log("Success In Adding");
                    //reload data table
                    $scope.addModalAlert('admin', 'success');
                }
                else if (typeof d.resultCode === 'undefined') {
                    console.log("Failure In Adding");
                    $scope.addModalAlert();
                }
            }, addUrl);

        };

        $scope.addMember = function (type) {
            var addUrl = "api/groupings/" + $scope.groupingPath + "/" + $scope.getCurrentUsername() + "/" + $scope.addUser + "/addMemberTo" + type + "Group";
            dataUpdater.updateData(function (d) {
                console.log(d);
                if(d.statusCode != null)
                {
                    console.log("failed");
                    $scope.addModalAlert();
                }
                else if (d.resultCode === "SUCCESS") {
                    console.log("success in adding " + $scope.addUser);
                    $scope.addModalAlert('grouping', 'success');
                }
                else if (typeof d.resultsCode === 'undefined') {
                    console.log($scope.addUser + " this user does not exist.");
                    $scope.addModalAlert();
                }
            }, addUrl);
            $scope.addUser = '';
        };

        $scope.addOwner = function () {
            var addOwnerUrl = "api/groupings/" + $scope.groupingPath + "/" + $scope.getCurrentUsername() + "/" + $scope.ownerUser + "/assignOwnership";
            dataUpdater.updateData(function (d) {
                console.log(d);
                if (d.resultCode === "SUCCESS") {
                    console.log("Assigned " + $scope.ownerUser + " as an owner");
                    $scope.addModalAlert('grouping', 'success');
                }
                else if (typeof d.resultsCode === 'undefined') {
                    console.log($scope.ownerUser + " this user does not exist.");
                    $scope.addModalAlert();
                }
            }, addOwnerUrl);
            $scope.ownerUser = '';
        };

        $scope.addModalAlert = function (location, success) {
            if (success === 'success') var message = "User has been added";
            else var message = "Error: There was an error in trying to add this user.";

            var modalHtml = '<div class="modal-body">' + message + '</div>';
            modalHtml += '<div class="modal-footer"><button class="btn btn-primary" ng-click="continue()">OK</button></div>';

            $scope.addModalInstance = $uibModal.open({
                template: modalHtml,
                scope: $scope
            });

            $scope.addModalInstance.result.then(function () {
                if (success === 'success') {
                    $scope.loading = true;
                    if (location === 'admin') $scope.init();
                    if (location === 'grouping') $scope.getData($scope.groupingPath);
                }
            });
        };

        $scope.continue = function () {
            $scope.addModalInstance.close();
        };

        // TODO: Find a way to make the 3 removes into a more singular function.

        /**
         * Remove function uses dataDelete Service to remove user from admin grouping.
         * Will not delete admin if there is only one admin in the list.
         *
         * @param index - the index of the user based on the html table.
         */
        $scope.removeAdmin = function (index) {
            var deleteUser = $scope.pagedItemsAdmins[$scope.currentPageAdmins][index].username;
            var deleteUrl = "api/groupings/" + $scope.getCurrentUsername() + "/" + deleteUser + "/deleteAdmin";

            $scope.deleteModal(deleteUser, deleteUrl, index, 'admin');
        };

        $scope.removeMember = function (type, row) {
            var user;
            if (type === 'Include') {
                user = $scope.pagedItemsInclude[$scope.currentPageInclude][row].username;
            }
            if (type === 'Exclude') {
                user = $scope.pagedItemsExclude[$scope.currentPageExclude][row].username;
            }

            var URL = "api/groupings/" + $scope.groupingPath + "/" + $scope.getCurrentUsername() + "/" + user + "/deleteMemberFrom" + type + "Group";

            $scope.deleteModal(user, URL, null, $scope.groupingPath);
        };

        $scope.removeOwner = function (index) {
            var removeOwner = $scope.pagedItemsOwners[$scope.currentPageOwners][index].username;
            var removeOwnerUrl = "api/groupings/" + $scope.groupingPath + "/" + $scope.getCurrentUsername() + "/" + removeOwner + "/removeOwnership";
            if ($scope.groupingOwners.length > 1) {
                $scope.deleteModal(removeOwner, removeOwnerUrl, null, $scope.groupingPath);
            }
        };

        /**
         *
         * @param user - name of the user to be deleted
         * @param url - api url call to remove user
         * @param location - The index of the user in the admin list table.
         * @param type - Declaring if removing from admin list or from a grouping path.
         */
        $scope.deleteModal = function (user, url, location, type) {
            var message = "Are you sure you want to delete " + user;
            var modalHtml = '<div class="modal-body">' + message + '</div>';
            modalHtml += '<div class="modal-footer"><button class="btn btn-primary" ng-click="ok()">OK</button><button class="btn btn-warning" ng-click="cancel()" data-dismiss="modal">Cancel</button></div>';

            $scope.deleteModalInstance = $uibModal.open({
                template: modalHtml,
                scope: $scope
            });

            $scope.deleteModalInstance.result.then(function () {
                $scope.loading = true;
                if (type === 'admin' && $scope.adminsList.length > 1) {
                    dataDelete.deleteData(function (d) {
                        $scope.adminsList.splice(location, 1);
                        $scope.init();
                    }, url);
                }
                else {
                    dataDelete.deleteData(function (d) {
                        console.log(d);
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

        $scope.infoModal = function (preference, group) {
            $scope.test = '';

            if (preference === 'opt')
                $scope.test = "members can " + group + " themselves from the grouping";
            else if (preference === 'publication')
                $scope.test = "the publication destination is active or not";


            $scope.infoModalInstance = $uibModal.open({
                templateUrl: 'infoModal.html',
                windowClass: 'center-modal',
                scope: $scope,
                resolve: {
                    items: function () {
                        return $scope.test;
                    }
                }
            });
        };

        $scope.infoDismiss = function () {
            $scope.infoModalInstance.dismiss();
        };

        $scope.updateAllowOptOut = function () {
            var url = "api/groupings/" + $scope.groupingPath + "/" + $scope.getCurrentUsername() + "lk/" + $scope.allowOptOut + "/setOptOut";
            dataUpdater.updateData(function (d) {
                console.log(d);
                if(d.statusCode != null)
                {
                    console.log("failed");
                    $scope.preferenceErrorModal();
                }
                else if (d[0].resultCode === "SUCCESS_ALLOWED" || d[0].resultCode === "SUCCESS_NOT_ALLOWED") {
                    console.log("success");
                }
            }, url);
            console.log(url);

        };

        $scope.updateAllowOptIn = function () {
            var url = "api/groupings/" + $scope.groupingPath + "/" + $scope.getCurrentUsername() + "/" + $scope.allowOptIn + "/setOptIn";
            dataUpdater.updateData(function (d) {
                if (d[0].resultCode === "SUCCESS_ALLOWED" || d[0].resultCode === "SUCCESS_NOT_ALLOWED") {
                    console.log("success");
                }
                else {
                    console.log("failed");
                    $scope.preferenceErrorModal();
                }
            }, url);
            console.log(url);
        };

        $scope.updateListserv = function () {
            var url = "api/groupings/" + $scope.groupingPath + "/" + $scope.getCurrentUsername() + "/" + $scope.listserv + "/setListserv";
            dataUpdater.updateData(function (d) {
                console.log(d);
                if (d.resultCode === "SUCCESS") {
                    console.log("success");
                }
                else {
                    console.log("failed");
                    $scope.preferenceErrorModal();
                }
            }, url);

            console.log(url);
        };

        $scope.checkLDAP = function () {
            console.log($scope.LDAP);
        };

        $scope.preferenceErrorModal = function () {
            $scope.preferenceErrorModalInstance = $uibModal.open({
                templateUrl: 'preferenceErrorModal.html',
                windowClass: 'center-modal',
                scope: $scope
            });
        };

        $scope.preferenceErrorDismiss = function () {
            $scope.preferenceErrorModalInstance.dismiss();
        };

        $scope.filter = function (list, whatList, whatQuery) {
            var query = "";
            query = $scope[whatQuery];
            $scope.filteredItems = [];
            $scope.filteredItems = $filter('filter')(list, function (item) {
                for (var key in item) {
                    if (item.hasOwnProperty(key) && typeof item[key] === 'string' && key !== '$$hashKey' && key !== 'basis') {
                        if (searchMatch(item[key], query)) return true;
                    }
                }
            });
            // console.log($scope.filteredItems);
            page = 0;
            // now group by pages
            var emptyList = [];
            $scope[whatList] = $scope.groupToPagesChanged(emptyList);
        };

        var searchMatch = function (haystack, needle) {
            if (!needle) {
                return true;
            }
            return haystack.toLowerCase().indexOf(needle.toLowerCase()) !== -1;
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
                console.log("I AM NULL ... WHY?!");
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
        $scope.showData = function(row) {
            $scope.selectedGrouping = $scope.pagedItemsGroupings[$scope.currentPageGroupings][row];
            if (!$scope.showGrouping) {
                $scope.showGrouping = true;
                $scope.getData($scope.selectedGrouping.path);
            } else {
                $scope.showGrouping = false;
            }
        };

        /**
         * Resets the arrays containing the members of each grouping and their page numbers.
         */
        $scope.resetGroupingInformation = function() {
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
            var content = $('#pill-content')[0].children
            for (var i = 0; i < pills.length; i++) {
                if (i === 0 && !$(content[i]).hasClass('active')) {
                    $(pills[i]).addClass('active');
                    $(content[i]).addClass('in active');
                } else if (i !== 0 && $(pills[i]).hasClass('active')) {
                    $(pills[i]).removeClass('active');
                    $(content[i]).removeClass('in active');
                }
            }
        };
    }

    adminApp.controller("AdminJsController", AdminJsController);
})
();
