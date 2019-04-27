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

        //Possible switch to $log
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


            // used with ng-view on selected-grouping.html to toggle description editing.
            $scope.descriptionForm = false;
            //The max length usable when getting input
            $scope.maxDescriptionLength = 40;
            //The user input
            $scope.modelDescription;

            var maxLength = 40;
            var noDescriptionMessage = "No description given for this Grouping.";

            angular.extend(this, $controller("TableJsController", { $scope: $scope }));

            /**
             * Initiates the retrieval of information about the grouping clicked by the user.
             * @param {number} currentPage - the current page number in the groupings list
             * @param {number} index - the index of the grouping clicked by the user
             */
            $scope.displayGrouping = function (currentPage, index) {
                $scope.selectedGrouping = $scope.pagedItemsGroupings[currentPage][index];
                $scope.description = $scope.selectedGrouping.description;
                $scope.getGroupingInformation();

                $scope.showGrouping = true;
            };

            /**
             * Generic handler for unsuccessful requests to the API.
             */
            function handleUnsuccessfulRequest(res) {
                if (res.status === 403) {
                    $scope.createOwnerErrorModal();
                } else {
                    return `Error: Status Code${res.statusCode}`;
                }
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
            };


            /**
             * Gets information about the grouping, such as its members and the preferences set.
             * Retrieves information asynchronously page by page
             */


            $scope.getGroupingInformation = function () {
                $scope.loading = true;
                $scope.paginatingComplete = false;

                const groupingPath = $scope.selectedGrouping.path;

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

                    // Gets the description go the group
                    if (res.description == null) {
                        $scope.description = "";
                    } else {
                        $scope.description = res.description;
                    }


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
                    if (res.statusCode === 403) {
                        $scope.createOwnerErrorModal();
                    }
                });
            };

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
                    if (res === null) {
                        $scope.largeGrouping = true;
                        $scope.paginatingComplete = false;
                        $scope.paginatingProgress = false;

                        // console.log("Progress", $scope.paginatingProgress);
                        // console.log("Complete", $scope.paginatingComplete);
                        // console.log("Large", $scope.largeGrouping);

                    } else if (res.statusCode === 403) {
                        $scope.createOwnerErrorModal();
                    } else {
                        dataProvider.handleException({ exceptionMessage: res.exceptionMessage }, "feedback/error", "feedback");
                    }
                });
            };

            // used to check the length of the text string entered in the description form box, for error handling of max length
            $scope.descriptionLengthWarning = function () {


                return (String($scope.modelDescription).length >= maxLength);
            };

            /**
             * Enable or disable editing of a Grouping's description, from selected-grouping.html.
             */
            $scope.editDescription = function () {
                $scope.descriptionForm = !($scope.descriptionForm);
            };

            /**
             * Cancel the editing of a description, and revert back to base selected-grouping page.
             */
            $scope.cancelDescriptionEdit = function () {
                // refer to last saved description when user cancels the edit
                $scope.modelDescription = $scope.description;
                $scope.descriptionForm = !($scope.descriptionForm);
            };

            /**
             * Used for placeholder text for a grouping's description in the form box.
             * @returns {string} either the description of the grouping, or, placeholder text if the description is empty.
             */
            $scope.descriptionDisplay = function () {
                var descriptionLength;

                if ($scope.description === "") {
                    (descriptionLength = "");
                } else {
                    descriptionLength = String($scope.description);
                }

                return (descriptionLength.length > 0)
                    ? $scope.description
                    : noDescriptionMessage;
            };

            /**
             * Sets a new description for a Grouping.
             * TODOS:   --> make this function call RestController to change the description in Grouper.
             *          --> error checking?
             */
            $scope.saveDescription = function () {
                $scope.description = $scope.modelDescription;
                console.log("Description value: ", $scope.description);
                if (String($scope.description).length === 0) {
                    $scope.description = "";
                }
                groupingsService.updateDescription($scope.selectedGrouping.path, function () {
                    // Explain why this empty todo
                }, function (res) {
                    if (res.status === 403) {
                        $scope.createOwnerErrorModal();
                    }
                }, $scope.description);
                $scope.descriptionForm = !($scope.descriptionForm);

        /**
         * Adds a user to a group.
         * @param {string} list - the list the user is being added to (either Include or Exclude)
         */
        $scope.addMember = function (list) {
            const userToAdd = $scope.userToAdd;

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
                    const memberUuid = member.uuid;
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

                    const memberUuid = member.uuid;
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
             * @param {string} list - the list the user is being added to (either Include or Exclude jsdlkfjaslfsdalsdfks)
             */
            $scope.addMember = function (list) {
                const groupingPath = $scope.selectedGrouping.path;
                groupingsService.getGrouping(groupingPath, 1, PAGE_SIZE, "name", true, function () {
                    const userToAdd = $scope.userToAdd;
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
                }, function (res) {
                    if (res.statusCode === 403) {
                        $scope.createOwnerErrorModal();
                    }
                });

            };
            /**
             * Lets a user import multiple members to a grouping, in the long run this method triggers the
             * api method includeMultipleMembers.
             * @param list
             */
            $scope.addMembers = function (list) {
                let str = $scope.usersToAdd;
                let usersToAdd = str.replace(/\n/g, ", ");
                $scope.createConfirmAddMembersModal({
                    usersToAdd: usersToAdd,
                    listName: list
                });
                console.log(usersToAdd);

            };

            /**
             * Creates a modal display for members added, and calls addMembersToInclude service.
             * @param usersToAdd
             * @param list
             */
            $scope.updateAddMembers = function (usersToAdd, list) {

                let groupingPath = $scope.selectedGrouping.path;

                let handleSuccessfulAdd = function (res, list, usersToAdd) {
                    $scope.createSuccessfulAddModal({
                        user: usersToAdd,
                        listName: list,
                        response: res
                    });
                };
                if (list === "Include") {
                    groupingsService.addMembersToInclude(groupingPath, usersToAdd, handleSuccessfulAdd, handleUnsuccessfulRequest);
                }
            };
            /**
             * Initiates the adding of a member to a list.
             * @param {string} userToAdd - user being added
             * @param {string} list - the list the user is being added to
             */
            $scope.updateAddMember = function (userToAdd, list) {

                // only initialize groupingPath is listName is not "admins"
                let groupingPath;

                if ($scope.listName !== "admins") {
                    groupingPath = $scope.selectedGrouping.path;
                }


                const handleSuccessfulAdd = function (res) {
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
                    return _.some($scope.groupingExclude, { username: user });
                } else if (list === "Exclude") {
                    return _.some($scope.groupingInclude, { username: user });
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
                    return _.some($scope.groupingInclude, { username: user });
                } else if (list === "Exclude") {
                    return _.some($scope.groupingExclude, { username: user });
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
             * Creates a modal that asks for confirmation when importing multiple users.
             * @param {object} options - the options object
             * @param {string} options.usersToAdd - the users to import
             * @param {string} options.listName - name of the list being added to
             */
            $scope.createConfirmAddMembersModal = function (options) {
                $scope.confirmAddModalInstance = $uibModal.open({
                    templateUrl: "modal/confirmAddModal",
                    scope: $scope
                });
                $scope.confirmAddModalInstance.result.then(function () {
                    $scope.updateAddMembers(options.usersToAdd, options.listName);
                });

      /**
       * Creates checkAddModal.
       */
      $scope.createCheckAddModal = function (user, userToAdd, listName) {

          $scope.user = user;
        $scope.userToAdd = userToAdd;
          $scope.listName = listName;

        $scope.checkAddModalInstance = $uibModal.open({
          templateUrl: "modal/checkAddModal",
          scope: $scope
        });
        $scope.checkAddModalInstance.result.then(function () {
          $scope.user = userToAdd;
        });
      }, function(userToAdd, listName){
        if ($scope.userToAdd === listName) {
          $scope.createCheckAddModal();
        } else {
          $scope.updateAddMember(userToAdd, options.listName);
        }
      };

        /**
         * Closes CheckModal and proceeds with the checkModalInstance result.then function
         */
        $scope.proceedCheckModal = function () {
            $scope.checkModalInstance.close();
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

                const ownerToAdd = $scope.ownerToAdd;

                if (_.isEmpty(ownerToAdd)) {
                    $scope.createAddErrorModal(ownerToAdd);
                } else {
                    $scope.createConfirmAddModal({
                        userToAdd: ownerToAdd,
                        listName: "owners"
                    });
                }
            }, function (res) {
                if (res.statusCode === 403) {
                    $scope.createOwnerErrorModal();
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
         * Removes a user from the include or exclude group.
         * @param {string} listName - the list to remove the user from (either Include or Exclude)
         * @param {number} currentPage - the current page in the table
         * @param {number} index - the index of the user clicked by the user
         * account
         */
        $scope.removeMember = function (listName, currentPage, index) {
            let userToRemove;
            if (listName === "Include") {
                userToRemove = $scope.pagedItemsInclude[currentPage][index];
            } else if (listName === "Exclude") {
                userToRemove = $scope.pagedItemsExclude[currentPage][index];
            }


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
             * Creates a modal telling the user that they do not have access to perform this action and that they
             * will be logged out and redirected to the homepage.
             */
            $scope.createRoleErrorModal = function () {
                $scope.loading = false;
                $scope.RoleErrorModalInstance = $uibModal.open({
                    templateUrl: "modal/roleErrorModal",
                    scope: $scope
                });
            };
            /**
             * Removes a user from the include or exclude group.
             * @param {string} listName - the list to remove the user from (either Include or Exclude)
             * @param {number} currentPage - the current page in the table
             * @param {number} index - the index of the user clicked by the user
             * account
             */
            $scope.removeMember = function (listName, currentPage, index) {

                let userToRemove;
                if (listName === "Include") {
                    userToRemove = $scope.pagedItemsInclude[currentPage][index];
                } else if (listName === "Exclude") {
                    userToRemove = $scope.pagedItemsExclude[currentPage][index];
                }

                $scope.createRemoveModal({
                    user: userToRemove,
                    listName: listName
                });
            }, function (res) {
                if (res.statusCode === 403) {
                    $scope.createOwnerErrorModal();
                }
            };


            /**
             * Removes a grouping owner. There must be at least one grouping owner remaining.
             * @param {number} currentPage - the current page in the owners table
             * @param {number} index - the index of the owner clicked by the user
             */
            $scope.removeOwner = function (currentPage, index) {
                const ownerToRemove = $scope.pagedItemsOwners[currentPage][index];

                if ($scope.groupingOwners.length > 1) {
                    $scope.createRemoveModal({
                        user: ownerToRemove,
                        listName: "owners"
                    });
                } else {
                    const userType = "owner";
                    $scope.createRemoveErrorModal(userType);
                }

            } , function (res) {
                if (res.statusCode === 403) {
                    $scope.createOwnerErrorModal();
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

                const windowClass = $scope.showWarningRemovingSelf() ? "modal-danger" : "";


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
                const pills = $("#group-pills")[0].children;
                const pillContents = $("#pill-content")[0].children;
                for (let i = 0; i < pills.length; i++) {
                    const anchorTag = $(pills[i].children[0]);
                    const pillContent = $(pillContents[i]);
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
                    console.log("Success");
                }
            }

            /**
             * Toggles the grouping preference which allows users to opt out of a grouping.
             */
            $scope.updateAllowOptOut = function () {
                const groupingPath = $scope.selectedGrouping.path;
                const allowOptOut = $scope.allowOptOut;

                groupingsService.setOptOut(groupingPath, allowOptOut, handleSuccessfulPreferenceToggle, handleUnsuccessfulRequest);
            };

            /**
             * Toggles the grouping preference which allows users to discover the grouping and opt into it.
             */
            $scope.updateAllowOptIn = function () {
                const groupingPath = $scope.selectedGrouping.path;
                const allowOptIn = $scope.allowOptIn;

                groupingsService.setOptIn(groupingPath, allowOptIn, handleSuccessfulPreferenceToggle, handleUnsuccessfulRequest);
            };

            /**
             * Toggles the grouping preference which creates a LISTSERV email list based off the grouping.
             */
            $scope.updateListserv = function () {
                const groupingPath = $scope.selectedGrouping.path;
                const listservOn = $scope.listserv;

                groupingsService.setListserv(groupingPath, listservOn, handleSuccessfulPreferenceToggle, handleUnsuccessfulRequest);
            };

            /**
             * Toggles the grouping preference to synchronize memberships with the uhReleasedGroupings attribute.
             */
            $scope.updateLdap = function () {
                const groupingPath = $scope.selectedGrouping.path;
                const ldapOn = $scope.ldap;

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
            $scope.createemailListModal = function () {
                $scope.listserv = !$scope.listserv;
                $scope.emailListInstance = $uibModal.open({
                    templateUrl: "modal/emailListModal.html",

                    scope: $scope
                });

                $scope.emailListInstance.result.then(function () {
                    $scope.listserv = !$scope.listserv;
                    $scope.updateListserv();
                }).catch(function () {
                    //do nothing
                });

            };

            /**
             * Proceeds with the change of the Email list
             */
            $scope.proceedemailListModal = function () {
                $scope.emailListInstance.close();
            };

            /**
             *Closes the Email list confirmation modal
             */
            $scope.closeemailListModal = function () {
                $scope.emailListInstance.dismiss();
            };

            /**
             * Create owner error modal when a grouping owner
             * is removed while still trying to access grouping
             * owner actions.
             */
            $scope.createOwnerErrorModal = function () {
                $scope.loading = false;
                $scope.OwnerErrorModalInstance = $uibModal.open({
                    templateUrl: "modal/ownerErrorModal",
                    scope: $scope
                });
            };

            /**
             * Exports data in a table to a CSV file
             * @param {object[]} table - the table to export
             * @param grouping - grouping name that you are exporting from
             * @param list - grouping list (i.e. include or exclude)
             */
            $scope.exportGroupToCsv = function (table, grouping, list) {
                let data, filename, link;

                let csv = $scope.convertListToCsv(table);
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
                let str = "Last,First,Username,Email\r\n";
                for (let i = 0; i < table.length; i++) {
                    let line = "";
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

            /**
             * Gets cookie information
             * @param cname = name of cookie you want to look for.
             * @returns {*}
             */
            $scope.getCookie = function (cname) {
                let name = cname + "=";
                let decodedCookie = decodeURIComponent(document.cookie);
                let ca = decodedCookie.split(";");
                for (let i = 0; i < ca.length; i++) {
                    let c = ca[i];
                    while (c.charAt(0) == " ") {
                        c = c.substring(1);
                    }
                    if (c.indexOf(name) == 0)
                        return c.substring(name.length, c.length);
                }
                return "";
            };

            /**
             * Logs out a user and redirects them to the homepage.
             */
            $scope.proceedLogoutUser = function () {
                $scope.RoleErrorModalInstance.close();
                let r = new XMLHttpRequest();
                r.open("POST", "/uhgroupings/logout", true);
                r.setRequestHeader("X-XSRF-TOKEN", $scope.getCookie("XSRF-TOKEN"));
                r.send();
                $window.location.href = "/uhgroupings/";
            };

            /**
             * redirects the user to the groupings page.
             */
            $scope.proceedRedirect = function () {
                $scope.OwnerErrorModalInstance.close();
                $window.location.href = "/uhgroupings/";
            };

        }

})();
