(function() {

    membershipApp.factory('dataProvider', function($http) {
        return {
            loadData: function(callback, url) {
                $http.get(encodeURI(url))
                    .success(callback)
                    .error(function(data, status) {
                        console.log(url);
                        console.log(callback);
                        console.log('Error in dataProvider; status: ', status);
                    });
            }
        }
    });

    membershipApp.factory('dataUpdater', function($http) {
        return {
            updateData: function(callback, url) {
                $http.post(encodeURI(url))
                    .success(callback)
                    .error(function(data, status) {
                        console.log('Error in dataProvider; status: ', status);
                        callback = "Error";
                    });
            }
        }
    });

})();
