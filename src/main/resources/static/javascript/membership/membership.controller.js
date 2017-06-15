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
    function MembershipJsController($scope, dataProvider, dataUpdater) {
        var currentUser = document.getElementById("name").innerText;
        var groupingURL = "api/groupings/" + currentUser + "/myGroupings";
        $scope.membersList = [];
        $scope.optInList = [];
        $scope.optOutList = [];
        $scope.optedIn = [];
        $scope.optedOut = [];
        $scope.loading = true;

        $scope.pagedItems=[];
        $scope.gap=5;
        $scope.itemsPerPage = 5;
        $scope.currentPageOptIn = 0;
        $scope.currentPageOptOut = 0;

        /**init is something that is ussualy called at the start of something
         * so calling init would be called at the start
        **/
        $scope.init = function () {
            /**Loads Data into a membersList
            *                  optOutList
            *                  optInList
            *                  optedIn
            *                  optedOut
            *takes all of that data and puts them into pages as called by "grouptToPages"
            **/
            dataProvider.loadData(function (d) {
                $scope.membersList = d.groupingsIn;
                for(var i = 0 ; i < 27;i++){
                    $scope.membersList.push({name:"Group "+i});
                }
                $scope.optOutList = d.groupingsToOptOutOf;
                // for(var i = 0 ; i < 10;i++){
                //     $scope.optOutList.push({name:"Group "+i});
                // }
                $scope.optInList = d.groupingsToOptInTo;
                // for(var i = 0 ; i < 508;i++){
                //     $scope.optInList.push({name:"Group "+i});
                // }
                $scope.optedIn = d.groupingsOptedInTo;
                // for(var i = 0 ; i < 10;i++){
                //     $scope.optedIn.push({name:"Group "+i});
                // }
                $scope.optedOut = d.groupingsOptedOutOf;
                // for(var i = 0 ; i < 10;i++){
                //     $scope.optedOut.push({name:"Group "+i});
                // }
                $scope.grouptToPages();
                if($scope.optedIn.length === 0)
                {
                    $scope.optedIn.push({'name': "NO GROUPINGS TO CANCEL OPT IN TO"});
                }
                if($scope.optedOut.length === 0)
                {
                    $scope.optedOut.push({'name': "NO GROUPINGS TO CANCEL OPT OUT"});
                }
                if($scope.optInList.length === 0)
                {
                    $scope.optInList.push({'name': "NO GROUPINGS TO OPT IN TO"});
                }

                $scope.loading = false;
            }, groupingURL);
        };


        /** Adds user to the exclude group.
        * Sends back an alert saying if it failed
        * other than that, it will go through with opting out
        *
        **/
    @describe("CampusJsController", function() {
    beforeEach(module('campusApp'));

    var scope;
    var controller;
    var dataProvider;

    beforeEach(inject(function($rootScope, $controller, dataProvider) {
        scope = $rootScope.$new();
        controller = $controller('CampusJsController', {
            $scope: scope,
            dataProvider: dataProvider
        });
    }));

    it("checkInitFunction", function() {
        spyOn(scope, "loadData").and.callFake(function() {
            scope.campuses.push({
                "id": 1,
                "code": "HA",
                "description": "Hawaii Community College"
            });
            scope.campuses.push({
                "id": 10,
                "code": "WO",
                "description": "UH West Oahu"
            });
        });

        expect(controller).toBeDefined();
        expect(scope.campuses).toBeDefined();
        expect(scope.campuses.length).toEqual(0);

        // What we are testing:
        scope.init();

        expect(scope.loadData).toHaveBeenCalled();
        expect(scope.campuses).toBeDefined();
        expect(scope.campuses.length).toEqual(2);

        expect(scope.campuses[0].id).toEqual(1);
        expect(scope.campuses[0].code).toEqual("HA");
        expect(scope.campuses[0].description).toEqual("Hawaii Community College");

        expect(scope.campuses[1].id).toEqual(10);
        expect(scope.campuses[1].code).toEqual("WO");
        expect(scope.campuses[1].description).toEqual("UH West Oahu");
    });

});
        //    takes in a grouping so it knows which group it is going into for the path
        $scope.optOut = function (index) {
            console.log(index);
            var optOutURL = "api/groupings/" +  $scope.membersList[index].path + "/" + currentUser + "/optOut";
            console.log(optOutURL);
            dataUpdater.updateData(function (d) {
                console.log(d);
                if(d[0].resultCode.includes("FAILURE")){
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
            var optInURL = "api/groupings/" +  $scope.optInList[index].path + "/" + currentUser + "/optIn";
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
            var cancelInURL = "api/groupings/" + $scope.optedIn[index].path + "/" + currentUser + "/cancelOptIn";
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
            var cancelOutURL = "api/groupings/" + $scope.optedOut[index].path + "/" + currentUser + "/cancelOptOut";
            console.log(cancelOutURL);
            dataUpdater.updateData(function (d) {
                $scope.loading = true;
                $scope.init();
            }, cancelOutURL);
        };

        $scope.disableOptOut = function(index) {
            for(var i = 0; i < $scope.optOutList.length; i++) {
                if($scope.membersList[index].name === $scope.optOutList[i].name)
                {
                    console.log($scope.optOutList[i].name);
                    return false;
                }
            }
            return true;
        };

        //Disables opt in button if there are no groupings to opt into.
        $scope.disableOptIn = function(index) {
            for(var i = 0; i < $scope.membersList.length; i++) {
                if($scope.membersList[i].name === $scope.optInList[index].name)
                {
                    return true;
                }
            }
        };

        //Disable button if list is empty
        $scope.disableButton = function(type, index) {
            var list = type[index];
            return list.name.includes("NO GROUPINGS TO");
        };

        $scope.tooltipText = function(index) {
            return ($scope.disableOptOut(index)) ? 'You cannot opt out of this grouping' : '';
        };
        //groups all the items to pages
        //have sepperate arrays (hopefully)
        //no param
        $scope.grouptToPages=function(){
            $scope.pagedItems=[];
            for(var i = 0; i < $scope.membersList.length ; i++){
                if(i % $scope.itemsPerPage === 0){
                    $scope.pagedItems[Math.floor(i/$scope.itemsPerPage)] = [ $scope.membersList[i]];
                }else{
                    $scope.pagedItems[Math.floor(i/$scope.itemsPerPage)].push( $scope.membersList[i]);
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

        // Conceptually the next bunch of functions are the
        // same but with different names

        //THIS SECTION WOULD BE FOR THE OptIn SECTION

        /**if the current page is not 0, it will minus the current page by one
        *current page will never go negative
        **/
        $scope.prevPage = function () {
            if ($scope.currentPageOptIn > 0) {
                $scope.currentPageOptIn--;
            }
        };

        /**if the current page is less than the items in the array, it will
        *add one to current page
        **/
        $scope.nextPage = function () {
            if ($scope.currentPageOptIn < $scope.pagedItems.length - 1) {
                $scope.currentPageOptIn = $scope.currentPageOptIn +1;
            }
        };

        //takes the clicked page and set that to the current page
        $scope.setPage = function () {
            $scope.currentPageOptIn = this.n;
        };

        //THIS SECTION WOULD BE FOR THE OptOut SECTION

        /**if the current page is not 0, it will minus the current page by one
        *current page will never go negative
        **/
        $scope.prevPageBot = function () {
            if ($scope.currentPageOptOut > 0) {
                $scope.currentPageOptOut--;
            }
        };

        /**if the current page is less than the items in the array, it will
        *add one to current page
        **/
        $scope.nextPageBot = function () {
            if ($scope.currentPageOptOut < $scope.pagedItems.length - 1) {
                $scope.currentPageOptOut = $scope.currentPageOptOut +1;
            }
        };
        //takes the clicked page and set that to the current page
        $scope.setPageBot = function () {
            $scope.currentPageOptOut = this.n;
        };

        /**code that will not be used
        * $scope.set5 = function () {
        *     $scope.itemsPerPage  = 5;
        *     $scope.grouptToPages();
        * };
        * $scope.set10 = function () {
        *     $scope.itemsPerPage  = 10;
        *     $scope.grouptToPages();
        * };
        * $scope.set25 = function () {
        *     $scope.itemsPerPage  = 25;
        *     $scope.grouptToPages();
        * };
        * $scope.set100 = function () {
        *     $scope.itemsPerPage  = 100;
        *     $scope.grouptToPages();
        **/ };

    }

    membershipApp.controller("MembershipJsController", MembershipJsController);

})();
