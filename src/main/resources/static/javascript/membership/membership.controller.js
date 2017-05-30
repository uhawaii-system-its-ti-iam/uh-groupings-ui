(function () {

    function MembershipJsController($scope, dataProvider, dataUpdater) {
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

        $scope.cancelOptIn = function (grouping) {
            var cancelInURL = "api/groupings/" + $scope.optedIn[grouping].path + "/" + currentUser + "/cancelOptIn";
            console.log(cancelInURL);
            dataUpdater.updateData(function (d) {
                console.log(d);
                $scope.loading = true;
                $scope.init();
            }, cancelInURL);
        };

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
