(function () {

    /**
     * This controller contains shared functions between the admin and groupings page.
     * @param $scope - binding between controller and HTML page
     * @param $uibModal - the UI Bootstrap service for creating modals
     * @param dataProvider - service function that provides GET and POST requests for getting or updating data
     */
    function GeneralJsController($scope, $window, $uibModal, $controller, dataProvider) {

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

        angular.extend(this, $controller('TableJsController', { $scope: $scope }));

        /**
         * Retrieves information about the grouping.
         */
        $scope.getData = function () {
            $scope.loading = true;
            var groupingDataUrl = "api/groupings/" + $scope.selectedGrouping.path + "fafaf/grouping";

            dataProvider.loadData(function (d) {
                console.log(d);
                if (d.path.length == 0) {
                    $scope.createApiErrorModal();
                } else {
                    //Gets members in the basis group
                    $scope.groupingBasis = d.basis.members;
                    $scope.modify($scope.groupingBasis);
                    $scope.pagedItemsBasis = $scope.groupToPages($scope.groupingBasis);

                    //Gets members in the include group
                    $scope.groupingInclude = d.include.members;
                    $scope.modify($scope.groupingInclude);
                    $scope.pagedItemsInclude = $scope.groupToPages($scope.groupingInclude);

                    //Gets members in the exclude group
                    $scope.groupingExclude = d.exclude.members;
                    $scope.modify($scope.groupingExclude);
                    $scope.pagedItemsExclude = $scope.groupToPages($scope.groupingExclude);

                    //Gets members in grouping
                    $scope.groupingMembers = d.composite.members;
                    $scope.modify($scope.groupingMembers, 'members');
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
                }
            },  function(d){
                console.log("error has occured");
                console.log(d);
                var error = encodeURI(d);
                $window.location.href = "/uhgroupings/feedback/" + error;
            }, groupingDataUrl);
        };

        /**
         * Creates a modal for errors in loading data from the API.
         */
        $scope.createApiErrorModal = function () {
            $scope.apiErrorModalInstance = $uibModal.open({
                templateUrl: 'modal/apiError.html',
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
                if (list === 'members') grouping[i].basis = "Include";
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
                        if (list === 'members') {
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
                return 0
            });
        };

        /**
         * Adds a user to a group.
         * @param {string} type - the type of group to add the user to (either Include or Exclude)
         */
        $scope.addMember = function (type) {
            var addUrl = "api/groupings/" + $scope.selectedGrouping.path + "/" + $scope.addUser + "/addMemberTo" + type + "Group";
            dataProvider.updateData(function (d) {
                var successful = false;
                var responseLength = d.length;
                if (responseLength === undefined || d[responseLength - 1].statusCode != null) {
                    console.log("Error, Status Code: " + d.statusCode);
                } else if (d[responseLength - 1].resultCode.indexOf('SUCCESS' === 0)) {
                    successful = true;
                }
                $scope.createAddModal($scope.addUser, successful, $scope.selectedGrouping.path);
                $scope.addUser = '';
            }, addUrl);
        };

        /**
         * Gives a user ownership of a grouping.
         */
        $scope.addOwner = function () {
            var addOwnerUrl = "api/groupings/" + $scope.selectedGrouping.path + "/" + $scope.ownerUser + "/assignOwnership";
            dataProvider.updateData(function (d) {
                var successful = false;
                if (d.statusCode != null) {
                    console.log("Error, Status Code: " + d.statusCode);
                } else if (d.resultCode === "SUCCESS") {
                    successful = true;
                    console.log("Assigned " + $scope.ownerUser + " as an owner");
                }
                $scope.createAddModal($scope.ownerUser, successful, $scope.selectedGrouping.path);
                $scope.ownerUser = '';
            }, addOwnerUrl);
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
            if (type === 'Include') {
                user = $scope.pagedItemsInclude[$scope.currentPageInclude][index].username;
            } else if (type === 'Exclude') {
                user = $scope.pagedItemsExclude[$scope.currentPageExclude][index].username;
            }
            var url = "api/groupings/" + $scope.selectedGrouping.path + "/" + user + "/deleteMemberFrom" + type + "Group";
            $scope.createRemoveModal(user, url, $scope.selectedGrouping.path);
        };

        /**
         * Removes a grouping owner. There must be at least one grouping owner remaining.
         * @param {number} index - the index of the owner clicked by the user
         */
        $scope.removeOwner = function (index) {
            var removeOwner = $scope.pagedItemsOwners[$scope.currentPageOwners][index].username;
            var removeOwnerUrl = "api/groupings/" + $scope.selectedGrouping.path + "/" + removeOwner + "/removeOwnership";
            if ($scope.groupingOwners.length > 1) {
                $scope.createRemoveModal(removeOwner, removeOwnerUrl, $scope.selectedGrouping.path);
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
        $scope.resetSelectedGroup = function() {
            var pills = $('#group-pills')[0].children;
            var tabContents = $('#pill-content')[0].children
            for (var i = 0; i < pills.length; i++) {
                if (i === 0 && !$(pills[i]).hasClass('active')) {
                    $(pills[i]).addClass('active');
                    $(tabContents[i]).addClass('in active');
                } else if (i !== 0 && $(pills[i]).hasClass('active')) {
                    $(pills[i]).removeClass('active');
                    $(tabContents[i]).removeClass('in active');
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
                templateUrl: 'modal/infoModal.html',
                scope: $scope,
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
            var url = "api/groupings/" + $scope.selectedGrouping.path + "/"  + $scope.allowOptOut + "/setOptOut";
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
                templateUrl: 'modal/preferenceErrorModal.html',
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

            $scope.queryGroupings = "";
            // Ensure the groupings list is reset with the now-blank filter
            $scope.filter($scope.groupingsList, 'pagedItemsGroupings', 'currentPageGroupings', 'queryGroupings');

        };

        $scope.resetFields = function(){
            $scope.addUser = "";
        }
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
         * @param name - the name of the group (i.e. include or exclude)
         */
        $scope.export = function (table, name) {
            var data, filename, link;

            var csv = $scope.convertArrayOfObjectsToCSV(table);
            if (csv == null) return;

            filename = name + '_export.csv';

            if (!csv.match(/^data:text\/csv/i)) {
                csv = 'data:text/csv;charset=utf-8,' + csv;
            }
            data = encodeURI(csv);

            link = document.createElement('a');
            link.setAttribute('href', data);
            link.setAttribute('download', filename);
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
            var str = 'Last,First,Username,Email\r\n';
            for (var i = 0; i < table.length; i++) {
                var line = '';
                line += table[i].lastName + ',';
                line += table[i].firstName + ',';
                if (table[i].username !== 'N/A') {
                    line += table[i].username + ',';
                    line += table[i].username + '@hawaii.edu,';
                } else {
                    line += ',,';
                }
                str += line + '\r\n';
            }
            return str;
        };

    }

    UHGroupingsApp.controller('GeneralJsController', GeneralJsController);

})();
