/* global UHGroupingsApp */

(() => {

    /**
     * Service for requesting data from the groupings API.
     * @name groupingsService
     * NOTE: All API requests defined here require at least two parameters, namely onSuccess and onError, which are
     * controller handlers(callback functions) for manipulating data on a successful or unsuccessful request.
     */
    UHGroupingsApp.service("groupingsService", function (dataProvider, BASE_URL) {
        return {

            /**
             * Say hello.
             */
            hello(onSuccess, onError) {
                let endpoint = BASE_URL + "/";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * Get a list of active announcements to display on the home page.
             */
            getAnnouncements(onSuccess, onError) {
                let endpoint = "announcements";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * @param {String[]} groupPaths - The paths to the grouping.
             * @param {Number} page - The number to fetch.
             * @param {Number} size - The size data chunk to be fetched.
             * @param {String} sortString - String to base sort off of.
             * @param isAscending - On true the data returns in ascending order.
             * @param onSuccess - Function to be called if HTTP request returns as a success.
             * @param onError - Function to be called if HTTP request returns an error.
             */
            getGrouping(groupPaths,
                        page,
                        size,
                        sortString,
                        isAscending,
                        onSuccess,
                        onError) {
                let endpoint = BASE_URL + "groupings/group?";
                let params = { page, size, sortString, isAscending };
                let query = this.encodeParameterizedQueryString(params);
                endpoint = endpoint + query;
                dataProvider.loadDataWithBodyRetry(endpoint, groupPaths, onSuccess, onError);
            },

            /**
             * Get the description of a grouping
             * @param groupingPath - The path of the grouping
             * @param onSuccess - Function to be called when HTTP request returns a success
             * @param onError - Function to be called if HTTP request returns an error.
             */
            getGroupingDescription(groupingPath, onSuccess, onError) {
                let endpoint = BASE_URL + "groupings/" + groupingPath + "/description";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * Get the sync destinations of a grouping
             * @param groupingPath - The path of the grouping
             * @param onSuccess - Function to be called when HTTP request returns a success
             * @param onError - Function to be called if HTTP request returns an error.
             */
            getGroupingSyncDest(groupingPath, onSuccess, onError) {
                let endpoint = BASE_URL + "groupings/" + groupingPath + "/groupings-sync-destinations";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * Get the Opt attributes of a grouping
             * @param groupingPath - The path of the grouping
             * @param onSuccess - Function to be called when HTTP request returns a success
             * @param onError - Function to be called if HTTP request returns an error.
             */
            getGroupingOptAttributes(groupingPath, onSuccess, onError) {
                let endpoint = BASE_URL + "groupings/" + groupingPath + "/opt-attributes";
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
                let endpoint = "currentUser";
                dataProvider.retrieveData(endpoint, onSuccess);
            },

            /**
             * Get a list of admins.
             */
            getGroupingAdmins(onSuccess, onError) {
                let endpoint = BASE_URL + "groupingAdmins";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * Get a list of all groupings.
             */
            getAllGroupings(onSuccess, onError) {
                let endpoint = BASE_URL + "allGroupings";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * Add a members to the include group of a grouping. A modal is passed in and displayed after a certain amount
             * of time has elapsed.
             */
            addIncludeMembers(members, path, onSuccess, onError, modal) {
                let endpoint = BASE_URL + path + "/addIncludeMembers";
                return new Promise(() => {
                    dataProvider.updateDataWithBodyAndTimeoutModal(endpoint, members, onSuccess, onError, modal);
                });
            },

            /**
             * Add a members to the include group of a grouping asynchronously. The initial poll gets as close to the
             * average time of the non-asyns version of this endpoint based on the number of members being added.
             * A modal is passed in and displayed after a certain amount of time has elapsed.
             */
            addIncludeMembersAsync(members, path, onSuccess, onError, modal) {
                let endpoint = BASE_URL + path + "/addIncludeMembersAsync";
                let initialPoll = Math.pow(members.length, 1.6);
                return new Promise(() => {
                    dataProvider.updateDataWithBodyAndTimeoutModalAsync(endpoint, members, initialPoll, onSuccess, onError, modal);
                });
            },

            /**
             * Add a members to the exclude group of a grouping. A modal is passed in and displayed after a certain amount
             * of time has elapsed.
             */
            addExcludeMembers(members, path, onSuccess, onError, modal) {
                let endpoint = BASE_URL + path + "/addExcludeMembers";
                return new Promise(() => {
                    dataProvider.updateDataWithBodyAndTimeoutModal(endpoint, members, onSuccess, onError, modal);
                });
            },

            /**
             * Add a members to the exclude group of a grouping asynchronously. The initial poll gets as close to the
             * average time of the non-asyns version of this endpoint based on the number of members being added.
             * A modal is passed in and displayed after a certain amount of time has elapsed.
             */
            addExcludeMembersAsync(members, path, onSuccess, onError, modal) {
                let endpoint = BASE_URL + path + "/addExcludeMembersAsync";
                let initialPoll = Math.pow(members.length, 1.6);
                return new Promise(() => {
                    dataProvider.updateDataWithBodyAndTimeoutModalAsync(endpoint, members, initialPoll, onSuccess, onError, modal);
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
             * Remove members from include group of grouping. A modal is passed in and displayed after a certain amount
             * of time has elapsed.
             */
            removeIncludeMembers(path, members, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/removeIncludeMembers";
                dataProvider.updateDataWithBody(endpoint, members, onSuccess, onError);
            },

            /**
             * Remove members from exclude group of grouping. A modal is passed in and displayed after a certain amount
             * of time has elapsed.
             */
            removeExcludeMembers(path, members, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/removeExcludeMembers";
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
             * Get the attributes of a user, which includes their uid, uhUuid, givenName, cn, and sn.
             * Get a list of invalid given a list of uhIdentifiers.
             */
            getMemberAttributeResults(members, onSuccess, onError) {
                let endpoint = BASE_URL + "members";
                dataProvider.loadDataWithBody(endpoint, members, onSuccess, onError);
            },

            /**
             * Get the attributes of a user, which includes their uid, uhUuid, givenName, cn, and sn asynchronously.
             * Get a list of invalid given a list of uhIdentifiers asynchronously. The initial poll gets as close to the
             * average time of the non-async version of this endpoint based on the number of uhIdentifiers.
             */
            getMemberAttributeResultsAsync(members, onSuccess, onError) {
                let endpoint = BASE_URL + "members/membersAsync";
                let initialPoll = Math.pow(members.length, 1.2);
                dataProvider.loadDataWithBodyAsync(endpoint, members, initialPoll, onSuccess, onError);
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
                let endpoint = BASE_URL + "members/memberships";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * Get a list of all groupings that a user is associated with.
             */
            managePersonResults(uhIdentifier, onSuccess, onError) {
                let endpoint = BASE_URL + "members/" + uhIdentifier + "/groupings";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },


            /**
             * Get the number of memberships that the current user is associated with.
             */
            getNumberOfMemberships(onSuccess, onError) {
                let endpoint = BASE_URL + "members/memberships/count";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * Get a list of memberships that username is associated with.
             */
            getMembershipAssignmentForUser(onSuccess, onError, username) {
                let endpoint = BASE_URL + "members/" + username + "/groupings";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },

            /**
             * Get a list of grouping paths that the current user can opt into.
             */
            getOptInGroups(onSuccess, onError) {
                let endpoint = BASE_URL + "groupings/optInGroups";
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

            /**
             * Remove all members from the include group.
             */
            resetIncludeGroup(groupingPath, onSuccess, onError) {
                let endpoint = BASE_URL + groupingPath + "/resetIncludeGroup";
                dataProvider.updateData(endpoint, onSuccess, onError);
            },

            /**
             * Remove all members from the include group asynchronously.
             */
            resetIncludeGroupAsync(groupingPath, onSuccess, onError) {
                let endpoint = BASE_URL + groupingPath + "/resetIncludeGroupAsync";
                let initialPoll = 15000;
                dataProvider.updateDataAsync(endpoint, initialPoll, onSuccess, onError);
            },

            /**
             * Remove all members from the exclude group.
             */
            resetExcludeGroup(groupingPath, onSuccess, onError) {
                let endpoint = BASE_URL + groupingPath + "/resetExcludeGroup";
                dataProvider.updateData(endpoint, onSuccess, onError);
            },

            /**
             * Remove all members from the exclude group asynchronously.
             */
            resetExcludeGroupAsync(groupingPath, onSuccess, onError) {
                let endpoint = BASE_URL + groupingPath + "/resetExcludeGroupAsync";
                let initialPoll = 15000;
                dataProvider.updateDataAsync(endpoint, initialPoll, onSuccess, onError);
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
             * Get a list of owners in the current path.
             */
            groupingOwners(groupingPath, onSuccess, onError) {
                let endpoint = BASE_URL + "grouping/" + groupingPath + "/owners";
                dataProvider.loadData(endpoint, onSuccess, onError);
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
                let endpoint = BASE_URL + "owners/groupings/count";
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
                return Object.entries(params)
                    .map(([key, value]) => euc(key) + "=" + euc(value))
                    .join("&");
            },

            /**
             * Throws an exception
             */
            throwException(onSuccess, onError) {
                let endpoint = BASE_URL + "testing/" + "exception";
                dataProvider.loadData(endpoint, onSuccess, onError);
            },
        };
    });
})();
