angular.module('stack.authentication.AuthenticationModel', [
    'stack.authentication.AuthenticationConfig',
    'stack.develop.DevelopConfig'
])

/**
 * The AuthenticationModel houses CRUD-based methods used to
 * authenticate users.
 *
 * @class AuthenticationModel
 * @constructor
 * @module stack.authentication.AuthenticationModel
 */
.factory('AuthenticationModel', [
    '$q',
    '$log',
    '$http',
    'DevelopConfig',
    'AuthenticationConfig',
    function ($q, $log, $http, DevelopConfig, AuthenticationConfig) {
        'use strict';

        var service = {
            /**
             * Property houses API base.
             *
             * @property base
             * @type {String}
             */
            base: (DevelopConfig().develop) ? AuthenticationConfig().developmentAPIBase : AuthenticationConfig().productionAPIBase,

            /**
             * Method returns user information.
             *
             * @method getUser
             * @param {Object} resourceParams Query parameters
             * @return {Object} Promise
             */
            getUser: function (resourceParams) {
                resourceParams = (angular.isUndefined(resourceParams)) ? {} : resourceParams;

                var promise = $http({
                    method: 'GET',
                    url: service.base + '/user',
                    params: angular.extend(resourceParams, {cb: new Date().getTime()})
                });

                return promise;
            },

            /**
             * Method authenticates user.
             *
             * @method login
             * @param {Object} resourceParams Post data
             * @return {Object} Promise
             */
            login: function (resourceParams) {
                resourceParams = (angular.isUndefined(resourceParams)) ? {} : resourceParams;

                var promise = $http({
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    url: service.base + '/login',
                    params: {cb: new Date().getTime()},
                    data: $.param(resourceParams)
                });

                return promise;
            },

            /**
             * Method un-authenticates a user.
             *
             * @method logout
             * @param {Object} resourceParams Post data
             * @return {Object} Promise
             */
            logout: function (resourceParams) {
                resourceParams = (angular.isUndefined(resourceParams)) ? {} : resourceParams;

                var promise = $http({
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    url: service.base + '/logout',
                    params: {cb: new Date().getTime()},
                    data: $.param(resourceParams)
                });

                return promise;
            }
        };

        return service;
    }
]);
