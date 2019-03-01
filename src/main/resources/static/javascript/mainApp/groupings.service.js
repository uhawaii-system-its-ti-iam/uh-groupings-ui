(function () {

    /**
     * Service for requesting data from the groupings API.
     * @name groupingsService
     * NOTE: All API requests defined here require at least two parameters, namely onSuccess and onError, which are
     * controller handlers for manipulating data on a successful or unsuccessful request.
     */
    UHGroupingsApp.factory("groupingsService", function (dataProvider, BASE_URL) {
        return {

            getGrouping: function (path, page, size, sortString, isAscending, onSuccess, onError) {

                var endpoint = BASE_URL + "groupings/" + path + "?";

                var params = "";
                if(page != null) params = params + "page=" + page;
                if(size != null) {
                    if(params !== "") params = params + "&";
                    params = params + "size=" + size;
                }
                if(sortString != null) {
                    if(params !== "") params = params + "&";
                    params = params + "sortString=" + sortString;
                }
                if(isAscending != null) {
                    if(params !== "") params = params + "&";
                    params = params + "isAscending=" + isAscending;
                }

                endpoint = endpoint + params;
                // console.log(endpoint);


                dataProvider.loadData(onSuccess, onError, endpoint);
            },

            /**
             * Get page of a grouping
             * @param {String} path - the path to the grouping
             * @param {String} page - the page to retrieve
             * @param {String} size - the size of each page
             */
            // getPaginatedGrouping: function (path, page, size, onSuccess, onError) {
            //     var endpoint = BASE_URL + "groupings/" + path + "?page=" + page + "&size=" + size;
            //     dataProvider.loadData(onSuccess, onError, endpoint);
            // },
            //
            // /**
            //  * Gets information about a grouping.
            //  * @param {string} path - the path to the grouping
            //  */
            // getGrouping: function (path, onSuccess, onError) {
            //     var endpoint = BASE_URL + path + "/grouping";
            //     dataProvider.loadData(onSuccess, onError, endpoint);
            // },

            /**
             * Gets the list of admins and groupings.
             */
            getAdminLists: function (onSuccess, onError) {
                var endpoint = BASE_URL + "adminLists";
                dataProvider.loadData(onSuccess, onError, endpoint);
            },

            /**
             * Adds a member to the include group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} userToAdd - the username of the member to add
             */
            addMemberToInclude: function (path, userToAdd, onSuccess, onError) {
                var endpoint = BASE_URL + path + "/" + userToAdd + "/addMemberToIncludeGroup";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Adds a member to the exclude group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} userToAdd - the username of the member to add
             */
            addMemberToExclude: function (path, userToAdd, onSuccess, onError) {
                var endpoint = BASE_URL + path + "/" + userToAdd + "/addMemberToExcludeGroup";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Adds a member to the exclude group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} newOwner - the new owner to add to the grouping
             */
            assignOwnership: function (path, newOwner, onSuccess, onError) {
                var endpoint = BASE_URL + path + "/" + newOwner + "/assignOwnership";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Adds a user to the list of admins
             * @param {string} adminToAdd - the username of the admin to add
             */
            addAdmin: function (adminToAdd, onSuccess, onError) {
                var endpoint = BASE_URL + adminToAdd + "/addAdmin";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Removes a member from the include group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} member - the member to remove
             */
            removeMemberFromInclude: function (path, member, onSuccess, onError) {
                var endpoint = BASE_URL + path + "/" + member + "/deleteMemberFromIncludeGroup";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Removes a member from the exclude group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} member - the member to remove
             */
            removeMemberFromExclude: function (path, member, onSuccess, onError) {
                var endpoint = BASE_URL + path + "/" + member + "/deleteMemberFromExcludeGroup";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Removes a member from the owners group of a grouping.
             * @param {string} path - the path to the grouping
             * @param {string} member - the member to remove
             */
            removeOwner: function (path, ownerToRemove, onSuccess, onError) {
                var endpoint = BASE_URL + path + "/" + ownerToRemove + "/removeOwnership";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Removes a member from the list of admins.
             * @param {string} path - the path to the grouping
             * @param {string} member - the member to remove
             */
            removeAdmin: function (adminToRemove, onSuccess, onError) {
                var endpoint = BASE_URL + adminToRemove + "/deleteAdmin";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Gets the attributes of a user, which includes their name, uid, and uuid.
             * @param {string} member - the UH username of the member
             */
            getMemberAttributes: function (member, onSuccess, onError) {
                var endpoint = BASE_URL + "members/" + member;
                dataProvider.loadData(onSuccess, onError, endpoint);
            },

            /**
             * Opts a member out of a grouping.
             * @param {string} path - the path of the grouping to opt out of
             */
            optOut: function (path, onSuccess, onError) {
                var endpoint = BASE_URL + path + "/optOut";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Opts a user into a grouping.
             * @param {string} path - the path of the grouping to opt in to
             */
            optIn: function (path, onSuccess, onError) {
                var endpoint = BASE_URL + path + "/optIn";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Gets the groupings a user is a part of, the groupings they can opt in to, and the groupings they own.
             */
            getMembershipAssignment: function (onSuccess, onError) {
                var endpoint = BASE_URL + "members/groupings";
                dataProvider.loadData(onSuccess, onError, endpoint);
            },

            /**
             * Toggles the preference option to allow users to opt into a grouping.
             * @param {string} path - the path of the grouping to update
             * @param {boolean} optInOn - true if users should be allowed to opt into the grouping, otherwise false
             */
            setOptIn: function (path, optInOn, onSuccess, onError) {
                var endpoint = BASE_URL + path + "/" + optInOn + "/setOptIn";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Toggles the preference option to allow users to opt out of a grouping.
             * @param {string} path - the path of the grouping to update
             * @param {boolean} optInOn - true if users should be allowed to opt out of the grouping, otherwise false
             */
            setOptOut: function (path, optOutOn, onSuccess, onError) {
                var endpoint = BASE_URL + path + "/" + optOutOn + "/setOptOut";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Toggles the publication destination for listserv.
             * @param {string} path - the path of the grouping to update
             * @param {boolean} listservOn - true if the listserv destination should be enabled, otherwise false
             */
            setListserv: function (path, listservOn, onSuccess, onError) {
                var endpoint = BASE_URL + path + "/" + listservOn + "/setListserv";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Toggles the publication destination for uhReleasedGroupings.
             * @param {string} path - the path of the grouping to update
             * @param {boolean} ldapOn - true if the uhReleasedGroupings destination should be enabled, otherwise
             * false
             */
            setLdap: function (path, ldapOn, onSuccess, onError) {
                var endpoint = BASE_URL + path + "/" + ldapOn + "/setLdap";
                dataProvider.updateData(onSuccess, onError, endpoint);
            },

            /**
             * Gets the groupings a member owns.
             */
            getGroupingsOwned: function (onSuccess, onError) {
                var endpoint = BASE_URL + "owners/groupings"
                dataProvider.loadData(onSuccess, onError, endpoint);
            }

        };
    });

})();
