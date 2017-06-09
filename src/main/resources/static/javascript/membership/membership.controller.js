(function () {

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
        $scope.itemsPerPage = 20;
        $scope.currentPage = 0;

        $scope.init = function () {
            //Loads Data
            dataProvider.loadData(function (d) {
                console.log(d);
                $scope.membersList = d.groupingsIn;
                $scope.optOutList = d.groupingsToOptOutOf;
                $scope.optInList = d.groupingsToOptInTo;
                $scope.optedIn = d.groupingsOptedInTo;
                $scope.optedOut = d.groupingsOptedOutOf;
                $scope.groupToPages();
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


        // Adds user to the exclude group.
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

        // Adds user to the include group
        $scope.optIn = function (index) {
            var optInURL = "api/groupings/" +  $scope.optInList[index].path + "/" + currentUser + "/optIn";
            console.log(optInURL);
            dataUpdater.updateData(function (d) {
                console.log(d);
                $scope.loading = true;
                $scope.init();
            }, optInURL);
        };

        // Cancel user opt into a grouping
        $scope.cancelOptIn = function (index) {
            var cancelInURL = "api/groupings/" + $scope.optedIn[index].path + "/" + currentUser + "/cancelOptIn";
            console.log(cancelInURL);
            dataUpdater.updateData(function (d) {
                console.log(d);
                $scope.loading = true;
                $scope.init();
            }, cancelInURL);
        };

        // Cancel user opt out of a grouping
        $scope.cancelOptOut = function (index) {
            var cancelOutURL = "api/groupings/" + $scope.optedOut[index].path + "/" + currentUser + "/cancelOptOut";
            console.log(cancelOutURL);
            dataUpdater.updateData(function (d) {
               console.log(d);
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

        //handles the groups of stuff on the pages.
        $scope.groupToPages=function(){
            $scope.pagedItems=[];
            for(var i = 0; i < $scope.membersList.length ; i++){
                if(i % $scope.itemsPerPage === 0){

                    $scope.pagedItems[Math.floor(i/$scope.itemsPerPage)] = [ $scope.membersList[i]];

                }else{
                    $scope.pagedItems[Math.floor(i/$scope.itemsPerPage)].push( $scope.membersList[i]);
                }
            }
        };

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

        $scope.prevPage = function () {
            if ($scope.currentPage > 0) {
                $scope.currentPage--;
            }
        };

        $scope.nextPage = function () {
            if ($scope.currentPage < $scope.pagedItems.length - 1) {
                $scope.currentPage = $scope.currentPage + 1;
            }
        };

        $scope.setPage = function () {
            $scope.currentPage = this.n;
        };
        $scope.set5 = function () {
            $scope.itemsPerPage  = 5;
            $scope.groupToPages();
        };
        $scope.set10 = function () {
            $scope.itemsPerPage  = 10;
            $scope.groupToPages();
        };
        $scope.set25 = function () {
            $scope.itemsPerPage  = 25;
            $scope.groupToPages();
        };
        $scope.set100 = function () {
            $scope.itemsPerPage  = 100;
            $scope.groupToPages();
        };


    }

    membershipApp.controller("MembershipJsController", MembershipJsController);

})();
