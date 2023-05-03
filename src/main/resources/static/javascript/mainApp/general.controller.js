/* global _, angular, UHGroupingsApp */

(() => {

    /**
     * This controller contains shared functions between the all pages.
     * @param $scope - binding between controller and HTML page
     * @param $controller - service for instantiating controllers
     * @param $window - the browser window object
     * @param $uibModal - the UI Bootstrap service for creating modals
     * @param dataProvider - service that handles redirection to the feedback page upon error
     * @param groupingsService - service for creating requests to the groupings API
     */

    function GeneralJsController($scope, $controller, $window, $uibModal, groupingsService, dataProvider) {
        // This is a regex Pattern that contains all valid UH Identifiers which consists of uid (Username) and uhUuid (UH Numbers) chars.
        $scope.uhIdentifierPattern = new RegExp("^[_?a-z-?@?0-9]{3,64}$");

        $scope.currentUser = {};

        $scope.group = "";
        $scope.resStatus = 0;
        $scope.showAdminTab = true;

        //Retrieve table column settings from local storage or default value if local storage is empty
        $scope.columnDisplaySetting = localStorage.getItem("columnDisplaySetting") ?? "description";
        $scope.showDescriptionColumn = JSON.parse(localStorage.getItem("showDescriptionColumn") ?? true);
        $scope.showGroupingPathColumn = JSON.parse(localStorage.getItem("showPathColumn") ?? false);

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

        $scope.init = function () {
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
        };

        /**
         * Shows/hides admin tabs: Manage Groupings, Manage Admins, Manage Person
         */
        $scope.toggleShowAdminTab = () => {
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
                    $scope.displayOwnerErrorModal();
                }
            });
        };
        $scope.fetchGrouping = function (currentPage, groupPaths) {
            return new Promise((resolve) => {
                groupingsService.getMyGrouping(groupPaths, currentPage, PAGE_SIZE, "name", true, function (res) {
                    console.log(res);
                    if (res.basis || res.include || res.exclude || res.owners) {
                        let putGroupMembers = combineGroupMembers;
                        if (currentPage === 1) {
                            putGroupMembers = setGroupMembers;
                        }
                        $scope.groupingBasis = putGroupMembers(res.basisMembers.groupMembers, $scope.groupingBasis);
                        $scope.filter($scope.groupingBasis, "pagedItemsBasis", "currentPageBasis", $scope.basisQuery, false);

                        $scope.groupingInclude = putGroupMembers(res.includeMembers.groupMembers, $scope.groupingInclude);
                        $scope.filter($scope.groupingInclude, "pagedItemsInclude", "currentPageInclude", $scope.includeQuery, false);

                        $scope.groupingExclude = putGroupMembers(res.excludeMembers.groupMembers, $scope.groupingExclude);
                        $scope.filter($scope.groupingExclude, "pagedItemsExclude", "currentPageExclude", $scope.excludeQuery, false);

                        $scope.groupingOwners = putGroupMembers(res.ownersMembers.groupMembers, $scope.groupingOwners);
                        $scope.filter($scope.groupingOwners, "pagedItemsOwners", "currentPageOwners", $scope.ownersQuery, false);

                        $scope.groupingMembers = putGroupMembers(res.groupingMembers.groupingMembers, $scope.groupingMembers);
                        $scope.filter($scope.groupingMembers, "pagedItemsMembers", "currentPageMembers", $scope.membersQuery, false);


                        $scope.groupingDescription = "";
                        $scope.descriptionLoaded = true;
                        $scope.allowOptIn = true;
                        $scope.allowOptOut = true;
                        $scope.syncDestArray = [];
                    } else {
                        $scope.paginatingComplete = true;
                        $scope.paginatingProgress = false;
                    }
                    resolve();

                }, (res) => {
                    $scope.paginatingComplete = true;
                    $scope.resStatus = res.status;
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
            let groupPaths = [groupingPath + ":basis", groupingPath + ":include", groupingPath + ":exclude", groupingPath + ":owners"];
            let currentPage = 1;
            $scope.loading = true;
            $scope.paginatingComplete = false;
            while (!($scope.paginatingComplete)) {
                $scope.paginatingProgress = true;
                await $scope.fetchGrouping(currentPage, groupPaths);
                currentPage++;
                $scope.loading = false;
            }
        };

        /**
         * Gets information about the grouping, such as its members and the preferences set.
         * Retrieves information asynchronously page by page
         */
        $scope.getMyGroupingInformation = function () {

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
            if (!$scope.paginatingComplete) {
                return Message.Csv.GROUP_NOT_LOADED;
            }
            if ($scope.groupingMembers.length === 0) {
                return Message.Csv.GROUP_EMPTY;
            }
            return "";
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
        $scope.displayApiErrorModal = () => {
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
        $scope.closeApiError = () => {
            $scope.apiErrorModalInstance.close();
        };

        /**
         * Checks that a users name matches the pattern of either a valid uid or a uhUuid.
         * @param {Object[]|string} names - An array of users or string of a single user to validate.
         * @returns {Object[]|string} - An array of valid users or valid string of a user .
         */
        $scope.sanitizer = (names) => {
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
         * Set a style attribute of a html component associated with id.
         * @param {string} id - id of html component
         * @param {string} attribute - the css attribute
         * @param {string} setAs - set attribute to
         */
        $scope.changeStyleAttribute = (id, attribute, setAs) => {
            document.getElementById(id).style[`${attribute}`] = setAs;
        };

        /**
         * Display a modal with a title, and body message. The modal will close in the case of pressing the ok button
         * and will cancel if the timeTillClose is set and time runs out. The modal will not timeout unless the
         * timeTillClose is set.
         */
        $scope.displayDynamicModal = (title, body, timeTillClose) => {
            $scope.currentModalTitle = title;
            $scope.currentModalBody = body;

            $scope.dynamicModal = $uibModal.open({
                templateUrl: "modal/dynamicModal",
                scope: $scope
            });
            if (!_.isUndefined(timeTillClose)) {
                let closeOnTimeout = () => {
                    $scope.dynamicModal.dismiss();
                };
                setTimeout(closeOnTimeout, timeTillClose);
            }
            $scope.closeDynamicModal = () => {
                $scope.dynamicModal.close();
            };
        };

        /**
         * Display a modal telling the user that they do not have access to perform this action and that they
         * will be logged out and redirected to the homepage.
         */
        $scope.displayRoleErrorModal = () => {
            $scope.loading = false;
            $scope.RoleErrorModalInstance = $uibModal.open({
                templateUrl: "modal/roleErrorModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false
            });
        };

        $scope.resetErrors = () => {
            $scope.resStatus = 0;
            $scope.emptyInput = false;
            $scope.emptySelect = false;
            $scope.containsInput = false;
            $scope.invalidInput = false;
            $scope.addInputError = false;
            $scope.removeInputError = false;
        };

        /**
         * Removes error message and resets errors
         */
        $scope.dismissErrors = () => {
            $scope.errorDismissed = true;
            $scope.resetErrors();
        };

        /**
         * Gets cookie information
         * @param cname = name of cookie you want to look for.
         * @returns {*}
         */
        $scope.getCookie = (cname) => {
            let name = cname + "=";
            let decodedCookie = decodeURIComponent(document.cookie);
            let ca = decodedCookie.split(";");
            for (let c of ca) {
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
         * Shows column when radio button is selected in table column settings dropdown then
         * saves it to local storage.
         * @param column {string} - the column to show
         */
        $scope.showColumn = (column) => {
            $scope.columnDisplaySetting = column;
            $scope.showDescriptionColumn = (column === "description") || (column === "all");
            $scope.showGroupingPathColumn = (column === "groupingPath") || (column === "all");

            localStorage.setItem("columnDisplaySetting", $scope.columnDisplaySetting);
            localStorage.setItem("showDescriptionColumn", JSON.stringify($scope.showDescriptionColumn));
            localStorage.setItem("showPathColumn", JSON.stringify($scope.showGroupingPathColumn));
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

        /**
         * Used with ng-keydown directive in HTML to click using the enter key.
         * @param event - pass in $event in HTML
         */
        $scope.clickWithEnter = (event) => {
            if (event.keyCode === 13) {
                event.target.click();
                event.preventDefault();
            }
        };

        /**
         * Copy the path of a grouping through the clipboard button
         * @param {Object} grouping - the current selected grouping
         */
        $scope.copyPath = (grouping) => {
            $("[data-content='copy']").popover("hide");

            $("[data-content='copied!']").popover();
            setTimeout(() => {
                $("[data-content='copied!']").popover("hide");
            }, 1000);

            let copyText = document.getElementById(grouping.path);
            copyText.select();
            document.execCommand("copy");
        };

        /**
         * Toggle 'copy' popover when clipboard is being hovered.
         */
        $scope.hoverCopy = () => {
            $("[data-content='copy']").popover();
        };
    }

    UHGroupingsApp.controller("GeneralJsController", GeneralJsController);
})();
