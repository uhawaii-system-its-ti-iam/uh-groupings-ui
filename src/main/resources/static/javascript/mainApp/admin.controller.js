/* global angular, UHGroupingsApp */

(() => {

    const isPositiveSafeInteger = (value) => Number.isSafeInteger(value) && value > 0;
    const isNonNegativeSafeInteger = (value) => Number.isSafeInteger(value) && value >= 0;
    const isGroupingPathsResponse = (response) => Boolean(response) && Array.isArray(response.groupingPaths);
    const totalPagesFor = (pageSize, totalCount) => {
        if (!isPositiveSafeInteger(pageSize) || !isNonNegativeSafeInteger(totalCount)) {
            return null;
        }
        return Math.max(1, Math.ceil(totalCount / pageSize));
    };
    const hasValidGroupingPageMetadata = (page, totalPages) =>
        isPositiveSafeInteger(page) && page <= totalPages;

    const groupingPageMetadata = (response) => {
        if (!isGroupingPathsResponse(response)) {
            return null;
        }

        const page = Number(response.page);
        const pageSize = Number(response.pageSize);
        const totalCount = Number(response.totalCount);
        const totalPages = totalPagesFor(pageSize, totalCount);
        if (totalPages === null || !hasValidGroupingPageMetadata(page, totalPages)) {
            return null;
        }

        return { page, totalPages };
    };

    /**
     * This controller contains functions specific to the admin page.
     * @param $scope - binding between controller and HTML page
     * @param $window - the browser window object
     * @param $controller - service for instantiating controllers
     * @param $uibModal - the UI Bootstrap service for creating modals
     * @param dataProvider - service function that provides GET and POST requests for getting or updating data
     * @param groupingsService - service for requesting data from the groupings API
     * @param Message - display messages
     */
    function AdminJsController($scope, $window, $uibModal, $controller, dataProvider, groupingsService, Message) {

        $scope.adminsList = [];
        $scope.pagedItemsAdmins = [];
        $scope.currentPageAdmins = 0;
        $scope.subjectList = [];
        $scope.pagedItemsSubject = [];
        $scope.currentPageSubject = 0;
        $scope.subjectToLookup = "";
        $scope.currentManageSubject = "";
        $scope.fromManageSubject = false;
        $scope.selectedGroupingsPaths = [];
        $scope.emptySelect = false;
        $scope.selectedOwnedGroupingsNames = [];
        $scope.allGroupingsLoading = false;
        $scope.userGroupingInformationLoading = false;
        $scope.loadingOwners = false;
        $scope.owners = [];

        let PAGE_SIZE = 20;
        let requestedGroupingsPage = 0;
        let groupingRequestId = 0;

        angular.extend(this, $controller("GroupingDetailsJsController", { $scope }));

        /**
         * Callback which takes the admin tab data and moves it into adminList, the object is then paginated.
         */
        $scope.getGroupingAdminsCallbackOnSuccess = (res) => {
            $scope.adminsList = _.sortBy(res.members, "name");
            $scope.pagedItemsAdmins = $scope.objToPageArray($scope.adminsList, PAGE_SIZE);
            $scope.loading = false;
        };

        /**
         * Callback which takes the admin tab data and moves it into groupingsList, the object
         * is then paginated.
         */
        $scope.getAllGroupingsCallbackOnSuccess = (res) => {
            const pageMetadata = groupingPageMetadata(res);
            if (!pageMetadata) {
                $scope.allGroupingsLoading = false;
                $scope.displayApiErrorModal(res);
                return;
            }

            $scope.groupingsList = _.sortBy(res.groupingPaths, "name");
            $scope.pagedItemsGroupings = Array.from({ length: pageMetadata.totalPages }, () => []);
            $scope.pagedItemsGroupings[pageMetadata.page - 1] = $scope.groupingsList;
            $scope.currentPageGroupings = pageMetadata.page - 1;
            $scope.allGroupingsLoading = false;
        };

        $scope.loadGroupingsPage = (page) => {
            const requestId = ++groupingRequestId;
            requestedGroupingsPage = page - 1;
            $scope.allGroupingsLoading = true;
            groupingsService.getAllGroupings(
                page,
                PAGE_SIZE,
                $scope.groupingsQuery,
                (res) => {
                    if (requestId === groupingRequestId) {
                        $scope.getAllGroupingsCallbackOnSuccess(res);
                    }
                },
                (res) => {
                    if (requestId === groupingRequestId) {
                        $scope.allGroupingsLoading = false;
                        $scope.displayApiErrorModal(res);
                    }
                }
            );
        };

        $scope.$watch("currentPageGroupings", (page, previousPage) => {
            if (page !== previousPage && page !== requestedGroupingsPage) {
                $scope.loadGroupingsPage(page + 1);
            }
        });

        /**
         * Complete initialization by fetching a list of admins and list of all groupings.
         * Load grouping from manage-subject if sessionStorage saved item into manageSubjectGrouping key
         * from $scope.displayGroupingInNewTab
         */
        $scope.init = () => {
            const manageSubjectGrouping = JSON.parse(sessionStorage.getItem("manageSubjectGrouping"));
            if (!_.isEmpty(manageSubjectGrouping)) {
                $scope.initManageSubjectGrouping(manageSubjectGrouping);
            } else {
                $scope.loading = true;
                $scope.allGroupingsLoading = true;
                groupingsService.getGroupingAdmins(
                    $scope.getGroupingAdminsCallbackOnSuccess,
                    $scope.displayApiErrorModal
                );

                $scope.loadGroupingsPage(1);
            }
        };


        $scope.searchForUserGroupingInformationOnSuccessCallback = (res) => {
            $scope.subjectList = _.sortBy(res.results, "name");
            $scope.filter($scope.subjectList, "pagedItemsSubject", "currentPageSubject", $scope.subjectQuery, true);
            $scope.user = $scope.subjectToLookup;
            $scope.userGroupingInformationLoading = false;
        };
        $scope.searchForUserGroupingInformationOnErrorCallback = (res) => {
            $scope.subjectList = [];
            $scope.filter($scope.subjectList, "pagedItemsSubject", "currentPageSubject", $scope.subjectQuery, true);
            $scope.user = $scope.subjectToLookup;
            $scope.loading = false;
            $scope.resStatus = res.status;
        };
        /**
         * Fetch a list of memberships pertaining to $scope.subjectToLookUp.
         */
        $scope.searchForUserGroupingInformation = () => {
            const validUser = $scope.sanitizer($scope.subjectToLookup);
            if (validUser !== "") {
                $scope.userGroupingInformationLoading = true;
                groupingsService.manageSubjectResults(validUser,
                    $scope.searchForUserGroupingInformationOnSuccessCallback,
                    $scope.searchForUserGroupingInformationOnErrorCallback
                );
                groupingsService.getMemberAttributeResults([validUser], (res) => {
                    const subject = res.results[0];
                    if (subject) {
                        $scope.initMemberDisplayName(subject);
                        $scope.setCurrentManageSubject(subject);
                    } else {
                        $scope.currentManageSubject = "";
                        $scope.invalidInput = true;
                    }
                }, () => { /* on promise rejection*/
                });
            } else {
                // sets proper error message
                if (!$scope.subjectToLookup) {
                    $scope.emptyInput = true;
                } else {
                    $scope.invalidInput = true;
                }
                $scope.loading = false;
                $scope.user = $scope.subjectToLookup;
                $scope.subjectList = [];
                $scope.filter($scope.subjectList, "pagedItemsSubject", "currentPageSubject", $scope.subjectQuery, true);
                $scope.currentManageSubject = "";
            }
            $scope.checkAll = false;
        };

        /**
         * Helper - searchForUserGroupingInformation
         * @param subject
         */
        $scope.setCurrentManageSubject = (subject) => {
            if ($scope.uhUuid != null) {
                $scope.currentManageSubject = "(" + subject.name + ", " + subject.uid + ", " + subject.uhUuid + ")";
            } else {
                $scope.currentManageSubject = "";
                $scope.invalidInput = true;
            }
        };

        /**
         * Helper - removeFromGroups
         * Checks if the user being removed is a sole owner of any grouping before proceeding
         */
        $scope.checkSoleOwner = (res) => {
            if (res === "") {
                return;
            }

            const { uid, name, uhUuid } = res.results[0];
            const memberToRemove = { uid, name, uhUuid };

            $scope.soleOwnerGroupingNames = [];

            if ($scope.selectedOwnedGroupings.length === 0) {
                $scope.removeFromGroupsCallbackOnSuccess(memberToRemove);
            }
            _.forEach($scope.selectedOwnedGroupings, (grouping) => {
                    groupingsService.getNumberOfDirectOwners(grouping.path, (res) => {
                        if (res === 1) {
                            $scope.soleOwnerGroupingNames.push(grouping.name);
                        }
                        if (grouping === $scope.selectedOwnedGroupings[$scope.selectedOwnedGroupings.length - 1]) {
                            $scope.removeFromGroupsCallbackOnSuccess(memberToRemove);
                        }
                    }, () => $scope.displayApiErrorModal());
                }
            );
        };

        $scope.removeFromGroupsCallbackOnSuccess = (memberToRemove) => {
            if (_.isEmpty($scope.selectedGroupingsPaths)) {
                $scope.emptySelect = true;
            } else if ($scope.soleOwnerGroupingNames.length >= 1) {
                $scope.soleOwnerGroupingNames = $scope.soleOwnerGroupingNames.join(", ");
                $scope.displayRemoveErrorModal("owner");
            } else {
                $scope.displayRemoveFromGroupsModal({
                    member: memberToRemove,
                    groupPaths: $scope.selectedGroupingsPaths,
                    listNames: $scope.selectedGroupingsNames
                });
            }
        };

        /**
         * Removes selected user from a list of groupings.
         */
        $scope.removeFromGroups = () => {
            $scope.selectedGroupingsNames = [];
            $scope.selectedGroupingsPaths = [];
            $scope.selectedOwnedGroupings = [];
            $scope.selectedOwnedGroupingsNames = [];
            $scope.createGroupPathsAndNames($scope.pagedItemsSubject[$scope.currentPageSubject], $scope.selectedGroupingsNames, $scope.selectedGroupingsPaths, $scope.selectedOwnedGroupingsNames, $scope.selectedOwnedGroupings);

            if ($scope.subjectToLookup != null) {
                const validUser = $scope.sanitizer($scope.subjectToLookup);
                groupingsService.getMemberAttributeResults([validUser], $scope.checkSoleOwner);
            }
        };

        /**
         * Helper - removeFromGroups
         * Pluck selectedGroupingsNames and selectedGroupingsPaths from currentPage
         */
        $scope.createGroupPathsAndNames = (currentPage, selectedGroupingsNames, selectedGroupingsPaths, selectedOwnedGroupingsNames, selectedOwnedGroupings) => {
            _.forEach(currentPage, (grouping) => {
                if (grouping.isSelected) {
                    let basePath = grouping.path;
                    let groupName = grouping.name;
                    selectedGroupingsNames.push(groupName);
                    if (grouping.inOwner) {
                        let temp = basePath + ":owners";
                        selectedGroupingsPaths.push(temp);
                        selectedOwnedGroupingsNames.push(groupName);
                        selectedOwnedGroupings.push(grouping);
                    }
                    if (grouping.inInclude) {
                        let temp = basePath + ":include";
                        selectedGroupingsPaths.push(temp);
                    }
                    if (grouping.inExclude) {
                        let temp = basePath + ":exclude";
                        selectedGroupingsPaths.push(temp);
                    }
                }
            });
        };

        $scope.updateCheckBoxes = () => {
            $scope.checkAll = !$scope.checkAll;
            _.forEach($scope.pagedItemsSubject[$scope.currentPageSubject], (grouping) => {
                if (grouping.inOwner || grouping.inInclude || grouping.inExclude) {
                    grouping.isSelected = $scope.checkAll;
                }
            });
        };

        /**
         * Helper - addAdmin
         * Checks if the user is already an admin
         * @param {string} user - the user you are checking to see if they are already in the list being added to
         * @returns {boolean} true if the user is already in the list being added to, otherwise returns false
         */
        const inAdminList = (user) => {
            return _.some($scope.adminsList, { uid: user }) ||
                _.some($scope.adminsList, { uhUuid: user });
        };

        const resetAddAdminValidation = () => {
            $scope.containsDeptAcc = false;
            $scope.containsServiceAccAdmin = false;
            $scope.addInputError = false;
            $scope.invalidMembers = [];
        };

        const isServiceAccountAdmin = (admin, members) =>
            (_.isString(admin) && admin.startsWith("_")) ||
            $scope.checkForServiceAccountMembers(members);

        const displayInvalidAdminInput = (admin, invalidMembers) => {
            $scope.invalidMembers = _.isEmpty(invalidMembers) ? [admin] : invalidMembers;
            $scope.addInputError = true;
        };

        const lookupMembers = (response) => response.results ?? [];
        const lookupInvalidMembers = (response) => response.invalid ?? [];

        const handleAddAdminLookup = (admin, response) => {
            const members = lookupMembers(response);
            const invalidMembers = lookupInvalidMembers(response);
            resetAddAdminValidation();

            if (isServiceAccountAdmin(admin, members)) {
                $scope.containsServiceAccAdmin = true;
                return;
            }
            if (!_.isEmpty(invalidMembers) || _.isEmpty(members)) {
                displayInvalidAdminInput(admin, invalidMembers);
                return;
            }
            if ($scope.checkForDeptAccount(members)) {
                $scope.containsDeptAcc = true;
                return;
            }
            $scope.displayAddModal({
                membersAttributes: response,
                uhIdentifiers: admin,
                listName: "admins"
            });
        };

        const isValidArrayIndex = (array, index) =>
            Array.isArray(array) && Number.isSafeInteger(index) && index >= 0 && index < array.length;

        const adminAtPageIndex = (currentPage, index) => {
            if (!isValidArrayIndex($scope.pagedItemsAdmins, currentPage)) {
                return null;
            }
            const adminPage = _.nth($scope.pagedItemsAdmins, currentPage);
            if (!isValidArrayIndex(adminPage, index)) {
                return null;
            }
            return _.nth(adminPage, index);
        };

        /**
         * Adds a user to the admin list.
         */
        $scope.addAdmin = () => {
            const sanitizedAdmin = $scope.sanitizer($scope.adminToAdd);
            $scope.user = sanitizedAdmin;
            if (_.isEmpty(sanitizedAdmin)) {
                $scope.emptyInput = true;
                return;
            }
            if (inAdminList(sanitizedAdmin)) {
                $scope.listName = "admins";
                $scope.containsInput = true;
                return;
            }

            groupingsService.getMemberAttributeResults([sanitizedAdmin], (res) => {
                handleAddAdminLookup(sanitizedAdmin, res);
            });
        };

        /**
         * Remove an admin from the admin list. There must be at least one admin remaining.
         * @param {number} currentPage - the current page in the admins list
         * @param {number} index - the index of the admin to delete, with the current page and items per page taken into
         * account
         */
        $scope.removeAdmin = (currentPage, index) => {
            const adminToRemove = adminAtPageIndex(currentPage, index);

            if (!adminToRemove) {
                return;
            }

            if ($scope.adminsList.length > 1) {
                $scope.displayRemoveModal({
                    membersToRemove: adminToRemove,
                    listName: "admins"
                });
            } else {
                const userType = "admin";
                $scope.displayRemoveErrorModal(userType);
            }
        };

        const handleRemoveFromGroupsOnSuccess = () => {
            $scope.loading = false;
            $scope.searchForUserGroupingInformation();
        };

        const handleRemoveFromGroupsOnError = () => {
            $scope.loading = false;
        };

        /**
         * Display a modal that prompts the user whether they want to delete the user or not. If 'Yes' is pressed, then
         * a request is made to delete the user.
         * @param {object} options - the options object
         * @param {object} options.member - the user being removed
         * @param {string} options.groupPaths - groups the user is being removed from
         * @param {object} options.listNames - groups the user is being removed from
         */
        $scope.displayRemoveFromGroupsModal = (options) => {
            $scope.memberToRemove = options.member;
            $scope.groupPaths = options.groupPaths;
            $scope.listNames = options.listNames.join(", ");

            $scope.removeFromGroupsModalInstance = $uibModal.open({
                templateUrl: "modal/removeFromGroupsModal",
                windowClass: $scope.showWarningRemovingSelf() ? "modal-danger" : "",
                scope: $scope,
                backdrop: "static",
                keyboard: false,
                ariaLabelledBy: "remove-from-groups-modal"
            });

            $scope.removeFromGroupsModalInstance.result.then(() => {
                $scope.loading = true;
                groupingsService.removeFromGroups($scope.groupPaths, $scope.memberToRemove.uhUuid, handleRemoveFromGroupsOnSuccess, handleRemoveFromGroupsOnError);
            });
        };

        /**
         * Closes the remove from groups modal instance.
         */
        $scope.proceedRemoveFromGroupsModal = () => {
            $scope.removeFromGroupsModalInstance.close();
        };

        /**
         * Cancels the remove from groups modal instance.
         */
        $scope.cancelRemoveFromGroupsModal = () => {
            $scope.clearManageSubjectCheckboxes();
            $scope.removeFromGroupsModalInstance.dismiss("cancel");
        };

        /**
         * Closes the remove error modal and clears checkboxes in Manage Subject.
         */
        $scope.closeRemoveErrorModal = () => {
            $scope.clearManageSubjectCheckboxes();
            $scope.removeErrorModalInstance.close();
        };

        /**
         * Saves the needed information manageSubjectGrouping and subjectToLookup to sessionStorage for $scope.init()
         * to display the grouping in a new tab. Opens a new /admin page and removes manageSubjectGrouping from
         * sessionStorage to prevent displaying the grouping upon reloading the current page.
         * @param name {string} - The grouping name
         * @param path {String} - The grouping path
         */
        $scope.displayGroupingInNewTab = (name, path) => {
            sessionStorage.setItem("manageSubjectGrouping", JSON.stringify({ name, path }));
            sessionStorage.setItem("subjectToLookup", $scope.subjectToLookup);

            $window.open("admin");
            sessionStorage.removeItem("manageSubjectGrouping");
        };

        /**
         * Initializes the grouping from manage-subject to be displayed.
         * @param manageSubjectGrouping {object} - The grouping from manage-subject
         */
        $scope.initManageSubjectGrouping = (manageSubjectGrouping) => {
            $scope.fromManageSubject = true;
            $scope.showGrouping = true;
            $scope.selectedGrouping = manageSubjectGrouping;
            $scope.getGroupingInformation();
            $scope.toggleShowAdminTab();
            sessionStorage.removeItem("manageSubjectGrouping");
        };

        /**
         * Returns to manage-subject page and reloads the admin lists and the current manage-subject being looked up.
         */
        $scope.returnToManageSubject = () => {
            $scope.loading = true;
            $scope.allGroupingsLoading = true;
            $scope.fromManageSubject = false;
            $scope.showGrouping = false;

            $("#manage-subject-tab").tab("show");

            $scope.subjectToLookup = sessionStorage.getItem("subjectToLookup");
            $scope.searchForUserGroupingInformation();
            groupingsService.getGroupingAdmins($scope.getGroupingAdminsCallbackOnSuccess, $scope.displayApiErrorModal);
            $scope.loadGroupingsPage(1);
        };

        /**
         * Helper - cancelRemoveFromGroupsModal, closeRemoveErrorModal
         * Clears all selected checkboxes in manage subject
         */
        $scope.clearManageSubjectCheckboxes = () => {
            $scope.checkAll = false;
            _.forEach($scope.pagedItemsSubject[$scope.currentPageSubject], (grouping) => {
                if (grouping.inOwner || grouping.inInclude || grouping.inExclude) {
                    grouping.isSelected = $scope.checkAll;
                }
            });
        };

        /**
         * Assign the proper values to scope variables referenced in groupingOwnersModal.
         * @param res - the information of the owners from API
         */
        $scope.handleGroupingOwnersOnSuccess = (res) => {
            $scope.loading = false;
            $scope.loadingOwners = false;
            $scope.owners = [];

            // The API returns owners in res.owners.members
            // Other response formats (e.g., res.members) are no longer supported here.
            const members = Array.isArray(res?.owners?.members) ? res.owners.members : [];
            members.forEach((member) => {
                // Filter out owner-groupings: only show people (names without colons)
                // Owner-groupings have colons in their names (e.g., "group:name:owners")
                if (member.name && !member.name.includes(":")) {
                    $scope.owners.push({
                        name: member.name,
                        uid: member.uid,
                        uhUuid: member.uhUuid
                    });
                }
            });

            $scope.displayGroupingOwnersModal();
        };

        /**
         * Displays error modal for displaying grouping owners.
         */
        $scope.handleGroupingOwnersOnError = () => {
            $scope.loading = false;
            $scope.loadingOwners = false;
            $scope.displayDynamicModal(Message.Title.DISPLAY_OWNERS_ERROR, Message.Body.DISPLAY_OWNERS_ERROR);
        };

        /**
         * Displays a modal - groupingOwnersModal.
         */
        $scope.displayGroupingOwnersModal = () => {
            $scope.groupingOwnersModal = $uibModal.open({
                templateUrl: "modal/groupingOwnersModal",
                scope: $scope,
                backdrop: "static",
                ariaLabelledBy: "grouping-owners-modal"
            });
            $scope.displayGroupingOwnersModalOnClose = () => {
                $scope.groupingOwnersModal.close();
                $scope.ownersModalGroupingPath = "";
            };
        };

        /**
         * Call gs to determine if it should proceed with the handleGroupingOwnersOnSuccess function.
         * @param groupingPath - The path of the grouping to display owners
         */
        $scope.getGroupingOwnersOnClick = (groupingPath) => {
            // Prevent multiple triggers while loading modal
            if ($scope.loadingOwners) {
                return;
            }
            $scope.loadingOwners = true;
            $scope.ownersModalGroupingPath = groupingPath;
            groupingsService.groupingOwners($scope.ownersModalGroupingPath, $scope.handleGroupingOwnersOnSuccess, $scope.handleGroupingOwnersOnError);
        };

        $scope.throwException = () => {
            groupingsService.throwException(() => {
            }, () => {
                $scope.displayApiErrorModal();
            });
        };
    }

    UHGroupingsApp.controller("AdminJsController", AdminJsController);
})();
