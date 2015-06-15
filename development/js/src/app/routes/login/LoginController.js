angular.module('routes.login.LoginController', [])

/**
 * The LoginController manages view logic rendered to the
 * login.html template for the /login route.
 *
 * @class LoginController
 * @constructor
 * @module routes.login.LoginController
 **/
.controller('LoginController', [
    '$timeout',
    function ($timeout) {
        'use strict';

        /**
         * Property houses a reference to the login controller.
         *
         * @property loginCtrl
         * @type {Object}
         */
        //var loginCtrl = this;

        /**
         * Method executes initialization process.
         *
         * @method initialize
         * @private
         */
        function initialize() {
            var t = $timeout(function () {
                $timeout.cancel(t);
            }, 0);
        }
        initialize();
    }
]);
