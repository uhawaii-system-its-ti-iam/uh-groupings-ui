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
                    route: 'about',
                    permissions: ['member', 'owner']
                },
                {
                    key: 'NAVIGATION_GROUPING',
                    route: 'grouping',
                    permissions: ['owner']
                },
                {
                    key: 'NAVIGATION_MEMBERSHIPS',
                    route: 'membership',
                    permissions: ['member', 'owner']
                },
                {
                    key: 'NAVIGATION_GROUPINGS_SEARCH',
                    route: 'grouping-search',
                    permissions: ['member', 'owner']
                },
                {
                    key: 'NAVIGATION_DESIGNATE',
                    route: 'designate',
                    permissions: ['owner']
                },
                {
                    key: 'NAVIGATION_OPT_IN',
                    route: 'grouping-opt-in',
                    permissions: ['member']
                }
            ]
        };

        return service;
    }
]);
