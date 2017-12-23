(function() {
    /**
     * Service function that makes a AJAX get call to a api url and sends back the json data retrieved.
     *
     * @param dataProvider : service name
     * @param $http        : service function that facilitates communication with a remote HTTP server
     * @param callback     : data returned from the AJAX call.
     * @param url          : url used for AJAX call
     * @param data         :
     * @param status       : HTTP status code of the response.
     *
     * @return callback    : The data that is returned from the service
     *
     */
    UHGroupingsApp.factory('dataProvider', function($http) {
        return {
            loadData: function(callback, url) {
                $http.get(encodeURI(url))
                    .success(callback)
                    .error(function(data, status) {
                        console.log('Error in dataProvider; status: ', status);
                        callback = "Error";
                    });
            }
        }
    });

    UHGroupingsApp.factory('modalFactory', function($uiModal) {
        return {
            open: function(size) {
                return $uibModal.open({});
            }
        }
    });

    /**
     * Service function that makes a AJAX post call from an api url
     *
     * @param dataUpdater  : service name
     * @param $http        : service function that facilitates communication with a remote HTTP server
     * @param callback     : data returned from the AJAX call.
     * @param url          : url used for AJAX call
     * @param data         :
     * @param status       : HTTP status code of the response.
     *
     * @return callback    : The data that is returned from the service
     */
    UHGroupingsApp.factory('dataUpdater', function($http) {
       return {
            updateData: function(callback, url) {
                $http.post(encodeURI(url))
                    .success(callback)
                    .error(function(data, status) {
                        console.log('Error in dataUpdater; status: ', status);
                        callback = "Error";
                    });
            }
       }
    });


    /**
     * Service function that makes a AJAX post call from an api url
     *
     * @param dataUpdater  : service name
     * @param $http        : service function that facilitates communication with a remote HTTP server
     * @param callback     : data returned from the AJAX call.
     * @param url          : url used for AJAX call
     * @param data         :
     * @param status       : HTTP status code of the response.
     *
     * @return callback    : The data that is returned from the service
     */
    ownerApp.factory('dataAdder', function($http) {
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

    /**
     * Service function that makes a AJAX post call from an api url
     *
     * @param dataDelete   : service name
     * @param $http        : service function that facilitates communication with a remote HTTP server
     * @param callback     : data returned from the AJAX call.
     * @param url          : url used for AJAX call
     * @param data         :
     * @param status       : HTTP status code of the response.
     *
     * @return callback    : The data that is returned from the service
     */
    UHGroupingsApp.factory('dataDelete', function($http) {
        return {
            deleteData: function(callback, url) {
                $http.post(encodeURI(url))
                    .success(callback)
                    .error(function(data, status) {
                        console.log('Error in dataDelete; status: ', status);
                        callback = "Error";
                    });
            }
        }
    });
})();
