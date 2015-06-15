/**
 * The app module is the top-level application module.
 *
 * @module app
 */
angular.module('app', [
    'ui.router',
    'routes'
])
.config(
    [
        '$locationProvider',
        '$urlRouterProvider',
        'TranslationConfigProvider',
        function ($locationProvider, $urlRouterProvider, TranslationConfigProvider) {
            'use strict';
            // Enable html5 push state.
            $locationProvider.html5Mode(true).hashPrefix('!');

            // Provide (i18n) translation map path.
            TranslationConfigProvider
                .setMapPath('i18n/')
                .setMapNamespace('application');

            // Redirect for unmatched urls.
            $urlRouterProvider.otherwise(function ($injector) {
                var state = $injector.get('$state'),
                    global = $injector.get('GLOBAL');

                state.go(global.routes.login);
            });
        }
    ]
);
