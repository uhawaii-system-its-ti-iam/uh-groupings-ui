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

        $scope.userToAdd = "";
        $scope.usersToAdd = [];
        $scope.multiAddThreshold = 100;
        $scope.maxImport = 100000;
        $scope.multiAddResults = [];
        $scope.multiAddResultsGeneric = [];
        $scope.personProps = [];
        $scope.waitingForImportResponse = false;

        $scope.itemsAlreadyInList = [];
        $scope.itemsInOtherList = [];

        $scope.currentUser = $window.document.getElementById("name").innerHTML;

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

        $scope.modalType = "";

        $scope.groupingCSV = [];
        $scope.groupNameCSV = [];

        let CURRENT_PAGE = 0;


        // used with ng-view on selected-grouping.html to toggle description editing.
        $scope.descriptionForm = false;

        //The max length usable when getting input
        $scope.maxDescriptionLength = 100;

        //The user input
        $scope.modelDescription = "";

        //Variable for holding description
        let groupingDescription = "";

        let displayTracker = 1;

        //Flag used for getGroupingInformation function to end async call
        let loadMembersList = false;
        //Keeps track of async calls made throughout this js controller
        let asyncThreadCount = 0;

        let maxLength = 100;
        let noDescriptionMessage = "No description given for this Grouping.";

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
            return _.uniqBy(members, "uhUuid");
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
            // Unique members only by UUID (assume no two users should have the same uuid)
            return _.uniqBy(_.concat(initialMembers, membersToAdd), "uhUuid");
        };

        /**
         * @returns {String[]} list of possible sync destinations
         */
        $scope.getAllSyncDestinations = function () {
            const groupingPath = $scope.selectedGrouping.path;
            groupingsService.getSyncDestList(groupingPath, function (res) {
                // console.log("This is the response of sync dest" + res);
                $scope.syncDestMap = res;
                // console.log("Mapping:"+ $scope.syncDestMap);
            }, function (res) {
                if (res.statusCode === 403) {
                    $scope.createOwnerErrorModal();
                }
            });
        };

        function launchOwnerErrorModal() {
            return (res) => {
                if (res.statusCode === 403) {
                    $scope.createOwnerErrorModal();
                }
            };
        }

        function getGroupingOnSuccess(res) {
            $scope.loading = false;
            $scope.groupingBasis = combineGroupMembers($scope.groupingBasis, res.basis.members);
            $scope.filter($scope.groupingBasis, "pagedItemsBasis", "currentPageBasis", $scope.basisQuery, true);

            //Gets members in the include group
            $scope.groupingInclude = combineGroupMembers($scope.groupingInclude, res.include.members);
            $scope.addInBasis($scope.groupingInclude);
            $scope.filter($scope.groupingInclude, "pagedItemsInclude", "currentPageInclude", $scope.includeQuery, true);

            //Gets members in the exclude group
            $scope.groupingExclude = combineGroupMembers($scope.groupingExclude, res.exclude.members);
            $scope.addInBasis($scope.groupingExclude);
            $scope.filter($scope.groupingExclude, "pagedItemsExclude", "currentPageExclude", $scope.excludeQuery, true);

            //Gets members in grouping
            let size = $scope.groupingMembers.length;
            $scope.groupingMembers = combineGroupMembers($scope.groupingMembers, res.composite.members);
            $scope.addWhereListed($scope.groupingMembers);
            $scope.filter($scope.groupingMembers, "pagedItemsMembers", "currentPageMembers", $scope.membersQuery, true);

            //Gets owners of the grouping
            $scope.groupingOwners = combineGroupMembers($scope.groupingOwners, res.owners.members);
            $scope.pagedItemsOwners = $scope.groupToPages($scope.groupingOwners);
            $scope.filter($scope.groupingOwners, "pagedItemsOwners", "currentPageMembers", $scope.ownersQuery, true);

            // Gets the description go the group

            //increments page to load and allows members to iteratively be loaded
            loadMembersList = true;
            const path = $scope.selectedGrouping.path;
            if (size < $scope.groupingMembers.length) {
                CURRENT_PAGE++;
                $scope.paginatingProgress = true;
                groupingsService.getGrouping(
                    path,
                    CURRENT_PAGE,
                    PAGE_SIZE,
                    "name",
                    true,
                    getGroupingOnSuccess,
                    launchOwnerErrorModal
                );
            } else {
                $scope.paginatingProgress = false;
                $scope.paginatingComplete = true;
            }
        }

        $scope.getGroupingInformation = () => {
            $scope.loading = true;
            $scope.paginatingComplete = false;
            const path = $scope.selectedGrouping.path;
            groupingsService.getGroupingMetaData(path, (res) => {
                    $scope.loading = false;
                    $scope.allowOptIn = res.optInOn;
                    $scope.allowOptOut = res.optOutOn;
                    $scope.syncDestArray = res.syncDestinations;
                    CURRENT_PAGE++;
                    if (res.description === null) {
                        groupingDescription = "";
                    } else {
                        groupingDescription = res.description;
                        displayTracker = 1;
                    }
                },
                launchOwnerErrorModal
            );
            CURRENT_PAGE++;
            groupingsService.getGrouping(path, CURRENT_PAGE, PAGE_SIZE, "name", true,
                getGroupingOnSuccess, launchOwnerErrorModal);
        };
        /**
         * Gets information about the grouping, such as its members and the preferences set.
         * Retrieves information asynchronously page by page
         */
        $scope.getGroupingInformationi = function () {
            //creates loading screen
            $scope.loading = true;
            $scope.paginatingComplete = false;

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
                 * @param {Integer} currentPage - Page of grouping to retrieve (increments after each async/await call)
                 * @param {Integer} PAGE_SIZE - Size of page to retrieve (Located in app.constants)
                 * @param {String} sortString - Parameter to sort the grouping database by before retrieving
                 *     information
                 * @param {Boolean} isAscending - If true, grouping database is sorted ascending (A-Z), false for
                 *     descending (Z-A)
                 */
                groupingsService.getGrouping(groupingPath, currentPage, PAGE_SIZE, "name", true, async function (res) {

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

                    // Gets the description go the group
                    if (res.description === null) {
                        groupingDescription = "";
                    } else {
                        groupingDescription = res.description;
                        displayTracker = 1;
                    }

                    $scope.allowOptIn = res.optInOn;
                    $scope.allowOptOut = res.optOutOn;

                    $scope.syncDestArray = res.syncDestinations;

                    //Stop loading spinner and turn on loading text
                    $scope.loading = false;
                    $scope.paginatingProgress = true;

                    //increments page to load and allows members to iteratively be loaded
                    currentPage++;
                    loadMembersList = true;

                    //While true loop that calls members asynchronously from Grouper based on PAGE_SIZE
                    while (loadMembersList) {
                        /**
                         *Calls getPages function to load the rest of the members,
                         * but waits till the page before is retrieved before getting another page
                         */
                        try {
                            await $scope.getPages(groupingPath, currentPage, PAGE_SIZE, "name", true);

                            //Catches in both fetch and response
                        } catch (error) {
                            console.log("Getting members from grouping has errored out please reload page to resume. If not please proceed to the feedback page and report the problem you have come across.");
                        }
                        currentPage++;
                    }
                }, function (res) {
                    if (res.statusCode === 403) {
                        $scope.createOwnerErrorModal();
                    }
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
         * @param {Integer} page - Page of grouping to retrieve (Paging starts from 1)
         * @param {Integer} size - Size of page to retrieve
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
                    if (res === null) {
                        $scope.largeGrouping = true;
                        $scope.paginatingComplete = false;
                        $scope.paginatingProgress = false;
                    } else if (res.statusCode === 403) {
                        $scope.createOwnerErrorModal();
                    } else {
                        dataProvider.handleException({ exceptionMessage: JSON.stringify(res, null, 4) }, "feedback/error", "feedback");
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
            return (String($scope.modelDescription).length > maxLength);
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
            $scope.modelDescription = groupingDescription;

            if ($scope.descriptionForm) {
                $scope.descriptionForm = !($scope.descriptionForm);
            }

        };

        /**
         * Used for placeholder text for a grouping's description in the form box.
         * @returns {string} either the description of the grouping, or, placeholder text if the description is empty.
         */
        $scope.descriptionDisplay = function () {
            if ($scope.showGrouping === true && displayTracker === 1) {
                $scope.modelDescription = groupingDescription;
                displayTracker = 0;
            }

            return (groupingDescription.length > 0)
                ? groupingDescription
                : noDescriptionMessage;
        };

        /**
         * Sets a new description for a Grouping.
         * TODOS:   --> make this function call RestController to change the description in Grouper.
         *          --> error checking?
         */
        $scope.saveDescription = function () {
            if (groupingDescription.localeCompare($scope.modelDescription) !== 0) {
                groupingDescription = $scope.modelDescription;

                groupingsService.updateDescription($scope.selectedGrouping.path,
                    function () {
                        // This is currently empty due to not needing to do anything with a successful response
                    },
                    function (res) {
                        if (res.status === 403) {
                            $scope.createOwnerErrorModal();
                        }
                    },
                    groupingDescription);
                $scope.descriptionForm = !($scope.descriptionForm);
            } else {
                $scope.cancelDescriptionEdit();
            }

        };

        /**
         * Creates a modal for errors in loading data from the API.
         */
        $scope.createApiErrorModal = function () {
            $scope.apiErrorModalInstance = $uibModal.open({
                templateUrl: "modal/apiError",
                scope: $scope,
                backdrop: "static",
                keyboard: false
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
                const memberUhUuid = member.uhUuid;
                member.inBasis = _.some($scope.groupingBasis, { uhUuid: memberUhUuid })
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
         * Launch a modal containing a browse local file system for import button.
         * @param listName - Current list
         */
        $scope.launchImportModal = function (listName) {
            $scope.listName = listName;

            $scope.confirmImportInstance = $uibModal.open({
                templateUrl: "modal/importModal",
                size: "lg",
                scope: $scope
            });
        };

        /**
         * Take $scope.usersToAdd count the number of words it contains and split it into a comma separated string, then
         * decide whether to a multi add or a single add is necessary.
         * @param listName
         */
        $scope.addMembers = function (listName) {
            $scope.listName = listName;
            let numMembers = ($scope.usersToAdd.split(" ").length - 1);
            if (numMembers > 0) {
                let users = $scope.usersToAdd.split(/[ ,]+/).join(",");

                $scope.usersToAdd = [];
                if (numMembers > $scope.maxImport) {
                    launchCreateGenericOkModal(
                        "Out of Bounds Import Warning",
                        `Importing more than ${$scope.maxImport} users is not allowed.`,
                        8000);
                } else {
                    if (numMembers > $scope.multiAddThreshold) {
                        launchCreateGenericOkModal(
                            "Large Import Warning",
                            `You are attempting to import ${numMembers} new users to the ${listName} list.
                             Imports larger than ${$scope.multiAddThreshold} can take a few minutes.  An email with 
                             the import results will be sent.`,
                            8000);
                    }
                    $scope.addMultipleMembers(users, listName);
                }
            } else {
                $scope.userToAdd = $scope.usersToAdd;
                $scope.addMember(listName);
            }
        };

        /**
         * Read a text file(.txt) from client side. The file should consist of a list of UH user names or ids
         * separated by newline characters.
         * @param $event - FileReader event sent from Include.html or Exclude.html
         */
        $scope.readTextFile = function ($event) {
            let input = $event.currentTarget.parentNode.childNodes[1];
            let file = input.files[0];
            let reader = new FileReader();
            reader.onload = function (e) {
                let str = e.target.result;
                $scope.usersToAdd = (str.split(/[\n]+/).join(" ")).slice(0, -1);
                $scope.addMembers($scope.listName);
            };
            reader.readAsText(file);
        };

        /**
         * Send the list of users to be added to the server as an HTTP POST request.
         * @param list - comma separated string of user names to be added
         * @param listName - current list being added to
         * @returns {Promise<void>}
         */
        $scope.addMultipleMembers = async function (list, listName) {
            let groupingPath = $scope.selectedGrouping.path;
            $scope.removeMultipleUsers(list);

            /* Callback: Return a modal which is launched after n seconds, see updateDataWithTimeoutModal() in app.service.js */
            let timeoutModal = function () {
                return launchCreateGenericOkModal(
                    "Slow Import Warning",
                    `This import could take awhile to complete. The process however does not require the browser 
                    to be open in order to finish.`,
                    8000);
            };

            /* Callback: Receive the HTTP response from the server, use console.log(res) to print response */
            let handleSuccessfulAdd = function (res) {
                $scope.waitingForImportResponse = false; /* Spinner off */
                for (let i = 0; i < res.length; i++) {
                    $scope.multiAddResults[i] = res[i].person;
                    $scope.multiAddResultsGeneric[i] = res[i].person;
                }
                if (undefined !== res[0].person) {
                    $scope.personProps = Object.keys(res[0].person);
                    $scope.personProps.shift();
                }
                $scope.launchMultiAddResultModal(listName);
            };
            $scope.waitingForImportResponse = true; /* Spinner on */
            await groupingsService["addMembersTo" + listName](
                groupingPath,
                list,
                handleSuccessfulAdd,
                handleUnsuccessfulRequest,
                timeoutModal);
        };

        /**
         * Launch a modal containing a table of the results(user info) received from the the server's response message.
         * @param listName - current list being added to
         */
        $scope.launchMultiAddResultModal = function (listName) {
            $scope.multiAddResultModalInstance = $uibModal.open({
                templateUrl: "modal/multiAddResultModal",
                scope: $scope
            });
            $scope.loading = false;
            $scope.multiAddResultModalInstance.result.finally(function () {
                clearAddMemberInput(listName);
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
         * Close the import modal instance then launch an error modal.
         */
        $scope.launchImportErrorModal = function () {
            $scope.cancelImportModalInstance();
            $scope.confirmImportErrorInstance = $uibModal.open({
                templateUrl: "modal/importErrorModal",
                scope: $scope
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
            clearAddMemberInput($scope.listName);
            $scope.multiAddResultModalInstance.dismiss();
        };
        /**
         * Cancel the import Modal instance
         */
        $scope.cancelImportModalInstance = function () {
            clearAddMemberInput($scope.listName);
            $scope.confirmImportInstance.dismiss();
        };

        /**
         * Close import modal instance when user confirms that they would like to add the list they imported
         */
        $scope.proceedAddMembers = function () {
            $scope.confirmImportInstance.close();
        };

        /**
         * Launch a modal with a title, body message, and an ok button which closes the modal.
         * @param title - message title to be displayed in modal header
         * @param body - message body to be displayed in modal body
         * @param timeTillClose - Millisecond till modal is modal is automatically closed.
         */
        function launchCreateGenericOkModal(title, body, timeTillClose) {
            $scope.currentModalTitle = title;
            $scope.currentModalBody = body;

            $scope.createGenericOkModal = $uibModal.open({
                templateUrl: "modal/genericOkModal",
                scope: $scope
            });

            if (undefined !== timeTillClose) {
                let closeOnTimeout = function () {
                    $scope.createGenericOkModal.dismiss();
                };
                setTimeout(closeOnTimeout, timeTillClose);
            }
        }

        /**
         * Remove Items from the pendingList Array
         * @param pendingList
         * @param itemsToRemove
         * @return {[]}
         */
        $scope.removeItemsFromArray = function (pendingList, itemsToRemove) {
            for (let item of itemsToRemove)
                pendingList = _.without(pendingList, _.forEach(item));
            return pendingList;
        };

        /**
         * Adds a user to a group.
         * @param {string} list - the list the user is being added to (either Include or Exclude)
         */
        $scope.addMember = function (list) {
            let user = $scope.userToAdd;
            let inBasis = _.some($scope.groupingBasis, { username: user });
            if (_.isEmpty(user)) {
                $scope.createAddErrorModal(user);
            } else if ($scope.existInList(user, list)) {
                $scope.createCheckModal(user, list, false, inBasis);
            } else if ($scope.isInAnotherList(user, list)) {
                $scope.createCheckModal(user, list, true, inBasis);
            } else if ((inBasis && list === "Include") || (!inBasis && list === "Exclude")) {
                $scope.createBasisWarningModal(user, list, inBasis);
            } else {
                $scope.createConfirmAddModal({
                    userToAdd: user,
                    listName: list
                });
            }
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
        $scope.createCheckModal = function (user, listName, swap, inBasis) {
            $scope.user = user;
            $scope.listName = listName;
            $scope.swap = swap;
            $scope.inBasis = inBasis;

            $scope.checkModalInstance = $uibModal.open({
                templateUrl: "modal/checkModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false
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
                scope: $scope,
                backdrop: "static",
                keyboard: false
            });
            $scope.confirmAddModalInstance.result.then(function () {
                $scope.updateAddMembers(options.usersToAdd, options.listName);
            });
        };

        /**
         * Creates a modal that asks for confirmation when adding a user.
         * @param {object} options - the options object
         * @param {string} options.userToAdd - the user to add
         * @param {string} options.listName - name of the list being added to
         */
        $scope.createConfirmAddModal = function (options) {
            const userToAdd = options.userToAdd;

            groupingsService.getMemberAttributes(userToAdd, function (attributes) {
                $scope.fullNameToAdd = attributes.cn;
                $scope.givenNameToAdd = attributes.givenName;
                $scope.uhUuidToAdd = attributes.uhUuid;
                $scope.uidToAdd = attributes.uid;

                $scope.listName = options.listName;

                // Ask for confirmation from the user to add the member
                $scope.confirmAddModalInstance = $uibModal.open({
                    templateUrl: "modal/confirmAddModal",
                    scope: $scope,
                    backdrop: "static",
                    keyboard: false
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
                scope: $scope,
                backdrop: "static",
                keyboard: false
            });

            $scope.addModalInstance.result.finally(function () {
                clearAddMemberInput(options.listName);
                $scope.loading = true;
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
         * Closes the add user modal.
         */
        $scope.closeSuccessfulAddModal = function () {
            $scope.addModalInstance.close();
        };

        $scope.createAddErrorModal = function (userAdded) {
            $scope.user = userAdded;

            $scope.addErrorModalInstance = $uibModal.open({
                templateUrl: "modal/addErrorModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false
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
                scope: $scope,
                backdrop: "static",
                keyboard: false
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
            $scope.modalType = "remove";
            if (listName === "Include") {
                userToRemove = $scope.pagedItemsInclude[currentPage][index];
            } else if (listName === "Exclude") {
                userToRemove = $scope.pagedItemsExclude[currentPage][index];
            }

            $scope.createRemoveModal({
                user: userToRemove,
                listName: listName,
                scope: $scope
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
            $scope.syncDestArray = [];
        }

        /**
         * Handler for successfully removing an owner from a grouping.
         */
        function handleOwnerRemove() {
            // Reload the grouping if you are not removing yourself, or if deleting anyone from the admins page
            if ($scope.currentUser !== $scope.userToRemove.username || !_.isUndefined($scope.adminsList)) {
                $scope.getGroupingInformation();
                $scope.syncDestArray = [];
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
                scope: $scope,
                backdrop: "static",
                keyboard: false
            });

            $scope.removeModalInstance.result.then(function () {
                $scope.loading = true;
                let userToRemove = options.user.username;
                let groupingPath = $scope.selectedGrouping.path;

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
                scope: $scope,
                backdrop: "static",
                keyboard: false
            });
        };

        /**
         * Closes the remove error modal.
         */
        $scope.closeRemoveErrorModal = function () {
            $scope.removeErrorModalInstance.close();
        };

        $scope.removeMultipleUsers = (list) => {

            groupingsService.removeMembersFromInclude($scope.selectedGrouping.path, list,
                (res) => {
                    //console.log(res);
                }, (res) => {
                    // console.log(res);
                });
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
            $scope.adminsQuery = "";
            $scope.optInQuery = "";
            $scope.ownersQuery = "";
        }

        /**
         * Clears the user input for adding a member to a list.
         * @param {string?} listName - the name of the list the member is being added to
         */
        function clearAddMemberInput(listName) {
            switch (listName) {
                case "Include":
                    $scope.userToAdd = "";
                    $scope.usersToAdd = "";
                    $scope.userNameList = [];
                    $scope.multiAddResults = [];
                    $scope.waitingForImportResponse = false;
                    $scope.personProps = [];
                    break;
                case "Exclude":
                    $scope.userToAdd = "";
                    $scope.usersToAdd = "";
                    $scope.userNameList = [];
                    $scope.multiAddResults = [];
                    $scope.waitingForImportResponse = false;
                    $scope.personProps = [];
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

            $scope.showGrouping = false;
            loadMembersList = false;

            $scope.modelDescription = "";
            groupingDescription = "";
            displayTracker = 1;
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
            $scope.syncDestArray = [];

        };


        /**
         * Creates a modal with a description of the preference selected.
         * @param {string} desc - the description of the preference
         */
        $scope.createPreferenceInfoModal = function (desc) {
            $scope.preferenceInfo = desc;

            $scope.infoModalInstance = $uibModal.open({
                templateUrl: "modal/infoModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false
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
            } else if (_.startsWith(res.resultCode, "SUCCESS")) {
                console.log("Success");
            }
        };

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
         * Gets the SyncDest value from the array given the name of the sync dest
         * @param {String} syncDestName Name of the Sync Dest to retrieve
         * @return {Boolean} Sync Dest value at the given name
         */
        $scope.getSyncDestValueInArray = function (syncDestName) {
            const indexOfSyncDest = $scope.syncDestArray.map((e) => {
                return e.name;
            }).indexOf(syncDestName);
            const syncDestOn = $scope.syncDestArray[indexOfSyncDest].isSynced;
            return syncDestOn;
        };

        /**
         * Gets the entire syncDest object given its name
         * @param {String} syncDestName Name of the Sync Dest to retrieve
         * @return {Object} The entire syncDest object with the given name
         */
        $scope.getEntireSyncDestInArray = function (syncDestName) {
            const indexOfSyncDest = $scope.syncDestArray.map((e) => {
                return e.name;
            }).indexOf(syncDestName);
            return $scope.syncDestArray[indexOfSyncDest];
        };

        /**
         * Sets a given sync dest to a given value
         * @param {String} syncDestName Name of the Sync Dest to set
         * @param {Boolean} syncDestvalue The value to set the Sync Dest to
         */
        $scope.setSyncDestInArray = function (syncDestName, syncDestvalue) {
            const indexOfSyncDest = $scope.syncDestArray.map((e) => {
                return e.name;
            }).indexOf(syncDestName);
            $scope.syncDestArray[indexOfSyncDest].isSynced = syncDestvalue;
        };

        /**
         * Toggles the grouping sync destinations according to a given syncDest
         * @param {String} syncDestName Name of the Sync Dest to toggle
         */
        $scope.updateSingleSyncDest = function (syncDestName) {
            const groupingPath = $scope.selectedGrouping.path;
            const syncDestOn = $scope.getSyncDestValueInArray(syncDestName);

            groupingsService.setSyncDest(groupingPath, syncDestName, syncDestOn, handleSuccessfulPreferenceToggle, handleUnsuccessfulRequest);
        };

        /**
         * Creates a modal indicating an error in saving the grouping's preferences.
         */
        $scope.createPreferenceErrorModal = function () {
            $scope.preferenceErrorModalInstance = $uibModal.open({
                templateUrl: "modal/preferenceErrorModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false
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
         * Create sync destination confirmation modal.
         * @param {String} syncDestName Name of the Sync Dest to create modal for
         */
        $scope.createSyncDestModal = function (syncDestName) {

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
         * Creates warning modal if user is in being added to include and is a basis member or if
         * a user is being added to exclude and is not a basis member
         * @param user - user being added
         * @param listName - grouping list (i.e. include or exclude)
         * @param inBasis - boolean if user is in basis or not
         */
        $scope.createBasisWarningModal = function (user, listName, inBasis) {
            $scope.user = user;
            $scope.listName = listName;
            $scope.inBasis = inBasis;

            $scope.basisWarningModalInstance = $uibModal.open({
                templateUrl: "modal/basisWarningModal",
                scope: $scope
            });

            $scope.basisWarningModalInstance.result.then(function () {
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
         * Proceeds with the syncDest confirmation
         */
        $scope.proceedSyncDestModal = function () {
            $scope.syncDestInstance.close();
        };

        /**
         * Closes the syncDest confirmation modal
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
                templateUrl: "modal/ownerErrorModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false
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
         * @returns string table in CSV format
         */
        $scope.convertListToCsv = function (table) {
            let str = "Last,First,Username,uhNumber,Email\r\n";
            for (let i = 0; i < table.length; i++) {
                let line = "";
                line += table[i].lastName + ",";
                line += table[i].firstName + ",";
                line += table[i].username + ",";
                line += table[i].uhUuid + ",";
                line += table[i].username + "@hawaii.edu,";
                str += line + "\r\n";
            }
            return str;
        };

        /**
         * Exports generic data in a table to a CSV file
         * @param {object[]} table - the table to export
         * @param grouping - grouping name that you are exporting from
         * @param list - grouping list (i.e. include or exclude)
         */
        $scope.exportGroupToCsvGeneric = function (table, grouping, list) {

            table = $scope.multiAddResultsGeneric;
            let data, filename, link;

            let csv = $scope.convertListToCsvGeneric(table);
            if (csv === null) {
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
         * Converts the generic data in the table into comma-separated values.
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
                while (c.charAt(0) === " ") {
                    c = c.substring(1);
                }
                if (c.indexOf(name) === 0)
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

    UHGroupingsApp.controller("GeneralJsController", GeneralJsController);
}());
//})();
