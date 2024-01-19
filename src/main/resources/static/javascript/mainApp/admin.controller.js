/* global _, angular, UHGroupingsApp */

(() => {

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
        $scope.personList = [];
        $scope.pagedItemsPerson = [];
        $scope.currentPagePerson = 0;
        $scope.personToLookup = "";
        $scope.currentManagePerson = "";
        $scope.fromManagePerson = false;
        $scope.selectedGroupingsPaths = [];
        $scope.emptySelect = false;
        $scope.selectedOwnedGroupingsNames = [];
        $scope.allGroupingsLoading = false;
        $scope.userGroupingInformationLoading = false;

        let PAGE_SIZE = 20;

        angular.extend(this, $controller("GroupingJsController", { $scope }));

        /**
         * Callback which takes the admin tab data and moves it into adminList, the object is then paginated.
         */
        $scope.getGroupingAdminsCallbackOnSuccess = (res) => {
            $scope.adminsList = _.sortBy(res.members, "name");
            $scope.pagedItemsAdmins = $scope.objToPageArray($scope.adminsList, PAGE_SIZE);
            $scope.loading = false;
        }

        /**
         * Callback which takes the admin tab data and moves it into groupingsList, the object
         * is then paginated.
         */
        $scope.getAllGroupingsCallbackOnSuccess = (res) => {
            $scope.groupingsList = _.sortBy(res.groupingPaths, "name");
            $scope.pagedItemsGroupings = $scope.objToPageArray($scope.groupingsList, PAGE_SIZE);
            $scope.allGroupingsLoading = false;
        }

        /**
         * Complete initialization by fetching a list of admins and list of all groupings.
         * Load grouping from manage-person if sessionStorage saved item into managePersonGrouping key
         * from $scope.displayGroupingInNewTab
         */
        $scope.init = () => {
            const managePersonGrouping = JSON.parse(sessionStorage.getItem("managePersonGrouping"));
            if (!_.isEmpty(managePersonGrouping)) {
                $scope.initManagePersonGrouping(managePersonGrouping);
            } else {
                $scope.loading = true;
                $scope.allGroupingsLoading = true;
                groupingsService.getGroupingAdmins(
                    $scope.getGroupingAdminsCallbackOnSuccess,
                    $scope.displayApiErrorModal);

                groupingsService.getAllGroupings(
                    $scope.getAllGroupingsCallbackOnSuccess,
                    $scope.displayApiErrorModal);
            }
        };

        $scope.searchForUserGroupingInformationOnSuccessCallback = (res) => {
            $scope.personList = _.sortBy(res, "name");
            $scope.filter($scope.personList, "pagedItemsPerson", "currentPagePerson", $scope.personQuery, true);
            $scope.user = $scope.personToLookup;
            $scope.userGroupingInformationLoading = false
        };
        $scope.searchForUserGroupingInformationOnErrorCallback = (res) => {
            $scope.personList = [];
            $scope.filter($scope.personList, "pagedItemsPerson", "currentPagePerson", $scope.personQuery, true);
            $scope.user = $scope.personToLookup;
            $scope.loading = false;
            $scope.resStatus = res.status;
        };
        /**
         * Fetch a list of memberships pertaining to $scope.personToLookUp.
         */
        $scope.searchForUserGroupingInformation = () => {
            const validUser = $scope.sanitizer($scope.personToLookup);
            if (validUser !== "") {
                $scope.userGroupingInformationLoading = true;
                groupingsService.managePersonResults(validUser,
                    $scope.searchForUserGroupingInformationOnSuccessCallback,
                    $scope.searchForUserGroupingInformationOnErrorCallback
                );
                groupingsService.getMemberAttributes(validUser, (person) => {
                    $scope.initMemberDisplayName(person);
                    $scope.setCurrentManagePerson(person);
                });
            } else {
                // sets proper error message
                if (!$scope.personToLookup) {
                    $scope.emptyInput = true;
                } else {
                    $scope.invalidInput = true;
                }
                $scope.loading = false;
                $scope.user = $scope.personToLookup;
                $scope.personList = [];
                $scope.filter($scope.personList, "pagedItemsPerson", "currentPagePerson", $scope.personQuery, true);
                $scope.currentManagePerson = "";
            }
            $scope.checkAll = false;
        };

        /**
         * Helper - searchForUserGroupingInformation
         * @param person
         */
        $scope.setCurrentManagePerson = (person) => {
            if ($scope.uhUuid != null) {
                $scope.currentManagePerson = "(" + person.name + ", " + person.username + ", " + person.uhUuid + ")";
            } else {
                $scope.currentManagePerson = "";
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
            let memberToRemove = {
                username: res.username,
                name: res.name,
                uhUuid: res.uhUuid
            };

            $scope.soleOwnerGroupingNames = [];

            if ($scope.selectedOwnedGroupings.length === 0) {
                $scope.removeFromGroupsCallbackOnSuccess(memberToRemove);
            }
            _.forEach($scope.selectedOwnedGroupings, (grouping) => {
                    groupingsService.isSoleOwner(grouping.path, memberToRemove.username, (res) => {
                        if (res) {
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
            $scope.createGroupPathsAndNames($scope.pagedItemsPerson[$scope.currentPagePerson], $scope.selectedGroupingsNames, $scope.selectedGroupingsPaths, $scope.selectedOwnedGroupingsNames, $scope.selectedOwnedGroupings);

            if ($scope.personToLookup != null) {
                groupingsService.getMemberAttributes($scope.personToLookup, $scope.checkSoleOwner);
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
            _.forEach($scope.pagedItemsPerson[$scope.currentPagePerson], (grouping) => {
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
            return _.some($scope.adminsList, { username: user }) ||
                _.some($scope.adminsList, { uhUuid: user });
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

            groupingsService.invalidUhIdentifiers([sanitizedAdmin], (res) => {
                if (!_.isEmpty(res)) {
                    $scope.invalidInput = true;
                    return;
                }

                $scope.displayAddModal({
                    membersToAdd: sanitizedAdmin,
                    listName: "admins"
                });
            });
        };

        /**
         * Remove an admin from the admin list. There must be at least one admin remaining.
         * @param {number} currentPage - the current page in the admins list
         * @param {number} index - the index of the admin to delete, with the current page and items per page taken into
         * account
         */
        $scope.removeAdmin = (currentPage, index) => {
            const adminToRemove = $scope.pagedItemsAdmins[currentPage][index];

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
            const memberToRemove = options.member.uhUuid;
            const sanitizedUser = $scope.sanitizer(memberToRemove);
            $scope.memberToRemove = options.member;
            $scope.groupPaths = options.groupPaths;
            $scope.listNames = options.listNames.join(", ");

            const windowClass = $scope.showWarningRemovingSelf() ? "modal-danger" : "";

            groupingsService.getMemberAttributes(sanitizedUser, (person) => {
                if (person === "") {
                    return;
                } else {
                    $scope.initMemberDisplayName(person);
                }
                $scope.removeFromGroupsModalInstance = $uibModal.open({
                    templateUrl: "modal/removeFromGroupsModal",
                    windowClass,
                    scope: $scope,
                    backdrop: "static",
                    keyboard: false,
                    ariaLabelledBy: "remove-from-groups-modal"
                });

                $scope.removeFromGroupsModalInstance.result.then(() => {
                    $scope.loading = true;
                    let memberToRemove = options.member.uhUuid;
                    let groupingPath = $scope.groupPaths;
                    groupingsService.removeFromGroups(groupingPath, memberToRemove, handleRemoveFromGroupsOnSuccess, handleRemoveFromGroupsOnError);
                });
            }, (res) => {
                $scope.user = memberToRemove;
                $scope.resStatus = res.status;
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
            $scope.clearManagePersonCheckboxes();
            $scope.removeFromGroupsModalInstance.dismiss("cancel");
        };

        /**
         * Closes the remove error modal and clears checkboxes in Manage Person.
         */
        $scope.closeRemoveErrorModal = () => {
            $scope.clearManagePersonCheckboxes();
            $scope.removeErrorModalInstance.close();
        };

        /**
         * Saves the needed information managePersonGrouping and personToLookup to sessionStorage for $scope.init()
         * to display the grouping in a new tab. Opens a new /admin page and removes managePersonGrouping from
         * sessionStorage to prevent displaying the grouping upon reloading the current page.
         * @param name {string} - The grouping name
         * @param path {String} - The grouping path
         */
        $scope.displayGroupingInNewTab = (name, path) => {
            sessionStorage.setItem("managePersonGrouping", JSON.stringify({ name, path }));
            sessionStorage.setItem("personToLookup", $scope.personToLookup);

            $window.open("admin");
            sessionStorage.removeItem("managePersonGrouping");
        };

        /**
         * Initializes the grouping from manage-person to be displayed.
         * @param managePersonGrouping {object} - The grouping from manage-person
         */
        $scope.initManagePersonGrouping = (managePersonGrouping) => {
            $scope.fromManagePerson = true;
            $scope.showGrouping = true;
            $scope.selectedGrouping = managePersonGrouping;
            $scope.getGroupingInformation();
            $scope.toggleShowAdminTab();
            sessionStorage.removeItem("managePersonGrouping");
        };

        /**
         * Returns to manage-person page and reloads the admin lists and the current manage-person being looked up.
         */
        $scope.returnToManagePerson = () => {
            $scope.loading = true;
            $scope.allGroupingsLoading = true;
            $scope.fromManagePerson = false;
            $scope.showGrouping = false;

            $("#manage-person-tab").tab("show");

            $scope.personToLookup = sessionStorage.getItem("personToLookup");
            $scope.searchForUserGroupingInformation();
            groupingsService.getGroupingAdmins($scope.getGroupingAdminsCallbackOnSuccess, $scope.displayApiErrorModal);
            groupingsService.getAllGroupings($scope.getAllGroupingsCallbackOnSuccess, $scope.displayApiErrorModal);
        };

        /**
         * Helper - cancelRemoveFromGroupsModal, closeRemoveErrorModal
         * Clears all selected checkboxes in manage person
         */
        $scope.clearManagePersonCheckboxes = () => {
            $scope.checkAll = false;
            _.forEach($scope.pagedItemsPerson[$scope.currentPagePerson], (grouping) => {
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
            $scope.owners = [];

            res.members.forEach((member) => {
                $scope.owners.push({
                    name: member.name,
                    uid: member.uid,
                    uhUuid: member.uhUuid
                });
            });

            $scope.displayGroupingOwnersModal();
        };

        /**
         * Displays error modal for displaying grouping owners.
         */
        $scope.handleGroupingOwnersOnError = () => {
            $scope.loading = false;
            $scope.displayDynamicModal(Message.Title.DISPLAY_OWNERS_ERROR, Message.Body.DISPLAY_OWNERS_ERROR);
        };

        /**
         * Displays a modal - groupingOwnersModal.
         */
        $scope.displayGroupingOwnersModal = () => {
            $scope.groupingOwnersModal = $uibModal.open({
                templateUrl: "modal/groupingOwnersModal",
                scope: $scope,
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
            $scope.ownersModalGroupingPath = groupingPath;
            groupingsService.groupingOwners($scope.ownersModalGroupingPath, $scope.handleGroupingOwnersOnSuccess, $scope.handleGroupingOwnersOnError);
        };

        $scope.throwException = () => {
            groupingsService.throwException(() => {}, () => {
                $scope.displayApiErrorModal();
            });
        }
    }

    UHGroupingsApp.controller("AdminJsController", AdminJsController);
})();
