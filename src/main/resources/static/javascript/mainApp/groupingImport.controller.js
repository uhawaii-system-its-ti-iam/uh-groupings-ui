/* global _, UHGroupingsApp */

(() => {

    /**
     * Imports members into the Include or Exclude list of a grouping: a CSV/text file import, and an import of more
     * than Threshold.MULTI_ADD members entered in the add box. It works on the $scope of GroupingDetailsJsController,
     * which instantiates it with the handlers that it also uses for its own requests.
     * @param $scope - binding between controller and HTML page
     * @param $uibModal - the UI Bootstrap service for creating modals
     * @param groupingsService - service for creating requests to the groupings API
     * @param Message - message object constant from app.constants.js
     * @param Threshold - threshold object constant from app.constants.js
     * @param handleSuccessfulAdd - handles the result of an add that is not a file import
     * @param handleUnsuccessfulRequest - handles a failed request to add members
     * @param handleAddApiError - handles a failed request to check the members to add
     * @param displaySlowImportModal - displays the modal for an import that takes a long time
     * @returns {{importFile: function, openImportConfirmationModal: function, addMembersAsync: function}} the
     * functions that GroupingDetailsJsController uses to import members
     */
    function GroupingImportJsController($scope, $uibModal, groupingsService, Message, Threshold, handleSuccessfulAdd,
        handleUnsuccessfulRequest, handleAddApiError, displaySlowImportModal) {

        /**
         * Open the import confirmation modal, which warns that a large import can take minutes.
         * @param {string} listName - current list
         * @param {number} importSize - the number of members to import
         * @returns {Promise} the result of the modal: resolved on "Yes", rejected on "Cancel"
         */
        const openImportConfirmationModal = (listName, importSize) => {
            // Set information to be displayed in the modal
            $scope.importSize = importSize;
            $scope.listName = listName;
            $scope.isMultiAdd = true;
            $scope.waitingForImportResponse = false;

            // Open importConfirmation modal
            $scope.importConfirmationModalInstance = $uibModal.open({
                templateUrl: "modal/importConfirmationModal",
                scope: $scope,
                backdrop: "static",
                ariaLabelledBy: "import-confirmation-modal"
            });
            return $scope.importConfirmationModalInstance.result;
        };

        /**
         * Add members to the Include or Exclude list with the async endpoints used for imports. While the add runs,
         * $scope.importProgress holds how far it has gotten, which the add's list shows next to the import spinner.
         * @param {string} listName - "Include" or "Exclude"
         * @param {string} groupingPath - the path of the grouping to add to
         * @param {Object[]} membersToAdd - the members to add
         * @param {function} [onSuccess] - handles the result of the add
         */
        const addMembersAsync = (listName, groupingPath, membersToAdd, onSuccess = handleSuccessfulAdd) => {
            $scope.waitingForImportResponse = true; // Small spinner on
            $scope.importProgress = null;
            const onProgress = (progress) => {
                $scope.importProgress = { ...progress, listName };
            };
            const whenDone = (handler) => (res) => {
                $scope.importProgress = null;
                handler(res);
            };
            if (listName === "Include") {
                groupingsService.addIncludeMembersAsync(membersToAdd, groupingPath, whenDone(onSuccess),
                    whenDone(handleUnsuccessfulRequest), displaySlowImportModal, onProgress);
            } else if (listName === "Exclude") {
                groupingsService.addExcludeMembersAsync(membersToAdd, groupingPath, whenDone(onSuccess),
                    whenDone(handleUnsuccessfulRequest), displaySlowImportModal, onProgress);
            }
        };

        /**
         * Helper - importFile
         * Validates the identifiers of a CSV/text file import up front, so a handful of bad entries don't block the
         * rest of the file.
         * @param {Object[]} identifiers - the sanitized identifiers to validate
         * @param {function} onValidated - called with the identifiers that Grouper could not resolve
         */
        const validateFileImport = (identifiers, onValidated) => {
            $scope.waitingForImportResponse = true; // Small spinner on
            groupingsService.getMemberAttributeResultsAsync(identifiers, (res) => {
                $scope.waitingForImportResponse = false;
                $scope.isAddingMembers = false;
                onValidated(res.invalid);
            }, handleAddApiError);
        };

        /**
         * Helper - addMembers
         * Imports the identifiers of a CSV/text file, then displays its results: the number of members imported and
         * the entries that are not valid identifiers (rejected by the sanitizer, or unknown to Grouper), in file order.
         *
         * An import of more than Threshold.MULTI_ADD entries is confirmed, with its large import warning, before
         * anything is sent, and then all of its identifiers are added at once: the add validates them itself and
         * reports the ones it could not resolve, so validating them separately first would only double the Grouper
         * lookups of an import that already takes minutes. A smaller import is validated first, then confirmed with the
         * number of members that can be added.
         *
         * The user can add or import other members while an import runs, which changes the $scope values that the
         * results are displayed from, so the import keeps its own until it displays them.
         * @param {string} listName
         * @param {Object[]} identifiers - the sanitized identifiers to import
         * @param {Object[]} importEntries - the entries that the import reports on
         */
        const importFile = (listName, identifiers, importEntries) => {
            const groupingPath = $scope.selectedGrouping.path;
            const importContext = {
                listName,
                importTotalCount: importEntries.length,
                importDuplicateCount: $scope.importDuplicateCount,
                importSourceRows: $scope.importSourceRows,
                importFileBaseName: $scope.importFileBaseName
            };
            const displayImportResults = (invalidIdentifiers) => {
                const invalid = new Set(invalidIdentifiers);
                const acceptedIdentifiers = new Set(identifiers);
                Object.assign($scope, importContext);
                // Entries the sanitizer rejected never reached the API, but are as unusable as those it reports.
                $scope.importInvalidMembers = importEntries.filter(
                    (entry) => invalid.has(entry) || !acceptedIdentifiers.has(entry));
                $scope.importSuccessCount = identifiers.filter((id) => !invalid.has(id)).length;
                $scope.displayImportFileResultsModal();
            };

            if (importEntries.length <= Threshold.MULTI_ADD) {
                validateFileImport(identifiers, (invalidIdentifiers) => {
                    const invalid = new Set(invalidIdentifiers);
                    const validIdentifiers = identifiers.filter((id) => !invalid.has(id));
                    // Nothing resolved to a real member: skip the add call and just report the failures.
                    if (_.isEmpty(validIdentifiers)) {
                        displayImportResults(invalidIdentifiers);
                        return;
                    }
                    openImportConfirmationModal(listName, validIdentifiers.length).then(() => {
                        addMembersAsync(listName, groupingPath, validIdentifiers,
                            () => displayImportResults(invalidIdentifiers));
                    }, () => { /* onRejected: the import was cancelled */
                    });
                });
                return;
            }

            openImportConfirmationModal(listName, importEntries.length).then(() => {
                $scope.isAddingMembers = false;
                addMembersAsync(listName, groupingPath, identifiers,
                    (res) => displayImportResults(res.invalidUhIdentifiers));
            }, () => { /* onRejected: the import was cancelled */
                $scope.isAddingMembers = false;
            });
        };

        /**
         * The text that shows how far an import has gotten, e.g. "Adding 4,000 of 12,284 members to the Include
         * list...". A move removes members from the list opposite the one they are added to.
         * @param {Object} progress - $scope.importProgress: the phase, done, total, and listName of an import
         * @returns {string} the text, or an empty string for a phase without one
         */
        $scope.importProgressText = (progress) => {
            const message = progress ? Message.ImportProgress[progress.phase] : null;
            if (!message) {
                return "";
            }
            const listName = progress.phase === "REMOVING"
                ? (progress.listName === "Include" ? "Exclude" : "Include")
                : progress.listName;
            return message.with(progress.done.toLocaleString("en-US"), progress.total.toLocaleString("en-US"), listName);
        };

        return { importFile, openImportConfirmationModal, addMembersAsync };
    }

    UHGroupingsApp.controller("GroupingImportJsController", GroupingImportJsController);
})();
