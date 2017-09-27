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
    function AdminJsController($scope, $filter, $window, dataProvider, dataUpdater, dataDelete) {

        $scope.currentUsername = "";
        $scope.filteredItems = [];
        $scope.list = [];
        $scope.basis = [];
        $scope.groupingList = [];
        $scope.groupingsList = [];
        $scope.groupingsBasis = [];
        $scope.groupingInclude = [];
        $scope.groupingExclude = [];

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
                $scope.list = d.adminGroup.members;
                $scope.groupingList = d.allGroupings;

                $scope.modify($scope.list);

                console.log($scope.list);
                $scope.pagedItems = $scope.groupToPages($scope.list, $scope.pagedItems);
                $scope.loading = false;
            }, url);
        };

        $scope.initCurrentUsername = function () {
            $scope.currentUsername = $window.document.getElementById("name").innerHTML;
        };

        $scope.getCurrentUsername = function () {
            return $scope.currentUsername;
        };

        $scope.sortCol = function (list, col, listPaged, symbol) {
            $scope.symbol = {'name': '', 'uuid': '', 'username':''};

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
                case 'uuid':
                    $scope.symbol.uuid = '\u21c5';
                    break;
                case 'username':
                    $scope.symbol.username = '\u21c5';
                    break;
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
                $scope.loading = false;
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

                /*$scope.pref = d.listservOn;
                $scope.allowOptIn = d.optInOn;
                $scope.allowOptOut = d.optOutOn;*/

                $scope.preference = {
                    optIn : d.optInOn,
                    optOut: d.optOutOn,
                    listserv: d.listservOn
                };

                //Stop loading spinner
                $scope.title = $scope.groupingName;
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
                grouping[i].basis = "\u2716";
                if (grouping[i].name.includes("hawaii.edu")) {
                    grouping.splice(i, 1);
                    i--;
                }
            }

            //Determines if member is in the basis or not
            for (var l = 0; l < $scope.basis.length; l++) {
                for (var m = 0; m < grouping.length; m++) {
                    if ($scope.basis[l].uuid === grouping[m].uuid) {
                        grouping[m].basis = "\u2714";
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
            console.log(addUrl);

            if (confirm("You are adding " + $scope.username + " to the include list of this grouping")) {
                dataUpdater.updateData(function (d) {
                    console.log(d);
                    if (d.resultCode === 'SUCCESS') {
                        console.log("Success In Adding");
                        //reload data table
                        $scope.loading = true;
                        $scope.init();
                    }
                    else if (typeof d.resultCode === 'undefined') {
                        console.log("Failure In Adding");
                    }
                }, addUrl);
            }
        };

        $scope.addMember = function (type) {
            var addUrl = "api/groupings/" + $scope.groupingPath + "/" + $scope.getCurrentUsername() + "/" + $scope.addUser + "/addMemberTo" + type + "Group";
            dataUpdater.updateData(function (d) {
                if (d.resultCode === "SUCCESS") {
                    console.log("success in adding " + $scope.addUser);
                    alert("SUCCESS IN ADDING " + $scope.addUser);
                    $scope.getData($scope.groupingPath);
                }
                else if (typeof d.resultsCode === 'undefined') {
                    console.log($scope.addUser + " this user does not exist.");
                    alert($scope.addUser + " this user does not exist.");
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
                    alert("Assigned " + $scope.ownerUser + " as an owner");
                    $scope.getData($scope.groupingPath);
                }
                else if (typeof d.resultsCode === 'undefined') {
                    console.log($scope.ownerUser + " this user does not exist.");
                    alert($scope.ownerUser + " this user does not exist.");
                }
            }, addOwnerUrl);
            $scope.ownerUser = '';
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
            console.log(deleteUrl);
            if ($scope.list.length > 1) {
                dataDelete.deleteData(function (d) {
                    $scope.list.splice(index, 1);
                    $scope.init();
                }, deleteUrl);
            }
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
            console.log(URL);
            dataDelete.deleteData(function (d) {
                console.log(d);
                $scope.getData($scope.groupingPath);
            }, URL);
        };

        $scope.removeOwner = function (index) {
            var removeOwner = $scope.ownerList[index].username;
            var removeOwnerUrl = "api/groupings/" + $scope.groupingPath + "/" + $scope.getCurrentUsername() + "/" + removeOwner + "/removeOwnership";
            console.log(removeOwnerUrl);
            if ($scope.ownerList.length > 1) {
                dataDelete.deleteData(function (d) {
                    $scope.getData($scope.groupingPath);
                }, removeOwnerUrl);
            }
        };

        $scope.savePref = function () {
            var prefUrls = [];
            console.log($scope.preference.optIn);
            console.log($scope.preference.optOut);
            console.log($scope.preference.listserv);

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
                        if(typeof d[0] != 'undefined' && (d[0].resultCode === "SUCCESS_ALLOWED" || d[0].resultCode === "SUCCESS_NOT_ALLOWED" )) {
                            console.log("OptIn/OptOut preference successfully updated");
                            alert("OptIn/OptOut preference successfully updated");
                            success = 1;
                        }
                        else {
                            console.log("Preference did not change");
                            alert("Preference did not change");
                        }
                    }
                    if(success == 1)
                    {
                        $scope.getData($scope.groupingPath);
                    }
                }, prefUrls[i].url);
            }
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
            /*$scope.pagedItems=[];
             for(var i = 0; i < $scope.list.length ; i++){
             if(i % $scope.itemsPerPage === 0){
             $scope.pagedItems[Math.floor(i/$scope.itemsPerPage)] = [ $scope.list[i]];
             }else{
             $scope.pagedItems[Math.floor(i/$scope.itemsPerPage)].push( $scope.list[i]);
             }
             }*/
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

        $(function () {
            $('[data-toggle="tooltip"]').tooltip();
        })

    }

    adminApp.controller("AdminJsController", AdminJsController);
})();