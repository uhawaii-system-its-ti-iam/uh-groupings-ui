angular.module('app.AppController', [
    'app.AppService',
    'ui.router'
])

/**
 * The AppController is the top-most controller for the application.
 *
 * @class AppController
 * @constructor
 * @module app.AppController
 **/
.controller('AppController', [
    '$scope',
    '$state',
    '$timeout',
    '$window',
    'AppService',
    'TranslationService',
    function ($scope, $state, $timeout, $window, AppService, TranslationService) {
        'use strict';

        // Define.
        var appCtrl, registeredEvents;

        /**
         * Property houses a reference to the AppController.
         *
         * @property appCtrl
         * @type {Object}
         */
        appCtrl = this;

        /**
         * Property houses a reference to application theme.
         *
         * @property appCtrl.theme
         * @type {String}
         */
        appCtrl.theme = 'uofhawaii';

        /**
         * Property houses a loading flag. This flag is used
         * to hide and show the application overlay.
         *
         * @property appCtrl.pageLoading
         * @type {Boolean}
         */
        appCtrl.pageLoading = true;

        /**
         * Property houses a opacity flag. This flag is used
         * to control the opacity of the application overlay.
         *
         * @property appCtrl.pageLoadingOpaque
         * @type {Boolean}
         */
        appCtrl.pageLoadingOpaque = true;

        /**
         * Property houses a collection of navigation objects.
         *
         * @property appCtrl.navigation
         * @type {Array}
         */
        appCtrl.navigation = AppService.navigation;

        /**
         * Property houses a reference to application-level timeout setting.
         *
         * @property appCtrl.timeout
         * @type {Object}
         */
        appCtrl.timeout = 1500;

        /**
         * Method is triggered when a state transition begins.
         *
         * @method onRouteChangeStart
         * @param {Object} evt Event object.
         * @param {Object} toState The state being transitioned to.
         * @param {Object} toParams The params supplied to the toState.
         * @param {Object} fromState The current state, pre-transition.
         * @param {Object} fromParams The params supplied to the fromState.
         * @private
         */
        function onRouteChangeStart(evt, toState, toParams, fromState, fromParams) {
            showApplicationOverlay();
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
        function onRouteChangeSuccess(evt, toState, toParams, fromState, fromParams, succession) {
            hideApplicationOverlay();
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
                // In some cases the redirect 'path' can equal the 'current'
                // path. When this happens, the ui-router implementation
                // (i.e., $state.go()) does not redirect or refresh the interface.
                // When this happens the loader is never hidden due to the fact
                // that the $stateChangeSuccess event is never fired. The below
                // code block resolves this case.
                if (rejection.path === $state.current.name) {
                    hideApplicationOverlay();
                }
            } else {
                hideApplicationOverlay();
            }
        }

        /**
         * Method renders the application overlay.
         *
         * @method showRouteMask
         * @private
         */
        function showApplicationOverlay() {
            appCtrl.pageLoading = true;
        }

        /**
         * Method hides the application overlay.
         *
         * @method hideApplicationOverlay
         * @private
         */
        function hideApplicationOverlay() {
            var t = $timeout(function () {
                $window.scrollTo(0, 0);
                appCtrl.pageLoading = false;

                if (appCtrl.pageLoadingOpaque) {
                    appCtrl.pageLoadingOpaque = false;
                }

                $timeout.cancel(t);
            }, appCtrl.timeout);
        }

        /**
         * Method initializes event-based listeners.
         *
         * @method eventListeners
         * @private
         */
        function eventListeners() {
            var start, success, error;
            start = $scope.$on('$stateChangeStart', onRouteChangeStart);
            success = $scope.$on('$stateChangeSuccess', onRouteChangeSuccess);
            error = $scope.$on('$stateChangeError', onRouteChangeError);
            registeredEvents = [start, success, error];

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
