angular.module('routes.about.AboutController', [
    'stack.page-loader',
    'stack.i18n'
])

/**
 * The AboutController manages view logic rendered to the
 * about.html template for the /about route.
 *
 * @class AboutController
 * @constructor
 * @module routes.about.AboutController
 **/
.controller('AboutController', [
    '$timeout',
    'translate',
    'userStatus',
    function ($timeout, translate, userStatus) {
        'use strict';

        // Define.
        var aboutCtrl;

        /**
         * Property houses a reference to the about controller.
         *
         * @property aboutCtrl
         * @type {Object}
         */
        aboutCtrl = this;

        /**
         * Method executes initialization process.
         *
         * @method initialize
         * @private
         */
        function initialize() {
            var t = $timeout(function () {
                // Call implementations here. Timeout is needed in order
                // for all potentially nested directives to execute.
                $timeout.cancel(t);
            }, 0);
        }
        initialize();
    }
]);
