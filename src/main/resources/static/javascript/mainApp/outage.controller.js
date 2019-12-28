(function () {

    function OutageJsController($scope, $uibModal) {

        /**
         * Planned Maintenance Warning Modal
         */


        $scope.planned = new Date('2020-01-02'); // Set the date which UH groupings will be down.
        $scope.today = new Date(); // Current date.
        $scope.backOnline = new Date('2020-01-02'); // Set the date which UH groupings will be back online.

        var Difference_In_Time = $scope.planned.getTime() - $scope.today.getTime(); // Calculate the time apart from planned date to current
        var Difference_In_Day = Difference_In_Time / (1000 * 3600 * 24); //
        var numDays = Math.round(Difference_In_Day);

        /**
         * Setting time of planned maintenance.
         */
        $scope.planned.setHours(1 + 24); // Set the hour which UH groupings will be down.
        // Using a 24 hour format.
        $scope.planned.setMinutes(25) // Set the minute which UH groupings will be down.

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

        if(numDays == 5) {
            $scope.openPlannedOutageModal();
        }

        /**
         * Setting the time UH groupings will be back online.
         */

        $scope.backOnline.setHours(2 + 24); // Set the hour which UH groupings will be back online.
        // Using 24 hour format.
        $scope.backOnline.setMinutes(10);




    }
    UHGroupingsApp.controller("OutageJsController", OutageJsController);
})();

