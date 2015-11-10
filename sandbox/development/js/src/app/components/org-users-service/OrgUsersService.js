angular.module('components.orgUsersService.OrgUsersService', [
        'components.orgUsersService.OrgUsersProxy'
    ])
    /**
     * The OrgUsersService handles interaction with the proxy and does any frontend-specific transforms
     *
     * @class OrgUsersService
     * @constructor
     * @module components.orgUsersService.OrgUsersService
     */
    .factory('OrgUsersService', [
        'OrgUsersProxy',
        function (Proxy) {
            return {

                /**
                 * Method returns a list of all users in an organization
                 *
                 * @method list
                 * @return {Array[OrgUser]}
                 */
                list: function () {
                    return Proxy.list().then(function (response) {
                        return response.data;
                    });
                },

                /**
                 * Method returns a list of all *active* users in an organization
                 *
                 * @method listActive
                 * @return {Array[OrgUser]}
                 */
                listActive: function () {
                    return Proxy.list().then(function onlyActive(user) {
                        return !!user.isActive;
                    });
                },

                /**
                 * Method returns a list of all *inactive* users in an organization
                 *
                 * @method listInactive
                 * @return {Array[OrgUser]}
                 */
                listInactive: function () {
                    return Proxy.list().then(function onlyInactive(user) {
                        return !user.isActive;
                    });
                }
            };
        }
    ]);

