(function() {

    function MembershipJsController($scope, dataProvider) {
        var url = "groupingsIn?username=mhodges";
        $scope.list = [];

        $scope.init = function() {

            dataProvider.loadData(function(d) {
                //sorts the data by name

                $scope.list = d;

                for (var i = 0; i < 10; i++){
                  $scope.list.push("User " + i);
                }
                var obj = JSON.stringify(d);
                var par = JSON.parse(obj);
                console.log(obj);
                console.log(par);
            }, url)
        };

        $(function(){
          $('#memTab a:first').tab('show')
        })

    }
    membershipApp.controller("MembershipJsController", MembershipJsController);

})();
