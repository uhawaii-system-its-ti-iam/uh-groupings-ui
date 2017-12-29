(function() {

    function FeedbackJsController($scope, $uibModal) {
        $scope.init = function() {
          console.log("test");
        };

        $scope.submit =function() {
            console.log($scope.name);
            console.log($scope.email);
            console.log($scope.description);
            $scope.feedbackModal();
        };

        $scope.feedbackModal = function () {
            $scope.name = "";
            $scope.email = "";
            $scope.description = "";
            $scope.feedbackModalInstance = $uibModal.open({
                templateUrl: 'feedbackModal.html',
                windowClass: 'center-modal',
                scope: $scope
            });
        };

        $scope.feedbackDismiss = function () {
            $scope.feedbackModalInstance.dismiss();
        };
    }
    UHGroupingsApp.controller("FeedbackJsController", FeedbackJsController);

})();
