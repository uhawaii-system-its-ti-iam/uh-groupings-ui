angular.module('routes.feedback.FeedbackController', [])

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
    function ($timeout) {
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
                $timeout.cancel(t);
            }, 0);
        }
        initialize();
    }
]);
