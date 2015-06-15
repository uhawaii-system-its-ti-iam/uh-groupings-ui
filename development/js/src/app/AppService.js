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
            api: ((DEVELOP) ? '/api' : '/api')
        };

        return service;
    }
]);
