angular.module('routes.groupingSearch.GroupingEditorController', [
    'stack.i18n',
    'components.orgUsersService.OrgUsersService',
    'components.groupingsService.GroupingsService'
])

/**
 * The GroupingEditorController houses state and view logic for the uhg-grouping-editor directive.
 *
 * @class GroupingEditorController
 * @constructor
 * @module routes.groupingSearch.GroupingEditorController
 **/
.controller('GroupingEditorController', [
    '$timeout',
    'OrgUsersService',
    'GroupingsService',
    'localizeFilter',
    function ($timeout, OrgUsersService, GroupingsService, localize) {
        'use strict';

        // Define.
        var groupingEditorCtrl;

        /**
         * Property to reference this controller instance.
         *
         * @property groupingEditorCtrl
         * @type {Object}
         */
        groupingEditorCtrl = this;

        /**
         * Property to reference state of the view.
         *
         * @property groupingEditorCtrl.uiState
         * @type {Object}
         */
        groupingEditorCtrl.uiState = {
            isLoadingGrouping: true
        };

        /**
         * List of tabs shown in the view.
         *
         * @property groupingEditorCtrl.uiState
         * @type {Array}
         */
        groupingEditorCtrl.tabs = [
            {
                name: 'membership-tab',
                viewUrl: 'js/src/app/routes/grouping-search/grouping-editor/membership/membershipTab.html',
                tabLabel: localize('GROUPING_SEARCH_MEMBERSHIP_TAB_HEADING'),
                active: true
            },
            {
                name: 'ownership-tab',
                viewUrl: 'js/src/app/routes/grouping-search/grouping-editor/ownership/ownershipTab.html',
                tabLabel: localize('GROUPING_SEARCH_OWNERSHIP_TAB_HEADING'),
                active: false
            },
            {
                name: 'preferences-tab',
                viewUrl: 'js/src/app/routes/grouping-search/grouping-editor/preferences/preferencesTab.html',
                tabLabel: localize('GROUPING_SEARCH_PREFERENCES_TAB_HEADING'),
                active: false
            }
        ];

        /**
         * Method to load grouping data based on the passed identifier.
         *
         * @method loadGroupingData
         * @private
         */
        function loadGroupingData() {
            // Implementation only depicts the happy path. Error handling was not implemented
            // due to time constraints.
            GroupingsService.getGroup(groupingEditorCtrl.groupingId).then(function (grouping) {
                grouping.owners = grouping.ownerMemberIds.map(getUserById);
                grouping.basisMembers = grouping.basisMemberIds.map(getUserById);
                grouping.includedMembers = grouping.includedMemberIds.map(getUserById);
                grouping.excludedMembers = grouping.excludedMemberIds.map(getUserById);

                groupingEditorCtrl.grouping = grouping;

                // Timeout renders loading spinner as more than a quick flicker.
                $timeout(function () {
                    groupingEditorCtrl.uiState.isLoadingGrouping = false;
                }, 200);
            });
        }

        /**
         * Method to get users based on identifier.
         *
         * @method getUserById
         * @param {Object|String} id Either user object or the user's identifier.
         * @returns {Object} User object
         * @private
         */
        function getUserById(id) {
            if ('userId' in id) {
                id = id.userId;
            }
            return groupingEditorCtrl.orgUsers.filter(function (u) {
                return u.userId === id;
            }).slice()[0];
        }

        /**
         * Method executes initialization process.
         *
         * @method initialize
         * @private
         */
        function initialize() {
            var t = $timeout(function () {
                OrgUsersService.list().then(function (orgUsers) {
                    groupingEditorCtrl.orgUsers = orgUsers;
                    loadGroupingData();
                });

                $timeout.cancel(t);
            }, 0);
        }
        initialize();
    }
]);
