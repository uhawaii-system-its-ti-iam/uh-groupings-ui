(function() {

    function MembershipJsController($scope, dataProvider) {
        var url = "groupingsIn?username=mhodges";
        $scope.list = [];

        $scope.init = function() {

            dataProvider.loadData(function(d) {
                //sorts the data by name


                $scope.list = d;
                var obj = JSON.stringify(d);
                var par = JSON.parse(obj);
                console.log(obj);
                console.log(par);
            }, url)
        };
    }
    membershipApp.controller("MembershipJsController", MembershipJsController);

})();
