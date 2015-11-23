angular.module('components.groupingsService.GroupingsService', [
    'components.groupingsService.GroupingsProxy'
])
    /**
     * The GroupingsService handles interaction with the proxy and does any frontend-specific transforms
     *
     * @class GroupingsService
     * @constructor
     * @module components.groupingsService.GroupingsService
     */
    .factory('GroupingsService', [
        'GroupingsProxy',
        function (Proxy) {

            /**
             * Method to transform the backend response as needed for frontend
             * @param {Object} response
             * @returns {Array[group]}
             */
            function transformGroupResponse(response) {
                var groupings = response.data;

                function transformGrouping(g) {
                    var groupSegments = g.id.split(':');

                    g.rootFolder = groupSegments.shift();
                    g.group = groupSegments.pop();
                    g.folder = groupSegments.join('/');
                }

                if (Array.isArray(groupings)) {
                    groupings.forEach(transformGrouping);
                } else {
                    transformGrouping(groupings);
                }

                return groupings;
            }

            return {

                /**
                 * Method returns groups matching a searchPhrase.
                 *
                 * @method query
                 * @param {String} searchPhrase Phrase to search groups by
                 * @return {Object} Promise
                 */
                query: function (searchPhrase) {
                    return Proxy.query(searchPhrase).then(transformGroupResponse);
                },

                /**
                 * Method returns complete data set for a single grouping whose id matches that passed in
                 *
                 * @method getGroup
                 * @param groupingId {String}
                 * @return {Object} Grouping
                 */
                getGroup: function (groupingId) {
                    return Proxy.getGroup(groupingId).then(transformGroupResponse);
                },

                /**
                 * Method returns groups owned by a specific user.
                 *
                 * @method getOwnedGroups
                 * @param {String|Number} userId Id of the user who is owner of groups
                 * @return {Object} Promise
                 */
                getOwnedGroups: function (userId) {
                    return Proxy.getOwnedGroups(userId).then(transformGroupResponse);
                },

                /**
                 * Method returns groups with a specific user as a member
                 *
                 * @method getGroupMemberships
                 * @param {String|Number} userId Id of the user who is member of groups
                 * @return {Object} Promise
                 */
                getGroupMemberships: function (userId) {
                    return Proxy.getGroupMemberships(userId).then(transformGroupResponse);
                }
            };
        }
    ]);
