
angular.module('routes.groupingSearch.IncludeGroupTabController', [
        'stack.i18n'
    ])
    .controller('IncludeGroupTabController', [
        '$scope',
        function ($scope) {
            // define
            var ctrl;

            // alias controller
            ctrl = this;

            /**
             * Property to track the sort field for our table of grouping owners
             *
             * @property sortField
             * @type {string}
             */
            ctrl.sortField = '+firstName';

            /**
             * Method to easily control adjusting the sort of the owners table
             *
             * @method changeSort
             * @param newSort {string} new sort field name
             */
            ctrl.changeSort = function (newSort) {
                //if current sort minus sign equals new sort, then we're just swapping direction
                if (ctrl.sortField.substr(1) === newSort) {
                    ctrl.sortField = (ctrl.sortField[0] === '+' ? '-' : '+') + newSort;
                } else {
                    ctrl.sortField = '+' + newSort;
                }
            };

            /**
             * Method to handle excluding a user by removing them from the include group and adding to the exclude group
             *
             * @method excludeUser
             * @param user {Object}
             */
            ctrl.excludeUser = function (user) {
                var includedMemberIdx = ctrl.grouping.includedMembers.indexOf(user),
                    includedMemberIdIdx = ctrl.grouping.includedMemberIds.indexOf(user.userId),
                    excludeMemberIdx = ctrl.grouping.excludedMembers.indexOf(user),
                    excludeMemberIdIdx = ctrl.grouping.excludedMemberIds.indexOf(user.userId);

                //really shouldn't happen - if it's in the list its index should be obtained...
                if (includedMemberIdx !== -1) {
                    ctrl.grouping.includedMembers.splice(includedMemberIdx, 1);
                }
                if (includedMemberIdIdx !== -1) {
                    ctrl.grouping.includedMemberIds.splice(includedMemberIdIdx, 1);
                }

                //this also should always happen, but best not to have duplicates in the exclude list...
                if (excludeMemberIdx === -1) {
                    ctrl.grouping.excludedMembers.push(user);
                }
                if (excludeMemberIdIdx === -1) {
                    ctrl.grouping.excludedMemberIds.push(user.userId);
                }
            };

            // main thing we'll be working with is the grouping from the parent
            // note the use of $scope service here
            // but it's used to access groupingEditorCtrl instead of $parent.$parent..
            ctrl.grouping = $scope.groupingEditorCtrl.grouping;

            // add event listener to $scope service so that this view controller can respond to export CSV event
            // NOTE: Using ng-if inside each tab so that views are instantiated each time a tab is changed
            //      If we weren't doing that, then we'd want to pass in the active tab from the MembershipTabController
            //      when it fires this event so each subscriber can know who should respond.
            $scope.$on('MembershipTab.exportCSV', function () {
                //TODO: Respond to event to export data-set as CSV
            });
        }
    ]);
