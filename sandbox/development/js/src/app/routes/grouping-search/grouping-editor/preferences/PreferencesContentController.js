angular.module('routes.groupingSearch.PreferencesContentController', [
    'stack.i18n',
    'components.groupingsService.GroupingsService'
])

/**
 * The PreferencesContentController manages view logic rendered to the
 * preferencesContent.html template.
 *
 * @class PreferencesContentController
 * @constructor
 * @module routes.groupingSearch.PreferencesContentController
 */
.controller('PreferencesContentController', [
    '$scope',
    function ($scope) {
        // Define.
        var preferencesContentController;

        /**
         * Property houses a reference to the controller.
         *
         * @property preferencesContentController
         * @type {Object}
         */
        preferencesContentController = this;

        /**
         * Property houses grouping collection.
         *
         * @property preferencesContentController.grouping
         * @type {Object}
         */
        preferencesContentController.grouping = angular.copy($scope.groupingEditorCtrl.grouping);

        /**
         * Method handler to save preferences.
         *
         * @method save
         */
        preferencesContentController.save = function () {
            // NOTE: We are just persisting in memory for demo purposes.
            $scope.groupingEditorCtrl.grouping.options.canAddSelf = preferencesContentController.grouping.options.canAddSelf;
            $scope.groupingEditorCtrl.grouping.options.canRemoveSelf = preferencesContentController.grouping.options.canRemoveSelf;
            $scope.groupingEditorCtrl.grouping.options.includeInListServe = preferencesContentController.grouping.options.includeInListServe;

            // Set the form state back to pristine. This will disable the
            // Save Options button and inform the user that we have completed
            // the save operation.
            $scope.groupingPreferencesForm.$setPristine();
        };
    }
]);
