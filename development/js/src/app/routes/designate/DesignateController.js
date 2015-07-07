angular.module('routes.designate.DesignateController', [
    'stack.page-loader',
    'stack.i18n'
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
    'translate',
    'userStatus',
    function ($timeout, translate, userStatus) {
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
                // Call implementations here. Timeout is needed in order
                // for all potentially nested directives to execute.
                $timeout.cancel(t);
            }, 0);
        }
        initialize();
    }
]);
