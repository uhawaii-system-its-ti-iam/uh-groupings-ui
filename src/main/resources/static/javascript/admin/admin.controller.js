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
    function AdminJsController($scope, $filter ,$window, dataProvider, dataUpdater, dataDelete) {

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
        $scope.gap = 2;
        $scope.itemsPerPage = 10;
        $scope.currentPage = 0;
        $scope.currentPageOptOut = 0;

        $scope.initCurrentUsername = function () {
            $scope.currentUsername = $window.document.getElementById("name").innerHTML;
        };

        $scope.getCurrentUsername = function () {
            return $scope.currentUsername;
        };

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

            var url = "api/groupings/tmp:win-many/" + $scope.getCurrentUsername() + "/grouping";

            dataProvider.loadData(function (d) {
                console.log(d);
                $scope.list = d.basis.members;

                $scope.modify($scope.list);

                console.log($scope.list);
                $scope.groupToPages();
                $scope.getGroupings();
            }, url);
        };

        $scope.search = function () {
            console.log($scope.groupingName);
            var groupingDataUrl = "api/groupings/" + $scope.groupingName + "/" + $scope.getCurrentUsername() + "/grouping";
            console.log(groupingDataUrl);
            dataProvider.loadData(function (d) {
                if (typeof d.path === 'undefined') {
                    console.log("Not a valid grouping");
                    $scope.error = true;
                    //Empties array
                    $scope.basis = [];
                    $scope.groupingsList = [];
                    $scope.groupingsBasis = [];
                    $scope.groupingInclude = [];
                    $scope.groupingExclude = [];
                    $scope.ownerList = [];
                } else {
                    $scope.error = false;
                    $scope.basis = d.basis.members;
                    //Gets members in grouping
                    $scope.groupingsList = d.composite.members;
                    $scope.modify($scope.groupingsList);

                    //Gets members in the basis group
                    $scope.groupingsBasis = d.basis.members;
                    $scope.modify($scope.groupingsBasis);

                    //Gets members in the include group
                    $scope.groupingInclude = d.include.members;
                    $scope.modify($scope.groupingInclude);

                    //Gets members in the exclude group
                    $scope.groupingExclude = d.exclude.members;
                    $scope.modify($scope.groupingExclude);

                    //Gets owners of the grouping
                    $scope.ownerList = d.owners.members;
                    $scope.modify($scope.ownerList);

                    $scope.pref = d.listservOn;
                    $scope.allowOptIn = d.optInOn;
                    $scope.allowOptOut = d.optOutOn;

                    if ($scope.pref == true) {
                        $('#listserv').prop("checked", true);
                    }
                    else {
                        $('#listserv').prop("checked", false);
                    }
                    if ($scope.allowOptIn == true) {
                        $('#optInOption').prop("checked", true);
                    }
                    else {
                        $('#optInOption').prop("checked", false);
                    }
                    if ($scope.allowOptOut == true) {
                        $('#optOutOption').prop("checked", true);
                    }
                    else {
                        $('#optOutOption').prop("checked", false);
                    }
                    //Stop loading spinner
                    $scope.title = $scope.groupingName;
                    $scope.loading = false;
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

        $scope.getGroupings = function() {
            var groupingUrl =  "api/groupings/" + $scope.getCurrentUsername() + "/myGroupings";
            dataProvider.loadData(function (d) {
                $scope.groupingList = d.groupingsOwned;
                $scope.loading = false;
            }, groupingUrl);

        };

        /**
         * Adds function that adds an member to the admin grouping.
         * Uses dataUpdater service to post the user that is being added.
         * If user is successfully added, dataProvider will return a Success in result Code.
         * Else if resultCode is undefined, then user was not successfully added.
         */
        $scope.add = function () {
            var addUrl = "api/groupings/hawaii.edu:custom:test:aaronvil:aaronvil-test/" + $scope.getCurrentUsername() + "/" + $scope.username + "/addMemberToIncludeGroup";
            $scope.testdata = [];

            if (confirm("You are adding " + $scope.username + " to the include list of this grouping")) {
                dataUpdater.updateData(function (d) {
                    console.log(d);
                    if (d.resultCode === 'SUCCESS') {
                        console.log("Success In Adding");
                        //reload data table
                        $scope.loading = true;
                        $scope.init();
                    }
                    else
                    if (typeof d.resultCode === 'undefined') {
                        console.log("Failure In Adding");
                    }
                }, addUrl);
            }
        };

        /**
         * Remove function uses dataDelete Service to remove user from admin grouping.
         * Will not delete admin if there is only one admin in the list.
         *
         * @param index - the index of the user based on the html table.
         */
        $scope.remove = function (index) {
            var deleteUser = $scope.list[index].username;
            var deleteUrl = "api/groupings/hawaii.edu:custom:test:aaronvil:aaronvil-test/" + $scope.getCurrentUsername() + "/" + deleteUser + "/deleteMemberFromIncludeGroup";
            console.log(deleteUrl);
            if ($scope.list.length > 1) {
                dataDelete.deleteData(function (d) {
                    $scope.list.splice(index, 1);
                    $scope.init();
                }, deleteUrl);
            }
        };

        $scope.groupToPages=function(){
            $scope.pagedItems=[];
            for(var i = 0; i < $scope.list.length ; i++){
                if(i % $scope.itemsPerPage === 0){
                    $scope.pagedItems[Math.floor(i/$scope.itemsPerPage)] = [ $scope.list[i]];
                }else{
                    $scope.pagedItems[Math.floor(i/$scope.itemsPerPage)].push( $scope.list[i]);
                }
            }
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
        $scope.range = function (size,start, end) {
            var ret = [];
            if (size < end) {
                end = size;
                //start = size-$scope.gap;
            }
            if(start < 0 ){
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
                        $scope.currentPage = $scope.currentPage +1;
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
        $('input[list]').on('input', function(e) {
            var input = $(e.target),
                datalist = input.attr('data-list');

            if(input.val().length < 3) {
                input.attr('list', '');
            } else {
                input.attr('list', datalist);
            }
        });
    }

    adminApp.controller("AdminJsController", AdminJsController);
})();