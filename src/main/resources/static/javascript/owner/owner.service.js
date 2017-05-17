(function() {

    ownerApp.factory('dataProvider', function($http) {
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

    ownerApp.factory('dataUpdater', function($http) {
       return {
           addData: function(callback, url) {
               $http.post(encodeURI(url))
                   .success(callback)
                   .error(function(data, status) {
                       console.log('Error in dataUpdater; status: ', status);
                   })
           }
       }
    });

    ownerApp.factory('dataDeleter', function($http) {
       return {
           deleteData: function(callback, url) {
               $http.post(encodeURI(url))
                   .success(callback)
                   .error(function(data, status) {
                       console.log('Error in dataDeleter; status: ', status);
                   })
           }
       }
    });
})();

