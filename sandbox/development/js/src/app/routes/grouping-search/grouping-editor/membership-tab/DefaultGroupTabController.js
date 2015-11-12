
angular.module('routes.groupingSearch.DefaultGroupTabController', [
        'stack.i18n'
    ])
    .controller('DefaultGroupTabController', [
        '$scope',
        function ($scope) {
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

            // main thing we'll be working with is the grouping from the parent
            // note the use of $scope service here
            // but it's used to access groupingEditorCtrl instead of $parent.$parent..
            ctrl.grouping = $scope.groupingEditorCtrl.grouping;
        }
    ]);

