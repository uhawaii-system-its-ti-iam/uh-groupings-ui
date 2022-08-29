/*  Main app that initializes the angular controller. */
/* global angular */
"use strict";
const UHGroupingsApp = angular.module("UHGroupingsApp", ["ui.bootstrap"]);

UHGroupingsApp.controller("UHGroupingsApp", function ($scope) {
    $scope.message = "msg";
});

UHGroupingsApp.controller("AdminJsController", function ($scope) {
    $scope.message = "msg";
});

UHGroupingsApp.controller("MembershipJsController", function ($scope) {
    $scope.message = "msg";
});
