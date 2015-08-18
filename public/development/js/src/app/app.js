/**
 * The app module is the top-level application module.
 *
 * @module app
 */
angular.module('app', [
    'plugins',
    'routes',
    'templates',
    'components',
    'stack',
    'app.AppService',
    'app.AppController'
])
.config(
    [
        '$locationProvider',
        '$urlRouterProvider',
        'DSCacheFactoryProvider',
        'TranslationConfigProvider',
        'AuthenticationConfigProvider',
        function ($locationProvider, $urlRouterProvider, DSCacheFactoryProvider, TranslationConfigProvider, AuthenticationConfigProvider) {
            'use strict';

            // Enable html5 push state.
            $locationProvider.html5Mode(true).hashPrefix('!');

            // Provide (i18n) translation map path.
            TranslationConfigProvider
                .setMapPath('i18n/')
                .setMapNamespace('application');

            // Provide API base & authentication routes.
            AuthenticationConfigProvider
                .setDevelopmentAPIBase('/api')
                .setProductionAPIBase('/api')
                .setUnauthenticatedRoute('about')
                .setAuthenticatedRoute('about');

            // Configure global cache settings.
            DSCacheFactoryProvider.setCacheDefaults({
                storageMode: 'memory'
            });

            // Redirect for unmatched urls.
            $urlRouterProvider.otherwise(function ($injector) {
                var state = $injector.get('$state');
                state.go('about');
            });
        }
    ]
)
.run([
    '$filter',
    '$rootScope',
    function ($filter, $rootScope) {
        /**
         * Method returns true if an enumerable object contains
         * no values (no enumerable own-properties). For strings
         * and array-like objects the isEmpty() method checks if
         * the length property is 0. This method is available
         * globally and can be leveraged within template files
         * using the $root.$isEmpty() syntax.
         *
         * @method isEmpty
         * @param {Mixed} context value
         * @return {Boolean} True or False
         */
        $rootScope.isEmpty = function (value) {
            return _.isEmpty(value);
        };

        /**
         * Method returns whether or not the object contains the given key.
         * This method is available globally and can be leveraged within
         * template files using the $root.$has() syntax.
         *
         * @method has
         * @param {Object} object Object
         * @param {String} property Name of property
         * @return {Boolean}
         */
        $rootScope.has = function (object, property) {
            return _.has(object, property);
        };

        /**
         * Method returns a localized string based upon the key.
         * This method is available globally and can be leveraged
         * within template files using $root.localize() syntax.
         *
         * @method localize
         * @param {String} key Localization key
         * @return {String} Localized string
         */
        $rootScope.localize = function (key) {
            return $filter('localize')(key);
        };
    }
]);
