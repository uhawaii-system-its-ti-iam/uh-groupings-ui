(function () {
    /**
     * Controller for the planned maintenance modal. When the current date is two or one day away from the maintenance date, the modal will alert the user about the date and time which UH groupings will be down, and when it will be back up again.
     * @param $scope - Binding between controller and HTML page
     * @param $uibModal - Creates modal
     * @constructor
     */
    function OutageJsController($scope, $uibModal) {

        /**
         * Instatiating the planned maintenance, current, and
         * back online dates.
         */
        $scope.planned = new Date('2020-01-01'); // Set the date which UH groupings will be down.
        $scope.today = new Date(); // Current date.
        $scope.backOnline = new Date('2020-01-02'); // Set the date which UH groupings will be back online.

        /**
         * Setting the time UH groupings will be offline.
         */

        $scope.planned.setHours(1 + 24); // Set the hour which UH groupings will be down.
        // Using a 24 hour format.
        $scope.planned.setMinutes(25) // Set the minute which UH groupings will be down.

        /**
         * Calculating the days apart from planned and current date.
         */

        var Difference_In_Time = $scope.planned.getTime() - $scope.today.getTime(); // Calculate the time apart from planned date to current date.
        var Difference_In_Day = Difference_In_Time / (1000 * 3600 * 24);  // Calculate days apart from planned date to current date.
        var numDays = Math.round(Difference_In_Day);


        $scope.openPlannedOutageModal = function() {
            $scope.outageModalInstance = $uibModal.open({
                templateUrl: "modal/plannedMaintenanceModal",
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
            });

            $scope.cancelPlannedOutageModal = function() {
                $scope.outageModalInstance.close();
            }
        };

        /**
         * Modal should open if the current date is either
         * 2 days away or 1 day away.
         */
        if(numDays == 2 || numDays == 1) {
            $scope.openPlannedOutageModal();
        }

        /**
         * Setting the time UH groupings will be back online.
         */

        $scope.backOnline.setHours(2 + 24); // Set the hour which UH groupings will be back online.
        // Using 24 hour format.
        $scope.backOnline.setMinutes(10);


        /**
         * $scope.myvalue = false;
         * $scope.showAlert = function() {
         *     var numDays = 12;
         *     if(numDays != 12) {
         *         $scope.myvalue = true;
         *     }
         *     else {
         *         $scope.myvalue = false;
         *     }
         * }
         */


        /** menubar.html
         * <div ng-controller="OutageJsController">
         *     <div ng-show="myvalue" class="ng-cloak">
         *         [What is being shown]
         *         </div>
         * <div id="containerDetailsTicket" class="clearfix" ng-show="showAlert()">
         *     </div>
         *
         *
         * <div>
         */


    }
    UHGroupingsApp.controller("OutageJsController", OutageJsController);
})();



