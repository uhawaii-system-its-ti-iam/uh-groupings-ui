/* global _, angular, UHGroupingsApp */

(() => {

    /**
     * This controller contains functions specific to the feedback page.
     * @param $scope - binding between controller and HTML page
     * @param $controller - service for instantiating controllers
     * @param Message - display messages
     */
    function FeedbackJsController($scope, $controller, Message) {
        // regex pattern for email to follow recipient@domain.TLD format
        $scope.emailPattern = new RegExp("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        // assigned value in groupingsService.getCurrentUser
        $scope.feedbackEmail = '';

        angular.extend(this, $controller("GeneralJsController", { $scope }));

        /**
         * Display a modal when an invalid email is entered on the feedback page
         */
        $scope.checkEmail = (event) => {
            if (!($scope.emailPattern.test($scope.feedbackEmail)) && event) {
                $scope.displayDynamicModal(Message.Title.INVALID_EMAIL_ERROR, Message.Body.INVALID_EMAIL_ERROR);
                event.preventDefault();
            }
        };
    }

    UHGroupingsApp.controller("FeedbackJsController", FeedbackJsController);
})();
