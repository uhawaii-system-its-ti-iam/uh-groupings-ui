angular.module('routes.groupingSearch.PreferencesTabController', [
    'stack.i18n',
    'components.groupingsService.GroupingsService'
])
    .controller('PreferencesTabController', [
        '$scope',
        function ($scope) {
            // define
            var ctrl;

            // alias controller
            ctrl = this;

            // main thing we'll be working with is the grouping from the parent
            // copy it so that it makes things like switching tabs without saving easy to revert
            // note the use of $scope service here - but it's used to access groupingEditorCtrl instead of $parent...
            ctrl.grouping = angular.copy($scope.groupingEditorCtrl.grouping);

            /**
             * Method to persist the changes
             *
             * @method save
             */
            ctrl.save = function () {
                //TODO: Build for-realz persistence, for now just updating in memory
                $scope.groupingEditorCtrl.grouping.options.canAddSelf = ctrl.grouping.options.canAddSelf;
                $scope.groupingEditorCtrl.grouping.options.canRemoveSelf = ctrl.grouping.options.canRemoveSelf;
                $scope.groupingEditorCtrl.grouping.options.includeInListServe = ctrl.grouping.options.includeInListServe;

                //set the form state back to pristine to disable the button and indicate save done
                $scope.groupingPreferencesForm.$setPristine();
            };
        }
    ]);
