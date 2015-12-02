angular.module('routes.groupingSearch.IncludeGroupContentController', [
    'stack.i18n'
])

/**
 * The IncludeGroupContentController manages view logic rendered to the
 * includeGroupContent.html template.
 *
 * @class IncludeGroupContentController
 * @constructor
 * @module routes.groupingSearch.IncludeGroupContentController
 */
.controller('IncludeGroupContentController', [
    '$scope',
    function ($scope) {
        // Define.
        var includeGroupContentController;

        /**
         * Property houses a reference to the controller.
         *
         * @property includeGroupContentController
         * @type {Object}
         */
        includeGroupContentController = this;

        /**
         * Property to track the sort field for our table of grouping owners.
         *
         * @property includeGroupContentController.sortField
         * @type {String}
         */
        includeGroupContentController.sortField = '+firstName';

        /**
         * Property houses grouping collection.
         *
         * @property includeGroupContentController.grouping
         * @type {Object}
         */
        includeGroupContentController.grouping = $scope.groupingEditorCtrl.grouping;

        /**
         * Method to easily control adjusting the sort of the owners table.
         *
         * @method includeGroupContentController.changeSort
         * @param {String} newSort Field name
         */
        includeGroupContentController.changeSort = function (newSort) {
            // If current sort minus sign equals new sort, then we're just swapping direction.
            if (includeGroupContentController.sortField.substr(1) === newSort) {
                includeGroupContentController.sortField = (includeGroupContentController.sortField[0] === '+' ? '-' : '+') + newSort;
            } else {
                includeGroupContentController.sortField = '+' + newSort;
            }
        };

        /**
         * Method to handle excluding a user by removing them from the include group and adding to the exclude group.
         *
         * @method includeGroupContentController.excludeUser
         * @param {Object} user User object
         */
        includeGroupContentController.excludeUser = function (user) {
            var includedMemberIdx = includeGroupContentController.grouping.includedMembers.indexOf(user),
                includedMemberIdIdx = includeGroupContentController.grouping.includedMemberIds.indexOf(user.userId),
                excludeMemberIdx = includeGroupContentController.grouping.excludedMembers.indexOf(user),
                excludeMemberIdIdx = includeGroupContentController.grouping.excludedMemberIds.indexOf(user.userId);

            // Really shouldn't happen - if it's in the list its index should be obtained.
            if (includedMemberIdx !== -1) {
                includeGroupContentController.grouping.includedMembers.splice(includedMemberIdx, 1);
            }

            if (includedMemberIdIdx !== -1) {
                includeGroupContentController.grouping.includedMemberIds.splice(includedMemberIdIdx, 1);
            }

            // This also should always happen, but best not to have duplicates in the exclude list.
            if (excludeMemberIdx === -1) {
                includeGroupContentController.grouping.excludedMembers.push(user);
            }

            if (excludeMemberIdIdx === -1) {
                includeGroupContentController.grouping.excludedMemberIds.push(user.userId);
            }
        };
    }
]);
