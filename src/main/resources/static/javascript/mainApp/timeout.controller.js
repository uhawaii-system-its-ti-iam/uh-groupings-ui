/* global angular, UHGroupingsApp */

(() => {

    /**
     * Controller for the timeout functionality. When a user is idle for more than 30 min, user will be logged out.
     * Also displays timeout warning modal when 5 minutes are left in the timer.
     *
     * If user clicks or presses any key within the 30 min of inactivity, timer is reset.
     *
     * @param $scope - Binding between controller and HTML page
     * @param $window - A reference to the browser's window object
     * @param $uibModal - Displays modal
     * @param dataProvider - Used for HTTP requests
     * @param BASE_URL - Base url for api calls
     * @param $timeout - AngularJS wrapper for window.setTimeout, used to implement session timeout
     * @param $interval - AngularJS wrapper for window.setInterval, used to implement session timeout
     * @constructor
     */
    function TimeoutJsController($scope, $window, $uibModal, $controller, dataProvider, BASE_URL, $timeout, $interval) {

        angular.extend(this, $controller("GeneralJsController", { $scope }));

        let displayTimeoutModalPromise;
        let countdownTimerPromise;
        let isModalOpen = false;
        const MAX_TIME_IDLE = 1000 * 60 * 25; // Time until modal opens - 25 minutes in milliseconds
        const TIME_TO_LOGOUT = 60 * 5; // When modal opens, seconds left until user is logged out - 5 minutes in seconds

        $scope.secondsRemaining = TIME_TO_LOGOUT; // Seconds remaining before log out
        $scope.timeRemaining = ""; // Formatted string of seconds remaining

        /**
         * Convert seconds to minutes and return as a formatted string.
         */
        const secondsToMinutes = (seconds) => {
            let minutes = Math.round((seconds - 30) / 60);
            let remainingSeconds = seconds % 60;
            if (remainingSeconds < 10) {
                remainingSeconds = "0" + remainingSeconds;
            }
            return `${minutes}:${remainingSeconds}`;
        };

        /**
         * Restart displayTimeoutModalPromise.
         */
        const restartTimeouts = () => {
            if (angular.isDefined(displayTimeoutModalPromise)) {
                displayTimeoutModalPromise = {};
                displayTimeoutModalPromise = $timeout(() => {
                    $scope.displayTimeoutModal();
                }, MAX_TIME_IDLE);

                displayTimeoutModalPromise.then(() => {
                    // timeout ends and modal is displayed
                }, () => {
                    // User resets timer or function execution fails
                    restartTimeouts();
                });
            }
        };

        angular.element(function () {
            // Start timeouts
            displayTimeoutModalPromise = $timeout(() => {
                $scope.displayTimeoutModal();
            }, MAX_TIME_IDLE);

            // Attach callback when the displayTimeoutModalPromise is canceled
            displayTimeoutModalPromise.then(() => {
                // Timeout ends and function is executed
            }, () => {
                // User resets timer or function execution failed
                restartTimeouts();
            });

            // If user clicks, reset timeout
            $(this).click((e) => {
                if (!isModalOpen) {
                    $timeout.cancel(displayTimeoutModalPromise);
                }
            });
            // If user presses a key on the keyboard reset timeout
            $(this).keypress((e) => {
                if (!isModalOpen) {
                    $timeout.cancel(displayTimeoutModalPromise);
                }
            });
        });

        /**
         * Clear timeouts and intervals when DOM is destroyed.
         */
        $scope.$on("$destroy", (event) => {
            if (angular.isDefined(displayTimeoutModalPromise)) {
                $timeout.cancel(displayTimeoutModalPromise);
                displayTimeoutModalPromise = {};
            }
            if (angular.isDefined(countdownTimerPromise)) {
                $timeout.cancel(countdownTimerPromise);
                countdownTimerPromise = {};
            }
        });

        /**
         * Create a countdown timer.
         */
         $scope.timer = () => {
            $scope.timeRemaining = secondsToMinutes($scope.secondsRemaining);
            if ($scope.secondsRemaining <= 0) {
                $scope.logoutOnIdle();
                restartTimeouts();
            }
            $scope.secondsRemaining--;
        };

        /**
         * Restart timer countdown.
         */
        const restartCountdown = () => {
            $interval.cancel(countdownTimerPromise);
            countdownTimerPromise = {};
            $scope.secondsRemaining = TIME_TO_LOGOUT;
            $scope.timeRemaining = secondsToMinutes(TIME_TO_LOGOUT);
        };

        /**
         * Display a timeout modal.
         */
        $scope.displayTimeoutModal = () => {
            $scope.timeoutModalInstance = $uibModal.open({
                templateUrl: "modal/timeoutModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false,
                ariaLabelledBy: "timeout-modal"
            });
            // Callback when timeout modal is opened.
            $scope.timeoutModalInstance.opened.then(() => {
                countdownTimerPromise = $interval($scope.timer, 1000); // Start 5 minute countdown
                isModalOpen = true;
            });
            // Callback when timeout modal is closed.
            // Execute when user clicks "Stay logged in" button.
            $scope.timeoutModalInstance.result.then(() => {
                restartTimeouts();
                restartCountdown();
                isModalOpen = false;
            }, () => {
                // Error catching if modal is not closed properly.
                restartTimeouts();
                restartCountdown();
                isModalOpen = false;
            });
        };

        /**
         * Close timeout modal.
         */
        $scope.closeTimeoutModal = () => {
            $scope.timeoutModalInstance.close();
        };

        /**
         * Logout method used only when user is idle for too long. The other logout method is implemented in the html.
         */
        $scope.logoutOnIdle = () => {
            let request = new XMLHttpRequest();
            request.open("POST", `${$scope.getBasePath()}/logout`, true);
            request.setRequestHeader("X-XSRF-TOKEN", $scope.getCookie("XSRF-TOKEN"));
            // Attach event handler when POST request is successful.
            request.onreadystatechange = () => {
                if (request.readyState === XMLHttpRequest.DONE && request.status === 200) {
                    // Redirect user to URL in location header in HTTP response, should be home page.
                    $window.location.href = request.responseURL;
                }
            };
            const requestBody = "_csrf: " + $scope.getCookie("XSRF-TOKEN");
            request.send(requestBody);
        };
    }

    UHGroupingsApp.controller("TimeoutJsController", TimeoutJsController);
})();
