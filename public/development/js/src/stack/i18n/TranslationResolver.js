angular.module('stack.i18n.TranslationResolver', [
    'stack.i18n.TranslationService'
])

/**
 * The TranslationResolver service loads a map of strings based
 * upon the locale reported by the browser.
 *
 * @class TranslationResolver
 * @constructor
 * @module stack.i18n.TranslationResolver
 */
.factory('TranslationResolver', [
    '$q',
    'TranslationService',
    function ($q, TranslationService) {
        'use strict';

        return function () {
            // Define.
            var d, model;

            // Initialize.
            d = $q.defer();

            // Load translation map.
            model = TranslationService.get();
            model.then(
                function (response) {
                    d.resolve(response);
                },
                function (response) {
                    d.resolve(response);
                }
            );

            return d.promise;
        };
    }
]);
