(function () {
    function AdminJsController($scope, dataProvider, dataUpdater, dataDelete) {
        var currentUser = document.getElementById("name").innerText;
        var url = "api/groupings/hawaii.edu:custom:test:aaronvil:aaronvil-test/" + currentUser + "/grouping";
        $scope.list = [];
        $scope.loading = true;

        $scope.init = function () {
            setTimeout(function () {
                dataProvider.loadData(function (d) {
                    var temp = d.basisPlusIncludeMinusExclude.members;

                    for (var k = 0; k < temp.length; k++) {
                        temp[k].basis = "\u2716";
                    }

                    //sorts the data by name
                    temp.sort(function (a, b) {
                        var nameA = a.name.toLowerCase(), nameB = b.name.toLowerCase();
                        if (nameA < nameB) //sort string ascending
                            return -1;
                        if (nameA > nameB)
                            return 1;
                        return 0
                    });
                    var basis = d.basis.members;

                    for (var l = 0; l < basis.length; l++) {
                        for (var m = 0; m < temp.length; m++) {
                            if (basis[l].name === temp[m].name) {
                                temp[m].basis = "\u2714";
                            }
                        }
                    }
                    $scope.list = temp;
                    $scope.loading = false;
                    console.log($scope.list);
                }, url)
            }, 2000);
        };

        $scope.add = function () {
            var addUrl  = "api/groupings/hawaii.edu:custom:test:aaronvil:aaronvil-test/" + currentUser + "/" + $scope.username + "/addMemberToIncludeGroup";
            $scope.testdata = [];

            if (confirm("You are adding " + $scope.username + " to the include list of this grouping")) {
                dataUpdater.updateData(function (d) {
                    console.log(d);
                    if (d.resultCode === 'SUCCESS') {
                        console.log("Success In Adding");
                        //reload data table
                        $scope.loading = true;
                        $scope.init();
                    }
                    else if (typeof d.resultCode === 'undefined') {
                        console.log("Failure In Adding");
                    }
                }, addUrl);
            }
        };

        $scope.remove = function (row) {
            var deleteUser = $scope.list[row].username;
            var deleteUrl = "api/groupings/hawaii.edu:custom:test:aaronvil:aaronvil-test/" + currentUser + "/" + deleteUser + "/deleteMemberFromIncludeGroup";
            console.log(deleteUser);
            if ($scope.list.length > 1) {
                dataDelete.deleteData(function(d) {
                    $scope.list.splice(row, 1);
                    $scope.init();
                }, deleteUrl);
            }
        }
    }

    adminApp.controller("AdminJsController", AdminJsController);
})();
