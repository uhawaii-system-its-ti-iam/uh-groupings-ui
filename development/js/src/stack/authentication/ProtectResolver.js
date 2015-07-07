angular.module('stack.authentication.ProtectResolver', [
    'stack.authentication.AuthenticationConfig',
    'stack.authentication.AuthenticationService'
])

/**
 * The ProtectResolver service should only be applied to routes that require
 * authentication. This service examines the authentication status of the
 * current user. If a user is authenticated they are allowed to view the
 * route. If a user is unauthenticated they are directed back to a configured
 * unauthenticated route.
 *
 * @class ProtectResolver
 * @constructor
 * @module stack.authentication.ProtectResolver
 */
.factory('ProtectResolver', [
    '$q',
    'AuthenticationConfig',
    'AuthenticationService',
    function ($q, AuthenticationConfig, AuthenticationService) {
        'use strict';
        return function () {
            var d = $q.defer(),
                user = AuthenticationService.getUser(),
                route = AuthenticationConfig().unauthenticatedRoute;

            user.then(
                function (response) {
                    // The AuthenticationService.getUser() API call will always return
                    // successful since the UI needs to know that state of a user's
                    // session. Errors for this call will only ever result in 500
                    // level errors.
                    if (!_.isEmpty(response)) {
                        d.resolve(response);
                    } else {
                        d.reject({path: route});
                    }
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
