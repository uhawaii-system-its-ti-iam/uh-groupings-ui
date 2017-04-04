(function () {

    function MembershipJsController($scope, dataProvider) {
        var currentUser = document.getElementById("name").innerText;
        var urlMember = "groupingsIn?username=" + currentUser;
        var urlOptIn = "groupingsToOptInto?username=" + currentUser;
        $scope.membersList = [];
        $scope.optInList = []

        $scope.init = function () {
            //Loads Data
            $scope.getMembership(urlMember);
            $scope.getOptIn(urlOptIn);
            console.log($scope.optInList);
        };

        $scope.getMembership = function (URL) {
            dataProvider.loadData(function (d) {
                $scope.membersList = d;
                console.log($scope.membersList);
            }, URL)
        };

        $scope.getOptIn = function (URL) {
            dataProvider.loadData(function (d) {
                $scope.optInList = _.difference(d, $scope.membersList);
                if($scope.optInList.length === 0)
                {
                    $scope.optInList = ["NO GROUPINGS TO OPT IN TO"]
                }
            }, URL);
        };

        $scope.optOut = function (grouping) {

            dataProvider.loadData(function (d) {
                $scope.init();
                $scope.membersList = d;
            console.log(optOutURL);
            console.log(grouping);
            var optOutURL = "optOut?username=" + currentUser + "&grouping=" + $scope.membersList[grouping];
        };

        $scope.optIn = function (grouping) {
            console.log(grouping);
            var optInURL = "optIn?username=" + currentUser + "&grouping=" + $scope.optInList[grouping];
            console.log(optInURL);
        };

        //Disables opt in button if there are no groupings to opt into.
        $scope.isDisabled = function(index) {
            var optIn = $scope.optInList[index];
            if(optIn === "NO GROUPINGS TO OPT IN TO")
            {
                return true;
            }
            else{
                return false;
            }
        }
    }

    membershipApp.controller("MembershipJsController", MembershipJsController);

})();
