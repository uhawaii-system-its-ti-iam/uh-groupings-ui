(function() {

    function OwnerJsController($scope, dataProvider) {
        var currentUser = document.getElementById("name").innerText;
        var groupingsOwned = "groupingsOwned?username=_api_groupings";//+ currentUser;
        var getUrl

        $scope.ownedList = [];
        $scope.groupingsList = [];
        $scope.ownerList = [];
        $scope.showList = false;
        $scope.groupingName = '';

        $scope.init = function() {
            dataProvider.loadData(function(d) {
                $scope.ownedList = d;
                console.log($scope.ownedList);
            }, groupingsOwned)

        };
        console.log($scope.showList);

        $scope.showData = function(row) {
            $scope.groupingName = $scope.ownedList[row];
            getUrl = "getMembers?grouping=" + $scope.groupingName + "&username=_api_groupings" //+ currentUser;
            var ownerUrl = "getOwners?grouping=" + $scope.groupingName + "&username=_api_groupings";


            if($scope.showList == false){
                $scope.showList = true;
                //Gets list of Members
                $scope.getMembers(getUrl);

                //Gets List of Owners
                //$scope.getOwners(ownerUrl);
                dataProvider.loadData(function(d) {
                    for (var i = 0; i < d.length; i++) {
                        d[i].attributeValues = _.pluck(_.pluck(d, "attributeValues"), 0)[i];
                    }
                    $scope.ownerList = d;
                }, ownerUrl)
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

        $scope.getMembers = function(URL) {

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
            }, URL)
        }

        $scope.addMember = function () {
            var addUrl;
            $scope.testdata = [];
            console.log($scope.username);
            addUrl = "addMember?userToAdd=" + $scope.username + "&grouping=hawaii.edu:custom:test:aaronvil:aaronvil-test&username=" + currentUser;


            dataProvider.loadData(function (d) {
                console.log(d);
                const pluck = _.pluck(d, "results");

                console.log(pluck);
                console.log(pluck[0]);
                if (typeof pluck[0] === 'undefined') {
                    console.log($scope.username + " this user does not exist.");
                    alert($scope.username + " this user does not exist.");
                }
                else
                {
                    const meta = pluck[0][0].resultMetadata;

                    console.log(meta.resultCode);

                    if (meta.resultCode === 'SUCCESS') {
                        console.log("Successfully added " + $scope.username);
                        alert("Successfully added " + $scope.username);
                        $scope.getMembers(getUrl);
                    }
                    else if (meta.resultCode === 'SUCCESS_ALREADY_EXISTED') {
                        console.log($scope.username + " already exists in this groupings.");
                        alert($scope.username + " already exists in this groupings.");
                    }
                }

                $scope.testdata = d;
            }, addUrl)

            $scope.username = '';
        };

        $scope.getOwners = function(URL) {

        }

    }
    ownerApp.controller("OwnerJsController", OwnerJsController);
})();