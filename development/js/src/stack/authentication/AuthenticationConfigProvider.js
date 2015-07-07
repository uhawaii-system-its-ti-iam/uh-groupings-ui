angular.module('stack.authentication.AuthenticationConfig', [])

/**
 * The AuthenticationConfig is a configuration implementation leveraged
 * by the authentication module. The AuthenticationConfig implementation
 * provides configuration settings to the Authentication module.
 *
 * @class AuthenticationConfig
 * @constructor
 * @module stack.authentication.AuthenticationConfig
 */
.provider('AuthenticationConfig', [
    function () {
        'use strict';

        /**
         * Property houses configuration.
         *
         * @property config
         * @type {Object}
         * @private
         */
        var config = {
            developmentAPIBase: '/api',
            productionAPIBase: '/api',
            unauthenticatedRoute: 'login',
            authenticatedRoute: 'dashboard'
        };

        /**
         * Method sets the API base route for a development environment.
         *
         * @method setDevelopmentAPIBase
         * @param {String} base API route
         * @return {Object} Chained object
         */
        this.setDevelopmentAPIBase = function (base) {
            config.developmentAPIBase = (angular.isUndefined(base) || !angular.isString(base)) ? config.developmentAPIBase : base;

            return this;
        };

        /**
         * Method sets the API base route for a production environment.
         *
         * @method setProductionAPIBase
         * @param {String} base API route
         * @return {Object} Chained object
         */
        this.setProductionAPIBase = function (base) {
            config.productionAPIBase = (angular.isUndefined(base) || !angular.isString(base)) ? config.productionAPIBase : base;

            return this;
        };

        /**
         * Method sets the unauthenticated route.
         *
         * @method setUnauthenticatedRoute
         * @param {String} route Route name
         * @return {Object} Chained object
         */
        this.setUnauthenticatedRoute = function (route) {
            config.unauthenticatedRoute = (angular.isUndefined(route) || !angular.isString(route)) ? config.unauthenticatedRoute : route;

            return this;
        };

        /**
         * Method sets the authenticated route.
         *
         * @method setAuthenticatedRoute
         * @param {String} route Route name
         * @return {Object} Chained object
         */
        this.setAuthenticatedRoute = function (route) {
            config.authenticatedRoute = (angular.isUndefined(route) || !angular.isString(route)) ? config.authenticatedRoute : route;

            return this;
        };

        /**
         * Method returns an injectable function.
         *
         * @method $get
         * @return {Function} Injectable function
         */
        this.$get = function () {
            return function () {
                return config;
            };
        };
    }
]);
