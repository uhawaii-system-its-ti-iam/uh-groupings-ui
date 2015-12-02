angular.module('routes.groupingOptIn.GroupingOptInController', [
    'stack.page-loader',
    'stack.i18n'
])

/**
 * The GroupingOptInController manages view logic rendered to the
 * groupingOptIn.html template for the /grouping-opt-in route.
 *
 * @class GroupingOptInController
 * @constructor
 * @module routes.groupingOptIn.GroupingOptInController
 **/
.controller('GroupingOptInController', [
    '$timeout',
    'translate',
    'protect',
    function ($timeout, translate, protect) {
        'use strict';

        // Define.
        var groupingOptInCtrl;

        /**
         * Property houses a reference to the controller.
         *
         * @property groupingOptInCtrl
         * @type {Object}
         */
        groupingOptInCtrl = this;

        /**
         * Property houses a reference to authenticated user object.
         *
         * @property groupingOptInCtrl.user
         * @type {Object}
         */
        groupingOptInCtrl.user = protect;

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
