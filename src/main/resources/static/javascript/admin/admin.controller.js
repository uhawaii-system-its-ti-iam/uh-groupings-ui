(function () {

    function AdminJsController($scope, dataProvider) {
        var currentUser = document.getElementById("name").innerText;
        var url = "getMembers?grouping=hawaii.edu:custom:test:aaronvil:aaronvil-test&username=" + currentUser;
        $scope.list = [];

        $scope.init = function () {

            dataProvider.loadData(function (d) {
                var temp;
                temp = d.basisPlusIncludeMinusExclude

                //sorts the data by name
                temp.sort(function (a, b) {
                    var nameA = a.name.toLowerCase(), nameB = b.name.toLowerCase()
                    if (nameA < nameB) //sort string ascending
                        return -1
                    if (nameA > nameB)
                        return 1
                    return 0
                })

                //Filters out names with hawaii.edu
                for (var i = 0; i < temp.length; i++) {
                    if (temp[i].name.includes("hawaii.edu")) {
                        temp.splice(i, 1);
                        i--;
                    }
                }

                //gets the username from the attributeValues array
                for (var i = 0; i < temp.length; i++) {
                    temp[i].attributeValues = _.pluck(_.pluck(temp, "attributeValues"), 0)[i];
                }

                $scope.list = temp;

                console.log($scope.list);
            }, url)
        };

        $scope.add = function () {
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
                else {
                    const meta = pluck[0][0].resultMetadata;

                    console.log(meta.resultCode);

                    if (meta.resultCode === 'SUCCESS') {
                        console.log("Successfully added " + $scope.username);
                        alert("Successfully added " + $scope.username);
                        $scope.init();
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

        $scope.remove = function (row) {
            var deleteUrl;
            var deleteUser = $scope.list[row].attributeValues;
            console.log(deleteUser);
            if ($scope.list.length > 1) {
                deleteUrl = "deleteMember?username=" + currentUser + "&userToDelete=" + deleteUser + "&grouping=hawaii.edu:custom:test:aaronvil:aaronvil-test";
                $.ajax({
                    url: deleteUrl,
                    method: 'GET',
                    success: function () {
                        console.log("Success In Deletion")
                        //reload data table
                        $scope.init();
                    },
                    error: function () {
                        console.log("Failed To Delete")
                    }
                });
                $scope.list.splice(row, 1);
            }
        }


    }

    adminApp.controller("AdminJsController", AdminJsController);

})();
