/* global _, angular, UHGroupingsApp */

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

    function GeneralJsController($scope, $window, $uibModal, $controller, groupingsService, dataProvider, PAGE_SIZE, Message) {

        $scope.userToAdd = "";
        $scope.manageMembers = "";
        $scope.multiAddThreshold = 100;
        $scope.maxImport = 100000;
        $scope.multiAddResults = [];
        $scope.multiAddResultsGeneric = [];
        $scope.personProps = [];
        $scope.resetResults = [];
        $scope.waitingForImportResponse = false;
        $scope.resetUser = [];
        $scope.resetID = [];
        $scope.resetName = [];
        // This is a regex Pattern that contains all valid UH Identifiers which consists of uid (Username) and uhUuid (UH Numbers) chars.
        $scope.uhIdentifierPattern = new RegExp("^[_?a-z-?@?0-9]{3,64}$");

        // Remove members
        $scope.memberToRemove = "";
        $scope.membersToRemove = [];
        $scope.multiRemoveThreshold = 100;
        $scope.multiRemoveResults = [];
        $scope.membersToModify = [];
        $scope.membersNotInList = [];
        $scope.isMultiRemove = false;

        $scope.currentUser = {};

        $scope.groupingsList = [];
        $scope.pagedItemsGroupings = [];
        $scope.currentPageGroupings = 0;
        $scope.selectedGrouping = {};

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

        $scope.syncDestMap = [];
        $scope.syncDestArray = [];
        $scope.selectedSyncDest = "Hello";

        $scope.showGrouping = false;

        $scope.loading = false;
        $scope.paginatingProgress = true;
        $scope.paginatingComplete = false;
        $scope.largeGrouping = false;

        $scope.groupingCSV = [];
        $scope.groupNameCSV = [];

        $scope.resetInclude = [];
        $scope.resetExclude = [];
        $scope.includeDisable = false;
        $scope.excludeDisable = false;
        $scope.includeCheck = false;
        $scope.excludeCheck = false;
        $scope.group = "";
        $scope.resStatus = 0;
        $scope.inGrouper = false;
        $scope.showAdminTab = true;
        $scope.groupLoaded = "";
        $scope.groupNotLoaded = "";

        // used with ng-view on selected-grouping.html to toggle description editing.
        $scope.descriptionForm = false;

        $scope.maxDescriptionLength = 98;

        //The user input
        $scope.modelDescription = "";

        $scope.membersInCheckboxList = {};

        //Variable for holding description
        $scope.groupingDescription = "";

        $scope.displayTracker = 1;
        $scope.descriptionLoaded = false;

        //Flag used for getGroupingInformation function to end async call
        let loadMembersList = false;
        //Keeps track of async calls made throughout this js controller
        let asyncThreadCount = 0;

        //Default description message when none is provided
        $scope.noDescriptionMessage = "No description given for this Grouping.";

        //Retrieve table column settings from local storage or default value if local storage is empty
        $scope.columnDisplaySetting = localStorage.getItem("columnDisplaySetting") || "first";
        $scope.showDescriptionColumn = JSON.parse(localStorage.getItem("showDescriptionColumn") || true);
        $scope.showPathColumn = JSON.parse(localStorage.getItem("showPathColumn") || false);

        angular.extend(this, $controller("TableJsController", { $scope }));

        /**
         * Get the username of the current user
         */
        groupingsService.getCurrentUser((res) => {
            $scope.currentUser = res.data.username;
        });

        /**
         * Get the number of memberships that the current user is associated with.
         */
        groupingsService.getNumberOfMemberships((res) => {
            $scope.numberOfMemberships = res;
        }, (res) => {
        });

        /**
         * Get the number of groupings that the current user is associated with.
         */
        groupingsService.getNumberOfGroupings((res) => {
            $scope.numberOfGroupings = res;
        });

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

        $scope.toggleShowAdminTab = function () {
            $scope.showAdminTab = $scope.showAdminTab === false;
        };

        /**
         * Generic handler for unsuccessful requests to the API.
         */
        function handleUnsuccessfulRequest(res) {
            $scope.loading = false;
            $scope.waitingForImportResponse = false;
            $scope.resStatus = res.status;
            if (res.status === 403) {
                $scope.createOwnerErrorModal();
            } else {
                $scope.createApiErrorModal();
            }
        }

        /** Remove all nameless members from members and return a sorted object of distinct members.
         * @param {object[]} members - the members of the group
         * @returns {object[]} the members of the group, sorted by name and with blank usernames filtered out
         */
        function setGroupMembers(members) {
            _.remove(members, function (member) {
                return _.isEmpty(member.name);
            });

            // Unique members only by UUID (assume no two users should have the same uuid)
            members = _.uniqBy(members, "uhUuid");

            return _.sortBy(members, "name");
        }

        /** Remove all nameless members from membersToAdd then create an object of distinct members as a sorted
         *  concatenation of initialMembers and membersToAdd objects.
         * @param {object[]} initialMembers - initial members in group
         * @param {object[]} membersToAdd - members to add to group
         * @returns {object[]} the members of both groups in one array, sorted by name with blank usernames filtered out
         */
        function combineGroupMembers(membersToAdd, initialMembers) {

            _.remove(membersToAdd, function (member) {
                return _.isEmpty(member.name);
            });

            let members = _.concat(initialMembers, membersToAdd);

            // Unique members only by UUID (assume no two users should have the same uuid)
            members = _.uniqBy(members, "uhUuid");

            return _.sortBy(members, "name");
        }

        /**
         * @returns {String[]} list of possible sync destinations
         */
        $scope.getAllSyncDestinations = function () {
            const groupingPath = $scope.selectedGrouping.path;
            groupingsService.getSyncDestList(groupingPath, function (res) {
                $scope.syncDestMap = res;
            }, function (res) {
                if (res.statusCode === 403) {
                    $scope.createOwnerErrorModal();
                }
            });
        };


        $scope.fetchGrouping = function (currentPage, groupingPath) {
            return new Promise((resolve) => {
                groupingsService.getGrouping(groupingPath, currentPage, PAGE_SIZE, "name", true, function (res) {
                    if (!res.empty) {
                        let putGroupMembers = combineGroupMembers;
                        if (currentPage === 1) {
                            putGroupMembers = setGroupMembers;
                        }
                        $scope.groupingBasis = putGroupMembers(res.basis.members, $scope.groupingBasis);
                        $scope.filter($scope.groupingBasis, "pagedItemsBasis", "currentPageBasis", $scope.basisQuery, false);

                        //Gets members in the include group
                        $scope.groupingInclude = putGroupMembers(res.include.members, $scope.groupingInclude);
                        $scope.addInBasis($scope.groupingInclude);
                        $scope.filter($scope.groupingInclude, "pagedItemsInclude", "currentPageInclude", $scope.includeQuery, false);

                        //Gets members in the exclude group
                        $scope.groupingExclude = putGroupMembers(res.exclude.members, $scope.groupingExclude);
                        $scope.addInBasis($scope.groupingExclude);
                        $scope.filter($scope.groupingExclude, "pagedItemsExclude", "currentPageExclude", $scope.excludeQuery, false);

                        //Gets members in grouping
                        $scope.groupingMembers = putGroupMembers(res.composite.members, $scope.groupingMembers);
                        $scope.addWhereListed($scope.groupingMembers);
                        $scope.filter($scope.groupingMembers, "pagedItemsMembers", "currentPageMembers", $scope.membersQuery, false);

                        //Gets owners of the grouping
                        $scope.groupingOwners = putGroupMembers(res.owners.members, $scope.groupingOwners);
                        $scope.pagedItemsOwners = $scope.groupToPages($scope.groupingOwners);
                        $scope.filter($scope.groupingOwners, "pagedItemsOwners", "currentPageOwners", $scope.ownersQuery, false);

                        $scope.groupingDescription = res.description !== null ? res.description : "";
                        $scope.descriptionLoaded = true;
                        $scope.allowOptIn = res.optInOn;
                        $scope.allowOptOut = res.optOutOn;
                        $scope.syncDestArray = res.syncDestinations;
                    } else {
                        $scope.paginatingComplete = true;
                        $scope.paginatingProgress = false;
                    }
                    resolve();

                }, (res) => {
                    $scope.paginatingComplete = true;
                    $scope.resStatus = res.status;
                    $scope.createApiErrorModal();
                    $scope.paginatingProgress = false;
                    resolve();
                });
            });
        };

        /**
         * Fetch information about the grouping including members and description.
         */
        $scope.getGroupingInformation = async function () {
            const groupingPath = $scope.selectedGrouping.path;
            let currentPage = 1;
            $scope.loading = true;
            $scope.paginatingComplete = false;
            while (!($scope.paginatingComplete)) {
                $scope.paginatingProgress = true;
                await $scope.fetchGrouping(currentPage, groupingPath);
                currentPage++;
                $scope.loading = false;
            }
        };

        /**
         * Check the length of the text string entered in the description form box, for error handling of max length
         */
        $scope.descriptionLengthWarning = function () {
            return (String($scope.modelDescription).length > $scope.maxDescriptionLength - 1);
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
            $scope.modelDescription = $scope.groupingDescription;

            if ($scope.descriptionForm) {
                $scope.descriptionForm = !($scope.descriptionForm);
            }

        };

        /**
         * Set a new description for a Grouping.
         */
        $scope.saveDescription = function () {
            if ($scope.groupingDescription.localeCompare($scope.modelDescription) === 0) {
                return $scope.cancelDescriptionEdit();
            }
            $scope.groupingDescription = $scope.modelDescription;
            groupingsService.updateDescription($scope.groupingDescription, $scope.selectedGrouping.path, () => {
                    $scope.descriptionForm = !($scope.descriptionForm);
                }, // close description form when done.
                (res) => {
                    $scope.resStatus = res.status;
                    $scope.createApiErrorModal();
                });
        };

        /**
         * If the grouping hasn't been fetched, return csv group loaded message, otherwise return csv group not loaded message.
         */
        $scope.getCSVToolTipMessage = () => {
            $scope.groupLoaded = Message.Csv.GROUP_LOADED;
            $scope.groupNotLoaded = Message.Csv.GROUP_NOT_LOADED;
            return ($scope.paginatingComplete) ? Message.Csv.GROUP_LOADED : Message.Csv.GROUP_NOT_LOADED;
        };

        /**
         * Used for placeholder text for a grouping's description in the form box.
         * @returns {string} either the description of the grouping, or, placeholder text if the description is empty.
         */
        $scope.descriptionDisplay = function () {
            if (!$scope.descriptionLoaded) {
                return "";
            }
            if ($scope.showGrouping === true && $scope.displayTracker === 1) {
                $scope.modelDescription = $scope.groupingDescription;
                $scope.displayTracker = 0;
            }
            return ($scope.groupingDescription.length > 0) ? $scope.groupingDescription : $scope.noDescriptionMessage;
        };

        /**
         * Create a modal for errors in loading data from the API.
         */
        $scope.createApiErrorModal = function () {
            $scope.loading = false;
            $scope.apiErrorModalInstance = $uibModal.open({
                templateUrl: "modal/apiError", scope: $scope, backdrop: "static", keyboard: false
            });
        };

        /**
         * Close the API error modal.
         */
        $scope.closeApiError = function () {
            $scope.apiErrorModalInstance.close();
        };

        /**
         * Check if the members in the group are in the basis group.
         * @param {object[]} group - the group to check
         */
        $scope.addInBasis = function (group) {
            _.forEach(group, function (member) {
                const memberUhUuid = member.uhUuid;
                member.inBasis = _.some($scope.groupingBasis, { uhUuid: memberUhUuid }) ? "Yes" : "No";
            });
        };


        /**
         * Check what lists a member in a grouping are in.
         * @param {object[]} compositeGroup - the composite / all members group
         */
        $scope.addWhereListed = function (compositeGroup) {
            _.forEach(compositeGroup, function (member) {

                const memberUhUuid = member.uhUuid;
                if (_.some($scope.groupingBasis, { uhUuid: memberUhUuid })) {

                    member.whereListed = "Basis";
                }

                if (_.some($scope.groupingInclude, { uhUuid: memberUhUuid })) {
                    member.whereListed = _.isUndefined(member.whereListed) ? "Include" : "Basis & Include";
                }
            });
        };

        /**
         * Launch a modal containing a browse local file system for import button.
         * @param listName - Current list
         */
        $scope.launchImportModal = function (listName) {
            $scope.listName = listName;

            $scope.confirmImportInstance = $uibModal.open({
                templateUrl: "modal/importModal", size: "lg", scope: $scope
            });
        };

        /**
         * Take $scope.manageMembers count the number of words it contains and split it into a comma separated string, then
         * decide whether to a multi add or a single add is necessary.
         * @param listName
         */
        $scope.addMembers = function (listName) {
            if (_.isEmpty($scope.manageMembers)) {
                $scope.emptyInput = true;
            } else {
                let numMembers = ($scope.manageMembers.split(" ").length - 1);
                if (numMembers > 0) {
                    let users = $scope.parseAddRemoveInputStr($scope.manageMembers);
                    $scope.manageMembers = [];
                    if (numMembers > $scope.maxImport) {
                        $scope.launchDynamicModal(Message.Title.IMPORT_OUT_OF_BOUNDS, `Importing more than ${$scope.maxImport} users is not allowed.`, 8000);
                    } else {
                        if (numMembers > $scope.multiAddThreshold) {
                            $scope.launchDynamicModal(Message.Title.LARGE_IMPORT, `You are attempting to import ${numMembers} new users to the ${listName} list.
                             Imports larger than ${$scope.multiAddThreshold} can take a few minutes.  An email with
                             the import results will be sent.`, 8000);
                        }
                        $scope.addMultipleMembers(users, listName);
                    }
                } else {
                    $scope.userToAdd = $scope.manageMembers;
                    $scope.validateAndAddUser($scope.userToAdd, listName);
                }
            }
        };

        /**
         * Checks that a users name matches the pattern of either a valid uid or a uhUuid.
         * @param {Object[]|string} names - An array of users or string of a single user to validate.
         * @returns {Object[]|string} - An array of valid users or valid string of a user .
         */
        $scope.sanitizer = function (names) {
            const validNames = [];
            for (const name of [].concat(names)) {
                if (name) {
                    const trimmedLowercaseName = name.toLowerCase().trim();
                    if (trimmedLowercaseName != null && $scope.uhIdentifierPattern.test(trimmedLowercaseName)) {
                        const validInput = trimmedLowercaseName.match($scope.uhIdentifierPattern);
                        validNames.push(validInput.toString());
                    }
                }
            }
            return _.isString(names) ? validNames.toString() : validNames;
        };

        /**
         * Read a text file(.txt) from client side. The file should consist of
         * a list of UH usernames or ids separated by newline characters. This
         * function is called implicitly from include.html and exclude.html.
         * The file is retrieved from the html input with id 'upload'.
         */
        $scope.readTextFile = function (inputFile) {
            let reader = new FileReader();
            reader.onload = function (e) {
                const str = e.target.result;
                const namesInFile = str.split(/[\r\n,]+/);
                $scope.manageMembers = $scope.sanitizer(namesInFile).join(" ");
                $scope.addMembers($scope.listName);
            };
            reader.readAsText(inputFile);
        };

        $scope.removeTextFile = function () {
            angular.element(document.querySelector("#upload")).val(null);
        };

        /**
         * Get the Person properties from members and puts them in a list
         * @param {string[]} attributes - list of attributes for a UH member
         */
        $scope.getPersonProps = function (attributes) {
            $scope.personProps = [];

            $scope.personProps.push(attributes.splice(attributes.indexOf("username"), 1));
            $scope.personProps.push(attributes.splice(attributes.indexOf("uhUuid"), 1));
            $scope.personProps.push(attributes.splice(attributes.indexOf("name"), 1));
        };

        $scope.successfulAddHandler = function (res, list, listName) {
            let membersNotInList = [];
            $scope.waitingForImportResponse = false; /* Small spinner off. */

            for (let data of res) {
                let result = data.result;
                let userWasAdded = data.userWasAdded;

                if (result === "FAILURE" || !userWasAdded) {
                    membersNotInList.push(data.name);
                    $scope.membersNotInList = membersNotInList.join(", ");
                } else {
                    let person = {
                        "uid": data.uid, "uhUuid": data.uhUuid, "name": data.name
                    };
                    $scope.multiAddResults.push(person);
                    $scope.multiAddResultsGeneric.push(person);
                }
            }
            if ($scope.multiAddResults.length > 0) {
                $scope.personProps = Object.keys($scope.multiAddResults[0]);
                $scope.launchMultiAddResultModal(listName);
            } else {
                $scope.launchDynamicModal(Message.Title.NO_MEMBERS_ADDED, Message.Body.NO_MEMBERS_ADDED);
            }
        };

        /**
         * Send the list of users to be added to the server as an HTTP POST request.
         * @param list - comma separated string of user names to be added
         * @param listName - current list being added to
         * @returns {Promise<void>}
         */
        $scope.addMultipleMembers = async function (list, listName) {
            let groupingPath = $scope.selectedGrouping.path;

            let timeoutModal = function () {
                return $scope.launchDynamicModal(Message.Title.SLOW_IMPORT, Message.Body.SLOW_IMPORT, 8000);
            };
            let handleSuccessfulAdd = (res) => {
                $scope.successfulAddHandler(res, list, listName);
            };
            $scope.waitingForImportResponse = true; /* Small spinner on. */
            if (listName === "Include") {
                await groupingsService.addMembersToIncludeAsync(list, groupingPath, handleSuccessfulAdd, handleUnsuccessfulRequest, timeoutModal);
            } else if (listName === "Exclude") {
                await groupingsService.addMembersToExcludeAsync(list, groupingPath, handleSuccessfulAdd, handleUnsuccessfulRequest, timeoutModal);
            } else if (listName === "owners") {
                await groupingsService.addOwnerships(groupingPath, list, handleSuccessfulAdd, handleUnsuccessfulRequest);
            }
        };

        /**
         * Helper - clearMemberInput
         * Small function that resets the checkboxes on the page
         */
        function resetCheckboxes() {
            for (let member of Object.values($scope.membersInCheckboxList)) {
                member = false;
            }
        }

        /**
         * Clears the user input for adding/deleting a member to/from a list.
         * @param {string?} listName - Name of the list that the user is being added to or removed from.
         */
        function clearMemberInput(listName) {
            switch (listName) {
                case "Include":
                case "Exclude":
                    $scope.userToAdd = "";
                    $scope.manageMembers = "";
                    $scope.userNameList = [];
                    $scope.multiAddResults = [];
                    $scope.waitingForImportResponse = false;
                    $scope.personProps = [];
                    $scope.multiRemoveResults = [];
                    $scope.membersToModify = [];
                    $scope.membersNotInList = [];
                    $scope.memberName = "";
                    $scope.memberUhUuid = "";
                    $scope.membersInCheckboxList = {};
                    resetCheckboxes();
                    break;
                case "owners":
                    $scope.manageMembers = "";
                    $scope.multiRemoveResults = [];
                    $scope.multiAddResults = [];
                    $scope.membersNotInList = [];
                    break;
                case "admins":
                    $scope.adminToAdd = "";
                    break;
                default:
                    $scope.userToAdd = "";
                    $scope.manageMembers = "";
                    $scope.adminToAdd = "";
            }
        }

        /**
         * Launch a modal containing a table of the results(user info) received from the server's response message.
         * @param listName - current list being added to
         */
        $scope.launchMultiAddResultModal = function (listName) {
            $scope.multiAddResultModalInstance = $uibModal.open({
                templateUrl: "modal/multiAddResultModal", scope: $scope
            });
            $scope.loading = false;
            $scope.multiAddResultModalInstance.result.finally(function () {
                clearMemberInput(listName);
                $scope.loading = true;
                $scope.waitingForImportResponse = false;
                if ($scope.listName === "admins") {
                    // Refreshes the groupings list and the admins list
                    $scope.init();
                } else {
                    $scope.getGroupingInformation();
                }
            });
        };

        /**
         * Close the import modal instance then launch an error modal.
         */
        $scope.launchImportErrorModal = function () {
            $scope.cancelImportModalInstance();
            $scope.confirmImportErrorInstance = $uibModal.open({
                templateUrl: "modal/importErrorModal", scope: $scope
            });
        };

        /**
         * Set a style attribute of a html component associated with id.
         * @param id - id of html component
         * @param attribute
         * @param setAs - set attribute too.
         */
        $scope.changeStyleAttribute = function (id, attribute, setAs) {
            document.getElementById(id).style[attribute] = setAs;
        };

        /**
         * Clear all data from the add member(s) instance. Close the modal.
         */
        $scope.closeMultiAddResultInstance = function () {
            clearMemberInput($scope.listName);
            $scope.multiAddResultModalInstance.dismiss();
        };
        /**
         * Cancel the import Modal instance
         */
        $scope.cancelImportModalInstance = function () {
            clearMemberInput($scope.listName);
            $scope.confirmImportInstance.dismiss();
        };

        /**
         * Close import modal instance when user confirms that they would like to add the list they imported
         */
        $scope.proceedAddMembers = function () {
            $scope.confirmImportInstance.close();
        };

        /**
         * Launch a modal with a title, and body message. The modal will dismiss in the case of pressing the ok button
         * and/or if the timeTillClose is set and time runs out. The modal will not timeout unless the timeTillClose is
         * set.
         */
        $scope.launchDynamicModal = function (title, body, timeTillClose) {
            $scope.currentModalTitle = title;
            $scope.currentModalBody = body;

            $scope.createDynamicModal = $uibModal.open({
                templateUrl: "modal/dynamicModal", scope: $scope
            });
            if (!_.isUndefined(timeTillClose)) {
                let closeOnTimeout = function () {
                    $scope.createDynamicModal.dismiss();
                };
                setTimeout(closeOnTimeout, timeTillClose);
            }
            $scope.dismissDynamicModal = function () {
                $scope.createDynamicModal.dismiss();
            };
        };

        /**
         * Remove Items from the pendingList Array
         * @param pendingList
         * @param itemsToRemove
         * @return {[]}
         */
        $scope.removeItemsFromArray = function (pendingList, itemsToRemove) {
            for (let item of itemsToRemove) {
                pendingList = _.without(pendingList, _.forEach(item));
            }
            return pendingList;
        };

        /**
         * Helper - validateAndAddUser
         * Add a user to a group.
         * @param {Object} list - the list the user is being added to (either Include or Exclude)
         */
        $scope.addMember = function (list) {
            $scope.waitingForImportResponse = true;
            let user = $scope.userToAdd;
            let inBasis = _.some($scope.groupingBasis, { username: user }) || _.some($scope.groupingBasis, { uhUuid: user });
            if ($scope.existInList(user, list)) {
                $scope.listName = list;
                $scope.swap = false;
            } else if ($scope.isInAnotherList(user, list)) {
                $scope.createCheckModal(user, list, true, inBasis);
            } else if ((inBasis && list === "Include") || (!inBasis && list === "Exclude")) {
                $scope.createBasisWarningModal(user, list, inBasis);
            } else {
                $scope.createConfirmAddModal({
                    userToAdd: user, listName: list
                });
            }
            $scope.waitingForImportResponse = false;
        };

        /**
         * Initiate the adding of a member to a list.
         * @param {string} userToAdd - user being added
         * @param {string} list - the list the user is being added to
         */
        $scope.updateAddMember = function (userToAdd, list) {
            // only initialize groupingPath if listName is not "admins"
            let groupingPath;
            const sanitizedUser = $scope.sanitizer([userToAdd]);

            if ($scope.listName !== "admins") {
                groupingPath = $scope.selectedGrouping.path;
            }

            const handleSuccessfulAdd = function (res) {
                $scope.createSuccessfulAddModal({
                    user: sanitizedUser, listName: list, response: res
                });
            };

            if (list === "Include") {
                groupingsService.addMembersToInclude(sanitizedUser, groupingPath, handleSuccessfulAdd, handleUnsuccessfulRequest);
            } else if (list === "Exclude") {
                groupingsService.addMembersToExclude(sanitizedUser, groupingPath, handleSuccessfulAdd, handleUnsuccessfulRequest);
            } else if (list === "owners") {
                groupingsService.addOwnerships(groupingPath, sanitizedUser, handleSuccessfulAdd, handleUnsuccessfulRequest);
            } else if (list === "admins") {
                groupingsService.addAdmin(sanitizedUser, handleSuccessfulAdd, handleUnsuccessfulRequest);
            }
        };

        /**
         * Helper - addMember
         * @param {string} user - the user you are checking to see if they are in another list.
         * @param {Object} list - the list the user is currently being added to
         * @returns {boolean} - true if the person is already in another list, else false.
         */
        $scope.isInAnotherList = function (user, list) {
            if (list === "Include") {
                return _.some($scope.groupingExclude, { username: user }) || _.some($scope.groupingExclude, { uhUuid: user });
            } else if (list === "Exclude") {
                return _.some($scope.groupingInclude, { username: user }) || _.some($scope.groupingInclude, { uhUuid: user });
            }
            return false;
        };

        /**
         * Helper - addMember
         * @param {string} user - the user you are checking to see if they are already in the list being added to
         * @param {Object} list - the list the user is currently being added to
         * @returns {boolean} true if the user is already in the list being added to, otherwise returns false
         */
        $scope.existInList = function (user, list) {
            if (list === "Include") {
                return _.some($scope.groupingInclude, { username: user }) || _.some($scope.groupingInclude, { uhUuid: user });
            } else if (list === "Exclude") {
                return _.some($scope.groupingExclude, { username: user }) || _.some($scope.groupingExclude, { uhUuid: user });
            } else if (list === "owners") {
                return _.some($scope.groupingOwners, { username: user }) || _.some($scope.groupingOwners, { uhUuid: user });
            }
            return false;
        };

        /**
         * Create a modal that asks whether or not they want to add a person that is already in another list.
         * @param user - Username of the user they are trying to add.
         * @param listName - name of the list they are adding to (either Include or Exclude)
         * @param swap - User being swapped
         * @param inBasis - boolean if user is in basis or not
         */
        $scope.createCheckModal = function (user, listName, swap, inBasis) {
            $scope.listName = listName;
            $scope.swap = swap;
            $scope.inBasis = inBasis;

            $scope.checkModalInstance = $uibModal.open({
                templateUrl: "modal/checkModal", scope: $scope, backdrop: "static", keyboard: false
            });
            $scope.checkModalInstance.result.then(function () {
                $scope.waitingForImportResponse = true;
                $scope.updateAddMember(user, listName);
            });
        };

        /**
         * Create a modal that asks for confirmation when importing multiple users.
         * @param {object} options - the options object
         * @param {string} options.manageMembers - the users to import
         * @param {string} options.listName - name of the list being added to
         */
        $scope.createConfirmAddMembersModal = function (options) {
            $scope.confirmAddModalInstance = $uibModal.open({
                templateUrl: "modal/confirmAddModal", scope: $scope, backdrop: "static", keyboard: false
            });
            $scope.confirmAddModalInstance.result.then(function () {
                $scope.updateAddMember(options.manageMembers, options.listName);
            });
        };

        /**
         * Initializes the name of the member to display on modals
         * @param {object} attributes - the user's attributes
         */
        $scope.initMemberDisplayName = function (attributes) {
            $scope.fullName = attributes.name;
            $scope.givenName = attributes.firstName;
            $scope.uhUuid = attributes.uhUuid;
            $scope.uid = attributes.username;

            if ($scope.fullName != null && $scope.fullName.length > 0) {
                $scope.user = $scope.fullName;
            } else if ($scope.fullName != null && $scope.uid.length > 0) {
                $scope.user = $scope.uid;
            } else {
                $scope.user = $scope.uhUuid;
            }
        };

        /**
         * Checks if the user is in the Grouper database
         * @param {string} user - the user you are checking to see if they are in Grouper
         * @param {object} list - the the list the user is being added to
         */
        $scope.validateAndAddUser = function (user, list) {
            const validUser = $scope.sanitizer([user]);
            groupingsService.getMemberAttributes(user, function (person) {
                $scope.user = person;
                if (person.uhUuid !== null) {
                    $scope.initMemberDisplayName(person);
                    $scope.addMember(list);
                } else {
                    $scope.user = user;
                    $scope.resStatus = 404;
                }
            }, function (res) {
                $scope.resStatus = res.status;
                $scope.user = user;
                if (res.status === -1) {
                    $scope.createApiErrorModal();
                }
            });
        };

        /**
         * Creates a modal that asks for confirmation when adding a user.
         * @param {{userToAdd: (*|string), listName: Object}} options - the options object
         * @param {string} options.userToAdd - the user to add
         * @param {string} options.listName - name of the list being added to
         */
        $scope.createConfirmAddModal = function (options) {
            const userToAdd = options.userToAdd;
            $scope.listName = options.listName;

            groupingsService.getMemberAttributes(userToAdd, function (person) {
                if (person === "") {
                    return;
                } else {
                    $scope.initMemberDisplayName(person);
                }
                // Ask for confirmation from the user to add the member
                $scope.confirmAddModalInstance = $uibModal.open({
                    templateUrl: "modal/confirmAddModal", scope: $scope, backdrop: "static", keyboard: false
                });

                $scope.confirmAddModalInstance.result.then(function () {
                    $scope.waitingForImportResponse = true;
                    $scope.updateAddMember(userToAdd, options.listName);
                });
            }, function (res) {
                $scope.user = userToAdd;
                $scope.resStatus = res.status;
            });
        };

        /**
         * Close CheckModal and proceeds with the checkModalInstance result.then function
         */
        $scope.proceedCheckModal = function () {
            $scope.checkModalInstance.close();
        };

        /**
         * Dismiss the CheckModal and closes it with proceeding with checkModalInstance's result.then function.
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
         * Give a user or multiple users ownership of a grouping.
         */
        $scope.addOwners = function () {
            const manageOwners = $scope.manageMembers;
            const list = "owners";
            $scope.userToAdd = manageOwners;
            if (_.isEmpty(manageOwners)) {
                $scope.emptyInput = true;
            } else {
                let numOwners = ($scope.manageMembers.split(" ").length - 1);
                if (numOwners > 0) {
                    let users = $scope.parseAddRemoveInputStr(manageOwners);
                    $scope.manageMembers = [];
                    if (numOwners > $scope.multiAddThreshold) {
                        $scope.launchDynamicModal(Message.Title.LARGE_IMPORT, `You are attempting to import ${numOwners} new users to the ${list} list.
                             Imports larger than ${$scope.multiAddThreshold} can take a few minutes.  An email with
                             the import results will be sent.`, 8000);
                    }
                    $scope.addMultipleMembers(users, list);
                } else {
                    $scope.userToAdd = manageOwners;
                    $scope.validateAndAddUser(manageOwners, list);
                }
            }
        };

        /**
         * Create a modal telling the user whether or not the user was successfully added into the grouping/admin list.
         * @param {object} options - the options object
         * @param {string} options.userToAdd - the user being added
         * @param {string?} options.response - the response from adding a member
         * @param {string?} options.listName - the list where the user was being added to
         */
        $scope.createSuccessfulAddModal = function (options) {
            $scope.listName = options.listName;

            $scope.addModalInstance = $uibModal.open({
                templateUrl: "modal/addModal", scope: $scope, backdrop: "static", keyboard: false
            });

            $scope.addModalInstance.result.finally(function () {
                clearMemberInput(options.listName);
                $scope.loading = true;
                $scope.waitingForImportResponse = false;
                if ($scope.listName === "admins") {
                    // Refreshes the groupings list and the admins list
                    $scope.init();
                } else {
                    $scope.getGroupingInformation();
                    $scope.syncDestArray = [];
                }
            });
        };

        /**
         * Close the add user modal.
         */
        $scope.closeSuccessfulAddModal = function () {
            $scope.addModalInstance.close();
        };

        /**
         * Create a modal telling the user the multi-removal was successful.
         */
        $scope.createSuccessfulMultiRemoveModal = function () {
            $scope.multiRemoveResultModalInstance = $uibModal.open({
                templateUrl: "modal/multiRemoveResultModal", scope: $scope, backdrop: "static"
            });

            $scope.multiRemoveResultModalInstance.result.finally(function () {
                $scope.loading = true;
                $scope.getGroupingInformation();
            });
        };

        /**
         * Close the add user modal.
         */
        $scope.closeSuccessfulMultiRemoveModal = function () {
            $scope.multiRemoveResultModalInstance.close();
        };

        /**
         * Close the reset notif modal.
         */
        $scope.closeResetNotifModal = function () {
            $scope.resetNotifModalInstance.close();
        };

        /**
         * Create a modal telling the user that they do not have access to perform this action and that they
         * will be logged out and redirected to the homepage.
         */
        $scope.createRoleErrorModal = function () {
            $scope.loading = false;
            $scope.RoleErrorModalInstance = $uibModal.open({
                templateUrl: "modal/roleErrorModal", scope: $scope, backdrop: "static", keyboard: false
            });
        };

        /**
         * Remove a single member from include/exclude by using the "trashcan" UI implementation.
         * @param {string} listName - the list to remove the user from (either Include or Exclude)
         * @param {number} currentPage - the current page in the table
         * @param {number} index - the index of the user clicked by the user
         * account
         */
        $scope.removeMemberWithTrashcan = function (listName, currentPage, index) {
            let memberToRemove;
            if (listName === "Include") {
                memberToRemove = $scope.pagedItemsInclude[currentPage][index];
            } else if (listName === "Exclude") {
                memberToRemove = $scope.pagedItemsExclude[currentPage][index];
            }

            $scope.createRemoveModal({
                members: memberToRemove, listName, scope: $scope
            });
        };

        /**
         * Copies the members in the current page to an object by UH number
         * that holds true/false value for triggering checkboxes.
         * @param currentPage - Current page that contains members.
         */
        $scope.transferMembersFromPageToCheckboxObject = function (currentPage) {
            currentPage.forEach((member) => $scope.membersInCheckboxList[member.uhUuid] = false);
        };

        /**
         * Toggles the "check-all" checkbox that selects or
         * de-selects all the members on the page.
         *
         * exclude.html and include.html uses $scope.allSelected for ng-model which dictates if all
         * members in the list are selected or not.
         *
         * @param group - Group (include or exclude) that the members are currently in.
         */
        $scope.toggleCheckAllSelection = function (group) {
            $scope.allSelected = !$scope.allSelected;
            let pageItems;
            let pageNumber;
            if (group === "Exclude") {
                pageItems = $scope.pagedItemsExclude;
                pageNumber = $scope.currentPageExclude;
            } else if (group === "Include") {
                pageItems = $scope.pagedItemsInclude;
                pageNumber = $scope.currentPageInclude;
            }

            for (let i = 0; i < pageItems[pageNumber].length; i++) {
                $scope.membersInCheckboxList[((pageItems[pageNumber][i]).uhUuid)] = $scope.allSelected;
            }
        };

        /**
         * Helper - removeMembers
         * Take in a list of booleans and return a comma separated string containing the identifiers of all true booleans.
         * @param obj: {obj1: true, obj2: false, obj3: true}
         * @returns {string}: "obj1,obj3"
         */
        $scope.extractSelectedUsersFromCheckboxes = function (obj) {
            for (const value of Object.values(obj)) {
                if (value !== true && value !== false) {
                    return "";
                }
            }
            return _.keys(_.pickBy(obj)).join(",");
        };

        /**
         *  Helper - addMembers, removeMembers
         *  Divides a string into an array where commas and spaces are present.
         */
        $scope.parseAddRemoveInputStr = function (str) {
            if (!_.isString(str)) {
                return "";
            }
            return str.split(/[ ,]+/);
        };

        /**
         * Helper - createRemoveModal, fetchMemberProperties
         * Returns the member object that contains either the provided username or UH number.
         * @param memberIdentifier - The username or UH ID number of the member object to return.
         * @param listName - The name of the list to search.
         */
        $scope.returnMemberObject = function (memberIdentifier, listName) {
            let currentPage;
            switch (listName) {
                case "Exclude":
                    currentPage = $scope.groupingExclude;
                    break;
                case "Include":
                    currentPage = $scope.groupingInclude;
                    break;
                case "owners":
                    currentPage = $scope.groupingOwners;
                    break;
                case "admins":
                    currentPage = $scope.adminsList;
                    break;
                default:
                    break;
            }

            let memberToReturn;
            if (/[0-9]{8}/.test(memberIdentifier)) {
                memberToReturn = _.find(currentPage, (member) => member.uhUuid === memberIdentifier);
            } else {
                memberToReturn = _.find(currentPage, (member) => member.username === memberIdentifier);
            }
            return memberToReturn;
        };

        /**
         * Helper - removeMembers
         * Searches an array of member objects, and checks if the members are in the groupings list.
         * If a member does not exist, it creates a comma-seperated string of their identifiers and
         * sets it to $scope.membersNotInList.
         * @param members {Object[]} - An array of members.
         * @returns {boolean} - true if some members exist in the grouping list, false if none.
         */
        $scope.fetchMemberProperties = function (members, listName) {
            let membersInList = [];
            let membersNotInList = [];

            for (let member of members) {
                let currentMember = $scope.returnMemberObject(member, listName);

                if (_.isUndefined(currentMember)) {
                    membersNotInList.push(member);
                } else {
                    let person = {
                        "uid": currentMember.username, "uhUuid": currentMember.uhUuid, "name": currentMember.name
                    };

                    membersInList.push(person);
                }
            }
            $scope.membersNotInList = membersNotInList.join(", ");
            $scope.multiRemoveResults = membersInList;

            if ($scope.multiRemoveResults.length > 0) {
                $scope.personProps = Object.keys($scope.multiRemoveResults[0]);
            }

            return !_.isEmpty($scope.multiRemoveResults);
        };

        /**
         * Extracts the string from $scope.manageMembers input field or members selected from checkboxes
         * (input field takes precedence) then sends it to the corresponding listName endpoint to perform the removal.
         * @param listName {string} - Name of list to remove the members from.
         */
        $scope.removeMembers = function (listName) {
            $scope.listName = listName;

            if (!_.isEmpty($scope.manageMembers)) {
                $scope.membersToModify = $scope.manageMembers;
            } else {
                $scope.membersToModify = $scope.extractSelectedUsersFromCheckboxes($scope.membersInCheckboxList);
            }

            if (_.isEmpty($scope.membersToModify)) {
                $scope.emptyInput = true;
            } else {
                let membersToRemove = $scope.sanitizer($scope.parseAddRemoveInputStr($scope.membersToModify));

                if ($scope.fetchMemberProperties(membersToRemove, listName)) {
                    if (listName === "owners" && $scope.multiRemoveResults.length === $scope.groupingOwners.length) {
                        $scope.createRemoveErrorModal("owner");
                        clearMemberInput(listName);
                    } else {
                        $scope.createRemoveModal({
                            members: membersToRemove, listName, scope: $scope
                        });
                    }
                } else {
                    $scope.launchDynamicModal(Message.Title.REMOVE_INPUT_ERROR, Message.Body.REMOVE_INPUT_ERROR);
                }
            }
        };

        /**
         * Remove a grouping owner by using the "trashcan" UI implementation.
         * There must be at least one grouping owner remaining.
         * @param {number} currentPage - the current page in the owners table
         * @param {number} index - the index of the owner clicked by the user
         */
        $scope.removeOwnerWithTrashcan = function (currentPage, index) {
            $scope.listName = "owners";
            const ownerToRemove = $scope.pagedItemsOwners[currentPage][index];

            if ($scope.groupingOwners.length > 1) {
                $scope.createRemoveModal({
                    members: ownerToRemove, listName: "owners", scope: $scope
                });
            } else {
                $scope.createRemoveErrorModal("owner");
            }
        };

        /**
         * Handler for successfully removing a member from the Include or Exclude group.
         */
        function handleSuccessfulRemove() {
            $scope.loading = false;
            if ($scope.isMultiRemove) {
                $scope.createSuccessfulMultiRemoveModal();
            } else {
                $scope.getGroupingInformation();
            }
            $scope.syncDestArray = [];
            $scope.membersToModify = [];
        }

        function handleGroupingReset() {
            $scope.getGroupingInformation();
            $scope.loading = false;
            $scope.createResetNotifModal($scope.selectedGrouping.name);
        }

        $scope.createEmptyGroupModal = function () {
            $scope.emptyGroupModalInstance = $uibModal.open({
                templateUrl: "modal/emptyGroupModal", scope: $scope, backdrop: "static", keyboard: false
            });
        };

        $scope.createResetNotifModal = function (groupReset) {
            $scope.group = groupReset;

            $scope.resetNotifModalInstance = $uibModal.open({
                templateUrl: "modal/resetNotifModal", scope: $scope, backdrop: "static", keyboard: false
            });
        };

        /**
         * Handler for successfully removing an owner from a grouping.
         */
        function handleOwnerRemove() {
            // Reload the grouping if you are not removing yourself, or if deleting anyone from the admins page
            if ($scope.currentUser !== $scope.memberToRemove || !_.isUndefined($scope.adminsList)) {
                $scope.getGroupingInformation();
                $scope.syncDestArray = [];
            } else if ($scope.currentUser === $scope.memberToRemove) {
                // Removing self from last grouping owned -> redirect to home page and then logout
                if ($scope.groupingsList.length === 1) {
                    $scope.returnHome();
                } else {
                    $window.location.href = "groupings";
                }
            }
        }

        /**
         * Handler for successfully removing an admin from a grouping.
         */
        function handleAdminRemove() {
            // Removing self as admin -> redirect to home page and then logout
            if ($scope.currentUser === $scope.memberToRemove) {
                $scope.returnHome();
            } else {
                $scope.init();
            }
        }

        /**
         * Create a modal that prompts the user whether they want to delete the user or not. If 'Yes' is pressed, then
         * a request is made to delete the user.
         * @param {object} options - the options object
         * @param {object|object[]} options.members - the member object or array of member(s) to remove
         * @param {string} options.listName - the grouping list the member(s) is/are being removed from
         */
        $scope.createRemoveModal = function (options) {
            if (_.isEmpty(options.members)) {
                $scope.removeInputError = true;
            } else {
                let templateUrl;
                let membersToRemove = options.members;
                $scope.listName = options.listName;

                if (_.isPlainObject(membersToRemove)) {
                    membersToRemove = [membersToRemove.username];
                }

                if (membersToRemove.length === 1) {
                    const memberObject = $scope.returnMemberObject(membersToRemove[0], $scope.listName);
                    $scope.initMemberDisplayName(memberObject);
                    $scope.memberToRemove = memberObject.username;
                    $scope.isMultiRemove = false;
                    templateUrl = "modal/removeModal";
                } else {
                    $scope.membersToRemove = membersToRemove;
                    $scope.isMultiRemove = true;
                    templateUrl = "modal/multiRemoveModal";
                }

                const windowClass = $scope.showWarningRemovingSelf() ? "modal-danger" : "";
                $scope.removeModalInstance = $uibModal.open({
                    templateUrl, windowClass, backdrop: "static", scope: $scope
                });

                $scope.removeModalInstance.result.then(function () {
                    $scope.loading = true;
                    $scope.manageMembers = "";
                    $scope.membersInCheckboxList = {};

                    const groupingPath = $scope.selectedGrouping.path;
                    if ($scope.listName === "Include") {
                        groupingsService.removeMembersFromInclude(groupingPath, membersToRemove, handleSuccessfulRemove, handleUnsuccessfulRequest);
                    } else if ($scope.listName === "Exclude") {
                        groupingsService.removeMembersFromExclude(groupingPath, membersToRemove, handleSuccessfulRemove, handleUnsuccessfulRequest);
                    } else if ($scope.listName === "owners") {
                        groupingsService.removeOwnerships(groupingPath, membersToRemove, handleOwnerRemove, handleUnsuccessfulRequest);
                    } else if ($scope.listName === "admins") {
                        groupingsService.removeAdmin(membersToRemove, handleAdminRemove, handleUnsuccessfulRequest);
                    }
                }, function () {
                    // handle $scope.removeModalInstance.dismiss() callback
                });
            }
        };

        /**
         * Log out user and redirect them to Homepage
         */
        $scope.returnHome = function () {
            $window.location.href = "/uhgroupings/";
            let r = new XMLHttpRequest();
            r.open("POST", "/uhgroupings/logout", true);
            r.setRequestHeader("X-XSRF-TOKEN", $scope.getCookie("XSRF-TOKEN"));
            r.send();
        };

        /**
         * Close the modal, then proceeds with removing a user from a grouping.
         */
        $scope.removeModalProceed = function () {
            $scope.removeModalInstance.close();
        };

        /**
         * Closes the modal for removing a user. This does not remove the user from the grouping/admin list.
         */
        $scope.removeModalCancel = function () {
            $scope.removeModalInstance.dismiss("cancel");
        };

        /**
         * Closes the modal, then proceeds with reseting the grouping.
         */
        $scope.proceedResetGroup = function () {
            $scope.resetModalInstance.close();
        };

        /**
         * Closes the modal for reseting group. This does not reset the grouping.
         */
        $scope.cancelResetGroup = function () {
            $scope.resetModalInstance.dismiss();
        };

        $scope.closeEmptyGroupModal = function () {
            $scope.emptyGroupModalInstance.dismiss();
        };

        /**
         * Creates a modal stating there was an error removing the user from a group.
         * @param {string} userType - the type of user being removed (either admin or owner)
         */
        $scope.createRemoveErrorModal = function (userType) {
            $scope.userType = userType;

            $scope.removeErrorModalInstance = $uibModal.open({
                templateUrl: "modal/removeErrorModal", scope: $scope, backdrop: "static", keyboard: false
            });
        };

        /**
         * Close the remove error modal.
         */
        $scope.closeRemoveErrorModal = function () {
            $scope.removeErrorModalInstance.close();
        };

        /**
         * Helper - resetGroupingInformation
         * Reset the grouping members and page numbers.
         */
        function resetGroupingMembers() {
            $scope.groupingMembers = [];
            $scope.groupingBasis = [];
            $scope.groupingInclude = [];
            $scope.groupingExclude = [];
            $scope.groupingOwners = [];
            $scope.personList = [];

            $scope.pagedItemsMembers = [];
            $scope.pagedItemsBasis = [];
            $scope.pagedItemsInclude = [];
            $scope.pagedItemsExclude = [];
            $scope.pagedItemsOwners = [];
            $scope.pagedItemsPerson = [];

            $scope.currentPageMembers = 0;
            $scope.currentPageBasis = 0;
            $scope.currentPageInclude = 0;
            $scope.currentPageExclude = 0;
            $scope.currentPageOwners = 0;
            $scope.currentPagePerson = 0;
        }

        /**
         * Helper - resetGroupingInformation
         */
        function resetFilterQueries() {
            $scope.basisQuery = "";
            $scope.excludeQuery = "";
            $scope.includeQuery = "";
            $scope.membersQuery = "";
            $scope.adminsQuery = "";
            $scope.optInQuery = "";
            $scope.ownersQuery = "";
            $scope.personQuery = "";
        }

        /**
         * Return to the list of groupings available for management/administration.
         */
        $scope.returnToGroupingsList = function () {
            $scope.resetGroupingInformation();

            $scope.showGrouping = false;
            loadMembersList = false;

            $scope.modelDescription = "";
            $scope.groupingDescription = "";
            $scope.displayTracker = 1;
        };

        /**
         * Reset the grouping members, page numbers, filters, and columns to sort by.
         */
        $scope.resetGroupingInformation = function () {
            resetGroupingMembers();
            resetFilterQueries();
            clearMemberInput();
            $scope.columnSort = {};
            $scope.syncDestArray = [];
            $scope.resetResults = [];
            $scope.personProps = [];
        };


        /**
         * Create a modal with a description of the preference selected.
         * @param {string} desc - the description of the preference
         */
        $scope.createPreferenceInfoModal = function (desc) {
            $scope.preferenceInfo = desc;

            $scope.infoModalInstance = $uibModal.open({
                templateUrl: "modal/infoModal", scope: $scope, backdrop: "static"
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
                $scope.createPreferenceErrorModal();
            }
        }

        /**
         * Toggle the grouping preference which allows users to opt out of a grouping.
         */
        $scope.updateAllowOptOut = function () {
            const groupingPath = $scope.selectedGrouping.path;
            const allowOptOut = $scope.allowOptOut;

            groupingsService.setOptOut(groupingPath, allowOptOut, handleSuccessfulPreferenceToggle, handleUnsuccessfulRequest);
        };

        /**
         * Creates a modal that prompts the user whether they want to delete the user or not. If 'Yes' is pressed, then
         * a request is made to delete the user.
         * @param {object} options - the options object
         * @param {String} options.users - the user being removed
         * @param {string} options.group - groups the user is being removed from
         */
        $scope.createResetGroupModal = function (options) {
            $scope.groupReset = options.group;
            $scope.listNames = options.listNames;
            const windowClass = $scope.showWarningRemovingSelfResetModal() ? "modal-danger" : "";

            $scope.resetModalInstance = $uibModal.open({
                templateUrl: "modal/resetModal", windowClass, scope: $scope, backdrop: "static", keyboard: false
            });
            $scope.resetModalInstance.result.then(function () {
                $scope.loading = true;
                let resetInclude = $scope.resetInclude;
                let resetExclude = $scope.resetExclude;
                let groupingPath = $scope.selectedGrouping.path;
                groupingsService.resetGroup(groupingPath, resetInclude, resetExclude, handleGroupingReset, handleUnsuccessfulRequest);
            });
        };

        $scope.resetGroup = function () {
            let listNames = "";
            let exBool = false;
            let inBool = false;
            if (Object.entries($scope.groupingInclude).length === 0 || $scope.includeCheck === false) {
                $scope.resetInclude = "empty";
            } else {
                inBool = true;
                $scope.resetInclude = [];
                for (let includedGrouping of $scope.groupingInclude) {
                    $scope.resetInclude.push(includedGrouping.uhUuid);
                }
            }
            if (Object.entries($scope.groupingExclude).length === 0 || $scope.excludeCheck === false) {
                $scope.resetExclude = "empty";
            } else {
                exBool = true;
                $scope.resetExclude = [];
                for (let excludedGrouping of $scope.groupingExclude) {
                    $scope.resetExclude.push(excludedGrouping.uhUuid);
                }
            }

            if (inBool && exBool) {
                listNames = "Exclude and Include lists";
            } else if (inBool) {
                listNames = "Include list";
            } else if (exBool) {
                listNames = "Exclude list";
            }

            let resetAll = null;
            if ($scope.excludeCheck === true && $scope.includeCheck === true) {
                resetAll = $scope.groupingInclude.concat($scope.groupingExclude);
            } else if ($scope.excludeCheck === true && $scope.includeCheck === false) {
                resetAll = $scope.groupingExclude;
            } else if ($scope.excludeCheck === false && $scope.includeCheck === true) {
                resetAll = $scope.groupingInclude;
            } else {
                resetAll = "";
            }

            $scope.resetResults = resetAll;

            if (!_.isUndefined($scope.resetResults[0])) {
                $scope.getPersonProps(Object.keys($scope.resetResults[0]));
            }

            $scope.createResetGroupModal({
                group: $scope.selectedGrouping.name, listNames
            });
        };

        $scope.updateIncludeCheck = function () {
            $scope.includeCheck = $scope.includeCheck === false;
        };

        $scope.updateExcludeCheck = function () {
            if ($scope.excludeCheck === false) {
                $scope.excludeCheck = true;
            } else {
                $scope.excludeCheck = false;
            }
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
         * Get the SyncDest value from the array given the name of the sync dest
         * @param {String} syncDestName Name of the Sync Dest to retrieve
         * @return {Boolean} Sync Dest value at the given name
         */
        $scope.getSyncDestValueInArray = function (syncDestName) {
            return $scope.syncDestArray.find((element) => element.name === syncDestName).synced;
        };

        /**
         * Get the entire syncDest object given its name
         * @param {String} syncDestName Name of the Sync Dest to retrieve
         * @return {Object} The entire syncDest object with the given name
         */
        $scope.getEntireSyncDestInArray = function (syncDestName) {
            return $scope.syncDestArray.find((element) => element.name === syncDestName);
        };

        /**
         * Set a given sync dest to a given value
         * @param {String} syncDestName Name of the Sync Dest to set
         * @param {Boolean} syncDestvalue The value to set the Sync Dest to
         */
        $scope.setSyncDestInArray = function (syncDestName, syncDestvalue) {
            $scope.syncDestArray.find((element) => element.name === syncDestName).synced = syncDestvalue;
        };

        /**
         * Helper - createSyncDestModal
         * Toggle the grouping sync destinations according to a given syncDest
         * @param {String} syncDestName Name of the Sync Dest to toggle
         */
        $scope.updateSingleSyncDest = function (syncDestName) {
            const groupingPath = $scope.selectedGrouping.path;
            const syncDestOn = $scope.getSyncDestValueInArray(syncDestName);
            groupingsService.setSyncDest(groupingPath, syncDestName, syncDestOn, handleSuccessfulPreferenceToggle, handleUnsuccessfulRequest);
        };

        /**
         * Create a modal indicating an error in saving the grouping's preferences.
         */
        $scope.createPreferenceErrorModal = function () {
            $scope.preferenceErrorModalInstance = $uibModal.open({
                templateUrl: "modal/preferenceErrorModal", scope: $scope, backdrop: "static", keyboard: false
            });
        };

        /**
         * Close the preference error modal.
         */
        $scope.closePreferenceError = function () {
            $scope.preferenceErrorModalInstance.close();
        };

        $scope.resetFields = function (type) {
            $scope.getGroupingInformation(type);
            $scope.userToAdd = "";
            $scope.manageMembers = "";
            $scope.membersInCheckboxList = {};
            $scope.allSelected = false;
            $scope.waitingForImportResponse = false;
        };

        $scope.resetErrors = function () {
            $scope.resStatus = 0;
            $scope.emptyInput = false;
            $scope.emptySelect = false;
            $scope.swap = true;
            $scope.inGrouper = false;
            $scope.removeInputError = false;
        };

        /**
         * Adds people to listName (to be used in on-click)
         * @param {String} listName grouping list (i.e. include, exclude, or owners)
         */
        $scope.addOnClick = function (listName) {
            $scope.resetErrors();
            if (listName === "owners") {
                $scope.addOwners();
            } else if (listName === "Include" || listName === "Exclude") {
                $scope.addMembers(listName, $scope.manageMembers);
            }
            $scope.errorDismissed = false;
        };

        /**
         * Removes people from listName (to be used in on-click)
         * @param {String} listName grouping list (i.e. Include, Exclude, or owners)
         */
        $scope.removeOnClick = function (listName) {
            $scope.resetErrors();
            if (listName === "Include" || listName === "Exclude" || listName === "owners") {
                $scope.removeMembers(listName);
            }
            $scope.errorDismissed = false;
        };

        /**
         * Removes error message and resets errors
         */
        $scope.dismissErrors = function () {
            $scope.errorDismissed = true;
            $scope.resetErrors();
        };

        /**
         * Create sync destination confirmation modal.
         * @param {String} syncDestName Name of the Sync Dest to create modal for
         */
        $scope.createSyncDestModal = function (syncDestName) {
            const isSyncDestOn = $scope.getSyncDestValueInArray(syncDestName);
            $scope.setSyncDestInArray(syncDestName, !isSyncDestOn);
            $scope.selectedSyncDest = $scope.getEntireSyncDestInArray(syncDestName);

            $scope.syncDestInstance = $uibModal.open({
                templateUrl: "modal/syncDestModal", scope: $scope, backdrop: "static", keyboard: false
            });

            $scope.syncDestInstance.result.then(function () {
                const isSyncDestOn = $scope.getSyncDestValueInArray(syncDestName);
                $scope.setSyncDestInArray(syncDestName, !isSyncDestOn);
                $scope.updateSingleSyncDest(syncDestName);
            }).catch(function () {
                //do nothing
            });
        };

        /**
         * Create warning modal if user is in being added to include and is a basis member or if
         * a user is being added to exclude and is not a basis member
         * @param user - user being added
         * @param listName - grouping list (i.e. include or exclude)
         * @param inBasis - boolean if user is in basis or not
         */
        $scope.createBasisWarningModal = function (user, listName, inBasis) {
            $scope.listName = listName;
            $scope.inBasis = inBasis;

            $scope.basisWarningModalInstance = $uibModal.open({
                templateUrl: "modal/basisWarningModal", scope: $scope
            });

            $scope.basisWarningModalInstance.result.then(function () {
                $scope.waitingForImportResponse = true;
                $scope.updateAddMember(user, listName);
            });
        };

        $scope.proceedBasisWarningModal = function () {
            $scope.basisWarningModalInstance.close();
        };

        $scope.closeBasisWarningModal = function () {
            $scope.basisWarningModalInstance.dismiss();
        };

        /**
         * Proceed with the syncDest confirmation
         */
        $scope.proceedSyncDestModal = function () {
            $scope.syncDestInstance.close();
        };

        /**
         * Close the syncDest confirmation modal
         */
        $scope.closeSyncDestModal = function () {
            $scope.syncDestInstance.dismiss();
        };

        /**
         * Create owner error modal when a grouping owner
         * is removed while still trying to access grouping
         * owner actions.
         */
        $scope.createOwnerErrorModal = function () {
            $scope.loading = false;
            $scope.OwnerErrorModalInstance = $uibModal.open({
                templateUrl: "modal/ownerErrorModal", scope: $scope, backdrop: "static", keyboard: false
            });
        };

        /**
         * Export data in a table to a CSV file
         * @param {object[]} table - the table to export - passing an empty table allows users to check their specified groups
         * @param grouping - grouping name that you are exporting from
         * @param list - grouping list (i.e. include or exclude)
         */
        $scope.exportGroupToCsv = function (table, grouping, list) {
            let data, filename, link, csv;
            if (_.isEmpty(table)) {
                const selectedMembersToExport = $scope.extractSelectedUsersFromCheckboxes($scope.membersInCheckboxList).split(",");
                const membersChecked = $scope.groupingInclude.filter((member) => (selectedMembersToExport.includes(member.uhUuid)));
                // When no members are checked export the list passed in or checked
                if (_.isEmpty(membersChecked)) {
                    csv = list === "include" ? $scope.convertListToCsv($scope.groupingInclude) : $scope.convertListToCsv($scope.groupingExclude);
                } else {
                    csv = $scope.convertListToCsv(membersChecked);
                }
            } else {
                csv = $scope.convertListToCsv(table);
            }
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
         * Helper - exportGroupToCSV
         * Converts the data in the table into comma-separated values.
         * @param {object[]} table - the table to convert
         * @returns string table in CSV format
         */
        $scope.convertListToCsv = function (table) {
            let str = "Last,First,Username,UH Number,Email\r\n";
            for (let i = 0; i < table.length; i++) {
                let line = "";
                line += table[i].lastName + ",";
                line += table[i].firstName + ",";
                line += table[i].username + ",";
                line += table[i].uhUuid + ",";
                if (table[i].username === "") {
                    line += "";
                } else {
                    line += table[i].username + Message.Csv.EMAIL_SUFFIX;
                }
                str += line + "\r\n";
            }
            return str;
        };

        /**
         * Convert the generic data in the table into comma-separated values.
         * @param {object[]} table - the table to convert
         * @returns the table in CSV format
         */
        $scope.convertListToCsvGeneric = function (table) {
            let str = "";
            for (let i = 0; i < Object.keys(table[0]).length; i++) {
                str += Object.keys(table[0])[i] + ",";
            }
            str += "\r\n";

            for (let i = 0; i < table.length; i++) {
                let line = "";
                for (let j = 0; j < Object.values(table[i]).length; j++) {
                    line += Object.values(table[i])[j] + ",";
                }
                str += line + "\r\n";
            }
            return str;
        };


        /**
         * Determine whether a warning message should be displayed when removing yourself from a list.
         * @returns {boolean} returns true if you are removing yourself from either the owners or admins list, otherwise
         * returns false
         */
        $scope.showWarningRemovingSelf = function () {
            return $scope.currentUser === $scope.memberToRemove && ($scope.listName === "owners" || $scope.listName === "admins");
        };

        /**
         * Determine whether a warning message should be displayed when removing yourself from a list.
         * @returns {boolean} returns true if you are removing yourself from either the owners or admins list, otherwise
         * returns false
         */
        $scope.showWarningRemovingSelfFromList = function () {
            return $scope.currentUser === $scope.memberToRemove || $scope.membersToRemove.includes($scope.currentUser);
        };

        /*** Determines whether a warning message should be displayed when removing yourself from a list.
         * @returns {boolean} returns true if you are removing yourself from either the owners or admins list, otherwise
         * returns false
         */
        $scope.showWarningRemovingSelfResetModal = function () {
            return $scope.membersToRemove.includes($scope.currentUser) && ($scope.listName === "owners" || $scope.listName === "admins");
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
                while (c.charAt(0) === " ") {
                    c = c.substring(1);
                }
                if (c.indexOf(name) === 0) {
                    return c.substring(name.length, c.length);
                }
            }
            return "";
        };

        /**
         * Log out a user and redirects them to the homepage.
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
         * Redirect the user to the feedback page.
         */
        $scope.proceedRedirectApiError = function () {
            $scope.apiErrorModalInstance.close();
            $window.location.href = "/uhgroupings/feedback";
        };

        /**
         * Redirect the user to the groupings page.
         */
        $scope.proceedRedirect = function () {
            $scope.OwnerErrorModalInstance.close();
            $window.location.href = "/uhgroupings/";
        };

        /**
         * Hides column when radio button is selected in table column settings dropdown,
         * saves to local storage.
         * @param columnToHide - the column to hide
         */
        $scope.hideColumn = function (columnToHide) {
            if (columnToHide === "path") {
                $scope.columnDisplaySetting = "first";
                $scope.showDescriptionColumn = true;
                $scope.showPathColumn = false;
            } else if (columnToHide === "description") {
                $scope.columnDisplaySetting = "second";
                $scope.showDescriptionColumn = false;
                $scope.showPathColumn = true;
            } else if (columnToHide === "none") {
                $scope.columnDisplaySetting = "third";
                $scope.showDescriptionColumn = true;
                $scope.showPathColumn = true;
            }
            localStorage.setItem("columnDisplaySetting", $scope.columnDisplaySetting);
            localStorage.setItem("showDescriptionColumn", JSON.stringify($scope.showDescriptionColumn));
            localStorage.setItem("showPathColumn", JSON.stringify($scope.showPathColumn));
        };
    }


    UHGroupingsApp.controller("GeneralJsController", GeneralJsController);
}());
