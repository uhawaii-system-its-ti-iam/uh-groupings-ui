angular.module('components.groupingsService.GroupingsProxy', [
    'stack.authentication.AuthenticationConfig',
    'stack.develop.DevelopConfig'
])

/**
 * The GroupingsProxy houses CRUD-based methods for group interactions.
 *
 * @class GroupingsProxy
 * @constructor
 * @module components.groupingsService.GroupingsProxy
 */
.factory('GroupingsProxy', [
    '$http',
    'DevelopConfig',
    'AuthenticationConfig',
    function ($http, DevelopConfig, AuthenticationConfig) {

        //define
        var svc, baseEndpoint, groupingsEndpoint, userEndpoint;

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
         * Property houses the endpoint for REST calls used in groupings interactions
         *
         * @property groupingsEndpoint
         * @type {string}
         */
        groupingsEndpoint = [baseEndpoint, 'groupings'].join('/');

        /**
         * Property houses the endpoint for REST calls used in user groupings
         *
         * @property userEndpoint
         * @type {string}
         */
        userEndpoint = [baseEndpoint, 'user'].join('/');

        /**
         * Property houses the service proxy for communicating with the backend
         * @type {{query: query, getOwnedGroups: getOwnedGroups, getGroupMemberships: getGroupMemberships}}
         */
        svc = {

            /**
             * Method returns groups matching a searchPhrase.
             *
             * @method query
             * @param {String} searchPhrase Phrase to search groups by
             * @return {Object} Promise
             */
            query: function (searchPhrase) {
                return $http.get([groupingsEndpoint, '?query=', searchPhrase].join(''));
            },

            /**
             * Method returns groups owned by a specific user.
             *
             * @method getOwnedGroups
             * @param {String|Number} userId Id of the user who is owner of groups
             * @return {Object} Promise
             */
            getOwnedGroups: function (userId) {
                return $http.get([userEndpoint, userId, 'groupings', 'owned'].join('/'));
            },

            /**
             * Method returns groups with a specific user as a member
             *
             * @method getGroupMemberships
             * @param {String|Number} userId Id of the user who is member of groups
             * @return {Object} Promise
             */
            getGroupMemberships: function (userId) {
                return $http.get([userEndpoint, userId, 'groupings'].join('/'));
            }
        };

        return svc;
    }]);
