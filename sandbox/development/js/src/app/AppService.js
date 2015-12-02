angular.module('app.AppService', [])

/**
 * The AppService houses application-level properties and methods.
 *
 * @class AppService
 * @constructor
 * @module app.AppService
 */
.factory('AppService', [
    function () {
        'use strict';

        var service = {
            /**
             * Property houses API namespace.
             *
             * @property api
             * @type {String}
             */
            navigation: [
                {
                    key: 'NAVIGATION_ABOUT',
                    route: 'about'
                },
                {
                    key: 'NAVIGATION_GROUPING',
                    route: 'grouping'
                },
                {
                    key: 'NAVIGATION_MEMBERSHIPS',
                    route: 'membership'
                },
                {
                    key: 'NAVIGATION_GROUPINGS_SEARCH',
                    route: 'grouping-search'
                },
                {
                    key: 'NAVIGATION_DESIGNATE',
                    route: 'designate'
                }
            ]
        };

        return service;
    }
]);
