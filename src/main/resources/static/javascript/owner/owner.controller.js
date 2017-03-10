(function() {

    function OwnerJsController($scope, dataProvider) {
        var currentUser = document.getElementById("name").innerText;
        var groupingsOwned = "groupingsOwned?username=_api_groupings";//+ currentUser;

        $scope.ownedList = [];
        $scope.groupingsList = [];
        $scope.ownerList = [];
        $scope.showList = false;
        $scope.groupingName = '';

        $scope.init = function() {
            dataProvider.loadData(function(d) {
                $scope.ownedList = d;
            }, groupingsOwned)

        };
        console.log($scope.showList);

        $scope.showData = function(row) {
            $scope.groupingName = $scope.ownedList[row];
            var getUrl = "getMembers?grouping=" + $scope.groupingName + "&username=_api_groupings" //+ currentUser;
            var getOwner = "getOwners?grouping=" + $scope.groupingName + "&username=_api_groupings";


            if($scope.showList == false){
                $scope.showList = true;
                //Gets list of Members
                dataProvider.loadData(function(d) {
                    d.sort(function (a, b) {
                        var nameA = a.name.toLowerCase(), nameB = b.name.toLowerCase()
                        if (nameA < nameB) //sort string ascending
                            return -1
                        if (nameA > nameB)
                            return 1
                        return 0
                    })

                    for (var i = 0; i < d.length; i++) {
                        d[i].attributeValues = _.pluck(_.pluck(d, "attributeValues"), 0)[i];
                    }
                    $scope.groupingsList = d;
                    console.log($scope.groupingsList);
                }, getUrl)

                //Gets List of Owners
                dataProvider.loadData(function(d) {
                    $scope.ownerList = d;
                }, getOwner)
            }
            else {
                $scope.showList = false;
            }
        }

        $scope.showGroups = function() {
            if($scope.showList == false){
                $scope.showList = true;
            }
            else {
                $scope.showList = false;
            }
        }

    }
    ownerApp.controller("OwnerJsController", OwnerJsController);
})();