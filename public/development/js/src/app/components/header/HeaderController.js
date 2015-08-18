angular.module('components.header.HeaderController', [
    'stack.authentication.AuthenticationService',
    'stack.location.LocationService'
])

/**
 * The HeaderController houses state and view logic
 * for the header directive.
 *
 * @class HeaderController
 * @constructor
 * @module components.header.HeaderController
 **/
.controller('HeaderController', [
    '$scope',
    '$window',
    '$timeout',
    'AuthenticationService',
    'LocationService',
    function ($scope, $window, $timeout, AuthenticationService, LocationService) {
        'use strict';

        // Define.
        var headerCtrl, windowEvent;

        /**
         * Property houses a reference to the Header Controller.
         *
         * @property headerCtrl
         * @type {Object}
         */
        headerCtrl = this;

        /**
         * Property houses the collapsed flag, which is used
         * to determine when the header's responsive menu
         * opens and closes.
         *
         * @property headerCtrl.collapsed
         * @type {Boolean}
         */
        headerCtrl.collapsed = true;

        /**
         * Method authenticates a user.
         *
         * @method headerCtrl.login
         * @private
         */
        headerCtrl.login = function () {
            // Only handle success condition. This functionality will be controlled
            // by a CAS integration. This code is for mock login purposes only.
            AuthenticationService.login('ckent', 'root').then(
                function (response) {
                    LocationService.redirect({route: headerCtrl.authenticatedRoute});
                }
            );
        };

        /**
         * Method un-authenticates a user.
         *
         * @method headerCtrl.logout
         */
        headerCtrl.logout = function () {
            // Only handle success condition. This functionality will be controlled
            // by a CAS integration. This code is for mock login purposes only.
            AuthenticationService.logout().then(
                function (response) {
                    LocationService.redirect({route: headerCtrl.unauthenticatedRoute});
                }
            );
        };

        /**
         * Method initializes event-based listeners.
         *
         * @method eventListeners
         * @private
         */
        function eventListeners() {
            windowEvent = angular.element($window).resize(function (e) {
                $scope.$apply(function () {
                    if (!headerCtrl.collapsed) {
                        headerCtrl.collapsed = true;
                    }
                });
            });

            $scope.$on('$destroy', destroyRegisteredEvents);
        }

        /**
         * Method destroys registered events.
         *
         * @method destroyRegisteredEvents
         * @private
         */
        function destroyRegisteredEvents() {
            windowEvent.off('resize');
        }

        /**
         * Method executes initialization process.
         *
         * @method initialize
         * @private
         */
        function initialize() {
            var t = $timeout(function () {
                eventListeners();
                $timeout.cancel(t);
            }, 0);
        }
        initialize();
    }
]);
