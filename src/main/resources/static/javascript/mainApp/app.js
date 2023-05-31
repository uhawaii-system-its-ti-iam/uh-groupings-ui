/*  Main app that initializes the angular controller. */
/* global angular */
"use strict";
const UHGroupingsApp = angular.module("UHGroupingsApp", ["ui.bootstrap"]);

UHGroupingsApp.controller("UHGroupingsApp", ($scope) => {
    $scope.message = "msg";
});
