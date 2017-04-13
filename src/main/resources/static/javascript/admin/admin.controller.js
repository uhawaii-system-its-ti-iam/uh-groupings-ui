(function () {

    function AdminJsController($scope, dataProvider) {
        var currentUser = document.getElementById("name").innerText;
        var url = "grouping?grouping=hawaii.edu:custom:test:aaronvil:aaronvil-test&username=" + currentUser;
        $scope.list = [];
        $scope.loading = true;

        $scope.init = function () {

            setTimeout(function(){
                dataProvider.loadData(function (d) {
                    var temp;

                    console.log(d);
                    temp = d.basisPlusIncludeMinusExclude.members;

                    for(var k = 0; k < temp.length; k++)
                    {
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

                    for(var l = 0; l < basis.length; l++)
                    {
                        for(var m = 0; m < temp.length; m++)
                        {
                            if(basis[l].name === temp[m].name)
                            {
                                temp[m].basis = "\u2714";
                            }
                        }
                    }
                    $scope.list = temp;
                    $scope.loading=false;
                    console.log($scope.list);
                }, url)
            }, 2000);
        };

        $scope.add = function () {
            var addUrl;
            $scope.testdata = [];
            console.log($scope.username);
            addUrl = "addMemberToIncludeGroup?userToAdd=" + $scope.username + "&grouping=hawaii.edu:custom:test:aaronvil:aaronvil-test&username=" + currentUser;


            if(confirm("You are adding " + $scope.username + " to the include list of this grouping")){
                dataProvider.loadData(function (d) {
                    console.log(d);

                    var result = d.results;

                    if(result[0].wsSubject.resultCode === 'SUCCESS') {
                        console.log("Successfully added " + $scope.username);
                        alert("Successfully added " + $scope.username);
                        $scope.loading = true;
                        $scope.init();
                    }
                    else if(typeof d.results === 'undefined'){
                        console.log($scope.username + " this user does not exist.");
                        alert($scope.username + " this user does not exist.");
                    }

                    $scope.testdata = d;
                    $scope.username = '';
                }, addUrl);
            }
        };

        $scope.remove = function (row) {
            var deleteUrl;
            var deleteUser = $scope.list[row].username;
            console.log($scope.list[row]);
            console.log(deleteUser);
                deleteUrl = "deleteMemberFromIncludeGroup?username=" + currentUser + "&userToDelete=" + deleteUser + "&grouping=hawaii.edu:custom:test:aaronvil:aaronvil-test";
            if ($scope.list.length > 1) {
                $.ajax({
                    url: deleteUrl,
                    method: 'GET',
                    success: function () {
                        console.log("Success In Deletion")
                        //reload data table
                        $scope.list.splice(row, 1);
                        $scope.loading = true;
                        $scope.init();
                    },
                    error: function () {
                        console.log("Failed To Delete")
                    }
                });
            }
        }
    }
    adminApp.controller("AdminJsController", AdminJsController);
})();
