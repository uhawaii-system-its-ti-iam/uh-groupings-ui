angular.module('components.header.uhgHeader', [
    'components.header.HeaderController',
    'stack.i18n.stackLocalize',
    'stack.i18n.localize'
])

/**
 * The Header directive houses the application header implementation.
 *
 * @class Header
 * @constructor
 * @module components.header.uhgHeader
 * @example
 *     <uhg-header
 *         brand-link="login"                       // Route to link.
 *         authenticated="true"                     // Boolean flag that controls rendering of login/logout links.
 *         authenticated-route="about"              // Route to redirect to on successful login.
 *         unauthenticated-route="about"            // Route to redirect to on unsuccessful login.
 *         logo-path="/path/to/image.png">          // Path to header logo.
 *     </uhg-header>
 */
.directive('uhgHeader', [
    function () {
        'use strict';

        return {
            restrict: 'E',
            scope: {
                brandLink: '@',
                logoPath: '@',
                authenticated: '=',
                authenticatedRoute: '@',
                unauthenticatedRoute: '@'
            },
            controller: 'HeaderController',
            controllerAs: 'headerCtrl',
            bindToController: true,
            templateUrl: 'js/src/app/components/header/header.html'
        };
    }
]);
