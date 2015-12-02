angular.module('routes.groupingSearch.ExcludeGroupContentController', [
    'stack.i18n'
])

/**
 * The ExcludeGroupContentController manages view logic rendered to the
 * excludeGroupContent.html template.
 *
 * @class ExcludeGroupContentController
 * @constructor
 * @module routes.groupingSearch.ExcludeGroupContentController
 */
.controller('ExcludeGroupContentController', [
    '$scope',
    function ($scope) {
        // Define.
        var excludeGroupContentController;

        /**
         * Property houses a reference to the controller.
         *
         * @property excludeGroupContentController
         * @type {Object}
         */
        excludeGroupContentController = this;

        /**
         * Property to track the sort field for our table of grouping owners.
         *
         * @property excludeGroupContentController.sortField
         * @type {String}
         */
        excludeGroupContentController.sortField = '+firstName';

        /**
         * Property houses grouping collection.
         *
         * @property excludeGroupContentController.grouping
         * @type {Object}
         */
        excludeGroupContentController.grouping = $scope.groupingEditorCtrl.grouping;

        /**
         * Method to easily control adjusting the sort of the owners table.
         *
         * @method excludeGroupContentController.changeSort
         * @param {String} newSort Field name
         */
        excludeGroupContentController.changeSort = function (newSort) {
            // If current sort minus sign equals new sort, then we're just swapping direction.
            if (excludeGroupContentController.sortField.substr(1) === newSort) {
                excludeGroupContentController.sortField = (excludeGroupContentController.sortField[0] === '+' ? '-' : '+') + newSort;
            } else {
                excludeGroupContentController.sortField = '+' + newSort;
            }
        };

        /**
         * Method to handle including a user by removing them from the exclude group and adding to the include group.
         *
         * @method excludeGroupContentController.includeUser
         * @param {Object} user User object
         */
        excludeGroupContentController.includeUser = function (user) {
            var includedMemberIdx = excludeGroupContentController.grouping.includedMembers.indexOf(user),
                includedMemberIdIdx = excludeGroupContentController.grouping.includedMemberIds.indexOf(user.userId),
                excludeMemberIdx = excludeGroupContentController.grouping.excludedMembers.indexOf(user),
                excludeMemberIdIdx = excludeGroupContentController.grouping.excludedMemberIds.indexOf(user.userId);

            // Really shouldn't happen - if it's in the list its index should be obtained.
            if (excludeMemberIdx !== -1) {
                excludeGroupContentController.grouping.excludedMembers.splice(excludeMemberIdx, 1);
            }

            if (excludeMemberIdIdx !== -1) {
                excludeGroupContentController.grouping.excludedMemberIds.splice(excludeMemberIdIdx, 1);
            }

            // This also should always happen, but best not to have duplicates in the exclude list.
            if (includedMemberIdx === -1) {
                excludeGroupContentController.grouping.includedMembers.push(user);
            }
            if (includedMemberIdIdx === -1) {
                excludeGroupContentController.grouping.includedMemberIds.push(user.userId);
            }
        };
    }
]);
