angular.module('stack.develop.DevelopConfig', [])

/**
 * The DevelopConfig provides adopting implementations with a 'develop'
 * configuration option. Depending upon the implementation, it is sometimes
 * useful to understand whether or not the application is in a 'develop' or
 * 'production' mode. DevelopConfig provides the .setDevelop(flag) method and
 * should be configured within the compiled app.html page.
 *
 * See /clientserver/views/app.html for example.
 *
 * @class DevelopConfig
 * @constructor
 * @module stack.develop.DevelopConfig
 */
.provider('DevelopConfig', [
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
            develop: true
        };

        /**
         * Method sets the develop flag.
         *
         * @method setDevelop
         * @param {Boolean} flag API route
         * @return {Object} Chained object
         */
        this.setDevelop = function (flag) {
            config.develop = (angular.isUndefined(flag) || typeof flag !== 'boolean') ? config.develop : flag;

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
