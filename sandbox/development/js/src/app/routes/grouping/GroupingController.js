angular.module('routes.grouping.GroupingController', [
    'stack.page-loader',
    'stack.i18n',
    'stack.authentication.AuthenticationService',
    'components.groupingsService.GroupingsService'
])

/**
 * The GroupingController manages view logic rendered to the
 * grouping.html template for the /grouping route.
 *
 * @class GroupingController
 * @constructor
 * @module routes.grouping.GroupingController
 **/
.controller('GroupingController', [
    '$timeout',
    'translate',
    'AuthenticationService',
    'GroupingsService',
    function ($timeout, translate, AuthenticationService, GroupingsService) {
        'use strict';

        // Define.
        var groupingCtrl;

        /**
         * Property houses a reference to the grouping controller.
         *
         * @property groupingCtrl
         * @type {Object}
         */
        groupingCtrl = this;

        /**
         * Property houses flags indicating current state of the UI.
         *
         * @property uiState
         * @type {Object}
         */
        groupingCtrl.uiState = {
            isLoadingGroupings: true
        };

        /**
         * Method to handle managing edit-state of groupings.
         *
         * @method editGrouping
         * @param {Object} grouping Grouping object
         */
        groupingCtrl.editGrouping = function (grouping) {
            console.log('GroupingsOwned:editGrouping', grouping);
        };

        /**
         * Method to handle managing edit-state of groupings. This handler
         * is executed when the enter key is detected.
         *
         * @method editGrouping
         * @param {Object} Event Event object
         * @param {Object} grouping Grouping object
         */
        groupingCtrl.editGroupingOnKeyDown = function (e, grouping) {
            if (e.keyCode === 13) {
                groupingCtrl.editGrouping(grouping);
            }
        };

        /**
         * Method executes loading of owned groups.
         *
         * @method loadOwnedGroups
         * @private
         */
        function loadOwnedGroups() {
            // Implementation only depicts the happy path. Error handling was not implemented
            // due to time constraints.
            AuthenticationService.getUser().then(function (user) {
                GroupingsService.getOwnedGroups(user.username).then(function (groups) {
                    groupingCtrl.ownedGroups = groups;
                    groupingCtrl.uiState.isLoadingGroupings = true;
                });
            });
        }

        /**
         * Method executes initialization process.
         *
         * @method initialize
         * @private
         */
        function initialize() {
            var t = $timeout(function () {
                loadOwnedGroups();
                // Call implementations here. Timeout is needed in order
                // for all potentially nested directives to execute.
                $timeout.cancel(t);
            }, 0);
        }
        initialize();
    }
]);
