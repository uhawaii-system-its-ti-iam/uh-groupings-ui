(function () {
    /**
     * Service function that provides GET and POST requests for getting or updating data
     * @name dataProvider
     */
    UHGroupingsApp.factory("dataProvider", function ($http, $window) {

        //Global time limit variable for time out modal.
        var timeLimit = 20000;

        return {
            /**
             * Perform a GET request to the specified URL.
             * @param {function} callback - the function to perform on a successful request (200)
             * @param callError
             * @param {string} url - the URL to perform the request on
             */
            loadData(callback, callError, url) {
                $http.get(encodeURI(url))
                    .then(function (response) {
                        callback(response.data);
                    }, function (response) {
                        callError(response);
                    });
            },

            /**
             * Perform a POST request to the specified URL.
             * @param {function} callback - the function to perform on a successful request (200)
             * @param {string} url - the URL to perform the request on
             */
            updateData(callback, callError, url) {
                $http.post(encodeURI(url))
                    .then(function (response) {
                        callback(response.data);
                    }, function (response) {
                        callError(response);
                    });
            },


            /**
             * POST data to the server, if the response is OK then call the callBack function, if the response is an
             * error then call the callError function. If the response is not received in n seconds, launch a modal.
             * @param {function} callback - Execute if response returns OK
             * @param {function} callError - Execute if response returns as an error.
             * @param {string} url - Path to which data is being posted too.
             * @param {function} modal - Launch a modal using a call back function.
             */
            updateDataWithTimeoutModal(callback, callError, url, modal) {
                let timeoutID = setTimeout(modal, timeLimit);
                $http.post(encodeURI(url))
                    .then(function (response) {
                        clearTimeout(timeoutID);
                        callback(response.data);
                    }, function (response) {
                        clearTimeout(timeoutID);
                        callError(response);
                    });
            },

            /**
             * Perform a PUT request to the specified URL.
             * @param {function} callback - the function to perform on a successful request (200)
             * @param {function} callError - the function to perform on a unsuccessful request
             * @param {string} url - the URL to perform the request on
             * @param {string} data - data to be updated
             */
            updateDataWithBody(callback, callError, url, data) {
                $http.put(encodeURI(url), data)
                    .then(function (response) {
                        callback(response);
                    }, function (response) {
                        callError(response);
                    });
            },

            /**
             * Handle Java exceptions by performing a POST request.
             * @param {object} exceptionData - an object containing the exception (stored as a string)
             * @param {string} url - the endpoint to perform the POST request
             * @param {string} redirectUrl - the location to redirect after
             */
            handleException(exceptionData, url, redirectUrl) {
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

}());
