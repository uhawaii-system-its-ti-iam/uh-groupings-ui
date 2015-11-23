
angular.module('routes.groupingSearch.DefaultGroupTabController', [
        'stack.i18n'
    ])
    .controller('DefaultGroupTabController', [
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

            // main thing we'll be working with is the grouping from the parent
            // note the use of $scope service here
            // but it's used to access groupingEditorCtrl instead of $parent.$parent..
            ctrl.grouping = $scope.groupingEditorCtrl.grouping;

            function init() {
                //default members = (basisMembers + includedMembers) - excludedMembers
                // NOTE: Assumes no overlap between basis and included
                //      if this is incorrect assumption, can use lodash, etc... to get unique values
                // NOTE 2: Specify the sourceGroup so the you know where each group came from
                ctrl.grouping.defaultMembers = ctrl.grouping.basisMembers.map(function (m) {
                        m.sourceGroup = 'Basis';
                        return m;
                    })
                    .concat(ctrl.grouping.includedMembers.map(function (m) {
                        m.sourceGroup = 'Include';
                        return m;
                    }))
                    //honestly not sure if this filter is needed -
                    // could a member ever be in both an include and exclude state?
                    // Seems extremely unlikely, but figured I'd put it here to show how to filter out if needed...
                    .filter(function (m) {
                        return ctrl.grouping.excludedMemberIds.indexOf(m.userId) === -1;
                    });
            }
            init();

            // add event listener to $scope service so that this view controller can respond to export CSV event
            // NOTE: Using ng-if inside each tab so that views are instantiated each time a tab is changed
            //      If we weren't doing that, then we'd want to pass in the active tab from the MembershipTabController
            //      when it fires this event so each subscriber can know who should respond.
            $scope.$on('MembershipTab.exportCSV', function () {
                //TODO: Respond to event to export data-set as CSV
            });
        }
    ]);

