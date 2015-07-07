angular.module('stack.authentication.PublicResolver', [
    'stack.authentication.AuthenticationService'
])

/**
 * The PublicResolver service should only be applied to publicly-facing routes.
 * This service examines and returns the authentication status of the current user.
 * The user status is then leveraged to show/hide areas within globally-accessible
 * components such as the header or sidebar that require authentication to be seen.
 *
 * @class PublicResolver
 * @constructor
 * @module stack.authentication.PublicResolver
 */
.factory('PublicResolver', [
    '$q',
    'AuthenticationService',
    function ($q, AuthenticationService) {
        'use strict';

        return function () {
            var d = $q.defer(),
                user = AuthenticationService.getUser();

            user.then(
                function (response) {
                    // The AuthenticationService.getUser() API call will always return
                    // successful since the UI needs to know that state of a user's
                    // session. Errors for this call will only ever result in 500
                    // level errors.
                    d.resolve(response);
                },
                function (response) {
                    // TODO: Handle 500 level error condition.
                    d.resolve(response);
                }
            );

            return d.promise;
        };
    }
]);
