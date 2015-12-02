angular.module('components.listNavigation.uhgListNavigation', [
    'components.listNavigation.ListNavigationController',
    'stack.i18n.stackLocalize',
    'stack.i18n.localize',
    'ui.bootstrap'
])

/**
 * The ListNavigation directive houses the navigation for the application.
 *
 * @class ListNavigation
 * @constructor
 * @module components.listNavigation.uhgListNavigation
 * @example
 *     <uhg-list-navigation
 *         authenticated="true"     // Boolean flag used to show/hide sidebar routes.
 *         user="{}"                // Object of user attributes.
 *         items="[]">              // Array of navigation objects.
 *     </uhg-list-navigation>
 */
.directive('uhgListNavigation', [
    function () {
        'use strict';

        return {
            restrict: 'E',
            scope: {
                items: '=',
                user: '=',
                authenticated: '='
            },
            controller: 'ListNavigationController',
            controllerAs: 'listNavigationCtrl',
            bindToController: true,
            templateUrl: 'js/src/app/components/list-navigation/listNavigation.html'
        };
    }
]);
