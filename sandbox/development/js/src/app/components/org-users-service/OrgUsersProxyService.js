angular.module('components.orgUsersService.OrgUsersProxy', [
        'stack.authentication.AuthenticationConfig',
        'stack.develop.DevelopConfig'
    ])

    /**
     * The OrgUsersProxy houses CRUD-based methods for organization users interactions.
     *
     * @class OrgUsersProxy
     * @constructor
     * @module components.orgUsersService.OrgUsersProxy
     */
    .factory('OrgUsersProxy', [
        '$http',
        'DevelopConfig',
        'AuthenticationConfig',
        function ($http, DevelopConfig, AuthenticationConfig) {

            //define
            var svc, baseEndpoint, usersEndpoint;

            /**
             * Property houses the base endpoint for all REST calls
             *
             * @property baseEndpoint
             * @type {string}
             */
            baseEndpoint = DevelopConfig().develop ?
                AuthenticationConfig().developmentAPIBase :
                AuthenticationConfig.productionAPIBase;

            /**
             * Property houses the endpoint for REST calls used in user groupings
             *
             * @property userEndpoint
             * @type {string}
             */
            usersEndpoint = [baseEndpoint, 'users'].join('/');

            /**
             * Property houses the service proxy for communicating with the backend
             * @type {Object}
             */
            svc = {

                /**
                 * Method returns a list of all users in an organization
                 *
                 * @method list
                 * @return {Array[OrgUser]}
                 */
                list: function () {
                    return $http.get(usersEndpoint);
                }
            };

            return svc;
        }]);

