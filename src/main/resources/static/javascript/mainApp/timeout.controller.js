(function () {

    /**
     * Controller for the memberships page.
     * @param $scope - binding between controller and HTML page
     */
    function TimeoutJsController($scope, $window) {

        /**
         * Loads the groups the user is a member in, the groups the user is able to opt in to, and the groups the user
         * is able to opt out of.
         */
        $scope.init = function () {

        };

        var idleTime = 0;
        /**
         * Every minute, checks whether or not user has clicked or pressed button
         */
        $(document).ready(function () {
            //Increment the idle time counter every minute.
            var idleInterval = setInterval(timerIncrement, 60000); // 1 minute
            //Zero the idle timer on mouse movement.
            $(this).click(function (e) {
                idleTime = 0;
            });

            $(this).keypress(function (e) {
                idleTime = 0;
            });
        });

        /**
         * Gets cookie information
         * @param cname = name of cookie you want to look for.
         * @returns {*}
         */
        function getCookie(cname) {
            var name = cname + "=";
            var decodedCookie = decodeURIComponent(document.cookie);
            var ca = decodedCookie.split(';');
            for(var i = 0; i <ca.length; i++) {
                var c = ca[i];
                while (c.charAt(0) == ' ') {
                    c = c.substring(1);
                }
                if (c.indexOf(name) == 0) {
                    return c.substring(name.length, c.length);
                }
            }
            return "";
        }

        /**
         *  Checks on time of inactivity, if time is meet, log out user.
         */
        function timerIncrement() {
            idleTime = idleTime + 1;
            console.log(idleTime);
            if (idleTime == 30) { // 30 minutes
                var r = new XMLHttpRequest();
                r.open('POST', '/uhgroupings/logout', true);
                r.setRequestHeader("X-XSRF-TOKEN", getCookie("XSRF-TOKEN"));
                r.send();
                $window.location.href = "/uhgroupings/";

            }
        }


    }

    UHGroupingsApp.controller("TimeoutJsController", TimeoutJsController);

})();
