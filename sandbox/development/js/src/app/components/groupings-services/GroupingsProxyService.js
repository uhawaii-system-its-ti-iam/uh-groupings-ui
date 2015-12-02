angular.module('components.groupingsServices.GroupingsProxy', [
    'stack.authentication.AuthenticationConfig',
    'stack.develop.DevelopConfig'
])

/**
 * The GroupingsProxy houses CRUD-based methods for group interactions.
 *
 * @class GroupingsProxy
 * @constructor
 * @module components.groupingsServices.GroupingsProxy
 */
.factory('GroupingsProxy', [
    '$http',
    'DevelopConfig',
    'AuthenticationConfig',
    function ($http, DevelopConfig, AuthenticationConfig) {

        // Define.
        var proxy, baseEndpoint, groupingsEndpoint, userEndpoint;

        /**
         * Property houses the base endpoint for all REST calls.
         *
         * @property baseEndpoint
         * @type {String}
         * @private
         */
        baseEndpoint = DevelopConfig().develop ? AuthenticationConfig().developmentAPIBase : AuthenticationConfig.productionAPIBase;

        /**
         * Property houses the endpoint for REST calls used in groupings interactions.
         *
         * @property groupingsEndpoint
         * @type {String}
         * @private
         */
        groupingsEndpoint = [baseEndpoint, 'groupings'].join('/');

        /**
         * Property houses the endpoint for REST calls used in user groupings.
         *
         * @property userEndpoint
         * @type {String}
         * @private
         */
        userEndpoint = [baseEndpoint, 'user'].join('/');

        /**
         * Property houses the service proxy for communicating with the backend.
         *
         * @property proxy
         * @type {Object}
         * @private
         */
        proxy = {
            /**
             * Method returns groups matching the given searchPhrase.
             *
             * Note: Method does not handle error condition.
             *
             * @method proxy.query
             * @param {String} searchPhrase Phrase to search groups by
             * @return {Object} Promise
             */
            query: function (searchPhrase) {
                return $http.get([groupingsEndpoint, '?query=', searchPhrase].join(''));
            },

            /**
             * Method returns complete data set for a single grouping
             * given the passed grouping identifier.
             *
             * Note: Method does not handle error condition.
             *
             * @method getGroup
             * @param {String} groupingId Group identifier
             * @return {Object} Promise
             */
            getGroup: function (groupingId) {
                return $http.get([groupingsEndpoint, groupingId].join('/'));
            },

            /**
             * Method returns groups owned by a specific user.
             *
             * Note: Method does not handle error condition.
             *
             * @method getOwnedGroups
             * @param {String|Number} userId User identifier who is the owner of groups
             * @return {Object} Promise
             */
            getOwnedGroups: function (userId) {
                return $http.get([userEndpoint, userId, 'groupings', 'owned'].join('/'));
            },

            /**
             * Method returns groups with a specific user as a member.
             *
             * Note: Method does not handle error condition.
             *
             * @method getGroupMemberships
             * @param {String|Number} userId Id of the user who is member of groups
             * @return {Object} Promise
             */
            getGroupMemberships: function (userId) {
                return $http.get([userEndpoint, userId, 'groupings'].join('/'));
            }
        };

        return proxy;
    }]);
