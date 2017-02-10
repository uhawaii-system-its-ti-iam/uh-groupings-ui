(function() {

    function HolidayJsController($scope, dataProvider) {
        var url = 'api/holidays';
        $scope.years = [];

        $scope.init = function() {
            var date = new Date();
            var year = date.getFullYear();
            $scope.yearCode = year.toString();
            $scope.maxYear = year;
            $scope.years = [];
            $scope.years.push(year);

            dataProvider.loadData(function(d) {
                var data = d.data;
                $scope.holidays = data;
                for (var i = 0; i < data.length; i++) {
                    var y = parseInt(data[i].year);
                    if ($scope.years.indexOf(y) < 0) {
                        $scope.years.push(y);
                    }
                }
                $scope.years.sort(function(a, b){return b - a});
            }, url);

        };
    }
    holidayApp.controller("HolidayJsController", HolidayJsController);

})();
