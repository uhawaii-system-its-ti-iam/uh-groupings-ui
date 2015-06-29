angular.module('routes.login.LoginController', [
    'stack.page-loader',
    'stack.i18n'
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
    'translate',
    function ($timeout, translate) {
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
         * Property houses a reference to the i18n object.
         *
         * @property loginCtrl.i18n
         * @type {Object}
         */
        loginCtrl.i18n = translate;

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
