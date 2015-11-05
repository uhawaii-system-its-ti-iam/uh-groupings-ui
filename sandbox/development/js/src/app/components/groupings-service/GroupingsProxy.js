angular.module('components.groupingsService.GroupingsProxy', [])

/**
 * The GroupingsProxy houses CRUD-based methods for group interactions.
 *
 * @class GroupingsProxy
 * @constructor
 * @module components.groupingsService.GroupingsProxy
 */
.factory('GroupingsProxy', [
    '$http',
    function ($http) {

        //define
        var svc, baseEndpoint;

        /**
         * Property houses the base endpoint for all REST calls used in groups interactions
         *
         * @property baseEndpoint
         * @type {String}
         */
        baseEndpoint = '/api/groupings';

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
                return $http.get([baseEndpoint, '?query=', searchPhrase].join(''));
            },

            /**
             * Method returns groups owned by a specific user.
             *
             * @method getOwnedGroups
             * @param {String|Number} userId Id of the user who is owner of groups
             * @return {Object} Promise
             */
            getOwnedGroups: function (userId) {
                return $http.get([baseEndpoint, userId, 'owned'].join('/'));
            },

            /**
             * Method returns groups with a specific user as a member
             *
             * @method getGroupMemberships
             * @param {String|Number} userId Id of the user who is member of groups
             * @return {Object} Promise
             */
            getGroupMemberships: function (userId) {
                return $http.get([baseEndpoint, userId].join('/'));
            }
        };

        return svc;
    }]);
