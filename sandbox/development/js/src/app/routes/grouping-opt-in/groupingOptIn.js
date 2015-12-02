/**
 * Module definition for the groupingOptIn module. The groupingOptIn module manages
 * the application's top-level groupingOptIn view (i.e., /grouping-opt-in).
 *
 * @module routes.groupingOptIn
 */
angular.module('routes.groupingOptIn', [
    'stack.i18n.TranslationResolver',
    'stack.authentication.ProtectResolver',
    'routes.groupingOptIn.GroupingOptInController'
])
.config(
    [
        '$stateProvider',
        function ($stateProvider) {
            'use strict';

            $stateProvider
                .state('grouping-opt-in', {
                    url: '/grouping-opt-in',
                    templateUrl: 'js/src/app/routes/grouping-opt-in/groupingOptIn.html',
                    controller: 'GroupingOptInController',
                    controllerAs: 'groupingOptInCtrl',
                    resolve: {
                        translate: ['TranslationResolver', function (TranslationResolver) {
                            return new TranslationResolver();
                        }],
                        protect: ['ProtectResolver', function (ProtectResolver) {
                            return new ProtectResolver({permissions: ['member']});
                        }]
                    }
                }
            );
        }
    ]
);
