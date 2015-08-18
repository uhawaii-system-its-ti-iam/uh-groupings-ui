angular.module('stack.i18n.TranslationService', [
    'stack.i18n.TranslationModel',
    'angular-data.DSCacheFactory'
])

/**
 * The TranslationService wraps the TranslationModel and provides
 * convenience methods for managing the the translation of strings
 * based upon different locales.
 *
 * @class TranslationService
 * @constructor
 * @module stack.i18n.TranslationService
 */
.factory('TranslationService', [
    '$q',
    '$log',
    '$locale',
    'TranslationModel',
    'DSCacheFactory',
    function ($q, $log, $locale, TranslationModel, DSCacheFactory) {
        'use strict';

        var service = {
            /**
             * Property houses cached model.
             *
             * @property cache
             * @type {Object}
             */
            cache: DSCacheFactory('i18n', {
                storageMode: 'memory'
            }),

            /**
             * Property houses resource namespace.
             *
             * @property resource
             * @type {String}
             */
            resource: 'map',

            /**
             * Method requests object containing localized strings from the
             * cached model. When the cache is empty, this method requests
             * localized strings from the server.
             *
             * @method get
             * @return {Object} Promise object
             */
            get: function () {
                var d = $q.defer(),
                    cache = service.getCache(),
                    promise;

                if (angular.isDefined(cache) && angular.isObject(cache) && !_.isEmpty(cache)) {
                    d.resolve(cache);
                } else {
                    promise = TranslationModel.read();
                    promise.then(
                        function (response) {
                            var result = response.data;
                            service.cache.put(service.resource, result);
                            d.resolve(service.cache.get(service.resource));
                        },
                        function () {
                            $log.warn('Localization data for ' + $locale.id + ' could not be loaded.');
                            service.cache.removeAll();
                            service.cache.put(service.resource, {});
                            d.resolve(service.cache.get(service.resource));
                        }
                    );
                }

                return d.promise;
            },

            /**
             * Method returns cached object.
             *
             * @method getCache
             * @return {Object} Cached object
             */
            getCache: function () {
                var cache = service.cache.get(service.resource);
                return (angular.isUndefined(cache)) ? service.cache.put(service.resource, {}) : service.cache.get(service.resource);
            },

            /**
             * Method returns a translated string based upon the
             * passed token.
             *
             * @method translate
             * @param {String} token Translation token
             */
            translate: function (token) {
                var d, promise, map, translated;

                d = $q.defer();
                promise = service.get();
                promise.then(
                    function (response) {
                        map = response;
                        translated = (_.has(map, token)) ? map[token] : token;

                        if (!_.has(map, token)) {
                            $log.warn('No localization data for: ' + token);
                        }

                        d.resolve(translated);
                    }
                );

                return d.promise;
            }
        };

        return service;
    }
]);
