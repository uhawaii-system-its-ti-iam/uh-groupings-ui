(function () {


    //Membership controller for the whole memberships page
    //@perams $scope
    //    defining what is within the controller
    //@perams dataProvider
    //     given the "loadData" function, it loads all the data to be viewed
    //@perams dataUpdater
    //    Using the CRUD opperators this would be the update of CRUD
    //
    function MembershipJsController($scope, dataProvider, dataUpdater) {
        var currentUser = document.getElementById("name").innerText;
        var groupings = "api/groupings/" + currentUser + "/myGroupings";
        $scope.membersList = [];
        $scope.optInList = [];
        $scope.optOutList = [];
        $scope.optedIn = [];
        $scope.optedOut = [];
        $scope.loading = true;

        $scope.pagedItems=[];
        $scope.gap=5;
        $scope.itemsPerPage = 5;
        $scope.currentPageTop = 0;
        $scope.currentPageBot = 0;

        //init is something that is ussualy called at the start of something
        //so calling init would be called at the start
        $scope.init = function () {
            //Loads Data into a membersList
            //                  optOutList
            //                  optInList
            //                  optedIn
            //                  optedOut
            //takes all of that data and puts them into pages as called by "grouptToPages"
            //
            dataProvider.loadData(function (d) {
                console.log(d);
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
                    $scope.optedIn.push({'name': "NO GROUPINGS TO OPT IN TO"});
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
            }, groupings);
        };


        // TODO create documentation
        // Adds user to the exclude group.
        $scope.optOut = function (grouping) {
            var optOutURL = "api/groupings/" +  $scope.membersList[grouping].path + "/" + currentUser + "/optOut";
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

        //TODO create documention
        // Adds user to the include group
        $scope.optIn = function (grouping) {
            var optInURL = "api/groupings/" +  $scope.optInList[grouping].path + "/" + currentUser + "/optIn";
            console.log(optInURL);
            dataUpdater.updateData(function (d) {
                console.log(d);
                $scope.loading = true;
                $scope.init();
            }, optInURL);
        };

        // TODO create documention
        $scope.cancelOptIn = function (grouping) {
            var cancelInURL = "api/groupings/" + $scope.optedIn[grouping].path + "/" + currentUser + "/cancelOptIn";
            console.log(cancelInURL);
            dataUpdater.updateData(function (d) {
                console.log(d);
                $scope.loading = true;
                $scope.init();
            }, cancelInURL);
        };

        // TODO create documentation
        $scope.cancelOptOut = function (grouping) {
            var cancelOutURL = "api/groupings/" + $scope.optedOut[grouping].path + "/" + currentUser + "/cancelOptOut";
            console.log(cancelOutURL);
            dataUpdater.updateData(function (d) {
               console.log(d);
                $scope.loading = true;
                $scope.init();
            }, cancelOutURL);
        };

        //Disables opt in button if there are no groupings to opt into.
        $scope.disabledOptIn = function(index) {
            var optIn = $scope.optInList[index];
            return optIn.name === "NO GROUPINGS TO OPT IN TO"
        };


        //handes the groups of stuff on the pages.
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

        // Perams size
        // Perams
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

        // Original Code make it to Functionality than location
        $scope.prevPage = function () {
            if ($scope.currentPageTop > 0) {
                $scope.currentPageTop--;
            }
        };

        $scope.nextPage = function () {
            if ($scope.currentPageTop < $scope.pagedItems.length - 1) {
                $scope.currentPageTop = $scope.currentPageTop +1;
            }
        };

        $scope.setPage = function () {
            $scope.currentPageTop = this.n;
        };
        //

        // New variable change
        $scope.prevPageBot = function () {
            if ($scope.currentPageBot > 0) {
                $scope.currentPageBot--;
            }
        };

        $scope.nextPageBot = function () {
            if ($scope.currentPageBot < $scope.pagedItems.length - 1) {
                $scope.currentPageBot = $scope.currentPageBot +1;
            }
        };

        $scope.setPageBot = function () {
            $scope.currentPageBot = this.n;
        };
        //

        $scope.set5 = function () {
            $scope.itemsPerPage  = 5;
            $scope.grouptToPages();
        };
        $scope.set10 = function () {
            $scope.itemsPerPage  = 10;
            $scope.grouptToPages();
        };
        $scope.set25 = function () {
            $scope.itemsPerPage  = 25;
            $scope.grouptToPages();
        };
        $scope.set100 = function () {
            $scope.itemsPerPage  = 100;
            $scope.grouptToPages();
        };

        $scope.disableOptOut = function(index) {
            for(var i = 0; i < $scope.optOutList.length; i++) {
                if($scope.membersList[index].name != $scope.optOutList[i].name)
                {
                    $('.disabled').attr('title','You cannot opt out of this grouping');
                    return true;
                }
            }
        };

        $scope.tooltipText = function(index) {
            return $scope.disableOptOut(index) ? 'You cannot opt out of this grouping' : '';
        };
    }

    membershipApp.controller("MembershipJsController", MembershipJsController);

})();
