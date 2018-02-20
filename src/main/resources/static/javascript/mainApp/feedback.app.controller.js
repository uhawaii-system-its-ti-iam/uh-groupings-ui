(function() {

    function FeedbackJsController($scope, $window, $uibModal, $http) {
        $scope.init = function() {
            $scope.email = document.getElementById("uemail").innerHTML + "@hawaii.edu";
            $scope.config = {
                headers : {
                    'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8;'
                }
            };
            $http.get('application.properties').then(function (response) {
              //console.log(response.data);

            });
        };


        $scope.submit = function() {
            var returnText = 'Please input the following missing element(s): ';
            var errors = 0;
            var onlyMail = 0;
            var onlyFT = 0;
            var URL = "https://www.test.hawaii.edu/uhgroupings/feedback/sendMail";
            var testURL = "http://localhost:8080/uhgroupings/feedback/sendMail"
            $scope.data = [];
            var optionVal = document.getElementById("feedback_type");
            var feedVal = optionVal.options[optionVal.selectedIndex].value;
            if(feedVal == "-- select an option --"){
                if (document.getElementById("reqFeed").style.display == 'none')
                {
                    document.getElementById("reqFeed").style.display = 'inline';
                    document.getElementById("typeError").className += ' has-error';
                }
                returnText += "Feedback Type, ";
                errors = errors + 1;
                onlyFT = onlyFT + 1;
            } else {
                document.getElementById("typeError").className = 'form-group';
                document.getElementById("reqFeed").style.display = 'none';
            }
            if($scope.email === undefined || $scope.email == ''){
                if (document.getElementById("reqMail").style.display == 'none')
                {
                    document.getElementById("reqMail").style.display = 'inline';
                    document.getElementById("emailError").className += ' has-error';
                }
                returnText += "Email, ";
                errors = errors + 1;
                onlyMail = onlyMail + 1;
            } else {
                document.getElementById("emailError").className = 'form-group';
                document.getElementById("reqMail").style.display = 'none';
            }
            if($scope.description === undefined || $scope.description == ''){
                if (document.getElementById("reqFB").style.display == 'none')
                {
                    document.getElementById("reqFB").style.display = 'inline';
                    document.getElementById("feedError").className += ' has-error';
                }
                returnText += "Feedback";
                errors = errors + 1;
            } else {
              document.getElementById("feedError").className = 'form-group';
              document.getElementById("reqFB").style.display = 'none';
            }
            if(errors > 0){
                if(onlyFT == 1 && errors == 1){
                    returnText = 'Please input the following missing element(s): Feedback Type';
                }
                if(onlyMail == 1 && errors == 1){
                    returnText = 'Please input the following missing element(s): Email';
                }
                if(onlyMail == 1 && onlyFT == 1 && errors == 2){
                    returnText = 'Please input the following missing element(s): Feedback Type, Email'
                }
                $scope.returnModal(returnText);
            } else {
                returnText = "Success! Thank you for your feedback."
                $scope.data = $.param({
                    "name": $scope.name,
                    "desc": $scope.description,
                    "email": $scope.email,
                    "type": optionVal.value
                });
                //console.log(typeof data);
                $scope.returnModal(returnText);
                $http.post(testURL, $scope.data, $scope.config);
                document.getElementById("reqFeed").style.display = 'none';
                document.getElementById("reqMail").style.display = 'none';
                document.getElementById("reqFB").style.display = 'none';
                document.getElementById("feedError").className = 'form-group';
                document.getElementById("typeError").className = 'form-group';
                document.getElementById("emailError").className = 'form-group';

                optionVal.value = "-- select an option --";
                $scope.name = '';
                $scope.description = '';
            }
        };

        $scope.returnModal = function (message) {
            var modalHtml = '<div class="modal-body">' + message + '</div>';
            modalHtml += '<div class="modal-footer"><button class="btn btn-primary" ng-click="errorDismiss()">OK</button></div>';
            $scope.returnModalInstance = $uibModal.open({
                template: modalHtml,
                windowClass: 'center-modal',
                scope: $scope
            });
        };

        $scope.errorDismiss = function () {
            $scope.returnModalInstance.dismiss();
        };
    }

    UHGroupingsApp.controller("FeedbackJsController", FeedbackJsController);

})();
