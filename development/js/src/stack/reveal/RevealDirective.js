angular.module('stack.reveal.stackReveal', [])

/**
 * The Reveal directive prevents UI flicker from happening
 * when an application is first loaded. This implementation
 * is different then using ng-cloak. The ng-clock directive
 * will prevent the angular brackets from rendering to end
 * users. The Reveal directive prevents a flicker caused
 * by css and js resources from loading at different times.
 *
 * @class Reveal
 * @constructor
 * @module stack.reveal.stackReveal
 * @example
 *     <body class="invisible" stack-reveal="invisible">
 */
.directive('stackReveal', [
    '$timeout',
    function ($timeout) {
        'use strict';

        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                // Add class name to element.
                element.addClass(attrs.stackReveal);

                // Remove class name at the end of the call stack.
                var t = $timeout(function () {
                    if (element.hasClass(attrs.stackReveal)) {
                        element.removeClass(attrs.stackReveal);
                    }

                    $timeout.cancel(t);
                }, 0);
            }
        };
    }
]);
