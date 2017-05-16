(function () {

    function MembershipJsController($scope, dataProvider) {
        var currentUser = document.getElementById("name").innerText;
        var groupings = "api/groupings/" + currentUser + "/myGroupings";
        $scope.membersList = [];
        $scope.optInList = [];
        $scope.optOutList = [];
        $scope.optedIn = [];
        $scope.optedOut = [];
        $scope.loading = true;

        $scope.totalItems = 12;
        $scope.currentPage = 1;

        $scope.init = function () {
            //Loads Data
            dataProvider.loadData(function (d) {
                console.log(d);
                $scope.membersList = d.groupingsIn;
                $scope.optOutList = d.groupingsToOptOutOf;
                $scope.optInList = d.groupingsToOptInTo;
                $scope.optedIn = d.groupingsOptedInTo;
                $scope.optedOut = d.groupingsOptedOutOf;
                console.log($scope.optedIn.length);
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
                console.log($scope.optedIn);
                console.log($scope.optedOut);
            }, groupings);
        };


        // Adds user to the exclude group.
        $scope.optOut = function (grouping) {
            var optOutURL = "api/groupings/" +  $scope.membersList[grouping].path + "/" + currentUser + "/optOut";
            console.log(optOutURL);
            /*dataProvider.loadData(function (d) {
                console.log(d);
                $scope.membersList = d;
                $scope.loading = true;
                $scope.init();
            }, optOutURL)*/
        };

        // Adds user to the include group
        $scope.optIn = function (grouping) {
            var optInURL = "api/groupings/" +  $scope.optInList[grouping].path + "/" + currentUser + "/optIn";
            console.log(optInURL);

            /*dataProvider.loadData(function (d) {
                console.log(d);
                $scope.membersList = d;
                $scope.loading = true;
                $scope.init();
            }, optInURL)*/
        };

        //Disables opt in button if there are no groupings to opt into.
        $scope.disabledOptIn = function(index) {
            var optIn = $scope.optInList[index];
            return optIn.name === "NO GROUPINGS TO OPT IN TO"
        };

        /*$scope.disableOptOut = function(index) {
            var i = $scope.membersList;
            var j =  $scope.optOutList;

            for(var k = 0; k < j.length; k++)
            {
                for(var l = 0; l < i.length; l++)
                {
                    if (i[l].name === j[k].name){
                        console.log(i[l]);
                    }
                }
            }
        };*/
    }

    membershipApp.controller("MembershipJsController", MembershipJsController);

})();
