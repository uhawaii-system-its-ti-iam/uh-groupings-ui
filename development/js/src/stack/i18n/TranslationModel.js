angular.module('stack.i18n.TranslationModel', [
    'stack.i18n.TranslationConfig'
])

/**
 * The TranslationModel houses CRUD-based methods used to
 * retrieve and manage localized strings.
 *
 * @class TranslationModel
 * @constructor
 * @module stack.i18n.TranslationModel
 */
.factory('TranslationModel', [
    '$q',
    '$http',
    '$locale',
    'TranslationConfig',
    function ($q, $http, $locale, TranslationConfig) {
        'use strict';

        // Define.
        var service = {
            /**
             * Method requests object containing localized strings.
             *
             * @method read
             * @return {Object} Promise object
             */
            read: function () {
                var promise = $http({
                    cache: true,
                    method: 'GET',
                    url: TranslationConfig().path + TranslationConfig().namespace + '-' + $locale.id.split('-')[0] + '.json',
                    data: {cb: new Date().getTime()}
                });

                return promise;
            }
        };

        return service;
    }
]);
