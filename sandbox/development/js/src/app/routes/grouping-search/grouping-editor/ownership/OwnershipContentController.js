angular.module('routes.groupingSearch.OwnershipContentController', [
    'stack.i18n',
    'components.orgUsersServices.OrgUsersService'
])

/**
 * The OwnershipContentController manages view logic rendered to the
 * ownershipContent.html template.
 *
 * @class OwnershipContentController
 * @constructor
 * @module routes.groupingSearch.OwnershipContentController
 */
.controller('OwnershipContentController', [
    '$scope',
    'OrgUsersService',
    function ($scope, OrgUsersService) {
        // Define.
        var ownershipContentController;

        /**
         * Property houses a reference to the controller.
         *
         * @property ownershipContentController
         * @type {Object}
         */
        ownershipContentController = this;

        /**
         * Property to track the sort field for our table of grouping owners.
         *
         * @property ownershipContentController.sortField
         * @type {String}
         */
        ownershipContentController.sortField = '+firstName';

        /**
         * Property to house the users that can be added as owners.
         *
         * @property ownershipContentController.nonOwnerUsers
         * @type {Array}
         */
        ownershipContentController.nonOwnerUsers = [];

        /**
         * Property houses grouping collection.
         *
         * @property ownershipContentController.grouping
         * @type {Object}
         */
        ownershipContentController.grouping = angular.copy($scope.groupingEditorCtrl.grouping);

        /**
         * Method to easily control adjusting the sort of the owners table.
         *
         * @method ownershipContentController.changeSort
         * @param {String} newSort Field name
         */
        ownershipContentController.changeSort = function (newSort) {
            // If current sort minus sign equals new sort, then we're just swapping direction.
            if (ownershipContentController.sortField.substr(1) === newSort) {
                ownershipContentController.sortField = (ownershipContentController.sortField[0] === '+' ? '-' : '+') + newSort;
            } else {
                ownershipContentController.sortField = '+' + newSort;
            }
        };

        /**
         * Method handler to add a user.
         *
         * @method ownershipContentController.addUser
         */
        ownershipContentController.addUser = function () {
            console.log('Add user.');
        };

        /**
         * Method executes initialization process.
         *
         * @method initialize
         * @private
         */
        function initialize() {
            // Populate array of non-owner users.
            // Only accounts for the happy path and does not address
            // error conditions.
            OrgUsersService.list().then(function (users) {
                ownershipContentController.nonOwnerUsers = users.filter(function (user) {
                    return ownershipContentController.grouping.ownerMemberIds.indexOf(user.userId) === -1;
                });
            });
        }
        initialize();
    }
]);
