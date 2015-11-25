
angular.module('routes.groupingSearch.OwnershipTabController', [
        'stack.i18n',
        'components.orgUsersService.OrgUsersService'
    ])
    .controller('OwnershipTabController', [
        '$scope',
        'OrgUsersService',
        function ($scope, OrgUsersService) {
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
             * Property to house the users that can be added as owners
             *
             * @property nonOwnerUsers
             * @type {Array}
             */
            ctrl.nonOwnerUsers = [];

            // main thing we'll be working with is the grouping from the parent
            // copy it so that it makes things like switching tabs without saving easy to revert
            // note the use of $scope service here - but it's used to access groupingEditorCtrl instead of $parent...
            ctrl.grouping = angular.copy($scope.groupingEditorCtrl.grouping);

            OrgUsersService.list().then(function (users) {
                ctrl.nonOwnerUsers = users.filter(function (user) {
                    return ctrl.grouping.ownerMemberIds.indexOf(user.userId) === -1;
                });
            });

            /**
             * Method to persist the changes
             *
             * @method save
             */
            ctrl.addUser = function () {
                //TODO: Pop up a modal or display a select list populated by the list of non-owners
                //TODO: Once a user is added, remove them from the list of non-owners
            };
        }
    ]);
