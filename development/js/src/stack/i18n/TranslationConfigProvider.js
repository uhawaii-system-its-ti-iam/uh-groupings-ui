angular.module('stack.i18n.TranslationConfig', [])

/**
 * The TranslationConfig is a configuration implementation leveraged
 * by the i18n implementation.
 *
 * @class TranslationConfig
 * @constructor
 * @module stack.i18n.TranslationConfig
 */
.provider('TranslationConfig', [
    function () {
        'use strict';

        /**
         * Property houses configuration.
         *
         * @property config
         * @type {Object}
         * @private
         */
        var config = {};

        /**
         * Method sets or registers the path needed to obtain a
         * list of translation strings.
         *
         * @method setMapPath
         * @param {String} path Url path
         * @return {Object} Chained object
         */
        this.setMapPath = function (path) {
            if (typeof path === 'string') {
                config.path = path;
            }

            return this;
        };

        /**
         * Method sets or registers the namespace needed to obtain a
         * list of translation strings.
         *
         * @method setMapNamespace
         * @param {String} namespace Namespace
         * @return {Object} Chained object
         */
        this.setMapNamespace = function (namespace) {
            if (typeof namespace === 'string') {
                config.namespace = namespace;
            }

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
