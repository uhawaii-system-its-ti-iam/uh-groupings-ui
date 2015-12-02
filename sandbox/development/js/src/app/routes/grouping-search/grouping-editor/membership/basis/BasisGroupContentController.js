angular.module('routes.groupingSearch.BasisGroupContentController', [
    'stack.i18n'
])

/**
 * The BasisGroupContentController manages view logic rendered to the
 * basisGroupContent.html template.
 *
 * @class BasisGroupContentController
 * @constructor
 * @module routes.groupingSearch.BasisGroupContentController
 */
.controller('BasisGroupContentController', [
    '$scope',
    function ($scope) {
        // Define.
        var basisGroupContentController;

        /**
         * Property houses a reference to the controller.
         *
         * @property basisGroupContentController
         * @type {Object}
         */
        basisGroupContentController = this;

        /**
         * Property to track the sort field for our table of grouping owners.
         *
         * @property basisGroupContentController.sortField
         * @type {String}
         */
        basisGroupContentController.sortField = '+firstName';

        /**
         * Property houses grouping collection.
         *
         * @property basisGroupContentController.grouping
         * @type {Object}
         */
        basisGroupContentController.grouping = $scope.groupingEditorCtrl.grouping;

        /**
         * Method to easily control adjusting the sort of the owners table.
         *
         * @method changeSort
         * @param {String} newSort Field name
         */
        basisGroupContentController.changeSort = function (newSort) {
            // If current sort minus sign equals new sort, then we're just swapping direction.
            if (basisGroupContentController.sortField.substr(1) === newSort) {
                basisGroupContentController.sortField = (basisGroupContentController.sortField[0] === '+' ? '-' : '+') + newSort;
            } else {
                basisGroupContentController.sortField = '+' + newSort;
            }
        };
    }
]);
