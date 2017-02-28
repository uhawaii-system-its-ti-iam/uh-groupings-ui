(function() {

    function MembershipJsController($scope, dataProvider) {
        var url = "groupingsIn?username=mhodges";
        $scope.list = [];

        $scope.init = function() {

            dataProvider.loadData(function(d) {
                //sorts the data by name


                $scope.list = d;
                console.log($scope.list);
            }, url)
        };
    }
    membershipApp.controller("MembershipJsController", MembershipJsController);

})();