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
             * @param sortString
             * @param isAscending
             * @param onSuccess
             * @param onError
             */

            getGrouping(path, page, size, sortString, isAscending, onSuccess, onError) {

                let endpoint = BASE_URL + "groupings/" + path + "?";


                let params = "";

                if (page !== null) {
                    params = params + "page=" + page;
                }
                if (size !== null) {
                    if (params !== "") {
                        params = params + "&";
                    }
                    params = params + "size=" + size;
                }
                if (sortString !== null) {
                    if (params !== "") {
                        params = params + "&";
                    }
                    params = params + "sortString=" + sortString;
                }
                if (isAscending !== null) {
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
             * @param onSuccess
             * @param onError
             * @param {string} data - description to be updated
             */

            updateDescription(path, onSuccess, onError, data) {
                let endpoint = BASE_URL + "groupings/" + path + "/description";
                dataProvider.updateDataWithBody(onSuccess, onError, endpoint, data);
            },

            /**
             * Gets the list of admins and groupings.
             */
            getAdminLists(onSuccess, onError) {
                let endpoint = BASE_URL + "adminLists";
                dataProvider.loadData(onSuccess, onError, endpoint);
            },

            /**
             * Adds a member to the include group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} userToAdd - the username of the member to add
             * @param onSuccess
             * @param onError
             */
            addMemberToInclude(path, userToAdd, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + userToAdd + "/addMemberToIncludeGroup";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Adds a members to the include group of a grouping.
             * @param {string} path - the path to the grouping
             * @param usersToAdd
             * @param onSuccess
             * @param onError
             * @param modal
             */
            addMembersToInclude(path, usersToAdd, onSuccess, onError, modal) {
                let endpoint = BASE_URL + path + "/" + usersToAdd + "/addMembersToIncludeGroup";
                return new Promise(resolve => {
                    dataProvider.updateDataWithTimeoutModal(onSuccess, onError, endpoint, modal);
                });
            },

            /**
             * Adds a member to the exclude group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} userToAdd - the username of the member to add
             * @param onSuccess
             * @param onError
             */
            addMemberToExclude(path, userToAdd, onSuccess, onError) {
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
            addMembersToExclude(path, usersToAdd, onSuccess, onError, modal) {
                let endpoint = BASE_URL + path + "/" + usersToAdd + "/addMembersToExcludeGroup";
                return new Promise(resolve => {
                    dataProvider.updateDataWithTimeoutModal(onSuccess, onError, endpoint, modal);
                });
            },

            /**
             * Adds a member to the exclude group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} newOwner - the new owner to add to the grouping
             * @param onSuccess
             * @param onError
             */
            assignOwnership(path, newOwner, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + newOwner + "/assignOwnership";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Adds a user to the list of admins
             * @param {string} adminToAdd - the username of the admin to add
             * @param onSuccess
             * @param onError
             */
            addAdmin(adminToAdd, onSuccess, onError) {
                let endpoint = BASE_URL + adminToAdd + "/addAdmin";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Removes a member from the include group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} member - the member to remove
             * @param onSuccess
             * @param onError
             */
            removeMemberFromInclude(path, member, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + member + "/deleteMemberFromIncludeGroup";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Removes a member from the exclude group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} member - the member to remove
             * @param onSuccess
             * @param onError
             */
            removeMemberFromExclude(path, member, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + member + "/deleteMemberFromExcludeGroup";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Removes a member from the owners group of a grouping.
             * @param {string} path - the path to the grouping
             * @param ownerToRemove
             * @param onSuccess
             * @param onError
             */
            removeOwner(path, ownerToRemove, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + ownerToRemove + "/removeOwnership";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Removes a member from the list of admins.
             * @param adminToRemove
             * @param onSuccess
             * @param onError
             */
            removeAdmin(adminToRemove, onSuccess, onError) {
                let endpoint = BASE_URL + adminToRemove + "/deleteAdmin";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Gets the attributes of a user, which includes their name, uid, and uuid.
             * @param {string} member - the UH username of the member
             * @param onSuccess
             * @param onError
             */
            getMemberAttributes(member, onSuccess, onError) {
                let endpoint = BASE_URL + "members/" + member;
                dataProvider.loadData(onSuccess, onError, endpoint);
            },
            /**
             * Opts a member out of a grouping.
             * @param {string} path - the path of the grouping to opt out of
             * @param onSuccess
             * @param onError
             */
            optOut(path, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/optOut";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Opts a user into a grouping.
             * @param {string} path - the path of the grouping to opt in to
             * @param onSuccess
             * @param onError
             */
            optIn(path, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/optIn";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Gets the groupings a user is a part of, the groupings they can opt in to, and the groupings they own.
             */
            getMembershipAssignment(onSuccess, onError) {
                let endpoint = BASE_URL + "members/groupings/";
                console.log(endpoint);
                dataProvider.loadData(onSuccess, onError, endpoint);
            },

            /**
             * Toggles the preference option to allow users to opt into a grouping.
             * @param {string} path - the path of the grouping to update
             * @param {boolean} optInOn - true if users should be allowed to opt into the grouping, otherwise false
             * @param onSuccess
             * @param onError
             */
            setOptIn(path, optInOn, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + optInOn + "/setOptIn";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Toggles the preference option to allow users to opt out of a grouping.
             * @param {string} path - the path of the grouping to update
             * @param optOutOn
             * @param onSuccess
             * @param onError
             */
            setOptOut(path, optOutOn, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + optOutOn + "/setOptOut";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            //todo Might not need this as the syncDests come back in getGrouping already
            /**
             * Get the list of sync destinations
             */
            getSyncDestList(path, onSuccess, onError) {
                let endpoint = BASE_URL + "groupings/" + path + "/syncDestinations";
                dataProvider.loadData(onSuccess, onError, endpoint);
            },

            /**
             * Toggles the given sync destination.
             * @param {string} path - the path of the grouping to update
             * @param syncDestId
             * @param turnOn
             * @param onSuccess
             * @param onError
             */
            setSyncDest(path, syncDestId, turnOn, onSuccess, onError) {
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
            getGroupingsOwned(onSuccess, onError) {
                let endpoint = BASE_URL + "owners/groupings";
                dataProvider.loadData(onSuccess, onError, endpoint);
            }
        };
    });

})();
