
angular.module('routes.groupingSearch.MembershipTabController', [
        'routes.groupingSearch.DefaultGroupTabController',
        'routes.groupingSearch.BasisGroupTabController',
        'routes.groupingSearch.IncludeGroupTabController',
        'routes.groupingSearch.ExcludeGroupTabController',
        'stack.i18n'
    ])
    .controller('MembershipTabController', [
        '$scope',
        'localizeFilter',
        function ($scope, localize) {
            // define
            var ctrl;

            // alias controller
            ctrl = this;

            /**
             * Property to house the users that can be added as owners
             *
             * @property nonOwnerUsers
             * @type {Array}
             */
            ctrl.nonOwnerUsers = [];

            /**
             * Property to contain all tabs shown as sub-views to this controller's view
             *
             * @property tabs
             * @type {Array}
             */
            ctrl.tabs = [
                {
                    name: 'default-group-tab',
                    viewUrl: 'js/src/app/routes/grouping-search/grouping-editor/membership-tab/defaultGroupTab.html',
                    tabLabel: localize('MEMBERSHIP_TAB_DEFAULT_GROUP_TAB_HEADING'),
                    tabIconClass: 'fa fa-users text-info',
                    active: true
                },
                {
                    name: 'basis-group-tab',
                    viewUrl: 'js/src/app/routes/grouping-search/grouping-editor/membership-tab/basisGroupTab.html',
                    tabLabel: localize('MEMBERSHIP_TAB_BASIS_GROUP_TAB_HEADING'),
                    tabIconClass: 'fa fa-lock'
                },
                {
                    name: 'include-group-tab',
                    viewUrl: 'js/src/app/routes/grouping-search/grouping-editor/membership-tab/includeGroupTab.html',
                    tabLabel: localize('MEMBERSHIP_TAB_INCLUDE_GROUP_TAB_HEADING'),
                    tabIconClass: 'fa fa-plus-circle text-success'
                },
                {
                    name: 'exclude-group-tab',
                    viewUrl: 'js/src/app/routes/grouping-search/grouping-editor/membership-tab/excludeGroupTab.html',
                    tabLabel: localize('MEMBERSHIP_TAB_EXCLUDE_GROUP_TAB_HEADING'),
                    tabIconClass: 'fa fa-minus-circle text-danger'
                }
            ];

            /**
             * Method to trigger a message to broadcast down to children that an export needs to happen
             *
             * @method exportAsCsv
             */
            ctrl.exportAsCsv = function () {
                $scope.$broadcast('MembershipTab.exportCSV');
                /**
                 * Children controllers can get the $scope service injected and can respond to this event:
                 * $scope.$on('MembershipTab.exportCSV', function(event, data) {
                 *   //do whatever is needed to export the sub-tab's dataset to CSV
                 * });
                 */
            };
        }
    ]);

