(function() {

    function AdminJsController($scope, dataProvider) {
        var currentUser = document.getElementById("name").innerText;
        var url = "getMembers?grouping=hawaii.edu:custom:test:ksanidad:ksanidad-test&username=" + currentUser;
        $scope.list = [];

        $scope.init = function() {

            dataProvider.loadData(function(d) {
                //sorts the data by name
                d.sort(function(a,b){
                    var nameA=a.name.toLowerCase(), nameB=b.name.toLowerCase()
                    if (nameA < nameB) //sort string ascending
                        return -1
                    if (nameA > nameB)
                        return 1
                    return 0
                })

                for(var i = 0; i < d.length; i++){
                    d[i].attributeValues = _.pluck(_.pluck(d,"attributeValues"), 0)[i];
                }

                $scope.list = d;
            }, url)
        };

        $scope.add = function(){
            var addUrl;
            //$scope.list.push({name:$scope.username, id: 25691470});
            console.log($scope.username);
            addUrl = "addMember?userToAdd=" + $scope.username + "&grouping=hawaii.edu:custom:test:ksanidad:ksanidad-test&username=" + currentUser;

            $.ajax({
                url: addUrl,
                method: 'GET',
                success:function(){
                    console.log("Success In Adding");
                    //reload data table
                    $scope.init();
                },
                error: function(){
                    console.log("Failed To add")
                }
            });
            $scope.username = '';
        };

        $scope.remove = function(row) {
            var deleteUrl;
            var deleteUser = $scope.list[row].attributeValues;
            console.log(deleteUser);
            if($scope.list.length > 1) {
                deleteUrl = "deleteMember?username=" + currentUser + "&userToDelete=" + deleteUser + "&grouping=hawaii.edu:custom:test:ksanidad:ksanidad-test";
                $.ajax({
                    url: deleteUrl,
                    method: 'GET',
                    success:function(){
                        console.log("Success In Deletion")
                        //reload data table
                        $scope.init();
                    },
                    error: function(){
                        console.log("Failed To Delete")
                    }
                });
                $scope.list.splice(row, 1);
            }
        }



    }
    adminApp.controller("AdminJsController", AdminJsController);

})();
