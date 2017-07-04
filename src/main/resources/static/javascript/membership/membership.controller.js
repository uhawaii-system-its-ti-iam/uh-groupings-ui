(function () {

    // BIG QUESTIONS: why is there so much output for the URL?

    /**Membership controller for the whole memberships page
     *@param $scope
     *    defining what is within the controller
     *@param dataProvider
     *     given the "loadData" function, it loads all the data to be viewed
     *@param dataUpdater
     *    Using the CRUD opperators this would be the update of CRUD
     **/
    function MembershipJsController($scope, $window, dataProvider, dataUpdater) {

        $scope.currentUsername = "";
        $scope.membersList = [];
        $scope.optInList = [];
        $scope.optOutList = [];
        $scope.optedIn = [];
        $scope.optedOut = [];
        $scope.loading = true;

        //these will be place holders for now
        $scope.pagedItems1=[];
        $scope.pagedItems2=[];
        $scope.pagedItems3=[];
        $scope.pagedItems4=[];
        $scope.pagedItems5=[];
        $scope.gap=5;

        $scope.itemsPerPage = 5;
        $scope.currentPageOptIn = 0;
        $scope.currentPageOptOut = 0;

        $scope.initCurrentUsername = function() {
            $scope.currentUsername = $window.document.getElementById("name").innerHTML;
        };

        $scope.getCurrentUsername = function() {
            return $scope.currentUsername;
        };

        /**init is something that is ussualy called at the start of something
         * so calling init would be called at the start
         **/
        $scope.init = function () {
            $scope.initCurrentUsername();
            var groupingURL = "api/groupings/" + $scope.getCurrentUsername() + "/myGroupings";
            /**Loads Data into a membersList
             *                  optOutList
             *                  optInList
             *                  optedIn
             *                  optedOut
             *takes all of that data and puts them into pages as called by "grouptToPages"
             **/
            dataProvider.loadData(function (d) {
                console.log(d);
                $scope.membersList = d.groupingsIn;
                $scope.optOutList = d.groupingsToOptOutOf;
                $scope.optInList = d.groupingsToOptInTo;
                $scope.optedIn = d.groupingsOptedInTo;
                $scope.optedOut = d.groupingsOptedOutOf;

                // $scope.pagedItems1 = $scope.groupToPages($scope.membersList);
                // $scope.pagedItems2 = $scope.groupToPages($scope.optOutList);
                $scope.pagedItems3 = $scope.groupToPages($scope.optInList, $scope.pagedItems3);
                // $scope.pagedItems4 = $scope.groupToPages($scope.optedIn);
                // $scope.pagedItems5 = $scope.groupToPages($scope.optedOut);
                if($scope.optedIn.length === 0)
                {
                    $scope.optedIn.push({'name': "NO GROUPINGS TO CANCEL OPT IN TO"});
                }
                if ($scope.optedOut.length === 0) {
                    $scope.optedOut.push({'name': "NO GROUPINGS TO CANCEL OPT OUT"});
                }
                if ($scope.optInList.length === 0) {
                    $scope.optInList.push({'name': "NO GROUPINGS TO OPT IN TO"});
                }

                $scope.loading = false;
            }, groupingURL);
            console.log("Finish dataProvider");
        };


        /** Adds user to the exclude group.
         * Sends back an alert saying if it failed
         * other than that, it will go through with opting out
         *
         **/
        $scope.optOut = function (index) {
            console.log(index);
            var optOutURL = "api/groupings/" + $scope.membersList[index].path + "/" + $scope.getCurrentUsername() + "/optOut";
            console.log(optOutURL);
            dataUpdater.updateData(function (d) {
                console.log(d);
                if (d[0].resultCode.includes("FAILURE")) {
                    console.log("Failed to opt out");
                    alert("Failed to opt out");
                }
                else {
                    $scope.loading = true;
                    $scope.init();
                }
            }, optOutURL);
        };

        /** Adds user to the include group
         * initializes using the init function.
         *@param grouping
         *takes in a grouping so it knows which group it is going into for the path
         **/
        $scope.optIn = function (index) {
            var optInURL = "api/groupings/" + $scope.optInList[index].path + "/" + $scope.getCurrentUsername() + "/optIn";
            console.log(optInURL);
            dataUpdater.updateData(function (d) {
                $scope.loading = true;
                $scope.init();
            }, optInURL);
        };

        /** Cancel user opt into a grouping
         *   Calls the URL "cancelOptIn" and gives it the data for the update in the
         *   CRUD operation
         *   @param grouping
         *   takes in a grouping so it knows which group it is going into for the path
         **/
        $scope.cancelOptIn = function (index) {
            var cancelInURL = "api/groupings/" + $scope.optedIn[index].path + "/" + $scope.getCurrentUsername() + "/cancelOptIn";
            console.log(cancelInURL);
            dataUpdater.updateData(function (d) {
                $scope.loading = true;
                $scope.init();
            }, cancelInURL);
        };

        /** Cancels the opt out
         * Calls the URL "cancelOptOut" and gives it the data for the update in the
         * CRUD operation
         *@param grouping
         *takes in a grouping so it knows which group it is going into for the path
         **/
        $scope.cancelOptOut = function (index) {
            var cancelOutURL = "api/groupings/" + $scope.optedOut[index].path + "/" + $scope.getCurrentUsername() + "/cancelOptOut";
            console.log(cancelOutURL);
            dataUpdater.updateData(function (d) {
                $scope.loading = true;
                $scope.init();
            }, cancelOutURL);
        };

        $scope.disableOptOut = function (index) {
            for (var i = 0; i < $scope.optOutList.length; i++) {
                if ($scope.membersList[index].name === $scope.optOutList[i].name) {
                    console.log($scope.optOutList[i].name);
                    return false;
                }
            }
            return true;
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
            return list.name.includes("NO GROUPINGS TO");
        };

        $scope.tooltipText = function (index) {
            return ($scope.disableOptOut(index)) ? 'You cannot opt out of this grouping' : '';
        };
        /**groups all the items to pages
           have sepperate arrays (hopefully)
           @param
        **/
        $scope.groupToPages=function(theList , pagedList){
            var pagedList = [];
            for(var i = 0; i < theList.length ; i++){
                if(i % $scope.itemsPerPage === 0){
                    pagedList[Math.floor(i/$scope.itemsPerPage)] = [ theList[i]];
                }else{
                    pagedList[Math.floor(i/$scope.itemsPerPage)].push( theList[i]);
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
                start = size - $scope.gap;
            }
            if (start < 0) {
                start = 0;
            }
            for (var i = start; i < end; i++) {
                ret.push(i);
            }
            return ret;
        };

        // Conceptually the next bunch of functions are the
        // same but with different names

        //THIS SECTION WOULD BE FOR THE OptIn SECTION

        /**if the current page is not 0, it will minus the current page by one
         *current page will never go negative
         **/
        $scope.prevPageOptIn = function () {
            if ($scope.currentPageOptIn > 0) {
                $scope.currentPageOptIn--;
            }
        };

        /**if the current page is less than the items in the array, it will
         *add one to current page
         **/
        $scope.nextPageOptIn = function () {
            if ($scope.currentPageOptIn < $scope.pagedItems.length - 1) {
                $scope.currentPageOptIn = $scope.currentPageOptIn + 1;
            }
        };

        //takes the clicked page and set that to the current page
        $scope.setPageOptIn = function () {
            $scope.currentPageOptIn = this.n;
        };

        //THIS SECTION WOULD BE FOR THE OptOut SECTION

        /**if the current page is not 0, it will minus the current page by one
         *current page will never go negative
         **/
        $scope.prevPageOptOut = function () {
            if ($scope.currentPageOptOut > 0) {
                $scope.currentPageOptOut--;
            }
        };

        /**if the current page is less than the items in the array, it will
         *add one to current page
         **/
        $scope.nextPageOptOut = function () {
            if ($scope.currentPageOptOut < $scope.pagedItems3.length - 1) {
                $scope.currentPageOptOut = $scope.currentPageOptOut + 1;
            }
        };
        //takes the clicked page and set that to the current page
        $scope.setPageOptOut = function () {
            $scope.currentPageOptOut = this.n;
        };
    }

    membershipApp.controller("MembershipJsController", MembershipJsController);
})();
