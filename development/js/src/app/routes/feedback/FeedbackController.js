angular.module('routes.feedback.FeedbackController', [
    'stack.page-loader',
    'stack.i18n'
])

/**
 * The FeedbackController manages view logic rendered to the
 * feedback.html template for the /feedback route.
 *
 * @class FeedbackController
 * @constructor
 * @module routes.feedback.FeedbackController
 **/
.controller('FeedbackController', [
    '$timeout',
    'translate',
    'userStatus',
    function ($timeout, translate, userStatus) {
        'use strict';

        // Define.
        var feedbackCtrl;

        /**
         * Property houses a reference to the feedback controller.
         *
         * @property feedbackCtrl
         * @type {Object}
         */
        feedbackCtrl = this;

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
