angular.module('routes.groupingSearch.GroupingSearchViewController', [
        'stack.page-loader',
        'stack.i18n',
        'components.groupingsService.GroupingsService'
    ])

    /**
     * The GroupingSearchViewController manages view logic rendered to the
     * groupingSearch.html template for the /grouping-search route.
     *
     * @class GroupingSearchViewController
     * @constructor
     * @module routes.groupingSearch.GroupingSearchViewController
     **/
    .controller('GroupingSearchViewController', [
        '$timeout',
        '$stateParams',
        'GroupingsService',
        'orgUsers',
        function ($timeout, $stateParams, GroupingsService, orgUsers) {
            'use strict';

            // Define.
            var ctrl;

            /**
             * Property houses a reference to the grouping search controller.
             *
             * @property ctrl
             * @type {Object}
             */
            ctrl = this;

            /**
             * Property to house result-set
             * @type {Array}
             */
            ctrl.searchResults = [];

            /**
             * Property to house flags representing the current UI State
             * @type {Object}
             */
            ctrl.uiState = {
                isSearching: false,
                hasSearched: false,
                isLoadingDrilldown: false,
                showDrilldown: false
            };

            /**
             * Method calls out to a service that queries back end for matching groupings
             *
             * @method onSearch
             * @param searchPhrase {String}
             */
            ctrl.onSearch = function (searchPhrase) {
                ctrl.uiState.isSearching = true;
                GroupingsService.query(searchPhrase).then(function (groups) {
                    //using timeout to make sure the loading spinner displays long enough to not just be a flicker
                    $timeout(function () {
                        ctrl.searchResults = groups;
                        ctrl.uiState.isSearching = false;
                        ctrl.uiState.hasSearched = true;
                    }, 1000);
                });
            };

            /**
             * Method to handle initiating the edit state of one grouping
             *
             * @method editGrouping
             * @param grouping {Object}
             */
            ctrl.editGrouping = function (grouping) {
                ctrl.uiState.isLoadingDrilldown = ctrl.uiState.showDrilldown = true;
                GroupingsService.getGroup(grouping.id).then(function (grouping) {
                    grouping.basisMembers = grouping.basisMemberIds.map(getUserById);
                    grouping.includedMembers = grouping.includedMemberIds.map(getUserById);
                    grouping.excludedMembers = grouping.excludedMemberIds.map(getUserById);

                    //default members = (basisMembers + includedMembers) - excludedMembers
                    grouping.defaultMembers = grouping.basisMembers
                        .concat(grouping.includedMembers)
                        .filter(function (m) {
                            return grouping.excludedMemberIds.indexOf(m.userId) !== -1;
                        });

                    ctrl.selectedGrouping = grouping;
                    ctrl.uiState.isLoadingDrilldown = false;
                });
            };

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
                return orgUsers.filter(function (u) {
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
                    if (($stateParams.searchPhrase || '').length > 0) {
                        //only matters for initial setting of uhg-grouping-search search-phrase
                        ctrl.searchPhrase = $stateParams.searchPhrase;
                        ctrl.onSearch(ctrl.searchPhrase);
                    }
                    $timeout.cancel(t);
                }, 0);
            }
            initialize();
        }
    ]);

