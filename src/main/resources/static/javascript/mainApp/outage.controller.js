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
        $scope.planned = new Date('2020-01-05'); // Set the date which UH groupings will be down.
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


        /**
         * Modal should open if the current date is either
         * 2 days away or 1 day away.
         */
        $scope.myvalue = false;
        if(numDays == 2 || numDays == 1 || numDays == 0) {
            $scope.showAlert = function() {
                $scope.myvalue = true;
            }
        }
        else {
            $scope.myvalue = false;
        }

        $scope.cancelPlannedOutage = function() {
            $scope.showAlert = function () {
                $scope.myvalue = false;
            }
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



