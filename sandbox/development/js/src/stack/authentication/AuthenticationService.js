angular.module('stack.authentication.AuthenticationService', [
    'stack.authentication.AuthenticationModel',
    'angular-data.DSCacheFactory'
])

/**
 * The AuthenticationService wraps the AuthenticationModel and houses a
 * collection of methods used to manage the authentication of users.
 *
 * @class AuthenticationService
 * @constructor
 * @module stack.authentication.AuthenticationService
 */
.factory('AuthenticationService', [
    '$q',
    'AuthenticationModel',
    'DSCacheFactory',
    function ($q, AuthenticationModel, DSCacheFactory) {
        'use strict';

        var service = {
            /**
             * Property houses cached model.
             *
             * @property cache
             * @type {Object}
             */
            cache: DSCacheFactory('UserModel'),

            /**
             * Property houses resource namespace.
             *
             * @property resource
             * @type {String}
             */
            resource: 'user',

            /**
             * Property houses authentication flag.
             *
             * @property authenticated
             * @type {Boolean}
             */
            authenticated: false,

            /**
             * Method returns the state of the cached user object.
             *
             * @method getUser
             * @return {Object} Cached user object
             */
            getUser: function () {
                var d = $q.defer(),
                    cache = service.cache.get(service.resource);

                if (angular.isDefined(cache) && angular.isObject(cache) && !_.isEmpty(cache) && _.has(cache, 'username') && _.has(cache, 'email')) {
                    d.resolve(cache);
                } else {
                    AuthenticationModel.getUser().then(
                        function (response) {
                            var result = response.data;
                            service.authenticated = (_.isEmpty(result)) ? false : true;
                            service.cache.put(service.resource, result);
                            d.resolve(service.cache.get(service.resource));
                        }
                    );
                }

                return d.promise;
            },

            /**
             * Method returns the user's authenticated state.
             *
             * @method isAuthenticated
             * @return {Boolean} Authenticated state
             */
            isAuthenticated: function () {
                return service.authenticated;
            },

            /**
             * Method leverages the AuthenticationModel to authenticate a user.
             *
             * @method login
             * @param {String} username Username
             * @param {String} password Password
             * @return {Object} Promise object
             */
            login: function (username, password) {
                var d = $q.defer();

                AuthenticationModel.login({username: username, password: password}).then(
                    function (response) {
                        var result = response.data;
                        service.authenticated = true;
                        service.cache.put(service.resource, result);
                        d.resolve(service.cache.get(service.resource));
                    },
                    function (response) {
                        // Pass execption along to implementation.
                        d.reject(response);
                    }
                );

                return d.promise;
            },

            /**
             * Method leverages the AuthenticationModel to un-authenticate a user.
             *
             * @method logout
             * @return {Object} Promise object
             */
            logout: function () {
                var d = $q.defer();

                AuthenticationModel.logout().then(
                    function (response) {
                        service.authenticated = false;
                        service.cache.put(service.resource, {});
                        d.resolve(response);
                    },
                    function (response) {
                        // Pass execption along to implementation.
                        d.reject(response);
                    }
                );

                return d.promise;
            }
        };

        return service;
    }
]);
