angular.module('components.footer.uhgFooter', [
    'components.footer.FooterController',
    'stack.i18n.stackLocalize',
    'stack.i18n.localize'
])

/**
 * The Footer directive houses the application footer implementation.
 *
 * @class Footer
 * @constructor
 * @module components.footer.uhgFooter
 * @example
 *     <uhg-footer></uhg-footer>
 */
.directive('uhgFooter', [
    function () {
        'use strict';

        return {
            restrict: 'E',
            scope: {},
            controller: 'FooterController',
            controllerAs: 'footer',
            bindToController: true,
            templateUrl: 'js/src/app/components/footer/footer.html'
        };
    }
]);
