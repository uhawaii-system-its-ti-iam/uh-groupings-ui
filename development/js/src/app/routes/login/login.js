/**
 * Module definition for the login module. The login module manages
 * the application's top-level login view (i.e., /login).
 *
 * @module routes.login
 */
angular.module('routes.login', [
    'stack.i18n',
    'routes.login.LoginController'
])
.config(
    [
        '$stateProvider',
        function ($stateProvider) {
            'use strict';

            $stateProvider
                .state('login', {
                    url: '/login',
                    templateUrl: 'js/src/app/routes/login/login.html',
                    controller: 'LoginController',
                    controllerAs: 'loginCtrl',
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
