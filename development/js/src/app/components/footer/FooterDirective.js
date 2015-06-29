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
 *     <uhg-footer
 *         logo-path="/path/to/image.png"           // Path to footer logo.
 *         copyrightKey="FOOTER_COPYRIGHT">         // Key for localized copyright.
 *     </uhg-footer>
 */
.directive('uhgFooter', [
    function () {
        'use strict';

        return {
            restrict: 'E',
            scope: {
                logoPath: '@',
                copyrightKey: '@'
            },
            controller: 'FooterController',
            controllerAs: 'footerCtrl',
            bindToController: true,
            templateUrl: 'js/src/app/components/footer/footer.html'
        };
    }
]);
