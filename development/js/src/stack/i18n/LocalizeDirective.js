angular.module('stack.i18n.stackLocalize', [
    'stack.i18n.TranslationService'
])

/**
 * The Localize directive takes a token and returns a translated
 * string using the Translation service and injects the translated
 * string into the DOM element.
 *
 * @class Localize
 * @constructor
 * @module stack.i18n.stackLocalize
 * @example
 *     <div stack-localize="APPLICATION_NAME"></div>
 */
.directive('stackLocalize', [
    'TranslationService',
    function (TranslationService) {
        'use strict';

        return {
            restrict: 'EA',
            link: function (scope, element, attrs) {
                /**
                 * Method injects translated token into the element DOM.
                 *
                 * @method inject
                 * @param {String} translated Localized string to inject
                 * @private
                 */
                function inject(translated) {
                    if ((translated !== null) && (translated !== undefined) && (translated !== '')) {
                        element.text(translated);
                    }
                }

                /**
                 * Method checks Translation service for token
                 * and inserts it into DOM element.
                 *
                 * @method updateText
                 * @param {String} token Text to translate
                 * @private
                 */
                function translate(token) {
                    var promise = TranslationService.translate(token);
                    promise.then(inject);
                }

                /**
                 * Method exectues the initialization process.
                 *
                 * @method initialize
                 * @private
                 */
                function initialize() {
                    attrs.$observe('stackLocalize', function (token) {
                        translate(token);
                    });
                }
                initialize();
            }
        };
    }
]);
