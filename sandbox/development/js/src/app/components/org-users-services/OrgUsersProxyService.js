angular.module('components.orgUsersServices.OrgUsersProxy', [
    'stack.authentication.AuthenticationConfig',
    'stack.develop.DevelopConfig'
])

/**
 * The OrgUsersProxy houses CRUD-based methods for organization users interactions.
 *
 * @class OrgUsersProxy
 * @constructor
 * @module components.orgUsersServices.OrgUsersProxy
 */
.factory('OrgUsersProxy', [
    '$http',
    'DevelopConfig',
    'AuthenticationConfig',
    function ($http, DevelopConfig, AuthenticationConfig) {

        // Define.
        var proxy, baseEndpoint, usersEndpoint;

        /**
         * Property houses the base endpoint for all REST calls.
         *
         * @property baseEndpoint
         * @type {String}
         * @private
         */
        baseEndpoint = DevelopConfig().develop ? AuthenticationConfig().developmentAPIBase : AuthenticationConfig.productionAPIBase;

        /**
         * Property houses the endpoint for REST calls used in user groupings.
         *
         * @property userEndpoint
         * @type {String}
         * @private
         */
        usersEndpoint = [baseEndpoint, 'users'].join('/');

        /**
         * Property houses the service proxy for communicating with the backend.
         *
         * @property proxy
         * @type {Object}
         * @private
         */
        proxy = {
            /**
             * Method returns a list of all users in an organization.
             *
             * Note: Method does not handle error condition.
             *
             * @method list
             * @return {Object} Promise
             */
            list: function () {
                return $http.get(usersEndpoint);
            }
        };

        return proxy;
    }
]);
