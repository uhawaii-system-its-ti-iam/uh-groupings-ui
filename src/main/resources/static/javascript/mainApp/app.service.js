(function() {

    /**
     * Service function that provides GET and POST requests for getting or updating data
     * @name dataProvider
     */
    UHGroupingsApp.factory('dataProvider', function($http) {
        return {
            /**
             * Performs a GET request to the specified URL.
             * @param {string} url - the URL to perform the request on
             * @param {function} callback - the function to perform on a successful request (200)
             */
            loadData: function(callback, callerror, url) {
                $http.get(encodeURI(url))
                    .success(callback)
                    .error(callerror ,function(data, status) {
                        console.log('Error in dataProvider; status: ', status);
                    });

            },

            /**
             * Performs a POST request to the specified URL.
             * @param {string} url - the URL to perform the request on
             * @param {function} callback - the function to perform on a successful request (200)
             */
            updateData: function(callback, callerror, url) {
                $http.post(encodeURI(url))
                    .success(callback)
                    .error(callerror, function(data, status) {
                        console.log('Error in dataUpdater; status: ', status);
                    });
            }
        };
    });

    UHGroupingsApp.factory('modalFactory', function($uiModal) {
        return {
            open: function(size) {
                return $uibModal.open({});
            }
        }
    });

})();
