/* global UHGroupingsApp */
(() => {
    /**
     * Service function that polls an async job of the groupings API until it has completed with a result. It returns
     * the function that does the polling.
     * @name asyncJobPoller
     */
    UHGroupingsApp.factory("asyncJobPoller", function ($http, $rootScope, $timeout, BASE_URL) {

        const delay = 5000;
        // The statuses of a failed poll that may pass on their own: no answer at all (-1 or 0, e.g. the network
        // dropped for a moment), a request timeout, rate limiting, or a server or gateway that is briefly down.
        const transientStatuses = [-1, 0, 408, 429, 500, 502, 503, 504];
        // The polls of an async job in a row that may fail transiently before the polling fails. Each is sent again
        // after twice the wait of the last, from delay up to maxRetryDelay, so together they span about 3 minutes.
        const maxPollsFailed = 8;
        const maxRetryDelay = 30000;

        /**
         * Whether a poll failed for a reason that may pass on its own. An error the API itself answered with (one with
         * a resultCode) never does: from the jobs endpoint, it is either a denial of access or the failure of the job,
         * which the API answers every later poll of the job with too.
         * @param {Object} response - the response of the failed poll
         * @returns {boolean} true if the poll may be sent again
         */
        const isTransientFailure = (response) =>
            transientStatuses.includes(response?.status) && !response.data?.resultCode;

        /**
         * Polls to getAsyncJobResult API endpoint until the async job has completed with a result. Each poll of a job
         * that is in progress or completed is broadcast as "asyncJobPolled", so the idle timeout (TimeoutJsController)
         * doesn't log out a user who is waiting on a job such as a large import. A job the API no longer has (e.g.
         * because the API restarted) can never complete, so it fails like an unsuccessful request, with status 404.
         * A poll that fails transiently (see isTransientFailure) is sent again, up to maxPollsFailed times in a row,
         * since the job runs on regardless; any other failure, or a transient one past that, ends the polling with
         * the failed poll's response. The API answers every poll of a finished job the same way for 10 minutes after
         * the job finishes, so a poll sent again after the answer to the last one was lost still gets the job's
         * outcome.
         * @param {number} jobId - the jobId returned from the response of an async endpoint
         * @param {function} callback - the function to perform on a successful request (200)
         * @param {function} callError - execute if response returns as an error
         * @param {Promise} [timeoutPromise] - the $timeout promise of a slow-request modal, cancelled once the job
         * has completed or polling has failed
         * @param {function} [onProgress] - called with the progress ({phase, done, total}) of a job in progress that
         * reports it
         */
        return (jobId, callback, callError, timeoutPromise, onProgress) => {
            let failed = 0; // The polls in a row that have failed transiently
            const poll = () => {
                $http.get(encodeURI(`${BASE_URL}jobs/${jobId}`))
                    .then((response) => {
                        const status = response.data?.status;
                        if (status === "IN_PROGRESS") {
                            failed = 0;
                            // Poll again first, so nothing done with the progress can stop the polling.
                            $timeout(poll, delay);
                            $rootScope.$broadcast("asyncJobPolled");
                            if (typeof onProgress === "function" && response.data.progress) {
                                onProgress(response.data.progress);
                            }
                            return;
                        }
                        $timeout.cancel(timeoutPromise);
                        if (status === "COMPLETED") {
                            $rootScope.$broadcast("asyncJobPolled");
                            callback(response.data.result);
                        } else {
                            callError({ ...response, status: 404 });
                        }
                    }, (response) => {
                        if (isTransientFailure(response) && failed < maxPollsFailed) {
                            failed++;
                            $timeout(poll, Math.min(delay * 2 ** (failed - 1), maxRetryDelay));
                            return;
                        }
                        $timeout.cancel(timeoutPromise);
                        callError(response);
                    });
            };
            poll();
        };
    });
})();
