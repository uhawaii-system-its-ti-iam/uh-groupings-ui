angular.module('stack.authentication.AuthenticationController', [
    'stack.authentication.AuthenticationService',
    'stack.authentication.AuthenticationConfig',
    'stack.location.LocationService',
    'ui.router'
])

/**
 * The AuthenticationController aids in the protecting and locking down
 * of authenticated routes. Unauthorized requests are redirected to a
 * configurable unauthenticated route.
 *
 * @class AuthenticationController
 * @constructor
 * @module stack.authentication.AuthenticationController
 **/
.controller('AuthenticationController', [
    '$scope',
    '$state',
    'LocationService',
    'AuthenticationConfig',
    'AuthenticationService',
    function ($scope, $state, LocationService, AuthenticationConfig, AuthenticationService) {
        'use strict';

        // Define.
        var authCtrl, registeredEvents;

        /**
         * Property houses a reference to the Authentication Controller.
         *
         * @property authCtrl
         * @type {Object}
         */
        authCtrl = this;

        /**
         * Property houses authentication flag.
         *
         * @property authCtrl.authenticated
         * @type {Boolean}
         */
        authCtrl.authenticated = AuthenticationService.isAuthenticated();

        /**
         * Method redirects the user.
         *
         * @method redirect
         * @param {String} path Path to evaluate
         * @private
         */
        function redirect(path) {
            path = (!path) ? AuthenticationConfig().unauthenticatedRoute : path;

            // Redirect.
            LocationService.redirect({route: path});
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
            if (rejection && rejection.hasOwnProperty('path')) {
                redirect(rejection.path);
            }
        }

        /**
         * Method initializes event-based listeners.
         *
         * @method eventListeners
         * @private
         */
        function eventListeners() {
            var error, authenticated;

            error = $scope.$on('$stateChangeError', onRouteChangeError);
            authenticated = $scope.$watch(
                function () {
                    return AuthenticationService.isAuthenticated();
                },
                function (value) {
                    authCtrl.authenticated = value;
                }
            );

            registeredEvents = [error, authenticated];
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
         * Method exectues initialization process.
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
