angular.module('routes.groupingSearch.BasisGroupTabController', [
    'stack.i18n'
])

/**
 * The BasisGroupTabController manages view logic rendered to the
 * basisGroupTab.html template.
 *
 * @class BasisGroupTabController
 * @constructor
 * @module routes.groupingSearch.BasisGroupTabController
 */
.controller('BasisGroupTabController', [
    '$scope',
    function ($scope) {
        // Define.
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

        // add event listener to $scope service so that this view controller can respond to export CSV event
        // NOTE: Using ng-if inside each tab so that views are instantiated each time a tab is changed
        //      If we weren't doing that, then we'd want to pass in the active tab from the MembershipTabController
        //      when it fires this event so each subscriber can know who should respond.
        $scope.$on('MembershipTab.exportCSV', function () {
            //TODO: Respond to event to export data-set as CSV
        });
    }
]);
