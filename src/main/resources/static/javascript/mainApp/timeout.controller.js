(function () {

    /**
     * Controller for the timeout functionality. When a user is idle for more than 30 min, user will be logged out.
     * Also creates timeout warning modal when 5 minutes are left in the timer.
     *
     * If user clicks or presses any key within the 30 min of inactivity, timer is reset.
     *
     * @param $scope - Binding between controller and HTML page
     * @param $window - A reference to the browser's window object
     * @param $uibModal - Creates modal
     * @param dataProvider - Used for HTTP requests
     * @param BASE_URL - Base url for api calls
     * @param $timeout - AngularJS wrapper for window.setTimeout, used to implement session timeout
     * @param $interval - AngularJS wrapper for window.setInterval, used to implement session timeout
     * @constructor
     */
    function TimeoutJsController($scope, $window, $uibModal, $controller, dataProvider, BASE_URL, $timeout, $interval) {

        angular.extend(this, $controller("GeneralJsController", { $scope: $scope }));

        let createTimeoutModalPromise = undefined;
        let countdownTimerPromise = undefined;
        let isModalOpen = false;
        const MAX_TIME_IDLE = 1000 * 60 * 25; // Time until modal opens - 25 minutes in milliseconds
        const TIME_TO_LOGOUT = 60 * 5; // When modal opens, seconds left until user is logged out

        $scope.secondsRemaining = TIME_TO_LOGOUT; // Seconds remaining before log out
        $scope.timeRemaining = ""; // Formatted string of seconds remaining

        angular.element(function () {
            // Start timeouts
            createTimeoutModalPromise = $timeout(() => {
                $scope.createTimeoutModal();
            }, MAX_TIME_IDLE);

            // Attach callback when the createTimeoutModalPromise is canceled
            createTimeoutModalPromise.then(() => {
                // Timeout ends and function is executed
            }, () => {
                // User resets timer or function execution failed
                restartTimeouts();
            });

            // If user clicks, reset timeout
            $(this).click(function (e) {
                if(!isModalOpen) {
                    $timeout.cancel(createTimeoutModalPromise);
                }
            });
            // If user presses a key on the keyboard reset timeout
            $(this).keypress(function (e) {
                if(!isModalOpen) {
                $timeout.cancel(createTimeoutModalPromise);
                }
            });
        });

        /* When DOM is destroyed, clear timeouts and intervals */
        $scope.$on("$destroy", function (event) {  
            if(angular.isDefined(createTimeoutModalPromise)) {
                $timeout.cancel(createTimeoutModalPromise);
                createTimeoutModalPromise = undefined; 
            }
            if(angular.isDefined(countdownTimerPromise)) {
                $timeout.cancel(countdownTimerPromise);
                countdownTimerPromise = undefined;  
            }
        });

        /**
         * Creates a countdown timer.
         */
        function timer() {
            $scope.timeRemaining = secondsToMinutes($scope.secondsRemaining);
            if($scope.secondsRemaining <= 0) {
                $scope.logoutOnIdle();
                restartTimeouts();             
            }
            $scope.secondsRemaining--;
        }

        /** 
         * Helper function to convert an amount of seconds to minutes in a formatted string 
         */
        function secondsToMinutes(seconds) {
            let minutes = Math.round((seconds - 30) / 60);
            let remainingSeconds = seconds % 60;
            if (remainingSeconds < 10) {
                remainingSeconds = "0" + remainingSeconds;
            }
            return `${minutes}:${remainingSeconds}`;
        }
        /**
         * Creates timeout modal.
         */
        $scope.createTimeoutModal = function () {
            $scope.timeoutModalInstance = $uibModal.open({
                templateUrl: "modal/timeoutModal",
                scope: $scope,
                backdrop: 'static',
                keyboard: false
            });
            /* Callback when timeout modal is opened */
            $scope.timeoutModalInstance.opened.then(() => {
                countdownTimerPromise = $interval(timer, 1000); // Start 5 minute countdown
                isModalOpen = true;
            });
            /* Callback when timeout modal is closed */
            $scope.timeoutModalInstance.result.then(function () {
                // Executes when user clicks "Stay logged in" button
                restartTimeouts();
                restartCountdown();
                $scope.pingServer();
                isModalOpen = false;
            }, function () {
                // Error catching if modal is not closed properly
                restartTimeouts();
                restartCountdown();
                $scope.pingServer();
                isModalOpen = false;
            });
        };

        /**
         * Closes modal
         */
        $scope.closeTimeoutModal = function () {
            $scope.timeoutModalInstance.close();
        };

        /**
         * Pings tomcat server with a GET request to retrieve user info.
         */
        $scope.pingServer = function () {
            const endpoint = BASE_URL + "members/aaronvil";
            dataProvider.loadData(function (res) {
                console.log("Success in pinging tomcat");
            }, function (res) {
                console.log("Error in pinging tomcat");
            }, endpoint);
        };

        /**
         * Restart createTimeoutModalPromise  
         */
        function restartTimeouts() {
            if(angular.isDefined(createTimeoutModalPromise)) {
                createTimeoutModalPromise = undefined;
                createTimeoutModalPromise = $timeout(() => {
                    $scope.createTimeoutModal();
                }, MAX_TIME_IDLE);

                createTimeoutModalPromise.then(() => {
                    // timeout ends and modal is created
                }, () => {
                    // User resets timer or function execution fails 
                    restartTimeouts();
                });
            }
        }

        function restartCountdown() {
            $interval.cancel(countdownTimerPromise);
            countdownTimerPromise = undefined;
            $scope.secondsRemaining = TIME_TO_LOGOUT;
            $scope.timeRemaining = secondsToMinutes(TIME_TO_LOGOUT);
        }
        /*
            Logout method used only when user is idle for too long.
            The other logout method is implemented in the html.
        */
        $scope.logoutOnIdle = () => {
            let request = new XMLHttpRequest();
            request.open("POST", "/uhgroupings/logout", true);
            request.setRequestHeader("X-XSRF-TOKEN", $scope.getCookie("XSRF-TOKEN"));
            // Attach event handler when POST request is successful
            request.onreadystatechange = () => {
                if(request.readyState === XMLHttpRequest.DONE && request.status === 200) {
                    // Redirect user to URL in location header in HTTP reponse, should be home page
                    $window.location.href = request.responseURL;
                }
            }
            const requestBody = "_csrf: " + $scope.getCookie("XSRF-TOKEN");
            request.send(requestBody);
        }
    }

    UHGroupingsApp.controller("TimeoutJsController", TimeoutJsController);
})();
