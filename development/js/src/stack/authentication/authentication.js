/**
 * Module definition houses the authentication feature.
 *
 * @module stack.authentication
 */
angular.module('stack.authentication', [
    'stack.authentication.AuthenticationConfig',
    'stack.authentication.AuthenticationModel',
    'stack.authentication.AuthenticationService',
    'stack.authentication.AuthenticationController',
    'stack.authentication.PublicResolver',
    'stack.authentication.ProtectResolver'
]);
