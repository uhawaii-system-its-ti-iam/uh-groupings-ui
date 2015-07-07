/**
 * Module definition for the feedback module. The feedback module manages
 * the application's top-level feedback view (i.e., /feedback).
 *
 * @module routes.feedback
 */
angular.module('routes.feedback', [
    'stack.i18n.TranslationResolver',
    'stack.authentication.ProtectResolver',
    'routes.feedback.FeedbackController'
])
.config(
    [
        '$stateProvider',
        function ($stateProvider) {
            'use strict';

            $stateProvider
                .state('feedback', {
                    url: '/feedback',
                    templateUrl: 'js/src/app/routes/feedback/feedback.html',
                    controller: 'FeedbackController',
                    controllerAs: 'feedbackCtrl',
                    resolve: {
                        translate: ['TranslationResolver', function (TranslationResolver) {
                            return new TranslationResolver();
                        }],
                        protect: ['ProtectResolver', function (ProtectResolver) {
                            return new ProtectResolver();
                        }]
                    }
                }
            );
        }
    ]
);
