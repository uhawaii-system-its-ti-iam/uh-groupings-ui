angular.module('routes.groupingSearch.GroupingEditorController', [
        'stack.i18n',
        'components.orgUsersService.OrgUsersService',
        'components.groupingsService.GroupingsService'
    ])

    /**
     * The GroupingEditorController houses state and view logic for the uhg-grouping-editor directive
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

            //define
            var groupingEditorCtrl;

            /**
             * Property to reference this controller instance
             *
             * @property groupingEditorCtrl
             * @type {Object}
             */
            groupingEditorCtrl = this;

            /**
             * Property to reference state of the View
             *
             * @type {Object}
             */
            groupingEditorCtrl.uiState = {
                isLoadingGrouping: true
            };

            /**
             * List of tabs shown in the view
             * @type {Array}
             */
            groupingEditorCtrl.tabs = [
                {
                    name: 'membership-tab',
                    viewUrl: 'js/src/app/routes/grouping-search/grouping-editor/membershipTab.html',
                    tabLabel: localize('GROUPING_SEARCH_MEMBERSHIP_TAB_HEADING'),
                    active: true
                },
                {
                    name: 'ownership-tab',
                    viewUrl: 'js/src/app/routes/grouping-search/grouping-editor/ownershipTab.html',
                    tabLabel: localize('GROUPING_SEARCH_OWNERSHIP_TAB_HEADING')
                },
                {
                    name: 'preferences-tab',
                    viewUrl: 'js/src/app/routes/grouping-search/grouping-editor/preferencesTab.html',
                    tabLabel: localize('GROUPING_SEARCH_PREFERENCES_TAB_HEADING')
                }
            ];

            /**
             * Method to load grouping data based on the id passed in
             *
             * @method loadGroupingData
             * @private
             */
            function loadGroupingData() {
                GroupingsService.getGroup(groupingEditorCtrl.groupingId).then(function (grouping) {
                    //assumes happy path of grouping being found...
                    grouping.owners = grouping.ownerMemberIds.map(getUserById);
                    grouping.basisMembers = grouping.basisMemberIds.map(getUserById);
                    grouping.includedMembers = grouping.includedMemberIds.map(getUserById);
                    grouping.excludedMembers = grouping.excludedMemberIds.map(getUserById);

                    //default members = (basisMembers + includedMembers) - excludedMembers
                    // NOTE: Assumes no overlap between basis and included
                    //      if this is incorrect assumption, can use lodash, etc... to get unique values
                    grouping.defaultMembers = grouping.basisMembers
                        .concat(grouping.includedMembers)
                        .filter(function (m) {
                            return grouping.excludedMemberIds.indexOf(m.userId) !== -1;
                        });

                    groupingEditorCtrl.grouping = grouping;
                    groupingEditorCtrl.uiState.isLoadingGrouping = false;
                });
            }

            /**
             * Method to get users based on id
             *
             * @method getUserById
             * @param id {Object|String} Pass in either User object or the User's Id
             * @returns {Object}
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


