angular.module('routes.about.AboutController', [
    'stack.i18n.stackLocalize',
    'stack.i18n.localize'
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
    function ($timeout) {
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
                $timeout.cancel(t);
            }, 0);
        }
        initialize();
    }
]);
