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
        $scope.list = [];
        $scope.basis = [];
        $scope.groupingList = [];
        $scope.groupingsList = [];
        $scope.groupingsBasis = [];
        $scope.groupingInclude = [];
        $scope.groupingExclude = [];
        $scope.symbol = [];

        //Variables for pagination
        $scope.pagedItems = [];

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

        $scope.gap = 2;
        $scope.itemsPerPage = 10;
        $scope.currentPage = 0;
        $scope.currentPageOptOut = 0;

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
                if(d.allGroupings.length == 0) {
                    $scope.errorModal();
                }
                else
                {
                    $scope.list = d.adminGroup.members;
                    $scope.groupingList = d.allGroupings;

                    $scope.modify($scope.list);
                    $scope.symbol.name = '\u21c5';
                    console.log($scope.list);
                    $scope.pagedItems = $scope.groupToPages($scope.list, $scope.pagedItems);
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

        $scope.errorDismiss = function() {
            $scope.errorModalInstance.dismiss();
        };

        $scope.initCurrentUsername = function () {
            $scope.currentUsername = $window.document.getElementById("name").innerHTML;
        };

        $scope.getCurrentUsername = function () {
            return $scope.currentUsername;
        };

        $scope.sortCol = function (list, col, listPaged, symbol) {
            $scope.symbol = {'name': '', 'uuid': '', 'username': ''};

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
         * Gets the information about a grouping based off the name in the input box.
         */
        $scope.search = function () {
            $scope.groupingPath = '';
            $scope.groupingURL = '';
            //Finds the path of the grouping based on the name of the grouping.
            for (var i = 0; i < $scope.groupingList.length; i++) {
                if ($scope.groupingList[i].name === $scope.groupingName) {
                    $scope.groupingPath = $scope.groupingList[i].path;
                }
            }
            if ($scope.groupingPath === '') {
                console.log("Not a valid grouping");
                $scope.error = true;
                //Empties array
                $scope.basis = [];
                $scope.pagedItemsList = [];
                $scope.pagedItemsBasis = [];
                $scope.pagedItemsInclude = [];
                $scope.pagedItemsExclude = [];
                $scope.pagedItemsOwners = [];
                $scope.preference = [];
                $scope.loading = false;
                $scope.title = '';
            } else {
                $scope.getData($scope.groupingPath);
            }
        };

        $scope.getData = function (path) {
            $scope.loading = true;
            var groupingDataUrl = "api/groupings/" + path + "/" + $scope.getCurrentUsername() + "/grouping";
            console.log(groupingDataUrl);

            dataProvider.loadData(function (d) {
                console.log(d);
                if(d.path.length == 0) {
                    $scope.errorModal();
                }
                else
                {
                    $scope.error = false;
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
                    $scope.title = $scope.groupingName;
                }
                $scope.loading = false;
            }, groupingDataUrl);
        };

        $scope.dismiss = function () {
            $scope.title = '';
            $scope.error = false;
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
                if (d.resultCode === "SUCCESS") {
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
            else var message = "Error: User is not a valid username";

            var modalHtml = '<div class="modal-body">' + message + '</div>';
            modalHtml += '<div class="modal-footer"><button class="btn btn-primary" ng-click="continue()">OK</button></div>';

            $scope.addModalInstance = $uibModal.open({
                template: modalHtml,
                scope: $scope
            });

            $scope.addModalInstance.result.then(function () {
                if (success === 'success')
                {
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
            var deleteUser = $scope.list[index].username;
            var deleteUrl = "api/groupings/" + $scope.getCurrentUsername() + "/" + deleteUser + "/deleteAdmin";

            $scope.deleteModal(deleteUser, deleteUrl, index, 'admin');
        };

        $scope.removeMember = function (type, row) {
            var user;
            if (type === 'Include') {
                user = $scope.groupingInclude[row].username;
            }
            if (type === 'Exclude') {
                user = $scope.groupingExclude[row].username;
            }

            var URL = "api/groupings/" + $scope.groupingPath + "/" + $scope.getCurrentUsername() + "/" + user + "/deleteMemberFrom" + type + "Group";

            $scope.deleteModal(user, URL, null, $scope.groupingPath);
        };

        $scope.removeOwner = function (index) {
            var removeOwner = $scope.ownerList[index].username;
            var removeOwnerUrl = "api/groupings/" + $scope.groupingPath + "/" + $scope.getCurrentUsername() + "/" + removeOwner + "/removeOwnership";
            if ($scope.ownerList.length > 1) {
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
        $scope.deleteModal = function(user, url, location, type)
        {
            var message = "Are you sure you want to delete " + user;
            var modalHtml = '<div class="modal-body">' + message + '</div>';
            modalHtml += '<div class="modal-footer"><button class="btn btn-primary" ng-click="ok()">OK</button><button class="btn btn-warning" ng-click="cancel()" data-dismiss="modal">Cancel</button></div>';

            $scope.deleteModalInstance = $uibModal.open({
                template: modalHtml,
                scope: $scope
            });

            $scope.deleteModalInstance.result.then(function () {
                $scope.loading = true;
                if (type === 'admin' && $scope.list.length > 1) {
                    dataDelete.deleteData(function (d) {
                        $scope.list.splice(location, 1);
                        $scope.init();
                    }, url);
                }
                else
                {
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

            if(preference === 'opt')
                $scope.test = "members can " + group + " themselves from the grouping";
            else if(preference === 'publication')
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

        $scope.infoDismiss = function() {
            $scope.infoModalInstance.dismiss();
        };

        $scope.savePref = function () {
            var prefUrls = [];

            prefUrls.push({
                "url": "api/groupings/" + $scope.groupingPath + "/" + $scope.getCurrentUsername() + "/" + $scope.preference.listserv + "/setListserv",
                "name": "Listserv"
            });
            prefUrls.push({
                "url": "api/groupings/" + $scope.groupingPath + "/" + $scope.getCurrentUsername() + "/" + $scope.preference.optIn + "/setOptIn",
                "name": "optInOption"
            });
            prefUrls.push({
                "url": "api/groupings/" + $scope.groupingPath + "/" + $scope.getCurrentUsername() + "/" + $scope.preference.optOut + "/setOptOut",
                "name": "optOutOption"
            });

            for (var i = 0; i < prefUrls.length; i++) {
                dataUpdater.updateData(function (d) {
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

        $scope.filter = function (list,whatList, whatQuery) {
            var query = "";
            query = $scope[whatQuery];
            $scope.filteredItems = [];
            $scope.filteredItems = $filter('filter')(list, function (item) {
                for (var key in item) {
                    if (item.hasOwnProperty(key) && key !== 'basis' && searchMatch(item[key], query)) {
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

        /**
         * Determines which page the pagination moves to. Defaults to setting the page to whatever page is.
         *
         * @param page - the page moving to.
         */
        $scope.paging = function (page) {
            switch (page) {
                case 'Include Next':
                    if ($scope.currentPageInclude < $scope.pagedItemsInclude.length - 1) {
                        $scope.currentPageInclude = $scope.currentPageInclude + 1;
                    }
                    break;

                case 'Include Set':
                    $scope.currentPageInclude = this.n;
                    break;

                case 'Include Prev':
                    if ($scope.currentPageInclude > 0) {
                        $scope.currentPageInclude--;
                    }
                    break;
                case 'Include First':
                    $scope.currentPageInclude = 0;
                    break;

                case 'Include Last':
                    if ($scope.currentPageInclude >= 0) {
                        $scope.currentPageInclude = $scope.pagedItemsInclude.length - 1;
                    }
                    break;
                // Split for the exclude
                case 'Exclude Next':
                    if ($scope.currentPageExclude < $scope.pagedItemsExclude.length - 1) {
                        $scope.currentPageExclude = $scope.currentPageExclude + 1;
                    }
                    break;

                case 'Exclude Set':
                    $scope.currentPageExclude = this.n;
                    break;

                case 'Exclude Prev':
                    if ($scope.currentPageExclude > 0) {
                        $scope.currentPageExclude--;
                    }
                    break;
                case 'Exclude First':
                    $scope.currentPageExclude = 0;
                    break;

                case 'Exclude Last':
                    if ($scope.currentPageExclude >= 0) {
                        $scope.currentPageExclude = $scope.pagedItemsExclude.length - 1;
                    }
                    break;
                // Cases for the basis
                case 'Basis Next':
                    if ($scope.currentPageBasis < $scope.pagedItemsBasis.length - 1) {
                        $scope.currentPageBasis = $scope.currentPageBasis + 1;
                    }
                    break;

                case 'Basis Set':
                    $scope.currentPageBasis = this.n;
                    break;

                case 'Basis Prev':
                    if ($scope.currentPageBasis > 0) {
                        $scope.currentPageBasis--;
                    }
                    break;
                case 'Basis First':
                    $scope.currentPageBasis = 0;
                    break;

                case 'Basis Last':
                    if ($scope.currentPageBasis >= 0) {
                        $scope.currentPageBasis = $scope.pagedItemsBasis.length - 1;
                    }
                    break;
                // Cases for Owners
                case 'Owners Next':
                    if ($scope.currentPageOwners < $scope.pagedItemsOwners.length - 1) {
                        $scope.currentPageOwners = $scope.currentPageOwners + 1;
                    }
                    break;

                case 'Owners Set':
                    $scope.currentPageOwners = this.n;
                    break;

                case 'Owners Prev':
                    if ($scope.currentPageOwners > 0) {
                        $scope.currentPageOwners--;
                    }
                    break;
                case 'Owners First':
                    $scope.currentPageOwners = 0;
                    break;

                case 'Owners Last':
                    if ($scope.currentPageOwners >= 0) {
                        $scope.currentPageOwners = $scope.pagedItemsOwners.length - 1;
                    }
                    break;
                // Cases for List
                case 'List Next':
                    if ($scope.currentPageList < $scope.pagedItemsList.length - 1) {
                        $scope.currentPageList = $scope.currentPageList + 1;
                    }
                    break;

                case 'List Set':
                    $scope.currentPageList = this.n;
                    break;

                case 'List Prev':
                    if ($scope.currentPageList > 0) {
                        $scope.currentPageList--;
                    }
                    break;
                case 'List First':
                    $scope.currentPageList = 0;
                    break;

                case 'List Last':
                    if ($scope.currentPageList >= 0) {
                        $scope.currentPageList = $scope.pagedItemsList.length - 1;
                    }
                    break;
                case "first":
                    $scope.currentPage = 0;
                    break;
                case "prev":
                    if ($scope.currentPage > 0) {
                        $scope.currentPage--;
                    }
                    break;
                case "next" :
                    if ($scope.currentPage < $scope.pagedItems.length - 1) {
                        $scope.currentPage = $scope.currentPage + 1;
                    }
                    break;
                case "last" :
                    $scope.currentPage = $scope.pagedItems.length - 1;
                    break;
                default :
                    $scope.currentPage = page;
                    break;
            }
        };

        //Makes it so that you have to type at least 3 characters in order for the datalist to autocomplete
        $('input[list]').on('input', function (e) {
            var input = $(e.target),
                datalist = input.attr('data-list');

            if (input.val().length < 3) {
                input.attr('list', '');
            } else {
                input.attr('list', datalist);
            }
        });
    }

    adminApp.controller("AdminJsController", AdminJsController);
})
();