(function() {

    holidayApp.factory('dataProvider', function($http) {
        return {
            loadData: function(callback, url) {
                $http.get(encodeURI(url))
                     .success(callback)
                     .error(function(data, status) {
                         console.log('Error in dataProvider; status: ', status);
                     });
            }
        }
    });

})();
