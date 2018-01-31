(function() {

    function FeedbackJsController($scope, $window, $uibModal, $http) {
        $scope.init = function() {
            $scope.email = document.getElementById("uemail").innerHTML + "@hawaii.edu";
            $scope.config = {
                headers : {
                    'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8;'
                }
            }

            //console.log($http);
        };

        $scope.submit = function() {
            var returnText = 'Please input the following missing element(s): ';
            var errors = 0;
            var onlyMail = 0;
            var onlyFT = 0;
            var URL = "http://localhost:8080/uhgroupings/feedback/sendMail/";
            $scope.data = [];
            var optionVal = document.getElementById("feedback_type");
            var feedVal = optionVal.options[optionVal.selectedIndex].value;
            if(feedVal == "-- select an option --"){
                if (document.getElementById("reqFeed").style.display == 'none')
                {
                    document.getElementById("reqFeed").style.display = 'inline';
                }
                returnText += "Feedback Type, ";
                errors = errors + 1;
                onlyFT = onlyFT + 1;
            }
            if($scope.email === undefined || $scope.email == ''){
                if (document.getElementById("reqMail").style.display == 'none')
                {
                    document.getElementById("reqMail").style.display = 'inline';
                }
                returnText += "Email, ";
                errors = errors + 1;
                onlyMail = onlyMail + 1;
            }
            if($scope.description === undefined || $scope.description == ''){
                if (document.getElementById("reqFB").style.display == 'none')
                {
                    document.getElementById("reqFB").style.display = 'inline';
                }
                returnText += "Feedback";
                errors = errors + 1;
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
                $http.post('https://localhost:8080/uhgroupings/feedback/sendMail/', $scope.data, $scope.config);
                document.getElementById("reqFeed").style.display = 'none';
                document.getElementById("reqMail").style.display = 'none';
                document.getElementById("reqFB").style.display = 'none';

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
