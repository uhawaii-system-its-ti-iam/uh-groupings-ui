angular.module('routes.grouping.GroupingController', [
    'stack.page-loader',
    'stack.i18n'
])

/**
 * The GroupingController manages view logic rendered to the
 * grouping.html template for the /grouping route.
 *
 * @class GroupingController
 * @constructor
 * @module routes.grouping.GroupingController
 **/
.controller('GroupingController', [
    '$timeout',
    'translate',
    'userStatus',
    function ($timeout, translate, userStatus) {
        'use strict';

        // Define.
        var groupingCtrl;

        /**
         * Property houses a reference to the grouping controller.
         *
         * @property groupingCtrl
         * @type {Object}
         */
        groupingCtrl = this;

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
