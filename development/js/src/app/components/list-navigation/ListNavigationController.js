angular.module('components.list-navigation.ListNavigationController', [
    'components.list-navigation.ListNavigationService'
])

/**
 * The ListNavigationController houses state and view logic
 * for the ListNavigation directive.
 *
 * @class ListNavigationController
 * @constructor
 * @module components.list-navigation.ListNavigationController
 **/
.controller('ListNavigationController', [
    '$scope',
    'ListNavigationService',
    function ($scope, ListNavigationService) {
        'use strict';

        // Define.
        var listNavigationCtrl, registeredEvents;

        /**
         * Property houses a reference to the listNavigationController.
         *
         * @property listNavigationCtrl
         * @type {Object}
         */
        listNavigationCtrl = this;

        /**
         * Property houses the index position of the selected
         * or active navigational item.
         *
         * @property listNavigationCtrl.selectedNavigationPosition
         * @type {Number}
         */
        listNavigationCtrl.selectedNavigationPosition = 0;

        /**
         * Property houses the label of the selected or active
         * navigational item.
         *
         * @property listNavigationCtrl.selectedNavigationLabel
         * @type {String}
         */
        listNavigationCtrl.selectedNavigationLabel = listNavigationCtrl.items[listNavigationCtrl.selectedNavigationPosition].label;

        /**
         * Property houses the route of the selected or active
         * navigational item.
         *
         * @property listNavigationCtrl.selectedNavigationLabel
         * @type {String}
         */
        listNavigationCtrl.selectedNavigationRoute = listNavigationCtrl.items[listNavigationCtrl.selectedNavigationPosition].route;

        /**
         * Method updates the selected navigation properties when the route changes.
         * This method updates the naviational dropdown that appears at smaller screen sizes.
         *
         * @method updateSelectedNavigation
         * @param {String} route The state being transitioned to.
         * @private
         */
        function updateSelectedNavigation(route) {
            var items = listNavigationCtrl.items,
                position = ListNavigationService.getNavigationPositionFromRoute(route, items);

            listNavigationCtrl.selectedNavigationPosition = position;
            listNavigationCtrl.selectedNavigationLabel = listNavigationCtrl.items[listNavigationCtrl.selectedNavigationPosition].label;
            listNavigationCtrl.selectedNavigationRoute = listNavigationCtrl.items[listNavigationCtrl.selectedNavigationPosition].route;
        }

        /**
         * Method is triggered when a state transition completes.
         *
         * @method onRouteChangeError
         * @param {Object} evt Event object.
         * @param {Object} toState The state being transitioned to.
         * @param {Object} toParams The params supplied to the toState.
         * @param {Object} fromState The current state, pre-transition.
         * @param {Object} fromParams The params supplied to the fromState.
         * @private
         */
        function onRouteChangeSuccess(evt, toState, toParams, fromState, fromParams) {
            updateSelectedNavigation(toState.name);
        }

        /**
         * Method is triggered when any resolve promises are rejected.
         *
         * @method onRouteChangeError
         * @param {Object} evt Event object.
         * @param {Object} toState The state being transitioned to.
         * @param {Object} toParams The params supplied to the toState.
         * @param {Object} fromState The current state, pre-transition.
         * @param {Object} fromParams The params supplied to the fromState.
         * @param {Object} rejection The resolve error object.
         * @private
         */
        function onRouteChangeError(evt, toState, toParams, fromState, fromParams, rejection) {
            updateSelectedNavigation(toState.name);
        }

        /**
         * Method initializes event-based listeners.
         *
         * @method eventListeners
         * @private
         */
        function eventListeners() {
            var success, error;
            success = $scope.$on('$stateChangeSuccess', onRouteChangeSuccess);
            error = $scope.$on('$stateChangeError', onRouteChangeError);
            registeredEvents = [success, error];

            $scope.$on('$destroy', destroyRegisteredEvents);
        }

        /**
         * Method destroys registered events.
         *
         * @method destroyRegisteredEvents
         * @private
         */
        function destroyRegisteredEvents() {
            angular.forEach(registeredEvents, function (registered) {
                if (angular.isFunction(registered)) {
                    registered();
                }
            });
        }

        /**
         * Method executes initialization process.
         *
         * @method initialize
         * @private
         */
        function initialize() {
            eventListeners();
        }
        initialize();
    }
]);
