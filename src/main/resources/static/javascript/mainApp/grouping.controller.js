/* global _, angular, UHGroupingsApp */

(() => {

    /**
     * This controller contains functions specific to an owned grouping.
     * @param $scope - binding between controller and HTML page
     * @param $controller - service for instantiating controllers
     * @param $window - the browser window object
     * @param $uibModal - the UI Bootstrap service for creating modals
     * @param groupingsService - service for creating requests to the groupings API
     * @param PAGE_SIZE - page size constant from app.constants.js
     * @param Message - message object constant from app.constants.js
     * @param Threshold - threshold object constant from app.constants.js
     * @param Utility - utility function constant from app.constants.js
     */
    function GroupingJsController($scope, $controller, $window, $uibModal, groupingsService, PAGE_SIZE, Message, Threshold, Utility) {

        $scope.loading = false;
        $scope.waitingForImportResponse = false;
        $scope.paginatingProgress = true;
        $scope.paginatingComplete = false;
        $scope.largeGrouping = false;

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

        $scope.displayTracker = 1;
        $scope.descriptionLoaded = false;
        $scope.maxDescriptionLength = 98;
        // Used with ng-view on selected-grouping.html to toggle description editing.
        $scope.descriptionForm = false;
        // The user input
        $scope.modelDescription = "";
        // Variable for holding description
        $scope.groupingDescription = "";
        // Default description message when none is provided
        $scope.noDescriptionMessage = "No description given for this Grouping.";

        $scope.membersInCheckboxList = {};
        $scope.manageMembers = "";

        // Add members
        $scope.multiAddResults = [];
        $scope.invalidMembers = [];
        $scope.membersInList = "";
        $scope.isMultiAdd = false;

        // Remove members
        $scope.multiRemoveResults = [];
        $scope.membersToRemove = [];
        $scope.membersToModify = "";
        $scope.membersNotInList = "";
        $scope.isMultiRemove = false;

        $scope.resetResults = [];

        $scope.includeDisable = false;
        $scope.excludeDisable = false;
        $scope.includeCheck = false;
        $scope.excludeCheck = false;

        // Keeps track of async calls made throughout this js controller
        let asyncThreadCount = 0;
        // Flag used for getGroupingInformation function to end async call
        let loadMembersList = false;

        angular.extend(this, $controller("GeneralJsController", { $scope }));

        /**
         * Initiates the retrieval of information about the grouping clicked by the user.
         * @param {number} currentPage - the current page number in the groupings list
         * @param {number} index - the index of the grouping clicked by the user
         */
        $scope.displayGrouping = (currentPage, index) => {
            $scope.selectedGrouping = $scope.pagedItemsGroupings[Number(currentPage)][Number(index)];
            $scope.getGroupingInformation();
            $scope.showGrouping = true;
        };

        /**
         * Reloads the grouping
         */
        $scope.reloadGrouping = () => {
            if ($scope.paginatingComplete) {
                $scope.getGroupingInformation();
            }
        };

        /** Remove all nameless members from members and return a sorted object of distinct members.
         * @param {object[]} members - the members of the group
         * @returns {object[]} the members of the group, sorted by name and with blank usernames filtered out
         */
        const setGroupMembers = (members) => {
            _.remove(members, (member) => {
                return _.isEmpty(member.name);
            });

            // Unique members only by UUID (assume no two users should have the same uuid)
            members = _.uniqBy(members, "uhUuid");

            return _.sortBy(members, "name");
        };

        /** Remove all nameless members from membersToAdd then display an object of distinct members as a sorted
         *  concatenation of initialMembers and membersToAdd objects.
         * @param {object[]} initialMembers - initial members in group
         * @param {object[]} membersToAdd - members to add to group
         * @returns {object[]} the members of both groups in one array, sorted by name with blank usernames filtered out
         */
        const combineGroupMembers = (initialMembers, membersToAdd) => {
            _.remove(membersToAdd, (member) => {
                return _.isEmpty(member.name);
            });

            let members = _.concat(initialMembers, membersToAdd);

            // Unique members only by UUID (assume no two users should have the same uuid)
            members = _.uniqBy(members, "uhUuid");

            return _.sortBy(members, "name");
        };

        /**
         * Get information about the grouping including members and description.
         */
        $scope.getGroupingInformation = async () => {
            loadMembersList = true;
            const groupingPath = $scope.selectedGrouping.path;
            const paths = [groupingPath + ":basis", groupingPath + ":include", groupingPath + ":exclude", groupingPath + ":owners"];
            let currentPage = 1;
            $scope.loading = true;
            $scope.paginatingComplete = false;
            await $scope.getGroupingDescription(groupingPath);
            await $scope.getGroupingSyncDest(groupingPath);
            await $scope.getGroupingOptAttributes(groupingPath);
            while (!($scope.paginatingComplete) && loadMembersList) {
                $scope.paginatingProgress = true;
                await $scope.fetchGrouping(currentPage, paths);
                currentPage++;
                $scope.loading = false;
            }
            loadMembersList = false;
        };

        $scope.fetchGrouping = (currentPage, groupPaths) => {
            return new Promise((resolve) => {
                groupingsService.getGrouping(groupPaths, currentPage, PAGE_SIZE, "name", true, (res) => {
                    if (res.paginationComplete) {
                        $scope.paginatingComplete = true;
                        $scope.paginatingProgress = false;
                    } else if (res.groupingBasis.groupPath.slice(0, -6) === ($scope.selectedGrouping.path)) {
                        let putGroupMembers = combineGroupMembers;
                        if (currentPage === 1) {
                            putGroupMembers = setGroupMembers;
                        }
                        $scope.groupingBasis = putGroupMembers(res.groupingBasis.members, $scope.groupingBasis);
                        $scope.filter($scope.groupingBasis, "pagedItemsBasis", "currentPageBasis", $scope.basisQuery, false);

                        $scope.groupingInclude = putGroupMembers(res.groupingInclude.members, $scope.groupingInclude);
                        $scope.addInBasis($scope.groupingInclude);
                        $scope.filter($scope.groupingInclude, "pagedItemsInclude", "currentPageInclude", $scope.includeQuery, false);

                        $scope.groupingExclude = putGroupMembers(res.groupingExclude.members, $scope.groupingExclude);
                        $scope.addInBasis($scope.groupingExclude);
                        $scope.filter($scope.groupingExclude, "pagedItemsExclude", "currentPageExclude", $scope.excludeQuery, false);

                        $scope.groupingOwners = putGroupMembers(res.groupingOwners.members, $scope.groupingOwners);
                        $scope.filter($scope.groupingOwners, "pagedItemsOwners", "currentPageOwners", $scope.ownersQuery, false);

                        $scope.groupingMembers = putGroupMembers(res.allMembers.members, $scope.groupingMembers);
                        $scope.filter($scope.groupingMembers, "pagedItemsMembers", "currentPageMembers", $scope.membersQuery, false);

                        $scope.loading = false;
                    }
                    resolve();

                }, (res) => {
                    $scope.paginatingComplete = true;
                    $scope.resStatus = res.status;
                    if (res === null) {
                        $scope.largeGrouping = true;
                        $scope.paginatingComplete = false;
                        $scope.paginatingProgress = false;
                    } else if (res.statusCode === 403) {
                        $scope.displayOwnerErrorModal();
                    } else {
                        $scope.displayApiErrorModal();
                    }
                    loadMembersList = false;
                    resolve();
                });
            });
        };


        /**
         * Get a grouping's description
         * @param groupPath
         * @returns {Promise<unknown>}
         */
        $scope.getGroupingDescription = (groupPath) => {
            return new Promise((resolve) => groupingsService.getGroupingDescription(groupPath, (res) => {
                $scope.groupingDescription = res.description;
                $scope.descriptionLoaded = true;
                resolve();
            }, (res) => {
                $scope.groupingDescription = "";
                $scope.descriptionLoaded = false;
                $scope.resStatus = res.status;
                resolve();
            }));
        };

        /**
         * Get a grouping's sync destinations
         * @param groupPath
         * @returns {Promise<unknown>}
         */
        $scope.getGroupingSyncDest = (groupPath) => {
            return new Promise((resolve) => groupingsService.getGroupingSyncDest(groupPath, (res) => {
                $scope.syncDestArray = res.syncDestinations;
                resolve();
            }, (res) => {
                $scope.resStatus = res.status;
                resolve();
            }))
        };

        /**
         * Get a grouping's opt attributes
         * @param groupPath
         * @returns {Promise<unknown>}
         */
        $scope.getGroupingOptAttributes = (groupPath) => {
            return new Promise((resolve) => groupingsService.getGroupingOptAttributes(groupPath, (res) => {
                $scope.allowOptIn = res.optInOn;
                $scope.allowOptOut = res.optOutOn;
                resolve();
            }, (res) => {
                $scope.resStatus = res.status;
                resolve();
            }))
        };

        /**
         * Check if the members in the group are in the basis group.
         * @param {object[]} group - the group to check
         */
        $scope.addInBasis = (group) => {
            group.forEach((member) => {
                const memberUhUuid = member.uhUuid;
                member.inBasis = _.some($scope.groupingBasis, { uhUuid: memberUhUuid })
                    ? "Yes"
                    : "No";
            });
        };

        /**
         * Check if the members in the group are in the include group.
         * @param {object[]} group - the group to check
         */
        $scope.addInInclude = (group) => {
            group.forEach((member) => {
                const memberUhUuid = member.uhUuid;
                member.inInclude = _.some($scope.groupingInclude, { uhUuid: memberUhUuid })
                    ? "Yes"
                    : "No";
            });
        };

        /**
         * Check if the members in the group are in the exclude group.
         * @param {object[]} group - the group to check
         */
        $scope.addInExclude = (group) => {
            group.forEach((member) => {
                const memberUhUuid = member.uhUuid;
                member.inExclude = _.some($scope.groupingExclude, { uhUuid: memberUhUuid })
                    ? "Yes"
                    : "No";
            });
        };

        /**
         * Check if the members in the group are in Basis, Include, and Exclude groups.
         * @param {object[]} group - the group to check
         */
        $scope.addInGroups = (group) => {
            $scope.addInBasis(group);
            $scope.addInInclude(group);
            $scope.addInExclude(group);
        };

        /**
         * Check the length of the text string entered in the description form box, for error handling of max length
         */
        $scope.descriptionLengthWarning = () => (String($scope.modelDescription).length > $scope.maxDescriptionLength - 1);

        /**
         * Enable or disable editing of a Grouping's description, from selected-grouping.html.
         */
        $scope.editDescription = () => {
            $scope.descriptionForm = !($scope.descriptionForm);
        };

        /**
         * Cancel the editing of a description, and revert back to base selected-grouping page.
         */
        $scope.cancelDescriptionEdit = () => {
            // refer to last saved description when user cancels the edit
            $scope.modelDescription = $scope.groupingDescription;

            if ($scope.descriptionForm) {
                $scope.descriptionForm = !($scope.descriptionForm);
            }
        };

        /**
         * Set a new description for a Grouping.
         */
        $scope.saveDescription = () => {
            if ($scope.groupingDescription.localeCompare($scope.modelDescription) === 0) {
                return $scope.cancelDescriptionEdit();
            }
            $scope.groupingDescription = $scope.modelDescription;
            groupingsService.updateDescription($scope.groupingDescription, $scope.selectedGrouping.path,
                () => {
                    $scope.descriptionForm = !($scope.descriptionForm);
                }, // close description form when done.
                (res) => {
                    $scope.resStatus = res.status;
                    $scope.displayApiErrorModal();
                });
        };

        /**
         * Used for placeholder text for a grouping's description in the form box.
         * @returns {string} either the description of the grouping, or, placeholder text if the description is empty.
         */
        $scope.descriptionDisplay = () => {
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
         * If the grouping hasn't been fetched, return csv group loaded message, otherwise return csv group not loaded message.
         */
        $scope.getCSVToolTipMessage = () => {
            if (!$scope.paginatingComplete) {
                return Message.Csv.GROUP_NOT_LOADED;
            }
            if ($scope.groupingMembers.length === 0) {
                return Message.Csv.GROUP_EMPTY;
            }
            return "";
        };

        /**
         * Helper - clearMemberInput
         * Small function that resets the checkboxes on the page
         */
        const resetCheckboxes = () => {
            $scope.allSelected = false;
            $scope.membersInCheckboxList = {};
        };

        /**
         * Clears the user input for adding/deleting a member to/from a list.
         */
        const clearMemberInput = () => {
            $scope.manageMembers = "";
            $scope.membersNotInList = "";
            $scope.membersInList = "";
            $scope.multiRemoveResults = [];
            $scope.waitingForImportResponse = false;
            resetCheckboxes();
        };

        $scope.resetFields = () => {
            $scope.manageMembers = "";
            $scope.membersInCheckboxList = {};
            $scope.allSelected = false;
            $scope.waitingForImportResponse = false;
        };
        
        /**
         * Display a modal containing a browse local file system for import button.
         * @param listName - Current list
         */
        $scope.displayImportModal = (listName) => {
            $scope.listName = listName;

            $scope.importModalInstance = $uibModal.open({
                templateUrl: "modal/importModal",
                size: "lg",
                ariaLabelledBy: "import-modal",
                scope: $scope
            });
        };

        /**
         * Cancel the import modal instance
         */
        $scope.cancelImportModal = () => {
            $scope.importModalInstance.dismiss();
        };

        /**
         * Display the import error modal.
         */
        $scope.displayImportErrorModal = () => {
            $scope.importErrorModalInstance = $uibModal.open({
                templateUrl: "modal/importErrorModal",
                scope: $scope,
                ariaLabelledBy: "import-error-modal"
            });
        };

        /**
         * Close the import error modal.
         */
        $scope.closeImportErrorModal = () => {
            $scope.importErrorModalInstance.dismiss();
        };

        /**
         * Read a text file(.txt) or csv file(.csv) from client side. The file should consist of
         * a list of UH usernames or ids separated by newline characters. This
         * function is called implicitly from include.html and exclude.html.
         * The file is retrieved from the html input with id 'upload'.
         */
        $scope.readTextFile = (inputFile) => {
            if (!$scope.verifyImportFileType(inputFile)) {
                $scope.displayDynamicModal(
                    Message.Title.INVALID_FILE,
                    "File must be a .txt or .csv file."
                );
                return;
            }
            if (!$scope.verifyImportFileSize(inputFile)) {
                $scope.displayDynamicModal(
                    Message.Title.INVALID_FILE,
                    "File is too large. Maximum file size: 5MB"
                );
                return;
            }
            if (!$scope.verifyImportFileNameSize(inputFile)) {
                $scope.displayDynamicModal(
                    Message.Title.INVALID_FILE,
                    "File name has too many characters. Maximum character amount: 50"
                );
                return;
            }
            if (!$scope.verifyImportFileName(inputFile)) {
                $scope.displayDynamicModal(
                    Message.Title.INVALID_FILE,
                    "File name has illegal characters."
                );
                return;
            }
            let reader = new FileReader();
            reader.readAsText(inputFile);

            reader.onload = (e) => {
                const str = e.target.result;
                $scope.resetErrors();
                $scope.errorDismissed = false;
                if (inputFile.type === "text/csv") {
                    const namesInFile = str.split(/[\r\n]+/);
                    const firstRow = namesInFile[0].split(",");
                    const indexOfUhNumber = firstRow.findIndex((header) => header.includes(Message.Csv.UUID_COLUMN_HEADER));
                    if (indexOfUhNumber < 0) {
                        $scope.displayDynamicModal(
                          Message.Title.INVALID_FILE,
                          `CSV file does not contain ${Message.Csv.UUID_COLUMN_HEADER} in the header`,
                        );
                        return;
                    }
                    const UHNumbersInFile = namesInFile.map((row) => row.split(",")[Number(indexOfUhNumber)]).slice(1, -1);
                    $scope.addMembers($scope.listName, UHNumbersInFile);
                } else {
                    const namesInFile = str.split(/[\r\n,]+/);
                    $scope.addMembers($scope.listName, namesInFile);
                }
            };
        };

        $scope.verifyImportFileType = (inputFile) => {
            if (inputFile == null || inputFile.type == null) {
                return false;
            }
            return inputFile.type.toLowerCase() === "text/plain" || inputFile.type.toLowerCase() === "text/csv";
        };

        $scope.verifyImportFileSize = (inputFile) => {
            return inputFile.size <= 5242880 && inputFile.size > 0;
        };

        $scope.verifyImportFileNameSize = (inputFile) => {
            return inputFile.name.length <= 53 && inputFile.name.length > 0;
        };

        $scope.verifyImportFileName = (inputFile) => {
            let regex = /^[a-zA-Z0-9 ._-]+$/;
            return regex.test(inputFile.name);
        };

        $scope.removeTextFile = () => {
            angular.element(document.querySelector("#upload")).val(null);
        };

        /**
         *  Helper - addMembers, removeMembers
         *  Divides a string into an array where commas and spaces are present.
         */
        $scope.parseAddRemoveInputStr = (str) => {
            if (!_.isString(str)) {
                return "";
            }
            return str.split(/[ ,]+/);
        };

        /**
         * Helper - displayAddModal, displayRemoveModal
         * Initializes the name of the member to display on modals
         * @param {object} attributes - the user's attributes
         */
        $scope.initMemberDisplayName = (attributes) => {
            $scope.fullName = attributes.name;
            $scope.givenName = attributes.firstName;
            $scope.uhUuid = attributes.uhUuid;
            $scope.uid = attributes.username ?? attributes.uid;
            $scope.inBasis = attributes.inBasis;
            $scope.inInclude = attributes.inInclude;
            $scope.inExclude = attributes.inExclude;

            if ($scope.fullName != null && $scope.fullName.length > 0) {
                $scope.member = $scope.fullName;
            } else if ($scope.fullName != null && $scope.uid.length > 0) {
                $scope.member = $scope.uid;
            } else {
                $scope.member = $scope.uhUuid;
            }
        };

        /**
         * Helper - existsInList, returnMemberObject
         * Returns grouping object given the listName
         * @param {string} listName - grouping list
         * @returns {object[]} - the array of member objects in a grouping
         */
        const getCurrentPage = (listName) => {
            switch (listName) {
                case "Basis":
                    return $scope.groupingBasis;
                case "Include":
                    return $scope.groupingInclude;
                case "Exclude":
                    return $scope.groupingExclude;
                case "owners":
                    return $scope.groupingOwners;
                case "admins":
                    return $scope.adminsList;
                default:
                    break;
            }
        };

        /**
         * Helper - addMembers
         * Sets $scope.membersInList to a comma-seperated string of members already in the list.
         * $scope.membersInList is used in addModal.html and multiAddModal.html.
         * If ALL the members are already are in the list, display add-error-messages.html instead of modal.
         * @param {Object[]} members - the array of members to check whether they are already in the list being added to
         * @param {string} listName - the list the user is currently being added to
         * @returns {boolean} - true if ALL the members are already in the list
         */
        $scope.existsInList = (listName, members) => {
            const currentPage = getCurrentPage(listName);
            const membersInList = members.filter((member) =>
                _.some(currentPage, { uhUuid: member }) || _.some(currentPage, { uid: member })
            );
            $scope.membersInList = membersInList.join(", ");

            if (_.isEqual(members, membersInList)) {
                $scope.containsInput = true;
            }

            return $scope.containsInput;
        };

        /**
         * Adds people to listName (to be used in on-click)
         * @param {String} listName grouping list (i.e. include, exclude, or owners)
         */
        $scope.addOnClick = (listName) => {
            $scope.resetErrors();
            if (listName === "Include" || listName === "Exclude" || listName === "owners") {
                $scope.addMembers(listName);
            }
            $scope.errorDismissed = false;
        };

        /**
         * Removes people from listName (to be used in on-click)
         * @param {String} listName grouping list (i.e. Include, Exclude, or owners)
         */
        $scope.removeOnClick = (listName) => {
            $scope.resetErrors();
            if (listName === "Include" || listName === "Exclude" || listName === "owners") {
                $scope.removeMembers(listName);
            }
            $scope.errorDismissed = false;
        };

        /**
         * Add uhIdentifiers to a group. Leave the uhIdentifiers parameter null to take the member input from
         * $scope.manageMembers. Checks that all uhIdentifiers are valid before displaying the
         * add/multiAdd/importConfirmation modal.
         * @param {string} listName
         * @param {Object[]|null} uhIdentifiers
         */
        $scope.addMembers = (listName, uhIdentifiers) => {
            // If uhIdentifiers parameter is null, get member input from $scope.manageMembers
            uhIdentifiers = $scope.sanitizer(uhIdentifiers ?? $scope.parseAddRemoveInputStr($scope.manageMembers));
            $scope.listName = listName;
            // Check if uhIdentifiers/member input is empty
            if (_.isEmpty(uhIdentifiers)) {
                $scope.emptyInput = true;
                return;
            }
            // Prevent adding more than Threshold.MAX_IMPORT
            if (uhIdentifiers.length > Threshold.MAX_IMPORT) {
                $scope.displayDynamicModal(
                    Message.Title.IMPORT_OUT_OF_BOUNDS,
                    Message.Body.IMPORT_OUT_OF_BOUNDS);
                return;
            }
            // Check for members already in list, display error when all members to add already exist in the list
            if ($scope.existsInList(listName, uhIdentifiers)) {
                // Determine whether to display members already in the list in a modal or add-error-messages.html
                if (uhIdentifiers.length > Threshold.MAX_LIST_SIZE) {
                    $scope.displayDynamicModal(
                        Message.Title.NO_MEMBERS_ADDED,
                        Message.Body.NO_MEMBERS_ADDED.with(listName));
                } else {
                    $scope.errorDismissed = false;
                }
                return;
            }

            // Call async invalidUhIdentifiers check if batch import
            $scope.isBatchImport = uhIdentifiers.length > Threshold.MULTI_ADD;
            const checkInvalidUhIdentifiers = $scope.isBatchImport
                ? groupingsService.invalidUhIdentifiersAsync
                : groupingsService.invalidUhIdentifiers;

            $scope.waitingForImportResponse = true; // Small spinner on
            checkInvalidUhIdentifiers(uhIdentifiers, (res) => { // Check for invalid uhIdentifiers
                $scope.waitingForImportResponse = false; // Small spinner off
                $scope.isBatchImport = uhIdentifiers.length > Threshold.MULTI_ADD;
                // Check if res returned any invalid uhIdentifiers
                if (!_.isEmpty(res)) {
                    // Display invalid uhIdentifiers in add-error-messages.html or importError modal
                    $scope.invalidMembers = res;
                    $scope.addInputError = true;
                    if ($scope.isBatchImport) {
                        $scope.displayImportErrorModal();
                        $scope.addInputError = false;
                    }
                    return;
                }

                // Filter out members already in the group
                uhIdentifiers = uhIdentifiers.filter((member) => !$scope.membersInList.includes(member));

                // Display the appropriate modal
                if ($scope.isBatchImport) {
                    $scope.displayImportConfirmationModal(listName, uhIdentifiers);
                } else {
                    $scope.displayAddModal({
                        membersToAdd: uhIdentifiers,
                        listName
                    });
                }
            }, (res) => {
                // Display API error modal
                $scope.waitingForImportResponse = false;
                $scope.resStatus = res.status;
                $scope.displayApiErrorModal();
            });
        };

        /**
         * Handler for successful member add.
         * Displays the appropriate modal if it was batch-import, multi-add, or single add.
         */
        const handleSuccessfulAdd = (res) => {
            $scope.waitingForImportResponse = false; // Small spinner off
            // Display the appropriate result modal
            if ($scope.isBatchImport) {
                $scope.batchImportResults = res.addResults.results;
                $scope.displayImportSuccessModal();
            } else if ($scope.isMultiAdd) {
                $scope.displayDynamicModal(
                    Message.Title.ADD_MEMBERS,
                    Message.Body.ADD_MEMBERS.with($scope.listName));
            } else {
                $scope.displayDynamicModal(
                    Message.Title.ADD_MEMBER,
                    Message.Body.ADD_MEMBER.with($scope.member, $scope.listName));
            }

            // On pressing "Ok" in the Dynamic modal, reload the grouping
            $scope.dynamicModal.result.finally(() => {
                clearMemberInput();
                $scope.loading = true;
                $scope.waitingForImportResponse = false;
                if ($scope.listName === "admins") {
                    // Refreshes the groupings list and the admins list
                    $scope.init();
                } else {
                    $scope.getGroupingInformation();
                    $scope.syncDestArray = [];
                }
            }).catch(() => {});
        };

        /**
         * Generic handler for unsuccessful requests to the API.
         */
        const handleUnsuccessfulRequest = (res) => {
            $scope.loading = false;
            $scope.waitingForImportResponse = false;
            $scope.resStatus = res.status;
            if (res.status === 403) {
                $scope.displayOwnerErrorModal();
            } else {
                $scope.displayApiErrorModal();
            }
        };

        /**
         * Displays modal if an import takes longer than 8 seconds
         */
        const displaySlowImportModal = () => {
            return $scope.displayDynamicModal(
                Message.Title.SLOW_IMPORT,
                Message.Body.SLOW_IMPORT,
                8000);
        };

        /**
         * Display a modal that prompts the user whether they want to add the member(s) or not. If 'Yes' is pressed, then
         * a request is made to add the member(s).
         * @param {object} options - the options object
         * @param {string|object[]} options.membersToAdd - the member object or array of member(s) to add
         * @param {string} options.listName - the grouping list the member(s) is/are being added to
         */
        $scope.displayAddModal = (options) => {
            const membersToAdd = [].concat(options.membersToAdd); // Allows either string or array to be passed in
            $scope.listName = options.listName;
            $scope.isMultiAdd = membersToAdd.length > 1;

            // Prevent displaying add/multiAdd modal with 0 members
            if (_.isEmpty(membersToAdd)) {
                $scope.containsInput = true;
                return;
            }

            $scope.waitingForImportResponse = true; // Small spinner on
            groupingsService.getMembersAttributes(membersToAdd, (res) => { // Get attributes for each member
                $scope.waitingForImportResponse = false; // Small spinner off

                // Sets information to be displayed in add/multiAdd modal
                $scope.multiAddResults = res;
                $scope.addInGroups($scope.multiAddResults);
                $scope.initMemberDisplayName($scope.multiAddResults[0]);

                // Open add or multiAdd modal
                const templateUrl = $scope.isMultiAdd ? "modal/multiAddModal" : "modal/addModal";
                $scope.addModalInstance = $uibModal.open({
                    templateUrl,
                    scope: $scope,
                    backdrop: "static",
                    ariaLabelledBy: "add-modal"
                });

                // On pressing "Yes/Add" in the modal, make API call to add members to the group
                $scope.addModalInstance.result.then(async () => {
                    $scope.waitingForImportResponse = true; // Small spinner on
                    const groupingPath = $scope.selectedGrouping.path;
                    if ($scope.listName === "Include") {
                        await groupingsService.addIncludeMembers(membersToAdd, groupingPath, handleSuccessfulAdd, handleUnsuccessfulRequest, displaySlowImportModal);
                    } else if ($scope.listName === "Exclude") {
                        await groupingsService.addExcludeMembers(membersToAdd, groupingPath, handleSuccessfulAdd, handleUnsuccessfulRequest, displaySlowImportModal);
                    } else if ($scope.listName === "owners") {
                        await groupingsService.addOwnerships(groupingPath, membersToAdd, handleSuccessfulAdd, handleUnsuccessfulRequest);
                    } else if ($scope.listName === "admins") {
                        await groupingsService.addAdmin(membersToAdd, handleSuccessfulAdd, handleUnsuccessfulRequest);
                    }
                });
            }, (res) => {
                // Display API error modal
                $scope.waitingForImportResponse = false;
                $scope.resStatus = res.status;
                $scope.displayApiErrorModal();
            });
        };

        /**
         * Closes the add modal instance
         */
        $scope.proceedAddModal = () => {
            $scope.addModalInstance.close();
        };

        /**
         * Cancels the add modal instance
         */
        $scope.cancelAddModal = () => {
            $scope.addModalInstance.dismiss("cancel");
        };

        /**
         * Display a modal to confirm/cancel the import
         * @param listName - current list
         * @param membersToAdd - the members to add
         */
        $scope.displayImportConfirmationModal = (listName, membersToAdd) => {
            // Set information to be displayed in the modal
            $scope.importSize = membersToAdd.length;
            $scope.listName = listName;
            $scope.isMultiAdd = true;
            $scope.waitingForImportResponse = false;

            // Open importConfirmation modal
            $scope.importConfirmationModalInstance = $uibModal.open({
                templateUrl: "modal/importConfirmationModal",
                scope: $scope,
                ariaLabelledBy: "import-confirmation-modal"
            });

            // On pressing "Add" in the modal, make API call to add members to the group
            const groupingPath = $scope.selectedGrouping.path;
            $scope.importConfirmationModalInstance.result.then(async () => {
                $scope.waitingForImportResponse = true; // Small spinner on
                if ($scope.listName === "Include") {
                    await groupingsService.addIncludeMembersAsync(membersToAdd, groupingPath, handleSuccessfulAdd, handleUnsuccessfulRequest, displaySlowImportModal);
                } else if ($scope.listName === "Exclude") {
                    await groupingsService.addExcludeMembersAsync(membersToAdd, groupingPath, handleSuccessfulAdd, handleUnsuccessfulRequest, displaySlowImportModal);
                }
            });
        };

        /**
         * Close the import confirmation modal instance
         */
        $scope.proceedImportConfirmationModal = () => {
            $scope.importConfirmationModalInstance.close();
        };

        /**
         * Cancel the import confirmation modal instance
         */
        $scope.cancelImportConfirmationModal = () => {
            $scope.importConfirmationModalInstance.dismiss();
        };

        /**
         * Display the import success modal
         */
        $scope.displayImportSuccessModal = () => {
            $scope.importSuccessModalInstance = $uibModal.open({
                templateUrl: "modal/importSuccessModal",
                scope: $scope,
                ariaLabelledBy: "import-success-modal"
            });

            $scope.importSuccessModalInstance.result.finally(() => {
                $scope.getGroupingInformation();
            });
        };

        /**
         * Close the import success modal
         */
        $scope.closeImportSuccessModal = () => {
            $scope.importSuccessModalInstance.close();
        };

        /**
         * Helper - displayRemoveModal, fetchMemberProperties
         * Returns the member object that contains either the provided username or UH number.
         * @param memberIdentifier - The username or UH ID number of the member object to return.
         * @param listName - The name of the list to search.
         */
        $scope.returnMemberObject = (memberIdentifier, listName) => {
            const currentPage = getCurrentPage(listName);
            let memberToReturn;
            if (/[0-9]{8}/.test(memberIdentifier)) {
                memberToReturn = _.find(currentPage, (member) => member.uhUuid === memberIdentifier);
            } else {
                memberToReturn = _.find(currentPage, (member) => member.uid === memberIdentifier);
            }
            return memberToReturn;
        };

        /**
         * Helper - removeMembers
         * Searches an array of member objects, and checks if the members are in the groupings list.
         * If a member does not exist, it displays a comma-seperated string of their identifiers and
         * sets it to $scope.membersNotInList.
         * @param members {Object[]} - An array of members.
         * @returns {boolean} - true if some members exist in the grouping list, false if none.
         */
        $scope.fetchMemberProperties = (members, listName) => {
            let membersInList = [];
            let membersNotInList = [];

            for (let member of members) {
                let currentMember = $scope.returnMemberObject(member, listName);

                if (_.isUndefined(currentMember)) {
                    membersNotInList.push(member);
                } else {
                    let person = {
                        "uid": currentMember.uid,
                        "uhUuid": currentMember.uhUuid,
                        "name": currentMember.name
                    };

                    membersInList.push(person);
                }
            }
            $scope.membersNotInList = membersNotInList.join(", ");
            $scope.multiRemoveResults = _.uniqBy(membersInList, (member) => member.uhUuid);

            return !_.isEmpty($scope.multiRemoveResults);
        };

        /**
         * Extracts the string from $scope.manageMembers input field or members selected from checkboxes
         * (input field takes precedence) then sends it to the corresponding listName endpoint to perform the removal.
         * @param listName {string} - Name of list to remove the members from.
         */
        $scope.removeMembers = (listName) => {
            // Extract members from checkboxes or input box
            $scope.listName = listName;
            $scope.membersToModify = _.isEmpty($scope.manageMembers)
                ? $scope.extractSelectedUsersFromCheckboxes($scope.membersInCheckboxList)
                : $scope.manageMembers;
            let uhIdentifiers = $scope.sanitizer($scope.parseAddRemoveInputStr($scope.membersToModify));

            // Check if members from checkboxes or input box are empty
            if (_.isEmpty($scope.membersToModify)) {
                $scope.emptyInput = true;
                return;
            }
            // Check if members to remove exist in the list
            if (!$scope.fetchMemberProperties(uhIdentifiers, listName)) {
                $scope.displayDynamicModal(Message.Title.REMOVE_INPUT_ERROR, Message.Body.REMOVE_INPUT_ERROR);
                $scope.membersNotInList = "";
                return;
            }
            // Prevent removing all owners
            if (listName === "owners" && $scope.multiRemoveResults.length === $scope.groupingOwners.length) {
                $scope.displayRemoveErrorModal("owner");
                clearMemberInput();
                return;
            }

            // Filter out members to remove that do not exist in the group
            uhIdentifiers = uhIdentifiers.filter((member) => !$scope.membersNotInList.includes(member));

            // Display the remove/multiRemove modal
            $scope.displayRemoveModal({
                membersToRemove: uhIdentifiers,
                listName
            });
        };

        /**
         * Handler for successfully removing a member from the Include or Exclude group.
         */
        const handleSuccessfulRemove = () => {
            $scope.loading = false;
            $scope.waitingForImportResponse = false;
            $scope.syncDestArray = [];

            // Display the appropriate modal
            if ($scope.isMultiRemove) {
                $scope.displayDynamicModal(
                    Message.Title.REMOVE_MEMBERS,
                    Message.Body.REMOVE_MEMBERS.with($scope.listName)
                );
            } else {
                $scope.displayDynamicModal(
                    Message.Title.REMOVE_MEMBER,
                    Message.Body.REMOVE_MEMBER.with($scope.member, $scope.listName)
                );
            }

            // On pressing "Ok" in the Dynamic modal, reload the grouping
            $scope.dynamicModal.result.finally(() => {
                $scope.loading = true;
                clearMemberInput();
                $scope.getGroupingInformation();
            }).catch(() => {});
        };

        /**
         * Handler for successfully removing an owner from a grouping.
         */
        const handleOwnerRemove = () => {
            // Reload the grouping if you are not removing yourself, or if deleting anyone from the admins page
            if (!($scope.membersToRemove.includes($scope.currentUser.uhUuid) || $scope.membersToRemove.includes($scope.currentUser.uid)) || !_.isUndefined($scope.adminsList)) {
                handleSuccessfulRemove();
            } else {
                // Removing self from last grouping owned -> redirect to home page and then logout
                if ($scope.groupingsList.length === 1) {
                    $scope.proceedLogoutUser();
                } else {
                    $window.location.href = `${$scope.getBasePath()}/groupings`;
                }
            }
        };

        /**
         * Handler for successfully removing an admin from a grouping.
         */
        const handleAdminRemove = () => {
            $scope.waitingForImportResponse = false;
            // Removing self as admin -> redirect to home page and then logout
            if ($scope.membersToRemove.includes($scope.currentUser.uhUuid)) {
                $scope.proceedLogoutUser();
            } else {
                $scope.init();
            }
        };

        /**
         * Display a modal that prompts the user whether they want to remove the member(s) or not. If 'Yes' is pressed,
         * then a request is made to remove the member(s).
         * @param {object} options - the options object
         * @param {object|object[]} options.membersToRemove - the array of members to remove
         * @param {string} options.listName - the grouping list the member(s) is/are being removed from
         */
        $scope.displayRemoveModal = (options) => {
            $scope.membersToRemove = options.membersToRemove;
            $scope.listName = options.listName;

            // Display invalid input error message in add-error-messages.html
            if (_.isEmpty($scope.membersToRemove)) {
                $scope.removeInputError = true;
                return;
            }

            // Allows for member object from $scope.pagedItemsInclude/Exclude/Owners to be passed in
            if (_.isPlainObject($scope.membersToRemove)) {
                $scope.membersToRemove = [$scope.membersToRemove.uhUuid];
            }

            // Set information for the remove/multiRemove modal
            const memberObject = $scope.returnMemberObject($scope.membersToRemove[0], $scope.listName);
            $scope.initMemberDisplayName(memberObject);
            $scope.isMultiRemove = _.isEmpty($scope.multiRemoveResults)
                ? $scope.membersToRemove.length > 1
                : $scope.multiRemoveResults.length > 1;

            // Open remove or multiRemove modal and set modal red when removing yourself (currentUser) from owners
            const templateUrl = $scope.isMultiRemove ? "modal/multiRemoveModal" : "modal/removeModal";
            $scope.removeModalInstance = $uibModal.open({
                templateUrl,
                backdrop: "static",
                scope: $scope,
                ariaLabelledBy: "remove-modal"
            });

            // On pressing "Yes/Remove" in the modal, make API call to remove members from the group
            $scope.removeModalInstance.result.then(() => {
                $scope.waitingForImportResponse = true; // Small spinner on
                const groupingPath = $scope.selectedGrouping.path;
                if ($scope.listName === "Include") {
                    groupingsService.removeIncludeMembers(groupingPath, $scope.membersToRemove, handleSuccessfulRemove, handleUnsuccessfulRequest);
                } else if ($scope.listName === "Exclude") {
                    groupingsService.removeExcludeMembers(groupingPath, $scope.membersToRemove, handleSuccessfulRemove, handleUnsuccessfulRequest);
                } else if ($scope.listName === "owners") {
                    groupingsService.removeOwnerships(groupingPath, $scope.membersToRemove, handleOwnerRemove, handleUnsuccessfulRequest);
                } else if ($scope.listName === "admins") {
                    groupingsService.removeAdmin($scope.membersToRemove, handleAdminRemove, handleUnsuccessfulRequest);
                }
            });
        };

        /**
         * Closes the remove modal instance.
         */
        $scope.proceedRemoveModal = () => {
            clearMemberInput();
            $scope.removeModalInstance.close();
        };

        /**
         * Cancels the remove modal instance
         */
        $scope.cancelRemoveModal = () => {
            clearMemberInput();
            $scope.removeModalInstance.dismiss("cancel");
        };

        /**
         * Remove a single member from include/exclude by using the "trashcan" UI implementation.
         * @param {string} listName - the list to remove the user from (either Include or Exclude)
         * @param {number} currentPage - the current page in the table
         * @param {number} index - the index of the user clicked by the user
         * account
         */
        $scope.removeMemberWithTrashcan = (listName, currentPage, index) => {
            let memberToRemove;
            if (listName === "Include") {
                memberToRemove = $scope.pagedItemsInclude[Number(currentPage)][Number(index)];
            } else if (listName === "Exclude") {
                memberToRemove = $scope.pagedItemsExclude[Number(currentPage)][Number(index)];
            }

            $scope.displayRemoveModal({
                membersToRemove: memberToRemove,
                listName
            });
            $scope.membersToRemove = [memberToRemove.uhUuid];
        };

        /**
         * Remove a grouping owner by using the "trashcan" UI implementation.
         * There must be at least one grouping owner remaining.
         * @param {number} currentPage - the current page in the owners table
         * @param {number} index - the index of the owner clicked by the user
         */
        $scope.removeOwnerWithTrashcan = (currentPage, index) => {
            const ownerToRemove = $scope.pagedItemsOwners[Number(currentPage)][Number(index)];
            $scope.listName = "owners";

            if ($scope.groupingOwners.length === 1) {
                $scope.displayRemoveErrorModal("owner");
                return;
            }

            $scope.displayRemoveModal({
                membersToRemove: ownerToRemove,
                listName: "owners"
            });
            $scope.membersToRemove = [ownerToRemove.uhUuid];
        };

        /**
         * Displays a modal stating there was an error removing the user from a group.
         * @param {string} userType - the type of user being removed (either admin or owner)
         */
        $scope.displayRemoveErrorModal = (userType) => {
            $scope.userType = userType;

            $scope.removeErrorModalInstance = $uibModal.open({
                templateUrl: "modal/removeErrorModal",
                scope: $scope,
                backdrop: "static",
                ariaLabelledBy: "remove-error-modal"
            });
        };

        /**
         * Close the remove error modal.
         */
        $scope.closeRemoveErrorModal = () => {
            clearMemberInput();
            $scope.removeErrorModalInstance.close();
        };

        /**
         * Determine whether a warning message should be displayed when removing yourself from a list.
         * @returns {boolean} returns true if you are removing yourself from either the owners or admins list, otherwise
         * returns false
         */
        $scope.showWarningRemovingSelf = () => {
            return ($scope.membersToRemove.includes($scope.currentUser.uid)
                    || $scope.membersToRemove.includes($scope.currentUser.uhUuid))
                && ($scope.listName === "owners" || $scope.listName === "admins");
        };

        /**
         * Displays a modal that prompts the user whether they want to delete the user or not. If 'Yes' is pressed, then
         * a request is made to delete the user.
         * @param {object} options - the options object
         * @param {String} options.users - the user being removed
         * @param {string} options.group - groups the user is being removed from
         */
        $scope.displayResetGroupModal = (options) => {
            $scope.groupReset = options.group;
            $scope.listNames = options.listNames;

            $scope.resetModalInstance = $uibModal.open({
                templateUrl: "modal/resetModal",
                scope: $scope,
                backdrop: "static",
                ariaLabelledBy: "reset-modal"
            });
            $scope.resetModalInstance.result.then($scope.initResetGroup);
        };

        $scope.initResetGroup = async () => {
            $scope.loading = true;
            let groupingPath = $scope.selectedGrouping.path;
            let resetInclude = $scope.includeCheck;
            let resetExclude = $scope.excludeCheck;
            var resetIncludeResult = [];
            var resetExcludeResult = [];

            let resetGroup = (resetFunction, groupingPath, resetResult) => {
                return new Promise((resolve) => {
                    resetFunction(groupingPath, (res) => {
                        resetResult.push(res);
                        resolve();
                    }, (res) => {
                        resetResult.push(res);
                        resolve();
                    });
                });
            };

            if (resetInclude) {
                await resetGroup(groupingsService.resetIncludeGroupAsync, groupingPath, resetIncludeResult);
            }
            if (resetExclude) {
                await resetGroup(groupingsService.resetExcludeGroupAsync, groupingPath, resetExcludeResult);
            }
            $scope.loading = false;
            $scope.handleGroupingReset(groupingPath, resetIncludeResult.pop(), resetExcludeResult.pop());
        };

        $scope.resetGroup = () => {
            let listNames = "";
            let resetAll = [];
            
            if ($scope.includeCheck && $scope.excludeCheck) {
                listNames = "Exclude and Include lists";
                resetAll = $scope.groupingInclude.concat($scope.groupingExclude);
            } else if ($scope.includeCheck) {
                listNames = "Include list";
                resetAll = $scope.groupingInclude;
            } else if ($scope.excludeCheck) {
                listNames = "Exclude list";
                resetAll = $scope.groupingExclude;
            }

            $scope.resetResults = resetAll;

            $scope.displayResetGroupModal({
                group: $scope.selectedGrouping.name,
                listNames
            });
        };

        $scope.updateIncludeCheck = () => {
            $scope.includeCheck = $scope.includeCheck === false;
        };

        $scope.updateExcludeCheck = () => {
            $scope.excludeCheck = $scope.excludeCheck === false;
        };

        /**
         * Close the successful group reset modal.
         */
        $scope.closeSuccessfulGroupResetModal = () => {
            $scope.getGroupingInformation();
            $scope.successfulGroupResetModalInstance.close();
        };

        /**
         * Display the unsuccessful group reset modal.
         */
        $scope.displayUnsuccessfulGroupResetModal = (message) => {
            $scope.displayDynamicModal(Message.ResetGroupError.ResetGroupErrorMessages.TITLE, message);
        };

        /**
         * Helper-Guard: handleGroupingReset:
         * If there are error results, render an error modal and return true, otherwise return false.
         */
        $scope.handleGroupingReset = (groupingPath, resetIncludeResult, resetExcludeResult) => {
            let wasError = (result) => {
                return (typeof result !== "undefined" && typeof result.status !== "undefined");
            };
            let wasSuccess = (groupingPath, resetResult) => {
                return (typeof resetResult !== "undefined") &&
                    (resetResult.resultCode === "SUCCESS" && resetResult.groupPath.includes(groupingPath));
            };
            // Display an object to be matched with certainObject ResetGroupErrorMessageMap in app.constants.
            let results = {
                "includeFailure": wasError(resetIncludeResult),
                "excludeFailure": wasError(resetExcludeResult),
                "includeSuccess": wasSuccess(groupingPath, resetIncludeResult),
                "excludeSuccess": wasSuccess(groupingPath, resetExcludeResult)
            };
            // If ether API request failed then display an error modal.
            if (results.includeFailure || results.excludeFailure) {
                for (let i = 0; i < Message.ResetGroupError.ResetGroupErrorMessageMap.length; i++) {
                    let object = Message.ResetGroupError.ResetGroupErrorMessageMap[Number(i)];
                    // If the object matches a certain ResetGroupErrorMessageMap object.
                    if (Utility.compareObjects(object, results)) {
                        // Then use the error message in ResetGroupErrorMessages at index i.
                        let message = Message.ResetGroupError.ResetGroupErrorMessages.Body[Number(i)];
                        $scope.displayUnsuccessfulGroupResetModal(message);
                        return;
                    }
                }
            }
            // Otherwise, display the result success modal.
            $scope.displaySuccessfulGroupResetModal((() => {
                if (results.includeSuccess && results.excludeSuccess) {
                    return Message.ResetGroupSuccess.INCLUDE_AND_EXCLUDE;
                } else if (results.includeSuccess) {
                    return Message.ResetGroupSuccess.INCLUDE;
                } else if (results.excludeSuccess) {
                    return Message.ResetGroupSuccess.EXCLUDE;
                } else {
                    return "ERROR";
                }
            })());
        };

        $scope.displayEmptyGroupModal = () => {
            $scope.emptyGroupModalInstance = $uibModal.open({
                templateUrl: "modal/emptyGroupModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false,
                ariaLabelledBy: "empty-group-modal"
            });
        };

        $scope.displaySuccessfulGroupResetModal = (resultString) => {
            $scope.group = resultString;

            $scope.successfulGroupResetModalInstance = $uibModal.open({
                templateUrl: "modal/successfulGroupResetModal",
                scope: $scope,
                backdrop: true,
                ariaLabelledBy: "successful-group-reset-modal"
            });

            $scope.successfulGroupResetModalInstance.result.finally(() => {
                $scope.loading = true;
                $scope.getGroupingInformation();
            }).catch(() => {});
        };

        /**
         * Closes the modal, then proceeds with reseting the grouping.
         */
        $scope.proceedResetGroup = () => {
            $scope.resetModalInstance.close();
        };

        /**
         * Closes the modal for reseting group. This does not reset the grouping.
         */
        $scope.cancelResetGroup = () => {
            $scope.resetModalInstance.dismiss();
        };

        $scope.closeEmptyGroupModal = () => {
            $scope.emptyGroupModalInstance.dismiss();
        };

        /**
         * Helper - resetGroupingInformation
         * Reset the grouping members and page numbers.
         */
        const resetGroupingMembers = () => {
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

            $scope.currentPageMembers = 0;
            $scope.currentPageBasis = 0;
            $scope.currentPageInclude = 0;
            $scope.currentPageExclude = 0;
            $scope.currentPageOwners = 0;
            $scope.currentPagePerson = 0;
        };

        /**
         * Helper - resetGroupingInformation
         */
        const resetFilterQueries = () => {
            $scope.basisQuery = "";
            $scope.excludeQuery = "";
            $scope.includeQuery = "";
            $scope.membersQuery = "";
            $scope.adminsQuery = "";
            $scope.optInQuery = "";
            $scope.ownersQuery = "";
            $scope.personQuery = "";
        };

        /**
         * Return to the list of groupings available for management/administration.
         */
        $scope.returnToGroupingsList = () => {
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
        $scope.resetGroupingInformation = () => {
            resetGroupingMembers();
            resetFilterQueries();
            clearMemberInput();
            $scope.columnSort = {};
            $scope.syncDestArray = [];
            $scope.resetResults = [];
        };

        /**
         * Handler for a successful preference toggle.
         * @param {object} res - the JSON response from toggling the preference
         */
        const handleSuccessfulPreferenceToggle = (res) => {
            if (!_.isUndefined(res.statusCode)) {
                $scope.displayPreferenceErrorModal();
            }
        };

        /**
         * Display a modal indicating an error in saving the grouping's preferences.
         */
        $scope.displayPreferenceErrorModal = () => {
            $scope.preferenceErrorModalInstance = $uibModal.open({
                templateUrl: "modal/preferenceErrorModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false,
                ariaLabelledBy: "preference-error-modal"
            });
        };

        /**
         * Close the preference error modal.
         */
        $scope.closePreferenceError = () => {
            $scope.preferenceErrorModalInstance.close();
        };

        /**
         * Toggle the grouping preference which allows users to opt out of a grouping.
         */
        $scope.updateAllowOptOut = () => {
            const groupingPath = $scope.selectedGrouping.path;
            const allowOptOut = $scope.allowOptOut;

            groupingsService.setOptOut(groupingPath, allowOptOut, handleSuccessfulPreferenceToggle, handleUnsuccessfulRequest);
        };

        /**
         * Toggles the grouping preference which allows users to discover the grouping and opt into it.
         */
        $scope.updateAllowOptIn = () => {
            const groupingPath = $scope.selectedGrouping.path;
            const allowOptIn = $scope.allowOptIn;

            groupingsService.setOptIn(groupingPath, allowOptIn, handleSuccessfulPreferenceToggle, handleUnsuccessfulRequest);
        };

        /**
         * Get the SyncDest value from the array given the name of the sync dest
         * @param {String} syncDestName Name of the Sync Dest to retrieve
         * @return {Boolean} Sync Dest value at the given name
         */
        $scope.getSyncDestValueInArray = (syncDestName) => {
            return $scope.syncDestArray.find((element) => element.name === syncDestName).synced;
        };

        /**
         * Get the entire syncDest object given its name
         * @param {String} syncDestName Name of the Sync Dest to retrieve
         * @return {Object} The entire syncDest object with the given name
         */
        $scope.getEntireSyncDestInArray = (syncDestName) => {
            return $scope.syncDestArray.find((element) => element.name === syncDestName);
        };

        /**
         * Set a given sync dest to a given value
         * @param {String} syncDestName Name of the Sync Dest to set
         * @param {Boolean} syncDestvalue The value to set the Sync Dest to
         */
        $scope.setSyncDestInArray = (syncDestName, syncDestvalue) => {
            $scope.syncDestArray.find((element) => element.name === syncDestName).synced = syncDestvalue;
        };

        /**
         * Helper - displaySyncDestModal
         * Toggle the grouping sync destinations according to a given syncDest
         * @param {String} syncDestName Name of the Sync Dest to toggle
         */
        $scope.updateSingleSyncDest = (syncDestName) => {
            const groupingPath = $scope.selectedGrouping.path;
            const syncDestOn = $scope.getSyncDestValueInArray(syncDestName);
            groupingsService.setSyncDest(groupingPath, syncDestName, syncDestOn, handleSuccessfulPreferenceToggle, handleUnsuccessfulRequest);
        };

        /**
         * Display sync destination confirmation modal.
         * @param {String} syncDestName Name of the Sync Dest to display modal for
         */
        $scope.displaySyncDestModal = (syncDestName) => {
            const isSyncDestOn = $scope.getSyncDestValueInArray(syncDestName);
            $scope.setSyncDestInArray(syncDestName, !isSyncDestOn);
            $scope.selectedSyncDest = $scope.getEntireSyncDestInArray(syncDestName);

            $scope.syncDestInstance = $uibModal.open({
                templateUrl: "modal/syncDestModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false,
                ariaLabelledBy: "sync-dest-modal"
            });

            $scope.syncDestInstance.result.then(() => {
                const isSyncDestOn = $scope.getSyncDestValueInArray(syncDestName);
                $scope.setSyncDestInArray(syncDestName, !isSyncDestOn);
                $scope.updateSingleSyncDest(syncDestName);
            }).catch(() => {
                //do nothing
            });
        };

        /**
         * Proceed with the syncDest confirmation
         */
        $scope.proceedSyncDestModal = () => {
            $scope.syncDestInstance.close();
        };

        /**
         * Close the syncDest confirmation modal
         */
        $scope.closeSyncDestModal = () => {
            $scope.syncDestInstance.dismiss();
        };

        /**
         * Copies the members in the current page to an object by UH number
         * that holds true/false value for triggering checkboxes.
         * @param currentPage - Current page that contains members.
         */
        $scope.transferMembersFromPageToCheckboxObject = (currentPage) => {
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
        $scope.toggleCheckAllSelection = (group) => {
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

            for (let member of pageItems[Number(pageNumber)]) {
                $scope.membersInCheckboxList[member.uhUuid] = $scope.allSelected;
            }
        };

        /**
         * Helper - removeMembers
         * Take in a list of booleans and return a comma separated string containing the identifiers of all true booleans.
         * @param obj: {obj1: true, obj2: false, obj3: true}
         * @returns {string}: "obj1,obj3"
         */
        $scope.extractSelectedUsersFromCheckboxes = (obj) => {
            for (const value of Object.values(obj)) {
                if (value !== true && value !== false) {
                    return "";
                }
            }
            return _.keys(_.pickBy(obj)).join(",");
        };

        /**
         * Used with ng-keydown directive in HTML to click a checkbox in include and exclude list using the enter key.
         * @param event - pass in $event in HTML
         * @param {number} i - index of checkbox
         */
        $scope.clickCheckbox = (event, i) => {
            if (event.keyCode === 13) {
                $scope.membersInCheckboxList[i.uhUuid] = !$scope.membersInCheckboxList[i.uhUuid];
            }
        };

        /**
         * Display owner error modal when a grouping owner
         * is removed while still trying to access grouping
         * owner actions.
         */
        $scope.displayOwnerErrorModal = () => {
            $scope.loading = false;
            $scope.OwnerErrorModalInstance = $uibModal.open({
                templateUrl: "modal/ownerErrorModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false,
                ariaLabelledBy: "owner-error-modal"
            });
        };

        /**
         * Export data in a grouping to a CSV file
         * @param {object[]} table - the table to export ($scope.groupingMembers/Basis/Include/Exclude)
         * @param {String} listName - the name of the list
         */
        $scope.exportGroupToCsv = (table, listName) => {
            let data, filename, link, csv;

            csv = $scope.convertListToCsv(table);

            if (csv == null) {
                $scope.displayApiErrorModal();
                return;
            }

            filename = $scope.selectedGrouping.name + ":" + listName + "_list.csv";
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
        $scope.convertListToCsv = (table) => {
            let str = "Last,First,Username,UH Number,Email\r\n";
            for (let data of table) {
                let line = "";
                line += data.lastName + ",";
                line += data.firstName + ",";
                line += data.uid + ",";
                line += data.uhUuid + ",";
                if (data.uid === "") {
                    line += "";
                } else {
                    line += data.uid + Message.Csv.EMAIL_SUFFIX;
                }
                str += line + "\r\n";
            }
            return str;
        };

        /**
         * Check if there are no members in the grouping
         */
        $scope.isGroupingEmpty = () => {
            return $scope.groupingMembers.length === 0
                && $scope.groupingBasis.length === 0
                && $scope.groupingInclude.length === 0
                && $scope.groupingExclude.length === 0;
        };
    }

    UHGroupingsApp.controller("GroupingJsController", GroupingJsController);
})();
