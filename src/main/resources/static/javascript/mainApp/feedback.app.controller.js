(function() {

    function FeedbackJsController($scope, $uibModal) {
        /**
        * init function. used for initializing the feedback page.
        */
        $scope.init = function() {
        //   console.log("test");
        };

        /**
        * submit function. takes in name, email, descritpion. and then a feedback modal tells it when it is a success
        */
        $scope.submit =function() {
            // console.log($scope.name);
            // console.log($scope.email);
            // console.log($scope.description);
            $scope.feedbackModal();
        };


        /**
        * the feedback modal. it also resets the values of the text fields
        */
        $scope.feedbackModal = function () {
            $scope.name = "";
            $scope.email = "";
            $scope.description = "";
            $scope.feedbackModalInstance = $uibModal.open({
                templateUrl: 'modal/feedbackModal.html',
                windowClass: 'center-modal',
                scope: $scope
            });
        };
        /**
        * a helper for the modal dismissal
        */
        $scope.feedbackDismiss = function () {
            $scope.feedbackModalInstance.dismiss();
        };
    }
    UHGroupingsApp.controller("FeedbackJsController", FeedbackJsController);

})();
