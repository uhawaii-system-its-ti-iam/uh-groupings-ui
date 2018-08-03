(function () {

    /**
     * This controller contains shared functions between the admin and groupings page.
     * @param $scope - binding between controller and HTML page
     * @param $window - the browser window object
     * @param $uibModal - the UI Bootstrap service for creating modals
     * @param $controller - service for instantiating controllers
     * @param dataProvider - service function that provides GET and POST requests for getting or updating data
     * @param BASE_URL - the constant base URL for endpoints
     */
    function GeneralJsController($scope, $window, $uibModal, $controller, dataProvider, BASE_URL) {

        $scope.currentUser = $window.document.getElementById("name").innerHTML;

        $scope.groupingsList = [];
        $scope.pagedItemsGroupings = [];
        $scope.currentPageGroupings = 0;

        $scope.groupingBasis = [];
        $scope.pagedItemsBasis = [];
        $scope.currentPageBasis = 0;

        $scope.groupingMembers = [];
        $scope.pagedItemsMembers = [];
        $scope.currentPageMembers = 0;

        $scope.groupingInclude = [];
        $scope.pagedItemsInclude = [];
        $scope.currentPageInclude = 0;

        $scope.groupingExclude = [];
        $scope.pagedItemsExclude = [];
        $scope.currentPageExclude = 0;

        $scope.groupingOwners = [];
        $scope.pagedItemsOwners = [];
        $scope.currentPageOwners = 0;

        $scope.allowOptIn = false;
        $scope.allowOptOut = false;
        $scope.listserv = false;
        $scope.ldap = false;

        $scope.showGrouping = false;

        $scope.loading = false;

        angular.extend(this, $controller("TableJsController", { $scope: $scope }));

        /**
         * Initiates the retrieval of information about the grouping clicked by the user.
         * @param {number} currentPage - the current page number in the groupings list
         * @param {number} index - the index of the grouping clicked by the user
         */
        $scope.displayGrouping = function (currentPage, index) {
            $scope.selectedGrouping = $scope.pagedItemsGroupings[currentPage][index];
            $scope.getGroupingInformation();
            $scope.showGrouping = true;
        };

        /**
         * Gets information about the grouping, such as its members and the preferences set.
         * @param {string} path - the path of the grouping to retrieve information
         */
        $scope.getGroupingInformation = function () {
            $scope.loading = true;
            var endpoint = BASE_URL + $scope.selectedGrouping.path + "/grouping";

            dataProvider.loadData(function (res) {
                console.log(res);
                if (_.isNull(res)) {
                    $scope.createApiErrorModal();
                } else {
                    //Gets members in the basis group
                    $scope.groupingBasis = _.sortBy(res.basis.members, "name");
                    $scope.filter($scope.groupingBasis, "pagedItemsBasis", "currentPageBasis", $scope.basisQuery);

                    //Gets members in the include group
                    $scope.groupingInclude = _.sortBy(res.include.members, "name");
                    $scope.addInBasis($scope.groupingInclude);
                    $scope.filter($scope.groupingInclude, "pagedItemsInclude", "currentPageInclude", $scope.includeQuery);

                    //Gets members in the exclude group
                    $scope.groupingExclude = _.sortBy(res.exclude.members, "name");
                    $scope.addInBasis($scope.groupingExclude);
                    $scope.filter($scope.groupingExclude, "pagedItemsExclude", "currentPageExclude", $scope.excludeQuery);

                    //Gets members in grouping
                    $scope.groupingMembers = _.sortBy(res.composite.members, "name");
                    $scope.addWhereListed($scope.groupingMembers);
                    $scope.filter($scope.groupingMembers, "pagedItemsMembers", "currentPageMembers", $scope.membersQuery);

                    //Gets owners of the grouping
                    $scope.groupingOwners = _.sortBy(res.owners.members, "name");
                    $scope.pagedItemsOwners = $scope.groupToPages($scope.groupingOwners);

                    $scope.allowOptIn = res.optInOn;
                    $scope.allowOptOut = res.optOutOn;
                    $scope.listserv = res.listservOn;
                    $scope.ldap = res.ldapOn;
                }
                //Stop loading spinner
                $scope.loading = false;
            }, function (res) {
                dataProvider.handleException({ exceptionMessage: res.exceptionMessage }, "feedback/error", "feedback");
            }, endpoint);
        };

        /**
         * Creates a modal for errors in loading data from the API.
         */
        $scope.createApiErrorModal = function () {
            $scope.apiErrorModalInstance = $uibModal.open({
                templateUrl: "modal/apiError.html",
                scope: $scope
            });
        };

        /**
         * Closes the API error modal.
         */
        $scope.closeApiError = function () {
            $scope.apiErrorModalInstance.close();
        };

        /**
         * Checks if the members in the group are in the basis group.
         * @param {object[]} group - the group to check
         */
        $scope.addInBasis = function (group) {
            _.forEach(group, function (member) {
                var memberUuid = member.uuid;
                member.inBasis = _.some($scope.groupingBasis, { uuid: memberUuid })
                    ? "Yes"
                    : "No";
            });
        };

        /**
         * Checks what lists a member in a grouping are in.
         * @param {object[]} compositeGroup - the composite / all members group
         */
        $scope.addWhereListed = function (compositeGroup) {
            _.forEach(compositeGroup, function (member) {
                var memberUuid = member.uuid;
                if (_.some($scope.groupingBasis, { uuid: memberUuid })) {
                    member.whereListed = "Basis";
                }

                if (_.some($scope.groupingInclude, { uuid: memberUuid })) {
                    member.whereListed = _.isUndefined(member.whereListed)
                        ? "Include"
                        : "Basis / Include";
                }
            });
        };

        /**
         * Adds a user to a group.
         * @param {string} list - the list the user is being added to (either Include or Exclude)
         */
        $scope.addMember = function (list) {
            var userToAdd = $scope.userToAdd;
            var endpoint = BASE_URL + $scope.selectedGrouping.path + "/" + userToAdd + "/addMemberTo" + list + "Group";

            if (_.isUndefined(userToAdd) || userToAdd.length === 0) {
                $scope.createAddModal({ user: userToAdd });
            } else if ($scope.existInList(userToAdd, list)) {
                $scope.createCheckModal(userToAdd, list, false, endpoint);
            } else if ($scope.isInAnotherList(userToAdd, list)) {
                $scope.createCheckModal(userToAdd, list, true, endpoint);
            } else {
                $scope.updateAddMember(userToAdd, list, endpoint);
            }
        };

        /**
         * Calls API to add the member to the grouping list
         * and if they are already in another list, removes them from the other list.
         *
         * @param userToAdd - user being added
         * @param list - the list the user is being added to
         * @param endpoint - the API call endpoint to add the user
         */
        $scope.updateAddMember = function (userToAdd, list, endpoint) {
            dataProvider.updateData(function (res) {
                $scope.createAddModal({
                    user: userToAdd,
                    response: res,
                    listName: list
                });
                $scope.userToAdd = "";
            }, function (res) {
                $scope.createAddModal({
                    user: userToAdd,
                    response: res,
                    listName: list
                });
            }, endpoint);
        };

        /**
         * @param user - the user you are checking to see if they are in another list.
         * @param list - the list the user is currently being added to
         * @returns {boolean} - true if the person is already in another list, else false.
         */
        $scope.isInAnotherList = function (user, list) {
            if (list === "Include") {
                return _.some($scope.groupingExclude, { username: user });
            } else if (list === "Exclude") {
                return _.some($scope.groupingInclude, { username: user });
            }
            return false;
        };

        $scope.existInList = function (user, list) {
            if (list === "Include") {
                return _.some($scope.groupingInclude, { username: user });
            }
            else if (list === "Exclude") {
                return _.some($scope.groupingExclude, { username: user });
            }
            return false;
        };

        /**
         * Creates a modal that asks whether or not they want to add a person that is already in another list.
         * @param user - Username of the user they are trying to add.
         * @param listName - name of the list they are adding to (either Include or Exclude)
         * @param endpoint - endpoint used to add the user
         */
        $scope.createCheckModal = function (user, listName, swap, endpoint) {
            $scope.user = user;
            $scope.listName = listName;
            $scope.swap = swap;

            $scope.checkModalInstance = $uibModal.open({
                templateUrl: "modal/checkModal.html",
                scope: $scope
            });

            $scope.checkModalInstance.result.then(function () {
                $scope.updateAddMember(user, listName, endpoint);
            }).catch(function () {
                // Do nothing if the user cancels or clicks the backdrop
            });

        };

        /**
         *  Creates a modal that asks for confirmation when adding a user.
         *  @param userToAdd - Username of the person being added
         *  @param listName, string - name of the list the person is being added to
         **/
        $scope.createConfirmAddModal = function (userToAdd, listName) {
            var endpoint = BASE_URL + "members/" + userToAdd;
            dataProvider.loadData(
                function (res) {
                    $scope.uidToAdd = res.uid;
                    $scope.uhuuidToAdd = res.uhuuid;
                    $scope.nameToAdd = res.cn;
                    $scope.listName = listName;
                },
                function (res) {
                    dataProvider.handleException({ exceptionMessage: res.exceptionMessage }, "feedback/error", "feedback");
                },
                endpoint);
        };

        /**
         * Closes CheckModal and proceeds with the checkModalInstance result.then function
         */
        $scope.proceedCheckModal = function () {
            $scope.checkModalInstance.close();
        };

        /**
         * Dismisses the CheckModal and closes it with proceeding with checkModalInstance's result.then function.
         */
        $scope.closeCheckModal = function () {
            $scope.checkModalInstance.dismiss();
        };

        /**
         * ConfirmAddModal "Yes" button
         */
        $scope.proceedConfirmAddUser = function () {
            $scope.confirmAddModalInstance.close();
        };

        /**
         * ConfirmAddModal "Cancel" button
         */
        $scope.cancelConfirmAddUser = function () {
            $scope.confirmAddModalInstance.dismiss();
        };
        /**
         * Gives a user ownership of a grouping.
         */
        $scope.addOwner = function () {
            var ownerToAdd = $scope.ownerToAdd;
            var endpoint = BASE_URL + $scope.selectedGrouping.path + "/" + ownerToAdd + "/assignOwnership";

            dataProvider.updateData(function (res) {
                $scope.createAddModal({
                    user: ownerToAdd,
                    response: res,
                    listName: "owners"
                });
                $scope.ownerToAdd = "";
            }, function (res) {
                $scope.createAddModal({
                    user: ownerToAdd,
                    response: res,
                    listName: "owners"
                });
            }, endpoint);
        };

        /**
         * Creates a modal telling the user whether or not the user was successfully added into the grouping/admin list.
         * @param {object} options - the options object
         * @param {string} options.userToAdd - the user being added
         * @param {string} options.response - the response from adding a member
         * @param {string} options.listName - the list where the user was being added to
         */
        $scope.createAddModal = function (options) {
            $scope.user = options.user || "";
            $scope.listName = options.listName;

            if (_.has(options, "response")) {
                var addResult = _.isArray(options.response)
                    ? _.last(options.response) // For adding a user to the include/exclude list
                    : options.response; // For adding a user to the admin or owner list

                $scope.wasSuccessful = _.startsWith(addResult.resultCode, "SUCCESS")
                    ? true
                    : false;
            } else {
                $scope.wasSuccessful = false;
            }

            $scope.addModalInstance = $uibModal.open({
                templateUrl: "modal/addModal.html",
                scope: $scope
            });

            $scope.addModalInstance.result.finally(function () {
                if ($scope.wasSuccessful) {
                    $scope.loading = true;
                    if ($scope.listName === "admins") {
                        // Refreshes the groupings list and the admins list
                        $scope.init();
                    } else {
                        $scope.getGroupingInformation();
                    }
                }
            });
        };

        /**
         * Closes the add user modal.
         */
        $scope.closeAddModal = function () {
            $scope.addModalInstance.close();
        };

        /**
         * Removes a user from the include or exclude group.
         * @param {string} listName - the list to remove the user from (either Include or Exclude)
         * @param {number} currentPage - the current page in the table
         * @param {number} index - the index of the user clicked by the user
         * account
         */
        $scope.removeMember = function (listName, currentPage, index) {
            var userToRemove;
            if (listName === "Include") {
                userToRemove = $scope.pagedItemsInclude[currentPage][index].username;
            } else if (listName === "Exclude") {
                userToRemove = $scope.pagedItemsExclude[currentPage][index].username;
            }
            var endpoint = BASE_URL + $scope.selectedGrouping.path + "/" + userToRemove + "/deleteMemberFrom" + listName + "Group";

            $scope.createRemoveModal({
                user: userToRemove,
                endpoint: endpoint,
                listName: listName
            });
        };

        /**
         * Removes a grouping owner. There must be at least one grouping owner remaining.
         * @param {number} currentPage - the current page in the owners table
         * @param {number} index - the index of the owner clicked by the user
         */
        $scope.removeOwner = function (currentPage, index) {
            var ownerToRemove = $scope.pagedItemsOwners[currentPage][index].username;
            var endpoint = BASE_URL + $scope.selectedGrouping.path + "/" + ownerToRemove + "/removeOwnership";

            if ($scope.groupingOwners.length > 1) {
                $scope.createRemoveModal({
                    user: ownerToRemove,
                    endpoint: endpoint,
                    listName: "owners"
                });
            }
        };

        /**
         * Closes the modal, then proceeds with deleting a user from a grouping.
         */
        $scope.proceedRemoveUser = function () {
            $scope.removeModalInstance.close();
        };

        /**
         * Closes the modal for deleting a user. This does not delete the user from the grouping/admin list.
         */
        $scope.cancelRemoveUser = function () {
            $scope.removeModalInstance.dismiss();
        };

        /**
         * Returns to the list of groupings available for management/administration.
         */
        $scope.returnToGroupingsList = function () {
            $scope.resetGroupingInformation();

            // Ensure the groupings list is reset with the now-blank filter
            $scope.filter($scope.groupingsList, "pagedItemsGroupings", "currentPageGroupings", $scope.groupingsQuery);

            $scope.showGrouping = false;
        };

        /**
         * Resets the grouping members, page numbers, filters, and columns to sort by.
         */
        $scope.resetGroupingInformation = function () {
            resetGroupingMembers();
            resetPillsToAllMembers();
            resetFilterQueries();
            $scope.columnSort = {};
        };

        /**
         * Resets the grouping members and page numbers.
         */
        function resetGroupingMembers() {
            $scope.groupingMembers = [];
            $scope.groupingBasis = [];
            $scope.groupingInclude = [];
            $scope.groupingExclude = [];
            $scope.groupingOwners = [];

            $scope.pagedItemsMembers = [];
            $scope.pagedItemsBasis = [];
            $scope.pagedItemsInclude = [];
            $scope.pagedItemsExclude = [];
            $scope.pagedItemsOwners = [];

            $scope.currentPageMembers = 0;
            $scope.currentPageBasis = 0;
            $scope.currentPageInclude = 0;
            $scope.currentPageExclude = 0;
            $scope.currentPageOwners = 0;
        }

        /**
         * Resets the selected group in the side navbar to the list of all members.
         */
        function resetPillsToAllMembers() {
            var pills = $("#group-pills")[0].children;
            var pillContents = $("#pill-content")[0].children;
            for (var i = 0; i < pills.length; i++) {
                if (i === 0 && !$(pills[i]).hasClass("active")) {
                    $(pills[i]).addClass("active");
                    $(pillContents[i]).addClass("in active");
                } else if (i !== 0 && $(pills[i]).hasClass("active")) {
                    $(pills[i]).removeClass("active");
                    $(pillContents[i]).removeClass("in active");
                }
            }
        }

        function resetFilterQueries() {
            $scope.basisQuery = "";
            $scope.excludeQuery = "";
            $scope.includeQuery = "";
            $scope.membersQuery = "";
            $scope.groupingsQuery = "";
        }

        /**
         * Creates a modal with a description of the preference selected.
         * @param {string} desc - the description of the preference
         */
        $scope.createPreferenceInfoModal = function (desc) {
            $scope.preferenceInfo = desc;

            $scope.infoModalInstance = $uibModal.open({
                templateUrl: "modal/infoModal.html",
                scope: $scope
            });
        };

        /**
         * Closes the preference information modal.
         */
        $scope.closePreferenceInfo = function () {
            $scope.infoModalInstance.close();
        };

        /**
         * Toggles the grouping preference which allows users to opt out of a grouping.
         */
        $scope.updateAllowOptOut = function () {
            var endpoint = BASE_URL + $scope.selectedGrouping.path + "/" + $scope.allowOptOut + "/setOptOut";
            dataProvider.updateData(function (res) {
                if (!_.isUndefined(res.statusCode)) {
                    console.log("Error, Status Code: " + res.statusCode);
                    $scope.createPreferenceErrorModal();
                } else if (_.startsWith(res[0].resultCode), "SUCCESS") {
                    console.log("success");
                }
            }, function (res) {
                console.log("Error, Status Code: " + res.statusCode);
            }, endpoint);
        };

        /**
         * Toggles the grouping preference which allows users to discover the grouping and opt into it.
         */
        $scope.updateAllowOptIn = function () {
            var endpoint = BASE_URL + $scope.selectedGrouping.path + "/" + $scope.allowOptIn + "/setOptIn";
            dataProvider.updateData(function (res) {
                if (!_.isUndefined(res.statusCode)) {
                    console.log("Error, Status Code: " + res.statusCode);
                    $scope.createPreferenceErrorModal();
                } else if (_.startsWith(res[0].resultCode), "SUCCESS") {
                    console.log("success");
                }
            }, function (res) {
                console.log("Error, Status Code: " + res.statusCode);
            }, endpoint);
        };

        /**
         * Toggles the grouping preference which creates a LISTSERV email list based off the grouping.
         */
        $scope.updateListserv = function () {
            var endpoint = BASE_URL + $scope.selectedGrouping.path + "/" + $scope.listserv + "/setListserv";
            dataProvider.updateData(function (res) {
                if (!_.isUndefined(res.statusCode)) {
                    console.log("Error, Status Code: " + res.statusCode);
                    $scope.createPreferenceErrorModal();
                } else if (res.resultCode === "SUCCESS") {
                    console.log("success");
                }
            }, function (res) {
                console.log("Error, Status Code: " + res.statusCode);
            }, endpoint);
        };

        $scope.updateLdap = function () {
            var endpoint = BASE_URL + $scope.selectedGrouping.path + "/" + $scope.ldap + "/setLdap";
            console.log($scope.ldap);

            dataProvider.updateData(function (res) {
                if (!_.isUndefined(res.statusCode)) {
                    console.log("Error, Status Code: " + res.statusCode);
                    $scope.createPreferenceErrorModal();
                } else if (res.resultCode === "SUCCESS") {
                    console.log("success");
                }
            }, function (res) {
                console.log("Error, Status Code: " + res.statusCode);
            }, endpoint);


        };

        /**
         * Creates a modal indicating an error in saving the grouping's preferences.
         */
        $scope.createPreferenceErrorModal = function () {
            $scope.preferenceErrorModalInstance = $uibModal.open({
                templateUrl: "modal/preferenceErrorModal.html",
                scope: $scope
            });
        };

        /**
         * Closes the preference error modal.
         */
        $scope.closePreferenceError = function () {
            $scope.preferenceErrorModalInstance.close();
        };

        $scope.resetFields = function () {
            $scope.userToAdd = "";
        };

        /**
         * Exports data in a table to a CSV file
         * @param {object[]} table - the table to export
         * @param grouping - grouping name that you are exporting from
         * @param list - grouping list (i.e. include or exclude)
         */
        $scope.exportGroupToCsv = function (table, grouping, list) {
            var data, filename, link;

            var csv = $scope.convertListToCsv(table);
            if (csv == null) {
                $scope.createApiErrorModal();
                return;
            }

            filename = grouping + ":" + list + "_list.csv";

            csv = "data:text/csv;charset=utf-8," + csv;
            data = encodeURI(csv);

            link = document.createElement("a");
            link.setAttribute("href", data);
            link.setAttribute("download", filename);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        };

        /**
         * Converts the data in the table into comma-separated values.
         * @param {object[]} table - the table to convert
         * @returns the table in CSV format
         */
        $scope.convertListToCsv = function (table) {
            var str = "Last,First,Username,Email\r\n";
            for (var i = 0; i < table.length; i++) {
                var line = "";
                line += table[i].lastName + ",";
                line += table[i].firstName + ",";
                if (_.isEmpty(table[i].username)) {
                    line += ",,";
                } else {
                    line += table[i].username + ",";
                    line += table[i].username + "@hawaii.edu,";
                }
                str += line + "\r\n";
            }
            return str;
        };

        /**
         * Determines whether a warning message should be displayed when removing yourself from a list.
         * @returns {boolean} returns true if you are removing yourself from either the owners or admins list, otherwise
         * returns false
         */
        $scope.showWarningRemovingSelf = function () {
            return $scope.currentUser === $scope.userToRemove
                && ($scope.listName === "owners" || $scope.listName === "admins");
        };

    }

    UHGroupingsApp.controller("GeneralJsController", GeneralJsController);

})();
