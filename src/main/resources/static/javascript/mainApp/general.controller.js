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

    function GeneralJsController($scope, $window, $uibModal, $controller, groupingsService, dataProvider, PAGE_SIZE, Message, Threshold, Utility) {
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

        // This is a regex Pattern that contains all valid UH Identifiers which consists of uid (Username) and uhUuid (UH Numbers) chars.
        $scope.uhIdentifierPattern = new RegExp("^[_?a-z-?@?0-9]{3,64}$");

        $scope.currentUser = {};
        $scope.manageMembers = "";
        $scope.personProps = [];
        $scope.resetResults = [];
        $scope.resetUser = [];
        $scope.resetID = [];
        $scope.resetName = [];

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
        $scope.waitingForImportResponse = false;
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
            $scope.currentUser = {
                uid: res.data.username,
                uhUuid: res.data.uhUuid
            };
        });

        /**
         * Get the number of memberships that the current user is associated with.
         */
        groupingsService.getNumberOfMemberships((res) => {
                $scope.numberOfMemberships = res;
            }, (res) => {
            }
        );

        /**
         * Get the number of groupings that the current user is associated with.
         */
        groupingsService.getNumberOfGroupings((res) => {
                $scope.numberOfGroupings = res;
            }
        );

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
                $scope.displayOwnerErrorModal();
            } else {
                $scope.displayApiErrorModal();
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

        /** Remove all nameless members from membersToAdd then display an object of distinct members as a sorted
         *  concatenation of initialMembers and membersToAdd objects.
         * @param {object[]} initialMembers - initial members in group
         * @param {object[]} membersToAdd - members to add to group
         * @returns {object[]} the members of both groups in one array, sorted by name with blank usernames filtered out
         */
        function combineGroupMembers(initialMembers, membersToAdd) {

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
                    $scope.displayOwnerErrorModal();
                }
            });
        };

        /**
         * Gets information about the grouping, such as its members and the preferences set.
         * Retrieves information asynchronously page by page
         */
        $scope.getGroupingInformation = function () {

            $scope.loading = true;

            //Increments due to being called again
            asyncThreadCount++;
            /**
             * Makes sure only one asyncThreadCount is running to call another grouping
             * Else it will return a false boolean repeatedly till asyncThreadCount has
             * decremented to allow another call
             */
            if (asyncThreadCount === 1) {
                let currentPage = 1;
                const groupingPath = $scope.selectedGrouping.path;
                /**
                 * Function to get pages of a grouping asynchronously
                 * @param {String} groupingPath - Path to the grouping to retrieve data from
                 * @param {Number} currentPage - Page of grouping to retrieve (increments after each async/await call)
                 * @param {Number} PAGE_SIZE - Size of page to retrieve (Located in app.constants)
                 * @param {String} sortString - Parameter to sort the grouping database by before retrieving
                 *     information
                 * @param {Boolean} isAscending - If true, grouping database is sorted ascending (A-Z), false for
                 *     descending (Z-A)
                 */

                groupingsService.getGrouping(groupingPath, currentPage, PAGE_SIZE, "name", true, async function (res) {

                    // Gets the description go the group
                    if (res.description === null) {
                        $scope.groupingDescription = "";
                    } else {
                        $scope.groupingDescription = res.description;
                        $scope.displayTracker = 1;
                    }
                    $scope.descriptionLoaded = true;
                    $scope.paginatingProgress = true;
                    $scope.paginatingComplete = false;

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
                    $scope.filter($scope.groupingOwners, "pagedItemsOwners", "currentPageMembers", $scope.ownersQuery, true);

                    $scope.allowOptIn = res.optInOn;
                    $scope.allowOptOut = res.optOutOn;

                    $scope.syncDestArray = res.syncDestinations;
                    $scope.loading = false;
                    $scope.paginatingProgress = false;

                    //increments page to load and allows members to iteratively be loaded
                    currentPage++;
                    loadMembersList = true;

                    $scope.includeDisable = false;
                    if (Object.entries($scope.groupingInclude).length === 0) {
                        $scope.includeCheck = false;
                        $scope.includeDisable = true;
                    }

                    $scope.excludeDisable = false;
                    if (Object.entries($scope.groupingExclude).length === 0) {
                        $scope.excludeCheck = false;
                        $scope.excludeDisable = true;
                    }

                    //While true loop that calls members asynchronously from Grouper based on PAGE_SIZE
                    while (loadMembersList) {
                        /**
                         *Calls getPages function to load the rest of the members,
                         * but waits till the page before is retrieved before getting another page
                         */
                        try {
                            await $scope.getPages(groupingPath, currentPage, PAGE_SIZE, "name", true);
                        } catch (error) {
                            //Catches in both fetch and response
                        }
                        currentPage++;
                    }
                }, function (res) {
                    $scope.resStatus = res.status;
                    $scope.displayApiErrorModal();
                });
                //Will only decrement threadcount if previous call absolutely finishes
                asyncThreadCount--;
                //If asyncThreadCount is more than one keep returning and keep setting loadMembersList to false
            } else {
                return loadMembersList = false;
            }
        };

        /**
         * Function to get pages of a grouping asynchronously
         * @param {String} groupingPath - Path to the grouping to retrieve data from
         * @param {Number} page - Page of grouping to retrieve (Paging starts from 1)
         * @param {Number} size - Size of page to retrieve
         * @param {String} sortString - Parameter to sort the grouping database by before retrieving information
         * @param {Boolean} isAscending - If true, grouping database is sorted ascending (A-Z), false for descending
         *     (Z-A)
         */
        $scope.getPages = function (groupingPath, page, size, sortString, isAscending) {

            /**
             * Promise returned so that the async/await call can be completed
             * Which will only complete if resolve is called
             * Resolve will be called based on 2 accounts:
             *    1) groupingPath has changed so async call needs to be terminated before starting to load new
             * groupingPath
             *    2) All members in a grouping have been loaded
             */
            return new Promise((resolve) =>
                groupingsService.getGrouping(groupingPath, page, size, sortString, isAscending, function (res) {

                    // Keep loading members till there are no members left and resolve promise
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
                        $scope.filter($scope.groupingOwners, "pagedItemsOwners", "currentPageOwners", $scope.ownersQuery, false);

                    } else {
                        // Stop loading text
                        $scope.paginatingProgress = false;
                        $scope.paginatingComplete = true;
                        loadMembersList = false;
                    }
                    //Completes the promise and returns
                    resolve();
                }, function (res) {
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
                    //stops while loop and completes promise then returns
                    loadMembersList = false;
                    resolve();
                })
            );
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
         * Display a modal for errors in loading data from the API.
         */
        $scope.displayApiErrorModal = function () {
            $scope.loading = false;
            $scope.apiErrorModalInstance = $uibModal.open({
                templateUrl: "modal/apiError",
                scope: $scope,
                backdrop: "static",
                keyboard: false
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
        $scope.addInInclude = function (group) {
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
        $scope.addInExclude = function (group) {
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
        $scope.addInGroups = function (group) {
            $scope.addInBasis(group);
            $scope.addInInclude(group);
            $scope.addInExclude(group);
        };

        /**
         * Check what lists a member in a grouping are in.
         * @param {object[]} compositeGroup - the composite / all members group
         */
        $scope.addWhereListed = function (compositeGroup) {
            compositeGroup.forEach((member) => {
                const memberUhUuid = member.uhUuid;
                if (_.some($scope.groupingBasis, { uhUuid: memberUhUuid })) {

                    member.whereListed = "Basis";
                }

                if (_.some($scope.groupingInclude, { uhUuid: memberUhUuid })) {
                    member.whereListed = _.isUndefined(member.whereListed)
                        ? "Include"
                        : "Basis & Include";
                }
            });
        };

        /**
         * Display a modal containing a browse local file system for import button.
         * @param listName - Current list
         */
        $scope.displayImportModal = function (listName) {
            $scope.listName = listName;

            $scope.importModalInstance = $uibModal.open({
                templateUrl: "modal/importModal",
                size: "lg",
                scope: $scope
            });
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
            if (!$scope.verifyImportFileType(inputFile)) {
                $scope.displayDynamicModal(
                    Message.Title.INVALID_FILE,
                    "File must be a .txt file."
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
            reader.onload = function (e) {
                const str = e.target.result;
                const namesInFile = str.split(/[\r\n,]+/);
                $scope.addMembers($scope.listName, namesInFile);
            };
            reader.readAsText(inputFile);
        };

        $scope.verifyImportFileType = function (inputFile) {
            if (inputFile == null || inputFile.type == null) {
                return false;
            }
            return inputFile.type.toLowerCase() === "text/plain";
        };

        $scope.verifyImportFileSize = function (inputFile) {
            return inputFile.size <= 5242880 && inputFile.size > 0;
        };

        $scope.verifyImportFileNameSize = function (inputFile) {
            return inputFile.name.length <= 53 && inputFile.name.length > 0;
        };

        $scope.verifyImportFileName = function (inputFile) {
            let regex = /^[a-zA-Z0-9 ._-]+$/;
            return regex.test(inputFile.name);
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
                case "owners:":
                    $scope.manageMembers = "";
                    $scope.membersNotInList = "";
                    $scope.membersInList = "";
                    $scope.personProps = [];
                    $scope.membersInCheckboxList = {};
                    $scope.waitingForImportResponse = false;
                    resetCheckboxes();
                    break;
                case "admins":
                    $scope.adminToAdd = "";
                    break;
                default:
                    $scope.manageMembers = "";
                    $scope.adminToAdd = "";
                    $scope.membersNotInList = "";
                    $scope.membersInList = "";
            }
        }

        /**
         * Display the import error modal.
         */
        $scope.displayImportErrorModal = function () {
            $scope.importErrorModalInstance = $uibModal.open({
                templateUrl: "modal/importErrorModal",
                scope: $scope
            });
        };

        /**
         * Close the import error modal.
         */
        $scope.closeImportErrorModal = function () {
            $scope.importErrorModalInstance.dismiss();
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
         * Cancel the import modal instance
         */
        $scope.cancelImportModal = function () {
            $scope.importModalInstance.dismiss();
        };

        /**
         * Display a modal with a title, and body message. The modal will close in the case of pressing the ok button
         * and will cancel if the timeTillClose is set and time runs out. The modal will not timeout unless the
         * timeTillClose is set.
         */
        $scope.displayDynamicModal = function (title, body, timeTillClose) {
            $scope.currentModalTitle = title;
            $scope.currentModalBody = body;

            $scope.dynamicModal = $uibModal.open({
                templateUrl: "modal/dynamicModal",
                scope: $scope
            });
            if (!_.isUndefined(timeTillClose)) {
                let closeOnTimeout = function () {
                    $scope.dynamicModal.dismiss();
                };
                setTimeout(closeOnTimeout, timeTillClose);
            }
            $scope.closeDynamicModal = function () {
                $scope.dynamicModal.close();
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
         * Add uhIdentifiers to a group. Leave the uhIdentifiers parameter null to take the member input from
         * $scope.manageMembers. Checks that all uhIdentifiers are valid before displaying the
         * add/multiAdd/importConfirmation modal.
         * @param {string} listName
         * @param {Object[]|null} uhIdentifiers
         */
        $scope.addMembers = function (listName, uhIdentifiers) {
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

            $scope.waitingForImportResponse = true; // Small spinner on
            groupingsService.invalidUhIdentifiers(uhIdentifiers, (res) => { // Check for invalid uhIdentifiers
                $scope.waitingForImportResponse = false; // Small spinner off
                $scope.isBatchImport = uhIdentifiers.length > Threshold.MULTI_ADD;

                // Check if res returned any invalid uhIdentifiers
                if (!_.isEmpty(res)) {
                    // Display invalid uhIdentifiers in add-error-messages.html or importError modal
                    $scope.invalidMembers = res;
                    $scope.addInputError = true;
                    if ($scope.isBatchImport) {
                        $scope.displayImportErrorModal();
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

        function timeoutModal() {
            return $scope.displayDynamicModal(
                Message.Title.SLOW_IMPORT,
                Message.Body.SLOW_IMPORT,
                8000);
        }

        function handleSuccessfulAdd(res) {
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
            $scope.dynamicModal.result.finally(function () {
                clearMemberInput($scope.listName);
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
        }

        /**
         * Display a modal that prompts the user whether they want to add the member(s) or not. If 'Yes' is pressed, then
         * a request is made to add the member(s).
         * @param {object} options - the options object
         * @param {string|object[]} options.membersToAdd - the member object or array of member(s) to add
         * @param {string} options.listName - the grouping list the member(s) is/are being added to
         */
        $scope.displayAddModal = function (options) {
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
                    backdrop: "static"
                });

                // On pressing "Yes/Add" in the modal, make API call to add members to the group
                $scope.addModalInstance.result.then(async function () {
                    $scope.waitingForImportResponse = true; // Small spinner on
                    const groupingPath = $scope.selectedGrouping.path;
                    if ($scope.listName === "Include") {
                        await groupingsService.addMembersToIncludeAsync(membersToAdd, groupingPath, handleSuccessfulAdd, handleUnsuccessfulRequest, timeoutModal);
                    } else if ($scope.listName === "Exclude") {
                        await groupingsService.addMembersToExcludeAsync(membersToAdd, groupingPath, handleSuccessfulAdd, handleUnsuccessfulRequest, timeoutModal);
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
        $scope.proceedAddModal = function () {
            $scope.addModalInstance.close();
        };

        /**
         * Cancels the add modal instance
         */
        $scope.cancelAddModal = function () {
            $scope.addModalInstance.dismiss("cancel");
        };

        /**
         * Display a modal to confirm/cancel the import
         * @param listName - current list
         * @param membersToAdd - the members to add
         */
        $scope.displayImportConfirmationModal = function (listName, membersToAdd) {
            // Set information to be displayed in the modal
            $scope.importSize = membersToAdd.length;
            $scope.listName = listName;
            $scope.isMultiAdd = true;
            $scope.waitingForImportResponse = false;

            // Open importConfirmation modal
            $scope.importConfirmationModalInstance = $uibModal.open({
                templateUrl: "modal/importConfirmationModal",
                scope: $scope
            });

            // On pressing "Add" in the modal, make API call to add members to the group
            const groupingPath = $scope.selectedGrouping.path;
            $scope.importConfirmationModalInstance.result.then(async function () {
                $scope.waitingForImportResponse = true; // Small spinner on
                if ($scope.listName === "Include") {
                    await groupingsService.addMembersToIncludeAsync(membersToAdd, groupingPath, handleSuccessfulAdd, handleUnsuccessfulRequest, timeoutModal);
                } else if ($scope.listName === "Exclude") {
                    await groupingsService.addMembersToExcludeAsync(membersToAdd, groupingPath, handleSuccessfulAdd, handleUnsuccessfulRequest, timeoutModal);
                }
            });
        };

        /**
         * Cancel the import confirmation modal instance
         */
        $scope.cancelImportConfirmationModal = function () {
            $scope.importConfirmationModalInstance.dismiss();
        };

        /**
         * Close the import confirmation modal instance
         */
        $scope.proceedImportConfirmationModal = function () {
            $scope.importConfirmationModalInstance.close();
        };

        /**
         * Display the import success modal
         */
        $scope.displayImportSuccessModal = () => {
            $scope.importSuccessModalInstance = $uibModal.open({
                templateUrl: "modal/importSuccessModal",
                scope: $scope
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
         * Helper - addMembers
         * Sets $scope.membersInList to a comma-seperated string of members already in the list.
         * $scope.membersInList is used in addModal.html and multiAddModal.html.
         * If ALL the members are already are in the list, display add-error-messages.html instead of modal.
         * @param {Object[]} members - the array of members to check whether they are already in the list being added to
         * @param {string} listName - the list the user is currently being added to
         * @returns {boolean} - true if ALL the members are already in the list
         */
        $scope.existsInList = function (listName, members) {
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
                default:
                    break;
            }

            const membersInList = members.filter((member) =>
                _.some(currentPage, { uhUuid: member }) || _.some(currentPage, { username: member })
            );
            $scope.membersInList = membersInList.join(", ");

            if (_.isEqual(members, membersInList)) {
                $scope.containsInput = true;
            }

            return $scope.containsInput;
        };

        /**
         * Initializes the name of the member to display on modals
         * @param {object} attributes - the user's attributes
         */
        $scope.initMemberDisplayName = function (attributes) {
            $scope.fullName = attributes.name;
            $scope.givenName = attributes.firstName;
            $scope.uhUuid = attributes.uhUuid;
            $scope.uid = attributes.username ?? attributes.uid;
            $scope.inBasis = attributes.inBasis ?? "No";
            $scope.inInclude = attributes.inInclude ?? "No";
            $scope.inExclude = attributes.inExclude ?? "No";

            if ($scope.fullName != null && $scope.fullName.length > 0) {
                $scope.member = $scope.fullName;
            } else if ($scope.fullName != null && $scope.uid.length > 0) {
                $scope.member = $scope.uid;
            } else {
                $scope.member = $scope.uhUuid;
            }
        };

        $scope.closeSuccessfulGroupResetModal = function () {
            $scope.getGroupingInformation();
            $scope.successfulGroupResetModalInstance.close();
        };

        /**
         * Display a modal telling the user that they do not have access to perform this action and that they
         * will be logged out and redirected to the homepage.
         */
        $scope.displayRoleErrorModal = function () {
            $scope.loading = false;
            $scope.RoleErrorModalInstance = $uibModal.open({
                templateUrl: "modal/roleErrorModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false
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
         * Helper - displayRemoveModal, fetchMemberProperties
         * Returns the member object that contains either the provided username or UH number.
         * @param memberIdentifier - The username or UH ID number of the member object to return.
         * @param listName - The name of the list to search.
         */
        $scope.returnMemberObject = function (memberIdentifier, listName) {
            let currentPage;
            switch (listName) {
                case "Basis":
                    currentPage = $scope.groupingBasis;
                    break;
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
         * If a member does not exist, it displays a comma-seperated string of their identifiers and
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
                        "uid": currentMember.username,
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
        $scope.removeMembers = function (listName) {
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
                clearMemberInput(listName);
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
        function handleSuccessfulRemove() {
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
            $scope.dynamicModal.result.finally(function () {
                $scope.loading = true;
                clearMemberInput($scope.listName);
                $scope.getGroupingInformation();
            });
        }

        /**
         * Handler for successfully removing an owner from a grouping.
         */
        function handleOwnerRemove() {
            // Reload the grouping if you are not removing yourself, or if deleting anyone from the admins page
            if (!$scope.membersToRemove.includes($scope.currentUser.uhUuid) || !_.isUndefined($scope.adminsList)) {
                handleSuccessfulRemove();
            } else if ($scope.membersToRemove.includes($scope.currentUser.uhUuid)) {
                // Removing self from last grouping owned -> redirect to home page and then logout
                if ($scope.groupingsList.length === 1) {
                    $scope.proceedLogoutUser();
                } else {
                    $window.location.href = `${$scope.getBasePath()}/groupings`;
                }
            }
        }

        /**
         * Handler for successfully removing an admin from a grouping.
         */
        function handleAdminRemove() {
            $scope.waitingForImportResponse = false;
            // Removing self as admin -> redirect to home page and then logout
            if ($scope.membersToRemove.includes($scope.currentUser.uhUuid)) {
                $scope.proceedLogoutUser();
            } else {
                $scope.init();
            }
        }

        /**
         * Display a modal that prompts the user whether they want to remove the member(s) or not. If 'Yes' is pressed,
         * then a request is made to remove the member(s).
         * @param {object} options - the options object
         * @param {object|object[]} options.membersToRemove - the array of members to remove
         * @param {string} options.listName - the grouping list the member(s) is/are being removed from
         */
        $scope.displayRemoveModal = function (options) {
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
            $scope.isMultiRemove = $scope.multiRemoveResults.length > 1;

            // Open remove or multiRemove modal and set modal red when removing yourself (currentUser) from owners
            const templateUrl = $scope.isMultiRemove ? "modal/multiRemoveModal" : "modal/removeModal";
            $scope.removeModalInstance = $uibModal.open({
                templateUrl,
                backdrop: "static",
                scope: $scope
            });

            // On pressing "Yes/Remove" in the modal, make API call to remove members from the group
            $scope.removeModalInstance.result.then(function () {
                $scope.waitingForImportResponse = true; // Small spinner on
                const groupingPath = $scope.selectedGrouping.path;
                if ($scope.listName === "Include") {
                    groupingsService.removeMembersFromInclude(groupingPath, $scope.membersToRemove, handleSuccessfulRemove, handleUnsuccessfulRequest);
                } else if ($scope.listName === "Exclude") {
                    groupingsService.removeMembersFromExclude(groupingPath, $scope.membersToRemove, handleSuccessfulRemove, handleUnsuccessfulRequest);
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
        $scope.proceedRemoveModal = function () {
            $scope.removeModalInstance.close();
        };

        /**
         * Cancels the remove modal instance
         */
        $scope.cancelRemoveModal = function () {
            $scope.removeModalInstance.dismiss("cancel");
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
        $scope.removeOwnerWithTrashcan = function (currentPage, index) {
            const ownerToRemove = $scope.pagedItemsOwners[currentPage][index];
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


        $scope.displayUnsuccessfulGroupResetModal = function (message) {
            $scope.displayDynamicModal(Message.ResetGroupError.ResetGroupErrorMessages.TITLE, message);
        };
        /**
         * Helper-Guard: handleGroupingReset:
         * If there are error results, render an error modal and return true, otherwise return false.
         */
        $scope.handleGroupingReset = function (groupingPath, resetIncludeResult, resetExcludeResult) {
            let wasError = function (result) {
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
                    let object = Message.ResetGroupError.ResetGroupErrorMessageMap[parseInt(i, 10)];
                    // If the object matches a certain ResetGroupErrorMessageMap object.
                    if (Utility.compareObjects(object, results)) {
                        // Then use the error message in ResetGroupErrorMessages at index i.
                        let message = Message.ResetGroupError.ResetGroupErrorMessages.Body[parseInt(i, 10)];
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

        $scope.displayEmptyGroupModal = function () {
            $scope.emptyGroupModalInstance = $uibModal.open({
                templateUrl: "modal/emptyGroupModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false
            });
        };

        $scope.displaySuccessfulGroupResetModal = function (resultString) {
            $scope.group = resultString;

            $scope.successfulGroupResetModalInstance = $uibModal.open({
                templateUrl: "modal/successfulGroupResetModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false
            });
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
         * Displays a modal stating there was an error removing the user from a group.
         * @param {string} userType - the type of user being removed (either admin or owner)
         */
        $scope.displayRemoveErrorModal = function (userType) {
            $scope.userType = userType;

            $scope.removeErrorModalInstance = $uibModal.open({
                templateUrl: "modal/removeErrorModal",
                scope: $scope,
                backdrop: "static"
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
         * Display a modal with a description of the preference selected.
         * @param {string} desc - the description of the preference
         */
        $scope.displayPreferenceInfoModal = function (desc) {
            $scope.preferenceInfo = desc;

            $scope.infoModalInstance = $uibModal.open({
                templateUrl: "modal/infoModal",
                scope: $scope,
                backdrop: "static"
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
                $scope.displayPreferenceErrorModal();
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
         * Displays a modal that prompts the user whether they want to delete the user or not. If 'Yes' is pressed, then
         * a request is made to delete the user.
         * @param {object} options - the options object
         * @param {String} options.users - the user being removed
         * @param {string} options.group - groups the user is being removed from
         */
        $scope.displayResetGroupModal = function (options) {
            $scope.groupReset = options.group;
            $scope.listNames = options.listNames;

            $scope.resetModalInstance = $uibModal.open({
                templateUrl: "modal/resetModal",
                scope: $scope,
                backdrop: "static"
            });
            $scope.resetModalInstance.result.then($scope.initResetGroup);
        };

        $scope.initResetGroup = async function () {
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
                await resetGroup(groupingsService.resetIncludeGroup, groupingPath, resetIncludeResult);
            }
            if (resetExclude) {
                await resetGroup(groupingsService.resetExcludeGroup, groupingPath, resetExcludeResult);
            }
            $scope.loading = false;
            $scope.handleGroupingReset(groupingPath, resetIncludeResult.pop(), resetExcludeResult.pop());
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

            $scope.displayResetGroupModal({
                group: $scope.selectedGrouping.name,
                listNames
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
         * Helper - displaySyncDestModal
         * Toggle the grouping sync destinations according to a given syncDest
         * @param {String} syncDestName Name of the Sync Dest to toggle
         */
        $scope.updateSingleSyncDest = function (syncDestName) {
            const groupingPath = $scope.selectedGrouping.path;
            const syncDestOn = $scope.getSyncDestValueInArray(syncDestName);
            groupingsService.setSyncDest(groupingPath, syncDestName, syncDestOn, handleSuccessfulPreferenceToggle, handleUnsuccessfulRequest);
        };

        /**
         * Display a modal indicating an error in saving the grouping's preferences.
         */
        $scope.displayPreferenceErrorModal = function () {
            $scope.preferenceErrorModalInstance = $uibModal.open({
                templateUrl: "modal/preferenceErrorModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false
            });
        };

        /**
         * Close the preference error modal.
         */
        $scope.closePreferenceError = function () {
            $scope.preferenceErrorModalInstance.close();
        };

        $scope.reloadGrouping = function () {
            if ($scope.paginatingComplete) {
                $scope.getGroupingInformation();
            }
        };

        $scope.resetFields = function () {
            $scope.manageMembers = "";
            $scope.membersInCheckboxList = {};
            $scope.allSelected = false;
            $scope.waitingForImportResponse = false;
        };

        $scope.resetErrors = function () {
            $scope.resStatus = 0;
            $scope.emptyInput = false;
            $scope.emptySelect = false;
            $scope.containsInput = false;
            $scope.inGrouper = false;
            $scope.addInputError = false;
            $scope.removeInputError = false;
        };

        /**
         * Adds people to listName (to be used in on-click)
         * @param {String} listName grouping list (i.e. include, exclude, or owners)
         */
        $scope.addOnClick = function (listName) {
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
         * Display sync destination confirmation modal.
         * @param {String} syncDestName Name of the Sync Dest to display modal for
         */
        $scope.displaySyncDestModal = function (syncDestName) {
            const isSyncDestOn = $scope.getSyncDestValueInArray(syncDestName);
            $scope.setSyncDestInArray(syncDestName, !isSyncDestOn);
            $scope.selectedSyncDest = $scope.getEntireSyncDestInArray(syncDestName);

            $scope.syncDestInstance = $uibModal.open({
                templateUrl: "modal/syncDestModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false
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
         * Display owner error modal when a grouping owner
         * is removed while still trying to access grouping
         * owner actions.
         */
        $scope.displayOwnerErrorModal = function () {
            $scope.loading = false;
            $scope.OwnerErrorModalInstance = $uibModal.open({
                templateUrl: "modal/ownerErrorModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false
            });
        };

        /**
         * Export data in a grouping to a CSV file
         * @param {object[]} group - the group array to export ($scope.groupingMembers/Basis/Include/Exclude)
         * @param {String} listName - the name of the list
         */
        $scope.exportGroupToCsv = function (group, listName) {
            let data, filename, link, csv;

            csv = $scope.convertListToCsv(group);
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
            return ($scope.membersToRemove.includes($scope.currentUser.uid)
                    || $scope.membersToRemove.includes($scope.currentUser.uhUuid))
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
         * Log out the user and redirect them to the home page
         */
        $scope.proceedLogoutUser = () => {
            const basePath = $scope.getBasePath();
            $window.location.href = basePath;
            let r = new XMLHttpRequest();
            r.open("POST", `${basePath}/logout`, true);
            r.setRequestHeader("X-XSRF-TOKEN", $scope.getCookie("XSRF-TOKEN"));
            r.send();
        };

        /**
         * Redirect the user to the feedback page.
         */
        $scope.proceedRedirectFeedback = () => {
            $window.location.href = `${$scope.getBasePath()}/feedback`;
        };

        /**
         * Redirect the user to the home page.
         */
        $scope.proceedRedirect = () => {
            $window.location.href = $scope.getBasePath();
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

        /**
         * Returns the correct basePath (/uhgroupings or /its/uhgroupings)
         */
        $scope.getBasePath = () => {
            const pathSegments = $window.location.pathname.split("/");

            if (pathSegments.includes("its")) {
                return "/its/uhgroupings";
            }
            return "/uhgroupings";
        };
    }


    UHGroupingsApp.controller("GeneralJsController", GeneralJsController);
}());
