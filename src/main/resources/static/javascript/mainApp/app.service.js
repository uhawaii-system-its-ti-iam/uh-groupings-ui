/* global UHGroupingsApp */
(function () {
    /**
     * Service function that provides GET and POST requests for getting or updating data
     * @name dataProvider
     */
    UHGroupingsApp.factory("dataProvider", function ($http, $window) {

        const timeLimit = 20000;

        return {

            /**
             * Perform a GET request to the specified URL.
             * @param {string} arguments[0] - if provided it will be the URL to perform the request on
             * @param {function} arguments[1] - if provided it will be the function to perform on a successful request (200)
             * @param {function} arguments[2] - if provided it will execute if response returns as an error.
             */
            retrieveData() {
                switch (arguments.length) {
                    case 3:
                        $http.get(encodeURI(arguments[0]))
                            .then(arguments[1], arguments[2]);
                        break;
                    case 2:
                        $http.get(encodeURI(arguments[0]))
                            .then(arguments[1]);
                        break;
                    default:
                        // Ignore request.
                        break;
                }
            },

            /**
             * Perform a GET request to the specified URL.
             * @param {string} url - the URL to perform the request on
             * @param {function} callback - the function to perform on a successful request (200)
             * @param {function} callError - Execute if response returns as an error.
             */
            loadData(url, callback, callError) {
                $http.get(encodeURI(url))
                    .then(function (response) {
                        callback(response.data);
                    }, function (response) {
                        callError(response);
                    });
            },

            /**
             * Perform a POST request to the specified URL.
             * @param {string} url - the URL to perform the request on
             * @param {function} callback - the function to perform on a successful request (200)
             * @param {function} callError - Execute if response returns as an error.
             */
            updateData(url, callback, callError) {
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
             * @param {string} url - Path to which data is being posted too.
             * @param {function} modal - Launch a modal using a call back function.
             * @param {function} callback - Execute if response returns OK
             * @param {function} callError - Execute if response returns as an error.
             */
            updateDataWithTimeoutModal(url, callback, callError, modal) {
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
             * @param {string} url - the URL to perform the request on
             * @param {string} data - data to be updated
             * @param {function} callback - the function to perform on a successful request (200)
             * @param {function} callError - the function to perform on a unsuccessful request
             */
            updateDataWithBody(url, data, callback, callError) {
                $http.put(encodeURI(url), data)
                    .then(function (response) {
                        callback(response.data);
                    }, function (response) {
                        callError(response);
                    });
            },

            /**
             * PUT data to the server, if the response is OK then call the callBack function, if the response is an
             * error then call the callError function. If the response is not received in n seconds, launch a modal.
             * @param {string} url - Path to which data is being posted too.
             * @param {string} data - data to be updated
             * @param {function} modal - Launch a modal using a call back function.
             * @param {function} callback - Execute if response returns OK
             * @param {function} callError - Execute if response returns as an error.
             */
            updateDataWithBodyAndTimeoutModal(url, data, callback, callError, modal) {
                let timeoutID = setTimeout(modal, timeLimit);
                $http.put(encodeURI(url), data)
                    .then(function (response) {
                        clearTimeout(timeoutID);
                        callback(response.data);
                    }, function (response) {
                        clearTimeout(timeoutID);
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
