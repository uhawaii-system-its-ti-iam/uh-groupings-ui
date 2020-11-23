(function () {
    function FeedbackJsController($scope, dataProvider) {
        $scope.init = function () {
            $scope.inFeedbackOnError = true;
        };
        $scope.redirectToFeedback = function (res) {
            $scope.inFeedbackOnError = true;
            dataProvider.handleException({
                exceptionMessage: JSON.stringify(res, null, 4)
            }, "feedback/error", "feedback");
        };
    }

    UHGroupingsApp.controller("FeedbackJsController", FeedbackJsController);
}());
