angular.module('stack.location.LocationService', [
    'ui.router'
])

/**
 * The LocationService extends the angular $location service
 * and the ui router $state service. It provides additional
 * url parsing methods and location functionality.
 *
 * @class LocationService
 * @constructor
 * @module stack.location.LocationService
 */
.factory('LocationService', [
    '$state',
    '$location',
    '$timeout',
    function ($state, $location, $timeout) {
        'use strict';

        var service = {
            /**
             * Method checks the existence of each parameter against
             * known url parameters.
             *
             * @method hasQueryParameter
             * @param {String} parameter Parameter name
             * @return {Boolean} True or False
             */
            hasQueryParameter: function (parameter) {
                var queries = $location.search();
                return (_.has(queries, parameter)) ? true : false;
            },

            /**
             * Method iterates over each parameter and checks the
             * existence of each parameter against known url
             * parameters.
             *
             * @method hasQueryParameters
             * @param {Array} parameters Parameter names
             * @return {Boolean} True or False
             */
            hasQueryParameters: function (parameters) {
                var hasQueries = [];

                angular.forEach(parameters, function (parameter) {
                    hasQueries.push(service.hasQueryParameter(parameter));
                });

                return (_.indexOf(hasQueries, false) === -1) ? true : false;
            },

            /**
             * Method strips first leading slash from the path.
             *
             * @method stripLeadingSlash
             * @param {String} path Route path
             * @return {String} Path
             */
            stripLeadingSlash: function (path) {
                if (angular.isUndefined(path) || typeof path !== 'string') {
                    return path;
                }

                var index = path.indexOf('/'),
                    str;

                if (index !== -1) {
                    str = path.slice((index + 1));
                } else {
                    str = path;
                }

                return str;
            },

            /**
             * Method returns the first route found from the path.
             *
             * @method getRootPath
             * @param {String} path Route path
             * @return {String} First route
             */
            getRootPath: function (path) {
                if (angular.isUndefined(path) || !angular.isString(path) || _.isEmpty(path)) {
                    return path;
                }

                var split = path.split('/'),
                    str = split[1];

                return str;
            },

            /**
             * Method forces a reload of the application.
             *
             * @method redirect
             * @param {Object} options Options object
             */
            redirect: function (options) {
                options = (angular.isUndefined(options)) ? {route: $state.current, params: null, timer: 0, config: {reload: true}} : options;
                if (!_.has(options, 'route')) {
                    options.route = $state.current;
                }
                if (!_.has(options, 'params')) {
                    options.params = null;
                }
                if (!_.has(options, 'timer')) {
                    options.timer = 0;
                }
                if (!_.has(options, 'config')) {
                    options.config = {reload: true};
                }

                var t = $timeout(function () {
                    $state.go(options.route, options.params, options.config);
                    $timeout.cancel(t);
                }, options.timer);
            }
        };

        return service;
    }
]);
