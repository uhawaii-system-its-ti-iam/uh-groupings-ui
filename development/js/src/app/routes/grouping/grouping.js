/**
 * Module definition for the grouping module. The grouping module manages
 * the application's top-level grouping view (i.e., /grouping).
 *
 * @module routes.grouping
 */
angular.module('routes.grouping', [
    'stack.i18n.TranslationResolver',
    'stack.authentication.ProtectResolver',
    'routes.grouping.GroupingController'
])
.config(
    [
        '$stateProvider',
        function ($stateProvider) {
            'use strict';

            $stateProvider
                .state('grouping', {
                    url: '/grouping',
                    templateUrl: 'js/src/app/routes/grouping/grouping.html',
                    controller: 'GroupingController',
                    controllerAs: 'groupingCtrl',
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
