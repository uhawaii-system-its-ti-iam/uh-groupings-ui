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
                    label: 'About',
                    key: 'NAVIGATION_ABOUT',
                    route: 'about'
                },
                {
                    label: 'Designate Admins',
                    key: 'NAVIGATION_DESIGNATE',
                    route: 'designate'
                },
                {
                    label: 'Groupings',
                    key: 'NAVIGATION_GROUPINGS',
                    route: 'grouping'
                },
                {
                    label: 'My Memberships',
                    key: 'NAVIGATION_MEMBERSHIPS',
                    route: 'membership'
                },
                {
                    label: 'Feedback',
                    key: 'NAVIGATION_FEEDBACK',
                    route: 'feedback'
                }
            ]
        };

        return service;
    }
]);
