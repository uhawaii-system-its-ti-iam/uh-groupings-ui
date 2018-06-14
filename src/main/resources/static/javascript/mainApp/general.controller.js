(function () {

    /**
     * This controller contains shared functions between the admin and groupings page.
     * @param $scope - binding between controller and HTML page
     * @param $uibModal - the UI Bootstrap service for creating modals
     * @param dataProvider - service function that provides GET and POST requests for getting or updating data
     */
    function GeneralJsController($scope, $http, $window, $uibModal, $controller, dataProvider) {

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
        $scope.LDAP = false;

        $scope.showGrouping = false;

        $scope.loading = false;

        angular.extend(this, $controller("TableJsController", { $scope: $scope }));

        /**
         * Retrieves information about the grouping.
         */
        $scope.getData = function () {
            $scope.loading = true;
            var groupingDataUrl = "api/groupings/" + $scope.selectedGrouping.path + "/grouping";

            dataProvider.loadData(function (d) {
                console.log(d);
                //Gets members in the basis group
                $scope.groupingBasis = d.basis.members;
                $scope.pagedItemsBasis = $scope.groupToPages($scope.groupingBasis);

                //Gets members in the include group
                $scope.groupingInclude = d.include.members;
                console.log($scope.groupingInclude.length);
                $scope.modify($scope.groupingInclude);
                $scope.pagedItemsInclude = $scope.groupToPages($scope.groupingInclude);

                //Gets members in the exclude group
                $scope.groupingExclude = d.exclude.members;
                $scope.modify($scope.groupingExclude);
                $scope.pagedItemsExclude = $scope.groupToPages($scope.groupingExclude);

                //Gets members in grouping
                $scope.groupingMembers = d.composite.members;
                $scope.modify($scope.groupingMembers, "members");
                $scope.pagedItemsMembers = $scope.groupToPages($scope.groupingMembers);

                //Gets owners of the grouping
                $scope.groupingOwners = d.owners.members;
                $scope.modify($scope.groupingOwners);
                $scope.pagedItemsOwners = $scope.groupToPages($scope.groupingOwners);

                $scope.allowOptIn = d.optInOn;
                $scope.allowOptOut = d.optOutOn;
                $scope.listserv = d.listservOn;

                //Stop loading spinner
                $scope.loading = false;
                $scope.showGrouping = true;
            }, function (d) {
                dataProvider.handleException({ exceptionMessage: d.exceptionMessage }, "feedback/error", "feedback");
            }, groupingDataUrl);
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
         * Modifies data in a grouping by filtering out the domain name, checking the user's membership in the group,
         * and checking for blanks in the username, and sorting the members by name.
         * @param {object[]} grouping - the grouping to modify
         * @param {string?} list - the name of the list being modified
         */
        $scope.modify = function (grouping, list) {
            //Filter out names with hawaii.edu and adds basis object.
            for (var i = 0; i < grouping.length; i++) {
                if (list === "members") grouping[i].basis = "Include";
                else grouping[i].basis = "No";
                if (grouping[i].name.indexOf("hawaii.edu") > -1) {
                    grouping.splice(i, 1);
                    i--;
                }
            }

            //Determines if member is in the basis or not
            for (var l = 0; l < $scope.groupingBasis.length; l++) {
                for (var m = 0; m < grouping.length; m++) {
                    if ($scope.groupingBasis[l].uuid === grouping[m].uuid) {
                        if (list === "members") {
                            grouping[m].basis = "Basis";
                            for (var k = 0; k < $scope.groupingInclude.length; k++) {
                                if ($scope.groupingInclude[k].uuid === grouping[m].uuid) {
                                    grouping[m].basis = "Basis / Include";
                                }
                            }
                        }
                        else grouping[m].basis = "Yes";
                    }
                }
            }

            //sorts data in alphabetic order
            grouping.sort(function (a, b) {
                var nameA = a.name.toLowerCase(), nameB = b.name.toLowerCase();
                if (nameA < nameB) //sort string ascending
                    return -1;
                if (nameA > nameB)
                    return 1;
                return 0;
            });
        };

        /**
         * Adds a user to a group.
         * @param {string} type - the type of group to add the user to (either Include or Exclude)
         */
        $scope.addMember = function (type) {
            var userToAdd = $scope.addUser;
            var addUrl = "api/groupings/" + $scope.selectedGrouping.path + "/" + userToAdd + "/addMemberTo" + type + "Group";
            if($scope.userCheck(userToAdd, type))
                $scope.createCheckModal(userToAdd, type, addUrl);
            else
                $scope.updateAddMember(userToAdd, type, addUrl);
        };

        /**
         * Calls API to add the member to the grouping list
         * and if they are already in another list, removes them from the other list.
         *
         * @param userToAdd - User you want to add
         * @param type - Grouping list you are adding to.
         * @param addUrl - Url for API call.
         */
        $scope.updateAddMember = function(userToAdd, type , addUrl) {
            dataProvider.updateData(function (d) {
                var successful = false;
                var responseLength = d.length;
                var wasRemoved = false;
                if (responseLength === undefined || d[responseLength - 1].statusCode != null) {
                    console.log("Error, Status Code: " + d.statusCode);
                } else if (d[responseLength - 1].resultCode.indexOf("SUCCESS" === 0)) {
                    successful = true;
                }
                // If we add a user to the include group, we want to check if they were removed from the exclude group.
                // Similarly, if we add a user to the exclude group, we want to check if they were removed from the
                // include group.
                if (successful && d[0].action.indexOf("delete Person") === 0) {
                    wasRemoved = true;
                }
                var listName = type;
                $scope.createAddModal(userToAdd, successful, listName, $scope.selectedGrouping.path, wasRemoved);
                $scope.addUser = "";
            }, addUrl);
        };

        /**
         * Checks whether the person is already in another group
         * @param person - the person you are checking to see if they are in another list.
         * @param type - the list that you are comparing against.
         * @returns {boolean} - True if the person is already in another list, else false.
         */
        $scope.userCheck = function(person, type)
        {
            if(type === "Include")
            {
                for(var i = 0; i < $scope.groupingExclude.length; i++) {
                    if ($scope.groupingExclude[i].username === person)
                        return true;
                }
            }
            if(type === "Exclude")
            {
                for(var j = 0; j < $scope.groupingInclude.length; j++) {
                    if ($scope.groupingInclude[j].username === person)
                        return true;
                }
            }

            return false;
        };

        /**
         * Gives a user ownership of a grouping.
         */
        $scope.addOwner = function () {
            var ownerToAdd = $scope.ownerUser;
            var addOwnerUrl = "api/groupings/" + $scope.selectedGrouping.path + "/" + ownerToAdd + "/assignOwnership";
            dataProvider.updateData(function (d) {
                var successful = false;
                if (d.statusCode != null) {
                    console.log("Error, Status Code: " + d.statusCode);
                } else if (d.resultCode === "SUCCESS") {
                    successful = true;
                    console.log("Assigned " + $scope.ownerUser + " as an owner");
                }
                var listName = "owners";
                $scope.createAddModal(ownerToAdd, successful, listName, $scope.selectedGrouping.path);
                $scope.ownerUser = "";
            }, addOwnerUrl);
        };

        /**
         * Creates modal that ask whether or not they want to add a person that is already in another list.
         * @param user - Username of the user they are trying to add.
         * @param listname - name of the list they are adding to. Either Include list or Exclude list.
         * @param addUrl - API Url that will be called to update for adding members
         */
        $scope.createCheckModal = function(user, listname, addUrl)
        {
            $scope.user = user;
            $scope.listName = listname;
            $scope.checkModalInstance = $uibModal.open({
                templateUrl: "modal/checkModal.html",
                scope: $scope
            });

            $scope.checkModalInstance.result.then(function () {
                $scope.updateAddMember(user, listname, addUrl);
            });

        };

        /**
         * Closes CheckModal and proceeds with the checkModalInstance result.then function
         */
        $scope.proceedCheckModal = function()
        {
            $scope.checkModalInstance.close();
        };

        /**
         * Dismisses the CheckModal and closes it with proceeding with checkModalInstance's result.then function.
         */
        $scope.closeCheckModal = function()
        {
            $scope.checkModalInstance.dismiss();
        };

        /**
         * Creates a modal telling the user whether or not the user was successfully added into the grouping/admin list.
         * @param {string} user - the user being added
         * @param {boolean} wasSuccessful - whether or not the user was successfully added
         * @param {string} listName - where the user is being added to
         * @param {string?} path - the path to the grouping (if deleting the user from a group)
         * @param {boolean?} wasRemoved - whether or not the user was removed from the opposite group (e.g. if adding a
         *                                user to the Include list, this parameter is true if the user was automatically
         *                                removed from the Exclude list from the addMember call)
         */
        $scope.createAddModal = function (user, wasSuccessful, listName, path, wasRemoved) {
            $scope.user = user;
            $scope.wasSuccessful = wasSuccessful;
            $scope.wasRemoved = wasRemoved;
            $scope.listName = listName;

            $scope.addModalInstance = $uibModal.open({
                templateUrl: "modal/addModal.html",
                scope: $scope
            });

            $scope.addModalInstance.result.finally(function () {
                if (wasSuccessful) {
                    $scope.loading = true;
                    // Path is only undefined if adding a user as an admin, so reload the admins list and groupings list
                    if (path === undefined) {
                        $scope.init();
                    } else {
                        // Reload the grouping data
                        $scope.getData(path);
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
         * @param {string} type - the type of group the user will be removed from (either Include or Exclude)
         * @param {number} index - the index of the user clicked by the user
         * account
         */
        $scope.removeMember = function (type, index) {
            var user;
            if (type === "Include") {
                user = $scope.pagedItemsInclude[$scope.currentPageInclude][index].username;
            } else if (type === "Exclude") {
                user = $scope.pagedItemsExclude[$scope.currentPageExclude][index].username;
            }
            var url = "api/groupings/" + $scope.selectedGrouping.path + "/" + user + "/deleteMemberFrom" + type + "Group";
            var listName = type;
            $scope.createRemoveModal(user, url, listName, $scope.selectedGrouping.path);
        };

        /**
         * Removes a grouping owner. There must be at least one grouping owner remaining.
         * @param {number} index - the index of the owner clicked by the user
         */
        $scope.removeOwner = function (index) {
            var removeOwner = $scope.pagedItemsOwners[$scope.currentPageOwners][index].username;
            var removeOwnerUrl = "api/groupings/" + $scope.selectedGrouping.path + "/" + removeOwner + "/removeOwnership";
            if ($scope.groupingOwners.length > 1) {
                var listName = "owners";
                $scope.createRemoveModal(removeOwner, removeOwnerUrl, listName, $scope.selectedGrouping.path);
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
         * Resets the arrays containing the members of each grouping and their page numbers.
         */
        $scope.resetGroupingInformation = function () {
            // Reset grouping member data for next load
            $scope.groupingMembers = [];
            $scope.groupingBasis = [];
            $scope.groupingInclude = [];
            $scope.groupingExclude = [];
            $scope.groupingOwners = [];
            // Reset paged items
            $scope.pagedItemsMembers = [];
            $scope.pagedItemsBasis = [];
            $scope.pagedItemsInclude = [];
            $scope.pagedItemsExclude = [];
            $scope.pagedItemsOwners = [];
            // Reset page numbers
            $scope.currentPageMembers = 0;
            $scope.currentPageBasis = 0;
            $scope.currentPageInclude = 0;
            $scope.currentPageExclude = 0;
            $scope.currentPageOwners = 0;
            // Reset column sorting
            $scope.columnSort = {};
        };

        /**
         * Resets the selected group to the list of all members.
         */
        $scope.resetSelectedGroup = function () {
            var pills = $("#group-pills")[0].children;
            var tabContents = $("#pill-content")[0].children;
            for (var i = 0; i < pills.length; i++) {
                if (i === 0 && !$(pills[i]).hasClass("active")) {
                    $(pills[i]).addClass("active");
                    $(tabContents[i]).addClass("in active");
                } else if (i !== 0 && $(pills[i]).hasClass("active")) {
                    $(pills[i]).removeClass("active");
                    $(tabContents[i]).removeClass("in active");
                }
            }
        };

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
            var url = "api/groupings/" + $scope.selectedGrouping.path + "/" + $scope.allowOptOut + "/setOptOut";
            dataProvider.updateData(function (d) {
                console.log(d);
                if (d.statusCode != null) {
                    console.log("Error, Status Code: " + d.statusCode);
                    $scope.createPreferenceErrorModal();
                } else if (d[0].resultCode === "SUCCESS_ALLOWED" || d[0].resultCode === "SUCCESS_NOT_ALLOWED") {
                    console.log("success");
                }
            }, url);
            console.log(url);

        };

        /**
         * Toggles the grouping preference which allows users to discover the grouping and opt into it.
         */
        $scope.updateAllowOptIn = function () {
            var url = "api/groupings/" + $scope.selectedGrouping.path + "/" + $scope.allowOptIn + "/setOptIn";
            dataProvider.updateData(function (d) {
                if (d.statusCode != null) {
                    console.log("Error, Status Code: " + d.statusCode);
                    $scope.createPreferenceErrorModal();
                } else if (d[0].resultCode === "SUCCESS_ALLOWED" || d[0].resultCode === "SUCCESS_NOT_ALLOWED") {
                    console.log("success");
                }
            }, url);
            console.log(url);
        };

        /**
         * Toggles the grouping preference which creates a LISTSERV email list based off the grouping.
         */
        $scope.updateListserv = function () {
            var url = "api/groupings/" + $scope.selectedGrouping.path + "/" + $scope.listserv + "/setListserv";
            dataProvider.updateData(function (d) {
                console.log(d);
                if (d.statusCode != null) {
                    console.log("Error, Status Code: " + d.statusCode);
                    $scope.createPreferenceErrorModal();
                } else if (d.resultCode === "SUCCESS") {
                    console.log("success");
                }
            }, url);

            console.log(url);
        };

        $scope.checkLDAP = function () {
            console.log($scope.LDAP);
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

        /**
         * Goes back to the list of groupings available for administration.
         */
        $scope.showGroups = function () {
            if (!$scope.showGrouping) {
                $scope.showGrouping = true;
            } else {
                $scope.showGrouping = false;
                $scope.resetGroupingInformation();
                $scope.resetSelectedGroup();
            }

            $scope.basisQuery = "";
            $scope.excludeQuery = "";
            $scope.includeQuery = "";
            $scope.membersQuery = "";

            $scope.groupingsQuery = "";
            // Ensure the groupings list is reset with the now-blank filter
            $scope.filter($scope.groupingsList, "pagedItemsGroupings", "currentPageGroupings", $scope.groupingsQuery);

        };

        $scope.resetFields = function () {
            $scope.addUser = "";
        };
        /**
         * Gets information about the grouping clicked by the user.
         * @param {number} index - the index of the grouping clicked by the user
         */
        $scope.showData = function (index) {
            $scope.selectedGrouping = $scope.pagedItemsGroupings[$scope.currentPageGroupings][index];
            if (!$scope.showGrouping) {
                $scope.showGrouping = true;
                $scope.getData();
            } else {
                $scope.showGrouping = false;
            }
        };

        /**
         * Exports data in a table to a CSV file
         * @param {object[]} table - the table to export
         * @param grouping - grouping name that you are exporting from
         * @param list - grouping list (i.e. include or exclude)
         */
        $scope.export = function (table, grouping, list) {
            var data, filename, link;

            var csv = $scope.convertArrayOfObjectsToCSV(table);
            if (csv == null) {
                $scope.createApiErrorModal();
                return;
            }

            filename = grouping + ":" + list + "_list.csv";

            if (!csv.match(/^data:text\/csv/i)) {
                csv = "data:text/csv;charset=utf-8," + csv;
            }
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
        $scope.convertArrayOfObjectsToCSV = function (table) {
            var str = "Last,First,Username,Email\r\n";
            for (var i = 0; i < table.length; i++) {
                var line = "";
                line += table[i].lastName + ",";
                line += table[i].firstName + ",";
                if (table[i].username) {
                    line += table[i].username + ",";
                    line += table[i].username + "@hawaii.edu,";
                } else {
                    line += ",,";
                }
                str += line + "\r\n";
            }
            return str;
        };

    }

    UHGroupingsApp.controller("GeneralJsController", GeneralJsController);

})();
