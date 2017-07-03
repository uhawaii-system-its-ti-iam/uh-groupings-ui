(function () {
    /**
     * Admin controller for the entire admin page.
     *
     * @param $scope        : Binding variable between controller and html page.
     * @param dataProvider  : service function that acts as the AJAX get.
     * @param dataUpdater   : service function that acts as AJAX post, used mainly for adding or updating
     * @param dataDelete    : service function that acts as AJAX psst, use function mainly for delete function.
     * @constructor
     */
    function AdminJsController($scope, $window, dataProvider, dataUpdater, dataDelete) {

        $scope.currentUsername = "";
        $scope.list = [];

        //Variables for pagination
        $scope.pagedItems = [];
        $scope.gap = 5;
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
                var tempList = d.basisPlusIncludeMinusExclude.members;

                // Sorts the data by name.
                tempList.sort(function (a, b) {
                    var nameA = a.name.toLowerCase(), nameB = b.name.toLowerCase();
                    if (nameA < nameB) //sort string ascending
                        return -1;
                    if (nameA > nameB)
                        return 1;
                    return 0
                });

                var basis = d.basis.members;

                // Default add everyone as not in basis.
                for (var k = 0; k < tempList.length; k++) {
                    tempList[k].basis = "\u2716";
                }

                // Adds whether or not a member is in the basis group or not.
                for (var l = 0; l < basis.length; l++) {
                    for (var m = 0; m < tempList.length; m++) {
                        if (basis[l].name === tempList[m].name) {
                            tempList[m].basis = "\u2714";
                        }
                    }
                }

                $scope.list = tempList;
                $scope.groupToPages();
                $scope.loading = false;
            }, url);
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
         * @param index, the index of the user based on the html table.
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
         * @param end
         *  all the param are self explanitory
         * @return ret
         *     everything within the range of start,
         *       end, and making sure it's that size
         **/
        $scope.range = function (size,start, end) {
            var ret = [];
            if (size < end) {
                end = size;
                start = size-$scope.gap;
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
         * @param page, the page moving to.
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
    }

    adminApp.controller("AdminJsController", AdminJsController);
})();