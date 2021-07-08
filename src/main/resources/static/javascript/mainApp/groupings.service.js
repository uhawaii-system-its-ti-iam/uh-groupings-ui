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
                let endpoint = BASE_URL + path + "/" + usersToAdd + "/addMembersToIncludeGroup";
                dataProvider.updateData(endpoint, onSuccess, onError);
            },

            /**
             * Add a members to the include group of a grouping. A modal is passed in an launched after a certain amount
             * of time has elapsed.
             */
            addMembersToIncludeAsync(usersToAdd, path, onSuccess, onError, modal) {
                let endpoint = BASE_URL + path + "/" + usersToAdd + "/addMembersToIncludeGroup";
                return new Promise(resolve => {
                    dataProvider.updateDataWithTimeoutModal(endpoint, onSuccess, onError, modal);
                });
            },

            /**
             * Add a members to the exclude group of a grouping. A modal is passed in an launched after a certain amount
             * of time has elapsed.
             */
            addMembersToExclude(usersToAdd, path, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + usersToAdd + "/addMembersToExcludeGroup";
                dataProvider.updateData(endpoint, onSuccess, onError);
            },
            /**
             * Add a members to the exclude group of a grouping. A modal is passed in an launched after a certain amount
             * of time has elapsed.
             */
            addMembersToExcludeAsync(usersToAdd, path, onSuccess, onError, modal) {
                let endpoint = BASE_URL + path + "/" + usersToAdd + "/addMembersToExcludeGroup";
                return new Promise(resolve => {
                    dataProvider.updateDataWithTimeoutModal(endpoint, modal, onSuccess, onError);
                });
            },

            /**
             * Add a member to the exclude group of a grouping.
             */
            assignOwnership(path, newOwner, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + newOwner + "/assignOwnership";
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
                let endpoint = BASE_URL + path + "/" + members + "/removeMembersFromIncludeGroup";
                dataProvider.updateData(endpoint, onSuccess, onError);
            },

            /**
             * Remove members from exclude group of grouping.
             */
            removeMembersFromExclude(path, members, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + members + "/removeMembersFromExcludeGroup";
                dataProvider.updateData(endpoint, onSuccess, onError);
            },

            /**
             * Remove a member from the owners group of a grouping.
             */
            removeOwner(path, ownerToRemove, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + ownerToRemove + "/removeOwnership";
                dataProvider.updateData(endpoint, onSuccess, onError);
            },

            /**
             * Remove a member from the list of admins.
             */
            removeAdmin(adminToRemove, onSuccess, onError) {
                let endpoint = BASE_URL + adminToRemove + "/deleteAdmin";
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
             * Get a list of memberships that username is associated with.
             */
            getMembershipAssignmentForUser: function (onSuccess, onError, username) {
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
                let endpoint = BASE_URL + "groupings/" + path + "/syncDestinations";
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
             * Parse a generic response data type.
             */
            parseGenericResponseData(response) {
                let parsedObject = {};
                if (!(_.isEqual(["data", "map"], Object.keys(response))))
                    parsedObject = { "Response Parse Error": "Keys were not set due to response format", ...response };
                else {
                    let keys = Object.keys(response.map);
                    for (let i = 0; i < keys.length; i++)
                        parsedObject[keys[i]] = response.data[response.map[keys[i]]];
                }
                return parsedObject;
            }
        };
    });
}());
