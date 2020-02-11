(function () {

    /**
     * Service for requesting data from the groupings API.
     * @name groupingsService
     * NOTE: All API requests defined here require at least two parameters, namely onSuccess and onError, which are
     * controller handlers for manipulating data on a successful or unsuccessful request.
     */
    UHGroupingsApp.factory("groupingsService", function (dataProvider, BASE_URL) {
        return {
            /**
             * Get page of a grouping
             * @param {String} path - the path to the grouping
             * @param {String} page - the page to retrieve
             * @param {String} size - the size of each page
             */

            getGrouping: function (path, page, size, sortString, isAscending, onSuccess, onError) {

                let endpoint = BASE_URL + "groupings/" + path + "?";


                let params = "";

                if (page != null) {
                    params = params + "page=" + page;
                }
                if (size != null) {
                    if (params !== "") {
                        params = params + "&";
                    }
                    params = params + "size=" + size;
                }
                if (sortString != null) {
                    if (params !== "") {
                        params = params + "&";
                    }
                    params = params + "sortString=" + sortString;
                }
                if (isAscending != null) {
                    if (params !== "") {
                        params = params + "&";
                    }
                    params = params + "isAscending=" + isAscending;
                }

                endpoint = endpoint + params;
                // console.log(endpoint);


                dataProvider.loadData(onSuccess, onError, endpoint);
            },

            /**

             * Gets information about a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} data - description to be updated
             */

            updateDescription: function (path, onSuccess, onError, data) {
                var endpoint = BASE_URL + "groupings/" + path + "/description";
                dataProvider.updateDataWithBody(onSuccess, onError, endpoint, data);
            },

            /**
             * Gets the list of admins and groupings.
             */
            getAdminLists: function (onSuccess, onError) {
                let endpoint = BASE_URL + "adminLists";
                dataProvider.loadData(onSuccess, onError, endpoint);
            },

            /**
             * Adds a member to the include group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} userToAdd - the username of the member to add
             */
            addMemberToInclude: function (path, userToAdd, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + userToAdd + "/addMemberToIncludeGroup";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Adds a members to the include group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} userToAdd - the usernames of the members to add
             */
            addMembersToInclude: function (path, usersToAdd, onSuccess, onError, modal) {
                var endpoint = BASE_URL + path + "/" + usersToAdd + "/addMembersToIncludeGroup";
                return new Promise(resolve => {
                    dataProvider.updateDataWithTimeoutModal(onSuccess, onError, endpoint, modal);
                });
            },

            /**
             * Adds a member to the exclude group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} userToAdd - the username of the member to add
             */
            addMemberToExclude: function (path, userToAdd, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + userToAdd + "/addMemberToExcludeGroup";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Adds a members to the exclude group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} usersToAdd - the usernames of the members to add
             * @param onSuccess
             * @param onError
             * @param modal
             */
            addMembersToExclude: function (path, usersToAdd, onSuccess, onError, modal) {
                let endpoint = BASE_URL + path + "/" + usersToAdd + "/addMembersToExcludeGroup";
                return new Promise(resolve => {
                    dataProvider.updateDataWithTimeoutModal(onSuccess, onError, endpoint, modal);
                });
            },

            /**
             * Adds a member to the exclude group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} newOwner - the new owner to add to the grouping
             */
            assignOwnership: function (path, newOwner, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + newOwner + "/assignOwnership";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Adds a user to the list of admins
             * @param {string} adminToAdd - the username of the admin to add
             */
            addAdmin: function (adminToAdd, onSuccess, onError) {
                let endpoint = BASE_URL + adminToAdd + "/addAdmin";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Removes a member from the include group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} member - the member to remove
             */
            removeMemberFromInclude: function (path, member, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + member + "/deleteMemberFromIncludeGroup";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Removes a member from the exclude group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} member - the member to remove
             */
            removeMemberFromExclude: function (path, member, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + member + "/deleteMemberFromExcludeGroup";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Removes a member from the owners group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} member - the member to remove
             */
            removeOwner: function (path, ownerToRemove, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + ownerToRemove + "/removeOwnership";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Removes a member from the list of admins.
             * @param {string} path - the path to the grouping
             * @param {string} member - the member to remove
             */
            removeAdmin: function (adminToRemove, onSuccess, onError) {
                let endpoint = BASE_URL + adminToRemove + "/deleteAdmin";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Gets the attributes of a user, which includes their name, uid, and uuid.
             * @param {string} member - the UH username of the member
             */
            getMemberAttributes: function (member, onSuccess, onError) {
                let endpoint = BASE_URL + "members/" + member;
                dataProvider.loadData(onSuccess, onError, endpoint);
            },

            /**
             * Checks if member exists so that multiple pending user names can be displayed for the user.
             * @param {string} member - the UH username of the member.
             * @param {Object[]} data - array of objects which hold username and status. [{string: name, string: status}]
             * @param onSuccess
             * @param onError
             */
            checkMember: function (member, data, onSuccess, onError) {
                let endpoint = BASE_URL + "members/" + member;
                dataProvider.loadData(onSuccess, onError, endpoint);
            },

            /**
             * Opts a member out of a grouping.
             * @param {string} path - the path of the grouping to opt out of
             */
            optOut: function (path, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/optOut";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Opts a user into a grouping.
             * @param {string} path - the path of the grouping to opt in to
             */
            optIn: function (path, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/optIn";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Gets the groupings a user is a part of, the groupings they can opt in to, and the groupings they own.
             */
            getMembershipAssignment: function (onSuccess, onError) {
                let endpoint = BASE_URL + "members/groupings/";
                console.log(endpoint);
                dataProvider.loadData(onSuccess, onError, endpoint);
            },

            /**
             * Toggles the preference option to allow users to opt into a grouping.
             * @param {string} path - the path of the grouping to update
             * @param {boolean} optInOn - true if users should be allowed to opt into the grouping, otherwise false
             */
            setOptIn: function (path, optInOn, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + optInOn + "/setOptIn";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Toggles the preference option to allow users to opt out of a grouping.
             * @param {string} path - the path of the grouping to update
             * @param {boolean} optInOn - true if users should be allowed to opt out of the grouping, otherwise false
             */
            setOptOut: function (path, optOutOn, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + optOutOn + "/setOptOut";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            //todo Might not need this as the syncDests come back in getGrouping already
            /**
             * Get the list of sync destinations
             */
            getSyncDestList: function (path, onSuccess, onError) {
                let endpoint = BASE_URL + "groupings/" + path + "/syncDestinations";
                dataProvider.loadData(onSuccess, onError, endpoint);
            },

            /**
             * Toggles the given sync destination.
             * @param {string} path - the path of the grouping to update
             * @param {boolean} ldapOn - true if the sync destination should be enabled, otherwise false
             */
            setSyncDest: function (path, syncDestId, turnOn, onSuccess, onError) {
                let endpoint = BASE_URL + "groupings/" + path + "/syncDests/" + syncDestId;
                if (turnOn) {
                    endpoint = endpoint.concat("/enable");
                } else {
                    endpoint = endpoint.concat("/disable");
                }
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Gets the groupings a member owns.
             */
            getGroupingsOwned: function (onSuccess, onError) {
                let endpoint = BASE_URL + "owners/groupings";
                dataProvider.loadData(onSuccess, onError, endpoint);
            }
        };
    });

})();
