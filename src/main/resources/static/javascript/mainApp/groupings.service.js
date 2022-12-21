/* global UHGroupingsApp */

(function () {

    /**
     * Service for requesting data from the groupings API.
     * @name groupingsService
     * NOTE: All API requests defined here require at least two parameters, namely onSuccess and onError, which are
     * controller handlers(callback functions) for manipulating data on a successful or unsuccessful request.
     */
    UHGroupingsApp.factory("groupingsService", function (dataProvider, BASE_URL) {
        return {

            /**
             * Say hello.
             */
            hello(onSuccess, onError) {
                let endpoint = BASE_URL + "/";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * Get page of a grouping.
             * @param {String} path - The path to the grouping.
             * @param {Number} page - The number to fetch.
             * @param {Number} size - The size data chunk to be fetched.
             * @param {String} sortString - String to base sort off of.
             * @param isAscending - On true the data returns in ascending order.
             * @param onSuccess - Function to be called if HTTP request returns as a success.
             * @param onError - Function to be called if HTTP request returns an error.
             */
            getGrouping(path, page, size, sortString, isAscending, onSuccess, onError) {
                let endpoint = BASE_URL + "groupings/" + path + "?";
                let params = { page, size, sortString, isAscending };
                let query = this.encodeParameterizedQueryString(params);
                endpoint = endpoint + query;
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * Update the description of grouping at path.
             * @param data {String}
             * @param path {String}
             * @param onSuccess {Function}
             * @param onError {Function}
             */
            updateDescription(data, path, onSuccess, onError) {
                let endpoint = BASE_URL + "groupings/" + path + "/description";
                dataProvider.updateDataWithBody(endpoint, data, onSuccess, onError);
            },

            /**
             * Get a User object containing information of the current user
             */
            getCurrentUser(onSuccess) {
                let endpoint = BASE_URL + "currentUser";
                dataProvider.retrieveData(endpoint, onSuccess);
            },

            /**
             * Get a list of all admins and groupings.
             */
            getAdminLists(onSuccess, onError) {
                let endpoint = BASE_URL + "adminLists";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * Add a members to the include group of a grouping. A modal is passed in an launched after a certain amount
             * of time has elapsed.
             */
            addMembersToInclude(usersToAdd, path, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/addMembersToIncludeGroup";
                dataProvider.updateDataWithBody(endpoint, usersToAdd, onSuccess, onError);
            },

            /**
             * Add a members to the include group of a grouping. A modal is passed in an launched after a certain amount
             * of time has elapsed.
             */
            addMembersToIncludeAsync(usersToAdd, path, onSuccess, onError, modal) {
                let endpoint = BASE_URL + path + "/addMembersToIncludeGroup";
                return new Promise((resolve) => {
                    dataProvider.updateDataWithBodyAndTimeoutModal(endpoint, usersToAdd, onSuccess, onError, modal);
                });
            },

            /**
             * Add a members to the exclude group of a grouping. A modal is passed in an launched after a certain amount
             * of time has elapsed.
             */
            addMembersToExclude(usersToAdd, path, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/addMembersToExcludeGroup";
                dataProvider.updateDataWithBody(endpoint, usersToAdd, onSuccess, onError);
            },
            /**
             * Add a members to the exclude group of a grouping. A modal is passed in an launched after a certain amount
             * of time has elapsed.
             */
            addMembersToExcludeAsync(usersToAdd, path, onSuccess, onError, modal) {
                let endpoint = BASE_URL + path + "/addMembersToExcludeGroup";
                return new Promise((resolve) => {
                    dataProvider.updateDataWithBodyAndTimeoutModal(endpoint, usersToAdd, onSuccess, onError, modal);
                });
            },

            /**
             * Add owners to owners group of grouping
             */
            addOwnerships(path, newOwner, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + newOwner + "/addOwnerships";
                dataProvider.updateData(endpoint, onSuccess, onError);
            },

            /**
             * Add a user to the list of admins.
             */
            addAdmin(adminToAdd, onSuccess, onError) {
                let endpoint = BASE_URL + adminToAdd + "/addAdmin";
                dataProvider.updateData(endpoint, onSuccess, onError);
            },

            /**
             * Remove a member from multiple groups.
             */
            removeFromGroups(groups, member, onSuccess, onError) {
                let endpoint = BASE_URL + groups + "/" + member + "/removeFromGroups";
                dataProvider.updateData(endpoint, onSuccess, onError);
            },

            /**
             * Remove members from include group of grouping.
             */
            removeMembersFromInclude(path, members, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/removeMembersFromIncludeGroup";
                dataProvider.updateDataWithBody(endpoint, members, onSuccess, onError);
            },

            /**
             * Remove members from exclude group of grouping.
             */
            removeMembersFromExclude(path, members, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/removeMembersFromExcludeGroup";
                dataProvider.updateDataWithBody(endpoint, members, onSuccess, onError);
            },

            /**
             * Remove owners from owners group of grouping
             */
            removeOwnerships(path, owners, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + owners + "/removeOwnerships";
                dataProvider.updateData(endpoint, onSuccess, onError);
            },

            /**
             * Remove a member from the list of admins.
             */
            removeAdmin(adminToRemove, onSuccess, onError) {
                let endpoint = BASE_URL + adminToRemove + "/removeAdmin";
                dataProvider.updateData(endpoint, onSuccess, onError);
            },

            /**
             * Get the attributes of a user, which includes their uid, uhUuid, givenName, cn, and sn.
             */
            getMemberAttributes(member, onSuccess, onError) {
                let endpoint = BASE_URL + "members/" + member;
                dataProvider.loadData(endpoint, onSuccess, onError);
            },
            /**
             * Opt member out of a grouping.
             */
            optOut(path, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/optOut";
                dataProvider.updateData(endpoint, onSuccess, onError);
            },

            /**
             * Opt a user into a grouping.
             */
            optIn(path, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/optIn";
                dataProvider.updateData(endpoint, onSuccess, onError);
            },

            /**
             * Get a list of memberships that the current user is associated with.
             */
            getMembershipResults(onSuccess, onError) {
                let endpoint = BASE_URL + "members/groupings/";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * Get a list of all groupings that a user is associated with.
             */
            managePersonResults(uhIdentifier, onSuccess, onError) {
                let endpoint = BASE_URL + "members/" + uhIdentifier + "/groupings/all";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },


            /**
             * Get the number of memberships that the current user is associated with.
             */
            getNumberOfMemberships(onSuccess, onError) {
                let endpoint = BASE_URL + "members/memberships/";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * Get a list of memberships that username is associated with.
             */
            getMembershipAssignmentForUser(onSuccess, onError, username) {
                let endpoint = BASE_URL + "members/" + username + "/groupings/";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * Get a list of grouping paths that the current user can opt into.
             */
            getOptInGroups(onSuccess, onError) {
                let endpoint = BASE_URL + "groupings/optInGroups/";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },


            /**
             * Toggle the preference option to allow users to opt into a grouping.
             */
            setOptIn(path, optInOn, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + optInOn + "/setOptIn";
                dataProvider.updateData(endpoint, onSuccess, onError);
            },

            /**
             * Toggle the preference option to allow users to opt out of a grouping.
             */
            setOptOut(path, optOutOn, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + optOutOn + "/setOptOut";
                dataProvider.updateData(endpoint, onSuccess, onError);
            },

            /**
             * Reset the entire groupings removing all basis,include, and exclude members.
             */
            resetGroup(path, include, exclude, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + include + "/" + exclude + "/resetGroup";
                dataProvider.updateData(endpoint, onSuccess, onError);
            },

            //todo Might not need this as the syncDests come back in getGrouping already
            /**
             * Get the list of sync destinations
             */
            getSyncDestList(path, onSuccess, onError) {
                let endpoint = BASE_URL + "groupings/" + path + "/sync-destinations";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * Toggle the given sync destination.
             */
            setSyncDest(path, syncDestId, turnOn, onSuccess, onError) {
                let endpoint = BASE_URL + "groupings/" + path + "/syncDests/" + syncDestId;
                endpoint = (turnOn) ? endpoint.concat("/enable") : endpoint.concat("/disable");
                dataProvider.updateData(endpoint, onSuccess, onError);
            },

            /**
             * Get the groupings a member owns.
             */
            getGroupingsOwned(onSuccess, onError) {
                let endpoint = BASE_URL + "owners/groupings";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * Get the number of groupings a member owns.
             */
            getNumberOfGroupings(onSuccess, onError) {
                let endpoint = BASE_URL + "owners/grouping/";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * Checks if the owner of a grouping is the sole owner
             */
            isSoleOwner(path, uidToCheck, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/owners/" + uidToCheck;
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * Create a parameterized query string. Helper method for getGrouping.
             */
            encodeParameterizedQueryString(params) {
                let euc = encodeURIComponent;
                return Object.keys(params)
                    .map((k) => euc(k) + "=" + euc(params[k]))
                    .join("&");
            }
        };
    });
}());