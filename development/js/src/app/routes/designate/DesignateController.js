angular.module('routes.designate.DesignateController', [
    'stack.i18n.stackLocalize',
    'stack.i18n.localize'
])

/**
 * The DesignateController manages view logic rendered to the
 * designate.html template for the /designate route.
 *
 * @class DesignateController
 * @constructor
 * @module routes.designate.DesignateController
 **/
.controller('DesignateController', [
    '$timeout',
    function ($timeout) {
        'use strict';

        // Define.
        var designateCtrl;

        /**
         * Property houses a reference to the designate controller.
         *
         * @property designateCtrl
         * @type {Object}
         */
        designateCtrl = this;

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
