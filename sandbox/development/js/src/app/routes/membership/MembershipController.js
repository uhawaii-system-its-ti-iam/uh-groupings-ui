angular.module('routes.membership.MembershipController', [
    'stack.page-loader',
    'stack.i18n',
    'stack.authentication.AuthenticationService',
    'components.groupingsService.GroupingsService'
])

/**
 * The MembershipController manages view logic rendered to the
 * membership.html template for the /membership route.
 *
 * @class MembershipController
 * @constructor
 * @module routes.membership.MembershipController
 **/
.controller('MembershipController', [
    '$timeout',
    'translate',
    'AuthenticationService',
    'GroupingsService',
    function ($timeout, translate, AuthenticationService, GroupingsService) {
        'use strict';

        // Define.
        var membershipCtrl;

        /**
         * Property houses a reference to the membership controller.
         *
         * @property membershipCtrl
         * @type {Object}
         */
        membershipCtrl = this;

        /**
         * Property houses flags indicating current state of the UI.
         *
         * @property uiState
         * @type {Object}
         */
        membershipCtrl.uiState = {
            isLoadingGroupings: true
        };

        /**
         * Method to handle managing edit-state of groupings.
         *
         * @method editGrouping
         * @param {Object} grouping Grouping object
         */
        membershipCtrl.editGrouping = function (grouping) {
            console.log('GroupingsMembership:editGrouping', grouping);
        };

        /**
         * Method to handle managing edit-state of groupings. This handler
         * is executed when the enter key is detected.
         *
         * @method editGroupingOnKeyDown
         * @param {Object} Event Event object
         * @param {Object} grouping Grouping object
         */
        membershipCtrl.editGroupingOnKeyDown = function (e, grouping) {
            if (e.keyCode === 13) {
                membershipCtrl.editGrouping(grouping);
            }
        };

        /**
         * Method executes loading of owned groups.
         *
         * @method loadGroupingMemberships
         * @private
         */
        function loadGroupingMemberships() {
            // Implementation only depicts the happy path. Error handling was not implemented
            // due to time constraints.
            AuthenticationService.getUser().then(function (user) {
                GroupingsService.getGroupMemberships(user.username).then(function (groups) {
                    membershipCtrl.groupingMemberships = groups;
                    membershipCtrl.uiState.isLoadingGroupings = false;
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
                loadGroupingMemberships();
                // Call implementations here. Timeout is needed in order
                // for all potentially nested directives to execute.
                $timeout.cancel(t);
            }, 0);
        }
        initialize();
    }
]);
