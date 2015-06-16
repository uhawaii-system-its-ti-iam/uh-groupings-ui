angular.module('stack.page-loader.stackPageLoader', [])

/**
 * The PageLoader directive provides a spinning loading icon
 * as well as a container overlay that the covers underlying
 * DOM elements.
 *
 * @class PageLoader
 * @constructor
 * @module stack.page-loader.stackPageLoader
 * @example
 *     <stack-page-loader
 *         trigger="true"                   // Show/Hide flag.
 *         center="false"                   // Vertical center flag.
 *         opaque="false"                   // Opacity flag.
 *     </stack-page-loader>
 */
.directive('stackPageLoader', [
    '$window',
    '$timeout',
    function ($window, $timeout) {
        'use strict';

        return {
            restrict: 'E',
            scope: {
                trigger: '=',
                center: '=',
                opaque: '=',
                focusOn: '@'
            },
            templateUrl: 'js/src/stack/page-loader/page-loader.html',
            link: function (scope, element, attrs) {
                // Define.
                var timer, listen, position, opacity, watchTrigger, center, opaque, container;

                // Set value for the center parameter.
                center = (angular.isUndefined(scope.center)) ? false : scope.center;
                center = (typeof center !== 'boolean') ? false : center;

                // Set value for the opaque parameter.
                opaque = (angular.isUndefined(scope.opaque)) ? false : scope.opaque;
                opaque = (typeof opaque !== 'boolean') ? false : opaque;

                // Set value for container.
                container = element.parent()[0];

                // When triggered prevents all keyboard access (i.e., tabbing, delete, spacebar, etc.).
                listen = function (evt) {
                    evt.preventDefault();
                    evt.stopImmediatePropagation();
                };

                // Positions loader when the center parameter is set to true.
                position = function () {
                    element.removeClass('stack-page-loader-center');

                    if (center) {
                        element.addClass('stack-page-loader-center');
                    }
                };

                // Set opacity of overlay.
                opacity = function () {
                    element.find('.overlay').removeClass('overlay-opaque');

                    if (opaque) {
                        element.find('.overlay').addClass('overlay-opaque');
                    }
                };

                // Toggle event listener based upon trigger.
                watchTrigger = function (value) {
                    if (angular.isUndefined(value)) {
                        return;
                    }

                    if (value) {
                        container.addEventListener('keydown', listen, false);
                        angular.element(container).css({position: 'relative'});
                        timer = $timeout(function () {
                            element.find('.loader').height(element.find('.loader-inner').outerHeight());
                            $timeout.cancel(timer);
                        }, 0);

                        scope.tabindex = 0;
                    } else {
                        container.removeEventListener('keydown', listen, false);
                        angular.element(container).removeAttr('style');
                        scope.tabindex = -1;
                    }
                };

                position();
                opacity();
                scope.$watch('trigger', watchTrigger);
            }
        };
    }
]);
