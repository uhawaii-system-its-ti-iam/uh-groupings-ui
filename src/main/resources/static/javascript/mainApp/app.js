/*  Main app that initializes the angular controller. */
"use strict";
const UHGroupingsApp = angular.module("UHGroupingsApp", ["ui.bootstrap"]);

UHGroupingsApp.controller("UHGroupingsApp",function($scope){
    $scope.message="msg";

});
UHGroupingsApp.controller("AdminJsController",function($scope){
    $scope.message="msg";

});

UHGroupingsApp.controller("MembershipJsController",function($scope){
    $scope.message="msg";

});

UHGroupingsApp.directive("fileModel", [ "$parse", function($parse) {
  return {
    restrict: "A",
    link(scope, element, attrs) {
      var model = $parse(attrs.fileModel);
      var modelSetter = model.assign;

      element.bind("change", function() {
        scope.$apply(function() {
          modelSetter(scope, element[0].files[0]);
        });
      });
    }
  };
} ]);
