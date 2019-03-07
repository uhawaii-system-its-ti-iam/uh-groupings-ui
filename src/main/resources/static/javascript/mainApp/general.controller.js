(function () {

    /**
     * This controller contains shared functions between the admin and groupings page.
     * @param $scope - binding between controller and HTML page
     * @param $window - the browser window object
     * @param $uibModal - the UI Bootstrap service for creating modals
     * @param $controller - service for instantiating controllers
     * @param dataProvider - service that handles redirection to the feedback page upon error
     * @param groupingsService - service for creating requests to the groupings API
     */
    function GeneralJsController($scope, $window, $uibModal, $controller, groupingsService, dataProvider, PAGE_SIZE) {

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
        $scope.paginatingProgress = true;
        $scope.paginatingComplete = false;
        $scope.largeGrouping = false;

        angular.extend(this, $controller("TableJsController", {$scope: $scope}));

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
         * Generic handler for unsuccessful requests to the API.
         */
        function handleUnsuccessfulRequest(res) {
            console.log("Error: Status Code " + res.statusCode);
        }

        /**
         * @param {object[]} members - the members of the group
         * @returns {object[]} the members of the group, sorted by name and with blank usernames filtered out
         */
        function setGroupMembers(members) {
            _.remove(members, function (member) {
                return _.isEmpty(member.username);
            });

            // Unique members only by UUID (assume no two users should have the same uuid)
            members = _.uniqBy(members, "uuid");

            return _.sortBy(members, "name");
        }

        /**
         * @param {object[]} initialMembers - initial members in group
         * @param {object[]} membersToAdd - members to add to group
         * @returns {object[]} the members of both groups in one array, sorted by name with blank usernames filtered out
         */
        function combineGroupMembers(initialMembers, membersToAdd) {

            _.remove(membersToAdd, function (member) {
                return _.isEmpty(member.username);
            });

            var newMembers = _.concat(initialMembers, membersToAdd);

            // Unique members only by UUID (assume no two users should have the same uuid)
            newMembers = _.uniqBy(newMembers, "uuid");

            return _.sortBy(newMembers, "name");
        }

        /**
         * Gets information about the grouping, such as its members and the preferences set.
         * Retrieves information asynchronously page by page
         */
        $scope.getGroupingInformation = function () {
            $scope.loading = true;

            var groupingPath = $scope.selectedGrouping.path;

            groupingsService.getGrouping(groupingPath, 1, PAGE_SIZE, "name", true, function (res) {

                // Gets members in the basis group
                $scope.groupingBasis = setGroupMembers(res.basis.members);
                $scope.filter($scope.groupingBasis, "pagedItemsBasis", "currentPageBasis", $scope.basisQuery, true);

                //Gets members in the include group
                $scope.groupingInclude = setGroupMembers(res.include.members);
                $scope.addInBasis($scope.groupingInclude);
                $scope.filter($scope.groupingInclude, "pagedItemsInclude", "currentPageInclude", $scope.includeQuery, true);

                //Gets members in the exclude group
                $scope.groupingExclude = setGroupMembers(res.exclude.members);
                $scope.addInBasis($scope.groupingExclude);
                $scope.filter($scope.groupingExclude, "pagedItemsExclude", "currentPageExclude", $scope.excludeQuery, true);

                //Gets members in grouping
                $scope.groupingMembers = setGroupMembers(res.composite.members);
                $scope.addWhereListed($scope.groupingMembers);
                $scope.filter($scope.groupingMembers, "pagedItemsMembers", "currentPageMembers", $scope.membersQuery, true);

                //Gets owners of the grouping
                $scope.groupingOwners = setGroupMembers(res.owners.members);
                $scope.pagedItemsOwners = $scope.groupToPages($scope.groupingOwners);

                $scope.allowOptIn = res.optInOn;
                $scope.allowOptOut = res.optOutOn;
                $scope.listserv = res.listservOn;
                $scope.ldap = res.ldapOn;

                //Stop loading spinner and turn on loading text
                $scope.loading = false;
                $scope.paginatingProgress = true;

                // Recursive function to retrieve the rest of the pages
                $scope.getPages(groupingPath, 2, PAGE_SIZE, "name", true);
            }, function (res) {
                dataProvider.handleException({exceptionMessage: res.exceptionMessage}, "feedback/error", "feedback");
            });

            //todo Keeping code in case
            // var page = 2;
            // // for (var i = 0; i < 20; i++) {
            // while ($scope.endPagination === false && page < 50) {
            //
            //     console.log("Page: " + page);
            //
            //     // On success function doesn't execute until loop finishes...
            //     groupingsService.getGrouping(groupingPath, page, 20, "name", true, function (res) {
            //
            //         $scope.groupingBasis = combineGroupMembers($scope.groupingBasis, res.basis.members);
            //         $scope.filter($scope.groupingBasis, "pagedItemsBasis", "currentPageBasis", $scope.basisQuery);
            //
            //         //Gets members in the include group
            //         $scope.groupingInclude = combineGroupMembers($scope.groupingInclude, res.include.members);
            //         $scope.addInBasis($scope.groupingInclude);
            //         $scope.filter($scope.groupingInclude, "pagedItemsInclude", "currentPageInclude", $scope.includeQuery);
            //
            //         //Gets members in the exclude group
            //         $scope.groupingExclude = combineGroupMembers($scope.groupingExclude, res.exclude.members);
            //         $scope.addInBasis($scope.groupingExclude);
            //         $scope.filter($scope.groupingExclude, "pagedItemsExclude", "currentPageExclude", $scope.excludeQuery);
            //
            //         //Gets members in grouping
            //         $scope.groupingMembers = combineGroupMembers($scope.groupingMembers, res.composite.members);
            //         $scope.addWhereListed($scope.groupingMembers);
            //         $scope.filter($scope.groupingMembers, "pagedItemsMembers", "currentPageMembers", $scope.membersQuery);
            //
            //         //Gets owners of the grouping
            //         $scope.groupingOwners = combineGroupMembers($scope.groupingOwners, res.owners.members);
            //         $scope.pagedItemsOwners = $scope.groupToPages($scope.groupingOwners);
            //
            //         if (res.basis.members.length === 0 && res.include.members.length === 0 &&
            //             res.exclude.members.length === 0 && res.composite.members.length === 0 && res.owners.members.length === 0) {
            //             $scope.endPagination = true;
            //         }
            //
            //
            //     }, function (res) {
            //         $scope.endPagination = true;
            //         dataProvider.handleException({exceptionMessage: res.exceptionMessage}, "feedback/error", "feedback");
            //     });
            //     page++;
            // }
        };

        //todo This breaks after 100~ pages (returns Connection: Close in headers and net::ERR_INCOMPLETE_CHUNKED_ENCODING 200
        //todo Possibly switch to an iterative approach in the future
        /**
         * Recursive function to get pages of a grouping asynchronously
         * @param {String} groupingPath - Path to the grouping to retrieve data from
         * @param {Integer} page - Page of grouping to retrieve (Paging starts from 1)
         * @param {Integer} size - Size of page to retrieve
         * @param {String} sortString - Parameter to sort the grouping database by before retrieving information
         * @param {Boolean} isAscending - If true, grouping database is sorted ascending (A-Z), false for descending (Z-A)
         */

        $scope.getPages = function (groupingPath, page, size, sortString, isAscending) {

            groupingsService.getGrouping(groupingPath, page, size, sortString, isAscending, function (res) {

                // Add members to grouping if the page we got wasn't completely empty of members
                if (res.basis.members.length !== 0 || res.include.members.length !== 0 ||
                    res.exclude.members.length !== 0 || res.composite.members.length !== 0 || res.owners.members.length !== 0) {

                    $scope.groupingBasis = combineGroupMembers($scope.groupingBasis, res.basis.members);
                    $scope.filter($scope.groupingBasis, "pagedItemsBasis", "currentPageBasis", $scope.basisQuery, false);

                    //Gets members in the include group
                    $scope.groupingInclude = combineGroupMembers($scope.groupingInclude, res.include.members);
                    $scope.addInBasis($scope.groupingInclude);
                    $scope.filter($scope.groupingInclude, "pagedItemsInclude", "currentPageInclude", $scope.includeQuery, false);

                    //Gets members in the exclude group
                    $scope.groupingExclude = combineGroupMembers($scope.groupingExclude, res.exclude.members);
                    $scope.addInBasis($scope.groupingExclude);
                    $scope.filter($scope.groupingExclude, "pagedItemsExclude", "currentPageExclude", $scope.excludeQuery, false);

                    //Gets members in grouping
                    $scope.groupingMembers = combineGroupMembers($scope.groupingMembers, res.composite.members);
                    $scope.addWhereListed($scope.groupingMembers);
                    $scope.filter($scope.groupingMembers, "pagedItemsMembers", "currentPageMembers", $scope.membersQuery, false);

                    //Gets owners of the grouping
                    $scope.groupingOwners = combineGroupMembers($scope.groupingOwners, res.owners.members);
                    $scope.pagedItemsOwners = $scope.groupToPages($scope.groupingOwners);

                    // Retrieve the next page
                    $scope.getPages(groupingPath, page + 1, size, "name", true);

                } else {
                    // Stop loading text
                    $scope.paginatingProgress = false;
                    $scope.paginatingComplete = true;
                }
            }, function (res) {
                if(res === null) {
                    $scope.largeGrouping = true;
                    $scope.paginatingComplete = false;
                    $scope.paginatingProgress = false;

                    // console.log("Progress", $scope.paginatingProgress);
                    // console.log("Complete", $scope.paginatingComplete);
                    // console.log("Large", $scope.largeGrouping);
                } else {
                    dataProvider.handleException({exceptionMessage: res.exceptionMessage}, "feedback/error", "feedback");
                }
            });
        }

        /**
         * Creates a modal for errors in loading data from the API.
         */
        $scope.createApiErrorModal = function () {
            $scope.apiErrorModalInstance = $uibModal.open({
                templateUrl: "modal/apiError",
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
                member.inBasis = _.some($scope.groupingBasis, {uuid: memberUuid})
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
                if (_.some($scope.groupingBasis, {uuid: memberUuid})) {
                    member.whereListed = "Basis";
                }

                if (_.some($scope.groupingInclude, {uuid: memberUuid})) {
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

            if (_.isEmpty(userToAdd)) {
                $scope.createAddErrorModal(userToAdd);
            } else if ($scope.existInList(userToAdd, list)) {
                $scope.createCheckModal(userToAdd, list, false);
            } else if ($scope.isInAnotherList(userToAdd, list)) {
                $scope.createCheckModal(userToAdd, list, true);
            } else {
                $scope.createConfirmAddModal({
                    userToAdd: userToAdd,
                    listName: list
                });
            }
        };

        /**
         * Initiates the adding of a member to a list.
         * @param {string} userToAdd - user being added
         * @param {string} list - the list the user is being added to
         */
        $scope.updateAddMember = function (userToAdd, list) {

            // only initialize groupingPath is listNmae is not "admins"

            if ($scope.listName != "admins") {
                groupingPath = $scope.selectedGrouping.path;
            }

            var handleSuccessfulAdd = function (res) {
                $scope.createSuccessfulAddModal({
                    user: userToAdd,
                    listName: list,
                    response: res
                });
            };

            if (list === "Include") {
                groupingsService.addMemberToInclude(groupingPath, userToAdd, handleSuccessfulAdd, handleUnsuccessfulRequest);
            } else if (list === "Exclude") {
                groupingsService.addMemberToExclude(groupingPath, userToAdd, handleSuccessfulAdd, handleUnsuccessfulRequest);
            } else if (list === "owners") {
                groupingsService.assignOwnership(groupingPath, userToAdd, handleSuccessfulAdd, handleUnsuccessfulRequest);
            } else if (list === "admins") {
                groupingsService.addAdmin(userToAdd, handleSuccessfulAdd, handleUnsuccessfulRequest);
            }
            // $scope.init();
        };

        /**
         * @param {string} user - the user you are checking to see if they are in another list.
         * @param {string} list - the list the user is currently being added to
         * @returns {boolean} - true if the person is already in another list, else false.
         */
        $scope.isInAnotherList = function (user, list) {
            if (list === "Include") {
                return _.some($scope.groupingExclude, {username: user});
            } else if (list === "Exclude") {
                return _.some($scope.groupingInclude, {username: user});
            }
            return false;
        };

        /**
         * @param {string} user - the user you are checking to see if they are already in the list being added to
         * @param {string} list - the list the user is currently being added to
         * @returns {boolean} true if the user is already in the list being added to, otherwise returns false
         */
        $scope.existInList = function (user, list) {
            if (list === "Include") {
                return _.some($scope.groupingInclude, {username: user});
            } else if (list === "Exclude") {
                return _.some($scope.groupingExclude, {username: user});
            }
            return false;
        };

        /**
         * Creates a modal that asks whether or not they want to add a person that is already in another list.
         * @param user - Username of the user they are trying to add.
         * @param listName - name of the list they are adding to (either Include or Exclude)
         */
        $scope.createCheckModal = function (user, listName, swap) {
            $scope.user = user;
            $scope.listName = listName;
            $scope.swap = swap;

            $scope.checkModalInstance = $uibModal.open({
                templateUrl: "modal/checkModal",
                scope: $scope
            });

            $scope.checkModalInstance.result.then(function () {
                $scope.updateAddMember(user, listName);
            });
        };

        /**
         * Creates a modal that asks for confirmation when adding a user.
         * @param {object} options - the options object
         * @param {string} options.userToAdd - the user to add
         * @param {string} options.listName - name of the list being added to
         */
        $scope.createConfirmAddModal = function (options) {
            var userToAdd = options.userToAdd;

            groupingsService.getMemberAttributes(userToAdd, function (attributes) {
                $scope.uidToAdd = attributes.uid;
                $scope.uhuuidToAdd = attributes.uhuuid;
                $scope.nameToAdd = attributes.cn;

                $scope.listName = options.listName;

                // Ask for confirmation from the user to add the member
                $scope.confirmAddModalInstance = $uibModal.open({
                    templateUrl: "modal/confirmAddModal",
                    scope: $scope
                });

                $scope.confirmAddModalInstance.result.then(function () {
                    $scope.updateAddMember(userToAdd, options.listName);
                });
            }, function (res) {
                if (res.statusCode === 404) {
                    $scope.createAddErrorModal(userToAdd);
                }
            });
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

            if (_.isEmpty(ownerToAdd)) {
                $scope.createAddErrorModal(ownerToAdd);
            } else {
                $scope.createConfirmAddModal({
                    userToAdd: ownerToAdd,
                    listName: "owners"
                });
            }
        };

        /**
         * Creates a modal telling the user whether or not the user was successfully added into the grouping/admin list.
         * @param {object} options - the options object
         * @param {string} options.userToAdd - the user being added
         * @param {string?} options.response - the response from adding a member
         * @param {string?} options.listName - the list where the user was being added to
         */
        $scope.createSuccessfulAddModal = function (options) {
            $scope.user = options.user;
            $scope.listName = options.listName;

            $scope.addModalInstance = $uibModal.open({
                templateUrl: "modal/addModal",
                scope: $scope
            });

            $scope.addModalInstance.result.finally(function () {
                clearAddMemberInput(options.listName);
                $scope.loading = true;
                if ($scope.listName === "admins") {
                    // Refreshes the groupings list and the admins list
                    $scope.init();
                } else {
                    $scope.getGroupingInformation();
                }
            });
        };

        /**
         * Closes the add user modal.
         */
        $scope.closeSuccessfulAddModal = function () {
            $scope.addModalInstance.close();
        };

        $scope.createAddErrorModal = function (userAdded) {
            $scope.user = userAdded;

            $scope.addErrorModalInstance = $uibModal.open({
                templateUrl: "modal/addErrorModal",
                scope: $scope
            });
        };

        $scope.closeAddErrorModal = function () {
            $scope.addErrorModalInstance.close();
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
                userToRemove = $scope.pagedItemsInclude[currentPage][index];
            } else if (listName === "Exclude") {
                userToRemove = $scope.pagedItemsExclude[currentPage][index];
            }

            $scope.createRemoveModal({
                user: userToRemove,
                listName: listName
            });
        };

        /**
         * Removes a grouping owner. There must be at least one grouping owner remaining.
         * @param {number} currentPage - the current page in the owners table
         * @param {number} index - the index of the owner clicked by the user
         */
        $scope.removeOwner = function (currentPage, index) {
            var ownerToRemove = $scope.pagedItemsOwners[currentPage][index];

            if ($scope.groupingOwners.length > 1) {
                $scope.createRemoveModal({
                    user: ownerToRemove,
                    listName: "owners"
                });
            } else {
                var userType = "owner";
                $scope.createRemoveErrorModal(userType);
            }
        };

        /**
         * Handler for successfully removing a member from the Include or Exclude group.
         */
        function handleMemberRemove() {
            $scope.getGroupingInformation();
        }

        /**
         * Handler for successfully removing an owner from a grouping.
         */
        function handleOwnerRemove() {
            // Reload the grouping if you are not removing yourself, or if deleting anyone from the admins page
            if ($scope.currentUser !== $scope.userToRemove.username || !_.isUndefined($scope.adminsList)) {
                $scope.getGroupingInformation();
            } else if ($scope.currentUser === $scope.userToRemove.username) {
                // Removing self from last grouping owned -> redirect to home page
                if ($scope.groupingsList.length === 1) {
                    $window.location.href = "home";
                } else {
                    $window.location.href = "groupings";
                }
            }
        }

        /**
         * Handler for successfully removing an admin from a grouping.
         */
        function handleAdminRemove() {
            if ($scope.currentUser === $scope.userToRemove.username) {
                $window.location.href = "home";
            } else {
                $scope.init();
            }
        }

        /**
         * Creates a modal that prompts the user whether they want to delete the user or not. If 'Yes' is pressed, then
         * a request is made to delete the user.
         * @param {object} options - the options object
         * @param {object} options.user - the user being removed
         * @param {string} options.listName - where the user is being removed from
         */
        $scope.createRemoveModal = function (options) {
            $scope.userToRemove = options.user;
            $scope.listName = options.listName;
            var windowClass = $scope.showWarningRemovingSelf() ? "modal-danger" : "";

            $scope.removeModalInstance = $uibModal.open({
                templateUrl: "modal/removeModal",
                windowClass: windowClass,
                scope: $scope
            });

            $scope.removeModalInstance.result.then(function () {
                $scope.loading = true;
                var userToRemove = options.user.username;

                // groupingPath should only be defined if listName is not "admins"
                if ($scope.listName != "admins") {
                    groupingPath = $scope.selectedGrouping.path;
                }

                if ($scope.listName === "Include") {
                    groupingsService.removeMemberFromInclude(groupingPath, userToRemove, handleMemberRemove, handleUnsuccessfulRequest);
                } else if ($scope.listName === "Exclude") {
                    groupingsService.removeMemberFromExclude(groupingPath, userToRemove, handleMemberRemove, handleUnsuccessfulRequest);
                } else if ($scope.listName === "owners") {
                    groupingsService.removeOwner(groupingPath, userToRemove, handleOwnerRemove, handleUnsuccessfulRequest);
                } else if ($scope.listName === "admins") {
                    groupingsService.removeAdmin(userToRemove, handleAdminRemove, handleUnsuccessfulRequest);
                }
            });
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
         * Creates a modal stating there was an error removing the user from a group.
         * @param {string} userType - the type of user being removed (either admin or owner)
         */
        $scope.createRemoveErrorModal = function (userType) {
            $scope.userType = userType;

            $scope.removeErrorModalInstance = $uibModal.open({
                templateUrl: "modal/removeErrorModal",
                scope: $scope
            });
        };

        /**
         * Closes the remove error modal.
         */
        $scope.closeRemoveErrorModal = function () {
            $scope.removeErrorModalInstance.close();
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
                var anchorTag = $(pills[i].children[0]);
                var pillContent = $(pillContents[i]);
                if (i === 0 && !anchorTag.hasClass("active")) {
                    anchorTag.addClass("active");
                    pillContent.addClass("show active");
                } else if (i !== 0 && anchorTag.hasClass("active")) {
                    anchorTag.removeClass("active");
                    pillContent.removeClass("show active");
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
         * Clears the user input for adding a member to a list.
         * @param {string?} listName - the name of the list the member is being added to
         */
        function clearAddMemberInput(listName) {
            switch (listName) {
                case "Include":
                case "Exclude":
                    $scope.userToAdd = "";
                    break;
                case "owners":
                    $scope.ownerToAdd = "";
                    break;
                case "admins":
                    $scope.adminToAdd = "";
                    break;
                default:
                    $scope.userToAdd = "";
                    $scope.ownerToAdd = "";
                    $scope.adminToAdd = "";
            }
        }

        /**
         * Returns to the list of groupings available for management/administration.
         */
        $scope.returnToGroupingsList = function () {
            $scope.resetGroupingInformation();

            // Ensure the groupings list is reset with the now-blank filter
            $scope.filter($scope.groupingsList, "pagedItemsGroupings", "currentPageGroupings", $scope.groupingsQuery, true);

            $scope.showGrouping = false;
        };

        /**
         * Resets the grouping members, page numbers, filters, and columns to sort by.
         */
        $scope.resetGroupingInformation = function () {
            resetGroupingMembers();
            resetPillsToAllMembers();
            resetFilterQueries();
            clearAddMemberInput();
            $scope.columnSort = {};
        };

        /**
         * Creates a modal with a description of the preference selected.
         * @param {string} desc - the description of the preference
         */
        $scope.createPreferenceInfoModal = function (desc) {
            $scope.preferenceInfo = desc;

            $scope.infoModalInstance = $uibModal.open({
                templateUrl: "modal/infoModal",
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
         * Handler for a successful preference toggle.
         * @param {object} res - the JSON response from toggling the preference
         */
        function handleSuccessfulPreferenceToggle(res) {
            if (!_.isUndefined(res.statusCode)) {
                console.log("Error, Status Code: " + res.statusCode);
                $scope.createPreferenceErrorModal();
            } else if (_.startsWith(res[0].resultCode, "SUCCESS")) {
                console.log("success");
            }
        }

        /**
         * Toggles the grouping preference which allows users to opt out of a grouping.
         */
        $scope.updateAllowOptOut = function () {
            var groupingPath = $scope.selectedGrouping.path;
            var allowOptOut = $scope.allowOptOut;

            groupingsService.setOptOut(groupingPath, allowOptOut, handleSuccessfulPreferenceToggle, handleUnsuccessfulRequest);
        };

        /**
         * Toggles the grouping preference which allows users to discover the grouping and opt into it.
         */
        $scope.updateAllowOptIn = function () {
            var groupingPath = $scope.selectedGrouping.path;
            var allowOptIn = $scope.allowOptIn;

            groupingsService.setOptIn(groupingPath, allowOptIn, handleSuccessfulPreferenceToggle, handleUnsuccessfulRequest);
        };

        /**
         * Toggles the grouping preference which creates a LISTSERV email list based off the grouping.
         */
        $scope.updateListserv = function () {
            var groupingPath = $scope.selectedGrouping.path;
            var listservOn = $scope.listserv;

            groupingsService.setListserv(groupingPath, listservOn, handleSuccessfulPreferenceToggle, handleUnsuccessfulRequest);
        };

        /**
         * Toggles the grouping preference to synchronize memberships with the uhReleasedGroupings attribute.
         */
        $scope.updateLdap = function () {
            var groupingPath = $scope.selectedGrouping.path;
            var ldapOn = $scope.ldap;

            groupingsService.setLdap(groupingPath, ldapOn, handleSuccessfulPreferenceToggle, handleUnsuccessfulRequest);
        };

        /**
         * Creates a modal indicating an error in saving the grouping's preferences.
         */
        $scope.createPreferenceErrorModal = function () {
            $scope.preferenceErrorModalInstance = $uibModal.open({
                templateUrl: "modal/preferenceErrorModal",
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
         * Create CAS/LDAP confirmation modal.
         */
        $scope.createCASLDAPModal = function () {
            $scope.ldap = !$scope.ldap;
            $scope.CASLDAPInstance = $uibModal.open({
                templateUrl: "modal/CASLDAPModal",
                scope: $scope
            });

            $scope.CASLDAPInstance.result.then(function () {
                $scope.ldap = !$scope.ldap;
                $scope.updateLdap();
            }).catch(function () {
                //do nothing
            });
        };

        /**
         * Proceeds with the CAS/LDAP confirmation
         */
        $scope.proceedCASLDAPModal = function () {
            $scope.CASLDAPInstance.close();
        };

        /**
         * Closes the CAS/LDAP confirmation modal
         */
        $scope.closeCASLDAPModal = function () {
            $scope.CASLDAPInstance.dismiss();
        };

        /**
         * Create Email list confirmation modal.
         */
        $scope.createEmailListModal = function () {
            $scope.listserv = !$scope.listserv;
            $scope.EmailListInstance = $uibModal.open({
                templateUrl: "modal/EmailListModal",
                scope: $scope
            });

            $scope.EmailListInstance.result.then(function () {
                $scope.listserv = !$scope.listserv;
                $scope.updateListserv();
            }).catch(function () {
                //do nothing
            });

        };

        /**
         * Proceeds with the change of the Email list
         */
        $scope.proceedEmailListModal = function () {
            $scope.EmailListInstance.close();
        };

        /**
         *Closes the Email list confirmation modal
         */
        $scope.closeEmailListModal = function () {
            $scope.EmailListInstance.dismiss();
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
                line += table[i].username + ",";
                line += table[i].username + "@hawaii.edu,";
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
            return $scope.currentUser === $scope.userToRemove.username
                && ($scope.listName === "owners" || $scope.listName === "admins");
        };

    }

    UHGroupingsApp.controller("GeneralJsController", GeneralJsController);

})();