angular.module('routes.groupingSearch.DefaultGroupContentController', [
    'stack.i18n'
])

/**
 * The DefaultGroupContentController manages view logic rendered to the
 * defaultGroupContent.html template.
 *
 * @class DefaultGroupContentController
 * @constructor
 * @module routes.groupingSearch.DefaultGroupContentController
 */
.controller('DefaultGroupContentController', [
    '$scope',
    function ($scope) {
        // Define.
        var defaultGroupContentController;

        /**
         * Property houses a reference to the controller.
         *
         * @property defaultGroupContentController
         * @type {Object}
         */
        defaultGroupContentController = this;

        /**
         * Property to track the sort field for our table of grouping owners.
         *
         * @property defaultGroupContentController.sortField
         * @type {String}
         */
        defaultGroupContentController.sortField = '+firstName';

        /**
         * Property houses grouping collection.
         *
         * @property defaultGroupContentController.grouping
         * @type {Object}
         */
        defaultGroupContentController.grouping = $scope.groupingEditorCtrl.grouping;

        /**
         * Method to easily control adjusting the sort of the owners table.
         *
         * @method changeSort
         * @param {String} newSort Field name
         */
        defaultGroupContentController.changeSort = function (newSort) {
            // If current sort minus sign equals new sort, then we're just swapping direction.
            if (defaultGroupContentController.sortField.substr(1) === newSort) {
                defaultGroupContentController.sortField = (defaultGroupContentController.sortField[0] === '+' ? '-' : '+') + newSort;
            } else {
                defaultGroupContentController.sortField = '+' + newSort;
            }
        };

        /**
         * Method executes initialization process.
         *
         * @method initialize
         * @private
         */
        function initialize() {
            // default members = ((basisMembers + includedMembers) - excludedMembers)
            // NOTE: This implementation assumes no overlap between basis and included.
            // NOTE: We specify the sourceGroup attribute so that we know the source
            // of each group.
            //
            // TODO: move this code into a service.
            defaultGroupContentController.grouping.defaultMembers = defaultGroupContentController.grouping.basisMembers.map(function (m) {
                    m.sourceGroup = 'Basis';
                    return m;
                })
                .concat(defaultGroupContentController.grouping.includedMembers.map(function (m) {
                    m.sourceGroup = 'Include';
                    return m;
                }))
                .filter(function (m) {
                    return defaultGroupContentController.grouping.excludedMemberIds.indexOf(m.userId) === -1;
                }
            );
        }
        initialize();
    }
]);
