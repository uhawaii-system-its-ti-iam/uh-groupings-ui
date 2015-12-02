angular.module('routes.groupingSearch.MembershipContentController', [
    'routes.groupingSearch.DefaultGroupContentController',
    'routes.groupingSearch.BasisGroupContentController',
    'routes.groupingSearch.IncludeGroupContentController',
    'routes.groupingSearch.ExcludeGroupContentController',
    'stack.i18n'
])

/**
 * The MembershipContentController manages view logic rendered to the
 * membershipContent.html template.
 *
 * @class MembershipContentController
 * @constructor
 * @module routes.groupingSearch.MembershipContentController
 */
.controller('MembershipContentController', [
    '$scope',
    'localizeFilter',
    function ($scope, localize) {
        // Define.
        var membershipContentCtrl;

        /**
         * Property houses a reference to the membership content controller.
         *
         * @property membershipContentCtrl
         * @type {Object}
         */
        membershipContentCtrl = this;

        /**
         * Property houses the users that can be added as owners.
         *
         * @property membershipContentCtrl.nonOwnerUsers
         * @type {Array}
         */
        membershipContentCtrl.nonOwnerUsers = [];

        /**
         * Property houses configuration for sub-views (i.e., default, basis, include and exclude).
         *
         * @property membershipContentCtrl.tabs
         * @type {Array}
         */
        membershipContentCtrl.tabs = [
            {
                name: 'default-group-tab',
                content: 'js/src/app/routes/grouping-search/grouping-editor/membership/default/defaultGroupContent.html',
                tabLabel: localize('MEMBERSHIP_TAB_DEFAULT_GROUP_TAB_HEADING'),
                tabIconClass: 'fa fa-users text-info',
                active: true
            },
            {
                name: 'basis-group-tab',
                content: 'js/src/app/routes/grouping-search/grouping-editor/membership/basis/basisGroupContent.html',
                tabLabel: localize('MEMBERSHIP_TAB_BASIS_GROUP_TAB_HEADING'),
                tabIconClass: 'fa fa-lock text-warning',
                active: false
            },
            {
                name: 'include-group-tab',
                content: 'js/src/app/routes/grouping-search/grouping-editor/membership/include/includeGroupContent.html',
                tabLabel: localize('MEMBERSHIP_TAB_INCLUDE_GROUP_TAB_HEADING'),
                tabIconClass: 'fa fa-plus-circle text-success',
                active: false
            },
            {
                name: 'exclude-group-tab',
                content: 'js/src/app/routes/grouping-search/grouping-editor/membership/exclude/excludeGroupContent.html',
                tabLabel: localize('MEMBERSHIP_TAB_EXCLUDE_GROUP_TAB_HEADING'),
                tabIconClass: 'fa fa-minus-circle text-danger',
                active: false
            }
        ];

        /**
         * Method to broadcast an export event.
         *
         * @method exportAsCsv
         */
        membershipContentCtrl.exportAsCsv = function () {
            $scope.$broadcast('MembershipTab.exportCSV');
        };
    }
]);
