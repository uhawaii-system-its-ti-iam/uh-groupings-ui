/**
 * Module definition for the about module. The about module manages
 * the application's top-level About view (i.e., /about).
 *
 * @module routes.about
 */
angular.module('routes.about', [
    'stack.i18n',
    'routes.about.AboutController'
])
.config(
    [
        '$stateProvider',
        function ($stateProvider) {
            'use strict';

            $stateProvider
                .state('about', {
                    url: '/about',
                    templateUrl: 'js/src/app/routes/about/about.html',
                    controller: 'AboutController',
                    controllerAs: 'aboutCtrl',
                    resolve: {
                        translate: ['TranslationResolver', function (TranslationResolver) {
                            return new TranslationResolver();
                        }]
                    }
                }
            );
        }
    ]
);
