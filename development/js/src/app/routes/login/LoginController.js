angular.module('routes.login.LoginController', [
    'stack.i18n.stackLocalize',
    'stack.i18n.localize'
])

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

        // Define.
        var loginCtrl;

        /**
         * Property houses a reference to the login controller.
         *
         * @property loginCtrl
         * @type {Object}
         */
        loginCtrl = this;

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
