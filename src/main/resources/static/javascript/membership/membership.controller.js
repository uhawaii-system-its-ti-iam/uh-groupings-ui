(function() {

    function MembershipJsController($scope, dataProvider) {
        var url = "groupingsIn?username=mhodges";
        $scope.list = [];
         $scope.totalItems = 3;
        $scope.currentPage = 1;
        $scope.init = function() {

            dataProvider.loadData(function(d) {
                //sorts the data by name

                $scope.list = d;

                // for (var i = 0; i < 100; i++){
                //   $scope.list.push("User " + i);
                // }
                var obj = JSON.stringify(d);
                var par = JSON.parse(obj);
                console.log(obj);
                console.log(par);
            }, url)
        };

    }
    membershipApp.controller("MembershipJsController", MembershipJsController);

})();
