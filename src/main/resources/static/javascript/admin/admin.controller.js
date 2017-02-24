(function() {

    function AdminJsController($scope, dataProvider) {
        var url = "getMembers?grouping=hawaii.edu:custom:test:zknoebel:zknoebel-test&username=zknoebel";
        $scope.list = [];

        $scope.init = function() {

            dataProvider.loadData(function(d) {
                //sorts the data by name
                const sort = d.sort(function(a,b){
                    var nameA=a.name.toLowerCase(), nameB=b.name.toLowerCase()
                    if (nameA < nameB) //sort string ascending
                        return -1
                    if (nameA > nameB)
                        return 1
                    return 0
                })


                //_.without(d, d.name.includes("hawaii.edu"))

                console.log("name: " + d.name);

                //filters out names with hawaii.edu
                for(var i = 0; i < d.length; i++){
                    if(d[i].name.includes("hawaii.edu")){
                        console.log(i);
                        d.splice(i,1);
                        i--;
                    }
                }

                //console.log(_.pluck(_.pluck(d,"attributeValues"), 0)[0]);

                for(var i = 0; i < d.length; i++){
                    d[i].attributeValues = _.pluck(_.pluck(d,"attributeValues"), 0)[i];
                }

                $scope.list = d;
                console.log($scope.list);
            }, url)
        };

        $scope.add = function(){
            $scope.list.push({name:$scope.username, id: 25691470});
            console.log($scope.username);
            $scope.username = '';

            setTimeout(function() {
               location.reload();
            }, 5000);
        };

        $scope.remove = function(row) {
            console.log($scope.list[row].attributeValues)
            if($scope.list.length > 1) {
                $scope.list.splice(row, 1);
                console.log($scope.list.length)
                setTimeout(function () {
                    location.reload();
                }, 5000);
            }
        }



    }
    adminApp.controller("AdminJsController", AdminJsController);

})();
