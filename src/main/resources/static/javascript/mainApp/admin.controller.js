/* global _, angular, UHGroupingsApp */

(function () {

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

        let PAGE_SIZE = 20;

        angular.extend(this, $controller("GeneralJsController", { $scope }));

        /**
         * Callback which takes the admin tab data and moves it into adminList and groupingsList, each of these objects
         * is then paginated.
         */
        $scope.getAdminListsCallbackOnSuccess = function (res) {
            $scope.adminsList = _.sortBy(res.adminGroup.members, "name");
            $scope.pagedItemsAdmins = $scope.objToPageArray($scope.adminsList, PAGE_SIZE);

            $scope.groupingsList = _.sortBy(res.allGroupingPaths, "name");
            $scope.pagedItemsGroupings = $scope.objToPageArray($scope.groupingsList, PAGE_SIZE);
            $scope.loading = false;
        };
        /**
         * Complete initialization by fetching a list of admins and list of all groupings.
         * Load grouping from manage-person if sessionStorage saved item into managePersonGrouping key
         * from $scope.displayGroupingInNewTab
         */
        $scope.init = function () {
            const managePersonGrouping = JSON.parse(sessionStorage.getItem("managePersonGrouping"));
            if (!_.isEmpty(managePersonGrouping)) {
                $scope.initManagePersonGrouping(managePersonGrouping);
            } else {
                $scope.loading = true;
                groupingsService.getAdminLists($scope.getAdminListsCallbackOnSuccess, $scope.displayApiErrorModal);
            }
        };

        $scope.searchForUserGroupingInformationOnSuccessCallback = function (res) {
            $scope.personList = _.sortBy(res, "name");
            $scope.filter($scope.personList, "pagedItemsPerson", "currentPagePerson", $scope.personQuery, true);
            $scope.user = $scope.personToLookup;
            $scope.loading = false;
        };
        $scope.searchForUserGroupingInformationOnErrorCallback = function (res) {
            $scope.personList = [];
            $scope.filter($scope.personList, "pagedItemsPerson", "currentPagePerson", $scope.personQuery, true);
            $scope.user = $scope.personToLookup;
            $scope.loading = false;
            $scope.resStatus = res.status;
        };
        /**
         * Fetch a list of memberships pertaining to $scope.personToLookUp.
         */
        $scope.searchForUserGroupingInformation = function () {
            $scope.loading = true;
            const validUser = $scope.sanitizer($scope.personToLookup);
            if (validUser !== "") {
                groupingsService.managePersonResults(validUser,
                    $scope.searchForUserGroupingInformationOnSuccessCallback,
                    $scope.searchForUserGroupingInformationOnErrorCallback
                );
                groupingsService.getMemberAttributes(validUser, function (person) {
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
        $scope.setCurrentManagePerson = function (person) {
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
        $scope.checkSoleOwner = function (res) {
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
            _.forEach($scope.selectedOwnedGroupings, function (grouping) {
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

        $scope.removeFromGroupsCallbackOnSuccess = function (memberToRemove) {
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
        $scope.removeFromGroups = function () {
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
        $scope.createGroupPathsAndNames = function (currentPage, selectedGroupingsNames, selectedGroupingsPaths, selectedOwnedGroupingsNames, selectedOwnedGroupings) {
            _.forEach(currentPage, function (grouping) {
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

        $scope.updateCheckBoxes = function () {
            $scope.checkAll = !$scope.checkAll;
            _.forEach($scope.pagedItemsPerson[$scope.currentPagePerson], function (grouping) {
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
        function inAdminList(user) {
            return _.some($scope.adminsList, { username: user }) ||
                _.some($scope.adminsList, { uhUuid: user });
        }

        /**
         * Adds a user to the admin list.
         */
        $scope.addAdmin = function () {
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
        $scope.removeAdmin = function (currentPage, index) {
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

        function handleRemoveFromGroupsOnSuccess() {
            $scope.loading = false;
            $scope.searchForUserGroupingInformation();
        }

        function handleRemoveFromGroupsOnError() {
            $scope.loading = false;
        }

        /**
         * Display a modal that prompts the user whether they want to delete the user or not. If 'Yes' is pressed, then
         * a request is made to delete the user.
         * @param {object} options - the options object
         * @param {object} options.member - the user being removed
         * @param {string} options.groupPaths - groups the user is being removed from
         * @param {object} options.listNames - groups the user is being removed from
         */
        $scope.displayRemoveFromGroupsModal = function (options) {
            const memberToRemove = options.member.uhUuid;
            const sanitizedUser = $scope.sanitizer(memberToRemove);
            $scope.memberToRemove = options.member;
            $scope.groupPaths = options.groupPaths;
            $scope.listNames = options.listNames.join(", ");

            const windowClass = $scope.showWarningRemovingSelf() ? "modal-danger" : "";

            groupingsService.getMemberAttributes(sanitizedUser, function (person) {
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
                    keyboard: false
                });

                $scope.removeFromGroupsModalInstance.result.then(function () {
                    $scope.loading = true;
                    let memberToRemove = options.member.uhUuid;
                    let groupingPath = $scope.groupPaths;
                    groupingsService.removeFromGroups(groupingPath, memberToRemove, handleRemoveFromGroupsOnSuccess, handleRemoveFromGroupsOnError);
                });
            }, function (res) {
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
         * Copy the path of a grouping through the clipboard button
         * @param {Object} grouping - the current selected grouping
         */
        $scope.copyPath = function (grouping) {
            $("[data-content='copy']").popover("hide");

            $("[data-content='copied!']").popover();
            setTimeout(function () {
                $("[data-content='copied!']").popover("hide");
            }, 1000);

            let copyText = document.getElementById(grouping.path);
            copyText.select();
            document.execCommand("copy");
        };

        /**
         * Toggle 'copy' popover when clipboard is being hovered.
         */
        $scope.hoverCopy = function () {
            $("[data-content='copy']").popover();
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
            $scope.fromManagePerson = false;
            $scope.showGrouping = false;

            $("#manage-person-tab").tab("show");

            $scope.personToLookup = sessionStorage.getItem("personToLookup");
            $scope.searchForUserGroupingInformation();
            groupingsService.getAdminLists($scope.getAdminListsCallbackOnSuccess, $scope.displayApiErrorModal);
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
        $scope.handleGroupingOwnersOnSuccess = function (res) {
            $scope.loading = false;
            $scope.names = [];
            for (let i = 0; i < res.groupMembers.length; i++) {
                $scope.names[parseInt(i, 10)] = res.groupMembers[parseInt(i, 10)].name;
            }
            $scope.usernames = [];
            for (let i = 0; i < res.groupMembers.length; i++) {
                $scope.usernames[parseInt(i, 10)] = res.groupMembers[parseInt(i, 10)].uid;
            }
            $scope.uhuids = [];
            for (let i = 0; i < res.groupMembers.length; i++) {
                $scope.uhuids[parseInt(i, 10)] = res.groupMembers[parseInt(i, 10)].uhUuid;
            }
            $scope.displayGroupingOwnersModal();
        };

        /**
         * Displays error modal for displaying grouping owners.
         */
        $scope.handleGroupingOwnersOnError = function () {
            $scope.loading = false;
            $scope.displayDynamicModal(Message.Title.DISPLAY_OWNERS_ERROR, Message.Body.DISPLAY_OWNERS_ERROR);
        };

        /**
         * Displays a modal - groupingOwnersModal.
         */
        $scope.displayGroupingOwnersModal = function () {
            $scope.groupingOwnersModal = $uibModal.open({
                templateUrl: "modal/groupingOwnersModal",
                scope: $scope
            });
            $scope.displayGroupingOwnersModalOnClose = function () {
                $scope.groupingOwnersModal.close();
                $scope.ownersModalGroupingPath = "";
            };
        };

        /**
         * Call gs to determine if it should proceed with the handleGroupingOwnersOnSuccess function.
         * @param groupingPath - The path of the grouping to display owners
         */
        $scope.getGroupingOwnersOnClick = function (groupingPath) {
            $scope.ownersModalGroupingPath = groupingPath;
            groupingsService.groupingOwners($scope.ownersModalGroupingPath, $scope.handleGroupingOwnersOnSuccess, $scope.handleGroupingOwnersOnError);
        };
    }

    UHGroupingsApp.controller("AdminJsController", AdminJsController);
}());