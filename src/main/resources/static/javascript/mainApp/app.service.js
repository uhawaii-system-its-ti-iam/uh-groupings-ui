(function () {

    /**
     * Service function that provides GET and POST requests for getting or updating data
     * @name dataProvider
     */
    UHGroupingsApp.factory("dataProvider", function ($http, $window) {
        return {
            //todo It might be possible to use async/await function to change the promise behavior to better accomodate async getGrouping
            /**
             * Performs a GET request to the specified URL.
             * @param {string} url - the URL to perform the request on
             * @param {function} callback - the function to perform on a successful request (200)
             */
            loadData: function (callback, callError, url) {
                $http.get(encodeURI(url))
                    .then(function(response){
                       callback(response.data);
                    }, function (response) {
                        // console.log("Error A", response);
                        // if(response.data == null) return false;
                        callError(response.data);
                        console.log("Error in dataProvider; status: ", response.status);
                    });
            },

            /**
             * Performs a POST request to the specified URL.
             * @param {string} url - the URL to perform the request on
             * @param {function} callback - the function to perform on a successful request (200)
             */
            updateData: function (callback, callError, url) {
                $http.post(encodeURI(url))
                    .then(function(response){
                        callback(response.data);
                    }, function (response) {
                        callError(response);
                        console.log("Error in dataProvider; status: ", response.status);
                    });
            },

            /**
             * Handles Java exceptions by performing a POST request.
             * @param {object} exceptionData - an object containing the exception (stored as a string)
             * @param {string} url - the endpoint to perform the POST request
             * @param {string} redirectUrl - the location to redirect after
             */
            handleException: function (exceptionData, url, redirectUrl) {
                $http.post(encodeURI(url), exceptionData, {
                    headers: {
                        "Content-Type": "application/json"
                    }
                })
                    .then(function () {
                        $window.location.href = redirectUrl;
                    });
            }
        };
    });

})();
