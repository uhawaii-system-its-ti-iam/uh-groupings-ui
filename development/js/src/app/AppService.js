angular.module('app.AppService', ['app.development'])

/**
 * The AppService houses application-level properties and methods.
 *
 * @class AppService
 * @constructor
 * @module app.AppService
 */
.factory('AppService', [
    'DEVELOP',
    function (DEVELOP) {
        'use strict';

        var service = {
            /**
             * Property houses base namespace.
             *
             * @property base
             * @type {String}
             */
            base: ((DEVELOP) ? '' : ''),

            /**
             * Property houses API namespace.
             *
             * @property api
             * @type {String}
             */
            api: ((DEVELOP) ? '/api' : '/api'),

            /**
             * Property houses API namespace.
             *
             * @property api
             * @type {String}
             */
            navigation: [
                {
                    label: 'Login',
                    key: 'NAVIGATION_LOGIN',
                    route: 'login'
                },
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
