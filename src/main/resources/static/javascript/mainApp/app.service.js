(function () {

    /**
     * Service function that provides GET and POST requests for getting or updating data
     * @name dataProvider
     */
    UHGroupingsApp.factory("dataProvider", function ($http, $window) {
        return {
            /**
             * Performs a GET request to the specified URL.
             * @param {string} url - the URL to perform the request on
             * @param {function} callback - the function to perform on a successful request (200)
             */
            loadData: function (callback, callError, url) {
                $http.get(encodeURI(url))
                    .success(callback)
                    .error(callError, function (data, status) {
                        console.log("Error in dataProvider; status: ", status);
                    });
            },

            /**
             * Performs a POST request to the specified URL.
             * @param {string} url - the URL to perform the request on
             * @param {function} callback - the function to perform on a successful request (200)
             */
            updateData: function (callback, callError, url) {
                $http.post(encodeURI(url))
                    .success(callback)
                    .error(callError, function (data, status) {
                        console.log("Error in dataProvider; status: ", status);
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
                    .success(function () {
                        $window.location.href = redirectUrl;
                    });
            }
        };
    });

})();
