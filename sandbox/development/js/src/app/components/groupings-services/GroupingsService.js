angular.module('components.groupingsServices.GroupingsService', [
    'components.groupingsServices.GroupingsProxy'
])
/**
 * The GroupingsService leverages the GroupingsProxyService for server requests. This
 * service is responsible for transforming data returned by GroupingsProxyService requests.
 *
 * @class GroupingsService
 * @constructor
 * @module components.groupingsServices.GroupingsService
 */
.factory('GroupingsService', [
    'GroupingsProxy',
    function (Proxy) {
        // Define.
        var service;

        /**
         * Method parses out a grouping based upon the grouping identifier.
         *
         * @param {Object} grouping Grouping object
         * @returns {Object}
         * @private
         */
        function transformGrouping(grouping) {
            var groupSegments = grouping.id.split(':');

            grouping.rootFolder = groupSegments.shift();
            grouping.group = groupSegments.pop();
            grouping.folder = groupSegments.join('/');

            return grouping;
        }

        /**
         * Method to transform backend responses as needed for frontend consumption.
         *
         * @param {Object} response Response object
         * @returns {Array[group]}
         * @private
         */
        function transformGroupResponse(response) {
            var groupings = response.data;

            if (angular.isArray(groupings)) {
                groupings.forEach(transformGrouping);
            } else {
                groupings = transformGrouping(groupings);
            }

            return groupings;
        }

        /**
         * Property houses service methods that wrap the GroupingProxyService methods,
         * which communicate with the backend. All transformations to data should be
         * made within the context of the below service methods.
         *
         * @property service
         * @type {Object}
         * @private
         */
        service = {
            /**
             * Method returns groups matching a searchPhrase.
             *
             * Note: Method does not handle error condition.
             *
             * @method query
             * @param {String} searchPhrase Phrase to search groups by
             * @return {Object} Promise
             */
            query: function (searchPhrase) {
                var promise = Proxy.query(searchPhrase).then(transformGroupResponse);
                return promise;
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
                var promise = Proxy.getGroup(groupingId).then(transformGroupResponse);
                return promise;
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
                var promise = Proxy.getOwnedGroups(userId).then(transformGroupResponse);
                return promise;
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
                var promise = Proxy.getGroupMemberships(userId).then(transformGroupResponse);
                return promise;
            }
        };

        return service;
    }
]);
