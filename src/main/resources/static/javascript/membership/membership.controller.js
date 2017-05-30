(function () {

    function MembershipJsController($scope, dataProvider) {
        var currentUser = document.getElementById("name").innerText;
        var groupings = "api/groupings/" + currentUser + "/myGroupings";
        $scope.membersList = [];
        $scope.optInList = [];
        $scope.loading = true;

        $scope.pagedItems=[];
        $scope.gap=5;
        $scope.itemsPerPage = 5;
        $scope.currentPage = 0;

        $scope.init = function () {
            //Loads Data
            dataProvider.loadData(function (d) {
                $scope.membersList = d.groupingsIn;
                for(var i = 0 ; i < 10;i++){
                    $scope.membersList.push({name:"Group "+i});
                }
                $scope.optOutList = d.groupingsToOptOutOf;
                $scope.optInList = d.groupingsToOptInTo;
                $scope.grouptToPages();
                if($scope.optInList.length === 0)
                {
                    $scope.optInList.push({'name': "NO GROUPINGS TO OPT IN TO"});
                }
                $scope.loading = false;
                // console.log($scope.optOutList);
            }, groupings);
        };


        // Adds user to the exclude group.
        $scope.optOut = function (grouping) {
            // console.log(grouping);
            var optOutURL = "optOut?username=" + currentUser + "&grouping=" + $scope.membersList[grouping].path;
            var cancelOptIn = "cancelOptIn?username=" + currentUser + "&grouping=" + $scope.membersList[grouping].path;
            // console.log(optOutURL);

            dataProvider.loadData(function (d) {
                // console.log(d);
                $scope.membersList = d;
                $scope.loading = true;
                $scope.init();
            }, optOutURL)
        };

        // Adds user to the include group
        $scope.optIn = function (grouping) {
            // console.log(grouping);
            var optInURL = "optIn?username=" + currentUser + "&grouping=" + $scope.optInList[grouping].path;
            // console.log(optInURL);

            dataProvider.loadData(function (d) {
                // console.log(d);
                $scope.membersList = d;
                $scope.loading = true;
                $scope.init();
            }, optInURL)
        };

        //Disables opt in button if there are no groupings to opt into.
        $scope.disabledOptIn = function(index) {
            var optIn = $scope.optInList[index];
            return optIn.name === "NO GROUPINGS TO OPT IN TO";
        };


        //handes the groups of stuff on the pages.
        $scope.grouptToPages=function(){
            $scope.pagedItems=[];
            //console.log($scope.membersList);
            for(var i = 0; i < $scope.membersList.length ; i++){
                console.log($scope.membersList[i]);
                if(i % $scope.itemsPerPage === 0){

                    $scope.pagedItems[Math.floor(i/$scope.itemsPerPage)] = [ $scope.membersList[i]];

                }else{
                    $scope.pagedItems[Math.floor(i/$scope.itemsPerPage)].push( $scope.membersList[i]);
                }
            }

        };

        $scope.range = function (size,start, end) {
            var ret = [];
            if (size < end) {
                end = size;
                start = size-$scope.gap;
            }
            if(start < 0 ){
                start = 0;
            }
            for (var i = start; i < end; i++) {
                ret.push(i);
            }
            //  console.log(ret);
            return ret;
        };

        $scope.prevPage = function () {
            if ($scope.currentPage > 0) {
                $scope.currentPage--;
            }
        };

        $scope.nextPage = function () {
            if ($scope.currentPage < $scope.pagedItems.length - 1) {
                $scope.currentPage++;
            }
        };

        $scope.setPage = function () {
            $scope.currentPage = this.n;
        };
    }

    membershipApp.controller("MembershipJsController", MembershipJsController);

})();
