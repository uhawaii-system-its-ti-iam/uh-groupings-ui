angular.module('stack.i18n.localize', [
    'stack.i18n.TranslationService'
])

/**
 * The Localize filter takes a token and returns a translated
 * string using the Translation service. This method is more
 * processor-intensive given the number of times a filter is
 * called and should be used sparingly. If possible, leverage
 * the stackLocalize directive instead.
 *
 * @class Localize
 * @constructor
 * @module stack.i18n.localize
 * @example
 *     // The Localize filter should be placed within a template.
 *     {{"BUTTON_SIGNIN" | localize}}
 */
.filter('localize', [
    'TranslationService',
    function (TranslationService) {
        'use strict';

        return function (input) {
            var map = TranslationService.getCache(),
                translated = input;

            if (angular.isDefined(map) && angular.isObject(map) && _.has(map, input)) {
                translated = map[input];
            }

            return translated;
        };
    }
]);
