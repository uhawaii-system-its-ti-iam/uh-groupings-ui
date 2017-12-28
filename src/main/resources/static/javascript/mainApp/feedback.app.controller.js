(function() {

    function FeedbackJsController($scope) {
        $scope.init = function() {
          console.log("test");
        };

        $scope.submit =function() {
            console.log($scope.name);
            console.log($scope.email);
            console.log($scope.description);
        }
    }
    UHGroupingsApp.controller("FeedbackJsController", FeedbackJsController);

})();
