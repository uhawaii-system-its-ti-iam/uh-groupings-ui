angular.module('app.AppController', [
    'app.AppService'
])

/**
 * The AppController is the top-most controller for the application.
 *
 * @class AppController
 * @constructor
 * @module app.AppController
 **/
.controller('AppController', [
    'AppService',
    function (AppService) {
        'use strict';

        // Define.
        var appCtrl;

        /**
         * Property houses a reference to the AppController.
         *
         * @property appCtrl
         * @type {Object}
         */
        appCtrl = this;

        /**
         * Property houses a collection of navigation objects.
         *
         * @property appCtrl.navigation
         * @type {Array}
         */
        appCtrl.navigation = AppService.navigation;
    }
]);
