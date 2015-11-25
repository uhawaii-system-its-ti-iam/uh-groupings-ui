angular.module('routes.groupingSearch.MembershipTabController', [
    'routes.groupingSearch.DefaultGroupTabController',
    'routes.groupingSearch.BasisGroupTabController',
    'routes.groupingSearch.IncludeGroupTabController',
    'routes.groupingSearch.ExcludeGroupTabController',
    'stack.i18n'
])

/**
 * The MembershipTabController manages view logic rendered to the
 * membershipTab.html template.
 *
 * @class MembershipTabController
 * @constructor
 * @module routes.groupingSearch.MembershipTabController
 */
.controller('MembershipTabController', [
    '$scope',
    'localizeFilter',
    function ($scope, localize) {
        // Define.
        var ctrl;

        /**
         * Property houses a reference to the membership tab controller.
         *
         * @property ctrl
         * @type {Object}
         */
        ctrl = this;

        /**
         * Property to house the users that can be added as owners.
         *
         * @property ctrl.nonOwnerUsers
         * @type {Array}
         */
        ctrl.nonOwnerUsers = [];

        /**
         * Property to contain all tabs shown as sub-views to this controller's view.
         *
         * @property tabs
         * @type {Array}
         */
        ctrl.tabs = [
            {
                name: 'default-group-tab',
                viewUrl: 'js/src/app/routes/grouping-search/grouping-editor/membership/default/defaultGroupTab.html',
                tabLabel: localize('MEMBERSHIP_TAB_DEFAULT_GROUP_TAB_HEADING'),
                tabIconClass: 'fa fa-users text-info',
                active: true
            },
            {
                name: 'basis-group-tab',
                viewUrl: 'js/src/app/routes/grouping-search/grouping-editor/membership/basis/basisGroupTab.html',
                tabLabel: localize('MEMBERSHIP_TAB_BASIS_GROUP_TAB_HEADING'),
                tabIconClass: 'fa fa-lock text-warning',
                active: false
            },
            {
                name: 'include-group-tab',
                viewUrl: 'js/src/app/routes/grouping-search/grouping-editor/membership/include/includeGroupTab.html',
                tabLabel: localize('MEMBERSHIP_TAB_INCLUDE_GROUP_TAB_HEADING'),
                tabIconClass: 'fa fa-plus-circle text-success',
                active: false
            },
            {
                name: 'exclude-group-tab',
                viewUrl: 'js/src/app/routes/grouping-search/grouping-editor/membership/exclude/excludeGroupTab.html',
                tabLabel: localize('MEMBERSHIP_TAB_EXCLUDE_GROUP_TAB_HEADING'),
                tabIconClass: 'fa fa-minus-circle text-danger',
                active: false
            }
        ];

        /**
         * Method to trigger a message to broadcast that an export needs to happen.
         *
         * @method exportAsCsv
         */
        ctrl.exportAsCsv = function () {
            // Children controllers can get the $scope service injected and can respond to this event:
            //$scope.$on('MembershipTab.exportCSV', function(event, data) {
            //  Do whatever is needed to export the sub-tab's dataset to CSV
            //});

            $scope.$broadcast('MembershipTab.exportCSV');
        };
    }
]);
