angular.module('components.list-navigation.uhgListNavigation', [
    'components.list-navigation.ListNavigationController',
    'stack.i18n.stackLocalize',
    'stack.i18n.localize'
])

/**
 * The ListNavigation directive houses the side navigation for the application.
 *
 * @class ListNavigation
 * @constructor
 * @module components.list-navigation.uhgListNavigation
 * @example
 *     <uhg-list-navigation
 *         items="[]">              // Array of navigation objects.
 *     </uhg-list-navigation>
 */
.directive('uhgListNavigation', [
    function () {
        'use strict';

        return {
            restrict: 'E',
            scope: {
                items: '='
            },
            controller: 'ListNavigationController',
            controllerAs: 'listNavigationCtrl',
            bindToController: true,
            templateUrl: 'js/src/app/components/list-navigation/list-navigation.html'
        };
    }
]);
