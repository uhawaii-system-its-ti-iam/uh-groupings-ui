/* global UHGroupingsApp */
(() => {
    /**
     * Service function that provides GET and POST requests for getting or updating data
     * @name dataProvider
     */
    UHGroupingsApp.factory("dataProvider", function ($http, $window, BASE_URL) {

        const timeLimit = 20000;
        const maxRetries = 3;

        /**
         * Sets delay in milliseconds. Used with await in async functions.
         * @param {number} ms - milliseconds of delay
         * @returns {Promise} setTimeout callback
         */
        const delay = (ms = 5000) => {
            return new Promise((resolve) => setTimeout(resolve, ms));
        };

        /**
         * Polls to getAsyncJobResult API endpoint until the async job has completed with a result
         * @param {number} jobId - the jobId returned from the response of an async endpoint
         * @param {function} callback - the function to perform on a successful request (200)
         * @param {function} callError - execute if response returns as an error
         * @param {number} timeoutID - the timeoutID created by a setTimeout() call to clear
         */
        const pollData = (jobId, callback, callError, timeoutID = 0) => {
            $http.get(encodeURI(`${BASE_URL}jobs/${jobId}`))
                .then(async (response) => {
                    if (response.data.status === "COMPLETED") {
                        clearTimeout(timeoutID);
                        callback(response.data.result);
                    } else {
                        await delay();
                        pollData(jobId, callback, callError, timeoutID);
                    }
                }, (response) => {
                    callError(response);
                });
        };

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
                    .then((response) => {
                        callback(response.data);
                    }, (response) => {
                        callError(response);
                    });
            },

            /**
             * Perform a POST request to the specified URL.
             * @param {string} url - the URL to perform the request on
             * @param {any} data - the data to perform the request with
             * @param {function} callback - the function to perform on a successful request (200)
             * @param {function} callError - Execute if response returns as an error.
             */
            loadDataWithBody(url, data, callback, callError) {
                $http.post(encodeURI(url), data)
                    .then((response) => {
                        callback(response.data);
                    }, (response) => {
                        callError(response);
                    });
            },

            /**
             * Perform a POST request to the specified async URL.
             * @param {string} url - the URL to perform the request on
             * @param {any} data - the data to perform the request with
             * @param {number} initialPoll - the milliseconds to wait before making the first poll
             * @param {function} callback - the function to perform on a successful request (200)
             * @param {function} callError - Execute if response returns as an error.
             */
            loadDataWithBodyAsync(url, data, initialPoll, callback, callError) {
                $http.post(encodeURI(url), data)
                    .then(async (response) => {
                        await delay(initialPoll);
                        pollData(response.data, callback, callError);
                    }, (response) => {
                        callError(response);
                    });
            },

            /**
             * Perform a POST request to the specified URL that retries on error with incremental delay.
             * @param {string} url - the URL to perform the request on
             * @param {any} data - the data to perform the request with
             * @param {function} callback - the function to perform on a successful request (200)
             * @param {function} callError - Execute if response returns as an error.
             * @param {number} retries - the number of retries to attempt
             */
            loadDataWithBodyRetry(url, data, callback, callError, retries = maxRetries) {
                $http.post(encodeURI(url), data)
                    .then((response) => callback(response.data))
                    .catch(async (response) => {
                        if (retries <= 0) {
                            callError(response);
                            return;
                        }
                        await delay(2000 * Math.log(maxRetries / retries));
                        this.loadDataWithBodyRetry(url, data, callback, callError, retries - 1);
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
                    .then((response) => {
                        callback(response.data);
                    }, (response) => {
                        callError(response);
                    });
            },

            /**
             * Perform a POST request to the specified async URL.
             * @param {string} url - the URL to perform the request on
             * @param {number} initialPoll - the milliseconds to wait before making the first poll
             * @param {function} callback - the function to perform on a successful request (200)
             * @param {function} callError - Execute if response returns as an error.
             */
            updateDataAsync(url, initialPoll, callback, callError) {
                $http.post(encodeURI(url))
                    .then(async (response) => {
                        await delay(initialPoll);
                        pollData(response.data, callback, callError);
                    }, (response) => {
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
                    .then((response) => {
                        callback(response.data);
                    }, (response) => {
                        callError(response);
                    });
            },

            /**
             * PUT data to the server, if the response is OK then call the callBack function, if the response is an
             * error then call the callError function. If the response is not received in n seconds, display a modal.
             * @param {string} url - Path to which data is being posted too.
             * @param {any} data - data to be updated
             * @param {function} modal - Display a modal using a call back function.
             * @param {function} callback - Execute if response returns OK
             * @param {function} callError - Execute if response returns as an error.
             */
            updateDataWithBodyAndTimeoutModal(url, data, callback, callError, modal) {
                let timeoutID = setTimeout(modal, timeLimit);
                $http.put(encodeURI(url), data)
                    .then((response) => {
                        clearTimeout(timeoutID);
                        callback(response.data);
                    }, (response) => {
                        clearTimeout(timeoutID);
                        callError(response);
                    });
            },

            /**
             * PUT data to the server asynchronously, if the response is OK then call the callBack function, if the response is an
             * error then call the callError function. If the response is not received in n seconds, display a modal.
             * @param {string} url - Path to which data is being posted too.
             * @param {any} data - data to be updated
             * @param {number} initialPoll - the milliseconds to wait before making the first poll
             * @param {function} modal - Display a modal using a call back function.
             * @param {function} callback - Execute if response returns OK
             * @param {function} callError - Execute if response returns as an error.
             */
            updateDataWithBodyAndTimeoutModalAsync(url, data, initialPoll, callback, callError, modal) {
                const timeoutID = setTimeout(modal, timeLimit);
                $http.put(encodeURI(url), data)
                    .then(async (response) => {
                        await delay(initialPoll);
                        pollData(response.data, callback, callError, timeoutID);
                    }, (response) => {
                        callError(response);
                        clearTimeout(timeoutID);
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
                    .then(() => {
                        $window.location.href = redirectUrl;
                    });
            }
        };
    });

})();
