(function () {

    /**
     * Controller for the timeout functionality. When a user is AFK for more than 30 min, user will be logged out.
     * Also creates timeout warning modal when 5 minutes are left in the timer.
     *
     * If user clicks or presses any key within the 30 min of inactivity, timer is reset.
     *
     * @param $scope - binding between controller and HTML page
     * @param $window -
     * @param $uibModal - creates modal
     * @param dataProvider - http request
     * @param BASE_URL - base url for api calls
     * @constructor
     */
    function TimeoutJsController($scope, $window, $uibModal, $controller, dataProvider, BASE_URL) {

        angular.extend(this, $controller("GeneralJsController", { $scope: $scope }));

        $scope.seconds = 300;
        $scope.idleTime = 0;
        /**
         * Every minute, checks whether or not user has clicked or pressed button
         */
        $(document).ready(function () {
            //Increment the idle time counter every minute.
            setInterval($scope.timerIncrement, 60000); // 1 minute
            //Zero the idle timer on mouse movement.
            $(this).click(function (e) {
                $scope.idleTime = 0;
            });

            $(this).keypress(function (e) {
                $scope.idleTime = 0;
            });
        });

        /**
         *  Checks on time of inactivity, if time is meet, log out user.
         */
        $scope.timerIncrement = function () {
            $scope.idleTime++;
            if ($scope.idleTime === 25) {// Create warning modal when 5 min left
                $scope.countdownTimer = setInterval(timer, 1000);
                $scope.createTimeoutModal();
            }
            if ($scope.idleTime === 30) { // Logout user after 30 min has passed
                let r = new XMLHttpRequest();
                r.open("POST", "/uhgroupings/logout", true);
                r.setRequestHeader("X-XSRF-TOKEN", $scope.getCookie("XSRF-TOKEN"));
                r.send();
                $window.location.href = "/uhgroupings/";
            }
        };

        /**
         * Creates a countdown timer.
         */
        function timer() {
            let minutes = Math.round(($scope.seconds - 30) / 60);
            let remainingSeconds = $scope.seconds % 60;
            if (remainingSeconds < 10) {
                remainingSeconds = "0" + remainingSeconds;
            }
            document.getElementById("countdown").innerHTML = minutes + ":" + remainingSeconds;
            if ($scope.seconds === 0) {
                clearInterval($scope.countdownTimer);
            } else {
                $scope.seconds--;
            }
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
            $scope.timeoutModalInstance.result.then(function () {
                //Filler in order to catch off click dismiss
            }, function () {
                $scope.idleTime = 0;
                $scope.pingServer();
            });
        };

        /**
         * Closes modal and restarts timer effect.
         */
        $scope.closeTimeoutModal = function () {
            $scope.timeoutModalInstance.close();
            $scope.idleTime = 0;
            $scope.pingServer();
        };

        /**
         * Pings tomcat server with a GET request to retrieve uses info.
         */
        $scope.pingServer = function () {

            const endpoint = BASE_URL + "members/aaronvil";

            clearInterval($scope.countdownTimer);
            $scope.seconds = 300;
            dataProvider.loadData(function (res) {
                console.log("Success in pinging tomcat");
            }, function (res) {
                console.log("Error in pinging tomcat");
            }, endpoint);
        };

    }

    UHGroupingsApp.controller("TimeoutJsController", TimeoutJsController);
})();
