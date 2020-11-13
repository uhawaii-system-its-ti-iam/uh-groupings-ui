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
                dataProvider.loadData(onSuccess, onError, endpoint);
            },

            /**
             * Update the description of grouping at path.
             */
            updateDescription(data, path, onSuccess, onError) {
                let endpoint = BASE_URL + "groupings/" + path + "/description";
                dataProvider.updateDataWithBody(onSuccess, onError, endpoint, data);
            },

            /**
             * Get a list of all admins and groupings.
             */
            getAdminLists(onSuccess, onError) {
                let endpoint = BASE_URL + "adminLists";
                dataProvider.loadData(onSuccess, onError, endpoint);
            },

            /**
             * Add a member to the include group of a grouping.
             */
            addMemberToInclude(path, userToAdd, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + userToAdd + "/addMemberToIncludeGroup";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Add a members to the include group of a grouping. A modal is passed in an launched after a certain amount
             * of time has elapsed.
             */
            addMembersToInclude(usersToAdd, path, modal, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + usersToAdd + "/addMembersToIncludeGroup";
                return new Promise(resolve => {
                    dataProvider.updateDataWithTimeoutModal(onSuccess, onError, endpoint, modal);
                });
            },

            /**
             * Add a member to the exclude group of a grouping.
             */
            addMemberToExclude(path, userToAdd, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + userToAdd + "/addMemberToExcludeGroup";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Add a members to the exclude group of a grouping. A modal is passed in an launched after a certain amount
             * of time has elapsed.
             */
            addMembersToExclude(usersToAdd, path, modal, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + usersToAdd + "/addMembersToExcludeGroup";
                return new Promise(resolve => {
                    dataProvider.updateDataWithTimeoutModal(onSuccess, onError, endpoint, modal);
                });
            },

            /**
             * Add a member to the exclude group of a grouping.
             */
            assignOwnership(path, newOwner, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + newOwner + "/assignOwnership";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Add a user to the list of admins.
             */
            addAdmin(adminToAdd, onSuccess, onError) {
                let endpoint = BASE_URL + adminToAdd + "/addAdmin";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },
            /**
             * Remove a member from multiple groups.
             */
            removeFromGroups(groups, member, onSuccess, onError) {
                let endpoint = BASE_URL + groups + "/" + member + "/removeFromGroups";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Remove a member from the include group of a grouping.
             */
            removeMemberFromInclude(path, member, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + member + "/deleteMemberFromIncludeGroup";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Remove members from include group of grouping.
             */
            removeMembersFromInclude(path, members, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + members + "/deleteMembersFromIncludeGroup";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Remove a member from the exclude group of a grouping.
             */
            removeMemberFromExclude(path, member, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + member + "/deleteMemberFromExcludeGroup";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Remove members from exclude group of grouping.
             */
            removeMembersFromExclude(path, members, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + members + "/deleteMembersFromExcludeGroup";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Remove a member from the owners group of a grouping.
             */
            removeOwner(path, ownerToRemove, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + ownerToRemove + "/removeOwnership";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Remove a member from the list of admins.
             */
            removeAdmin(adminToRemove, onSuccess, onError) {
                let endpoint = BASE_URL + adminToRemove + "/deleteAdmin";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Get the attributes of a user, which includes their uid, uhUuid, givenName, cn, and sn.
             */
            getMemberAttributes(member, onSuccess, onError) {
                let endpoint = BASE_URL + "members/" + member;
                dataProvider.loadData(onSuccess, onError, endpoint);
            },
            /**
             * Opt member out of a grouping.
             */
            optOut(path, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/optOut";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Opt a user into a grouping.
             */
            optIn(path, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/optIn";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Get a list of memberships that the current user is associated with.
             */
            getMembershipResults(onSuccess, onError) {
                let endpoint = BASE_URL + "members/groupings/";
                dataProvider.loadData(onSuccess, onError, endpoint);
            },

            /**
             * Get a list of grouping paths that the current user can opt into.
             */
            getOptInGroups(onSuccess, onError) {
                let endpoint = BASE_URL + "groupings/optInGroups/";
                dataProvider.loadData(onSuccess, onError, endpoint);
            },

            /*todo:copy code*/
            getMembershipAssignmentForUser: function (onSuccess, onError, username) {
                let endpoint = BASE_URL + "members/" + username + "/groupings/";
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

            /**
             * Resets the entire groupings removing all basis,include, and exclude members.
             * @param basis
             * @param include
             * @param exclude
             * @param onSuccess
             * @param onError
             */
            resetGroup(path, include, exclude, onSuccess, onError) {
                let endpoint = BASE_URL + path + "/" + include + "/" + exclude + "/resetGroup";
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
             * Toggle the given sync destination.
             */
            setSyncDest(path, syncDestId, turnOn, onSuccess, onError) {
                let endpoint = BASE_URL + "groupings/" + path + "/syncDests/" + syncDestId;
                endpoint = (turnOn) ? endpoint.concat("/enable") : endpoint.concat("/disable");
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Gets the groupings a member owns.
             */
            getGroupingsOwned(onSuccess, onError) {
                let endpoint = BASE_URL + "owners/groupings";
                dataProvider.loadData(onSuccess, onError, endpoint);
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
