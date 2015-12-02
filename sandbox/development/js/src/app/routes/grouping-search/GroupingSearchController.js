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
    'protect',
    'GroupingsService',
    function ($timeout, $stateParams, protect, GroupingsService) {
        'use strict';

        // Define.
        var groupingSearchViewCtrl;

        /**
         * Property houses a reference to the grouping search controller.
         *
         * @property groupingSearchViewCtrl
         * @type {Object}
         */
        groupingSearchViewCtrl = this;

        /**
         * Property houses search result.
         *
         * @property groupingSearchViewCtrl.searchResults
         * @type {Array}
         */
        groupingSearchViewCtrl.searchResults = [];

        /**
         * Property houses flags representing the current UI State.
         *
         * @property groupingSearchViewCtrl.uiState
         * @type {Object}
         */
        groupingSearchViewCtrl.uiState = {
            isSearching: false,
            hasSearched: false,
            showDrilldown: false
        };

        /**
         * Property houses a reference to authenticated user object.
         *
         * @property groupingSearchViewCtrl.user
         * @type {Object}
         */
        groupingSearchViewCtrl.user = protect;

        /**
         * Method to close editor view and go back to result-set from last search.
         *
         * @method groupingSearchViewCtrl.backToResults
         */
        groupingSearchViewCtrl.backToResults = function () {
            groupingSearchViewCtrl.uiState.showDrilldown = false;
        };

        /**
         * Method calls out to a service that queries back-end for matching groupings.
         *
         * @method groupingSearchViewCtrl.onSearch
         * @param {String} searchPhrase Pharse to search
         */
        groupingSearchViewCtrl.onSearch = function (searchPhrase) {
            groupingSearchViewCtrl.uiState.isSearching = true;
            groupingSearchViewCtrl.searchPhrase = searchPhrase;

            // Implementation only depicts the happy path.
            // Error handling was not implemented due to time constraints.
            GroupingsService.query(searchPhrase).then(function (groups) {
                // Using a timeout to make sure the loading spinner displays
                // long enough to not just be a flicker.
                $timeout(function () {
                    groupingSearchViewCtrl.searchResults = groups;
                    groupingSearchViewCtrl.uiState.isSearching = false;
                    groupingSearchViewCtrl.uiState.hasSearched = true;
                }, 1000);
            });
        };

        /**
         * Method to reset the search.
         *
         * @method groupingSearchViewCtrl.clearSearchResults
         */
        groupingSearchViewCtrl.clearSearchResults = function () {
            groupingSearchViewCtrl.searchResults = [];
            groupingSearchViewCtrl.searchPhrase = '';
            groupingSearchViewCtrl.uiState.showDrilldown = groupingSearchViewCtrl.uiState.hasSearched = false;
        };

        /**
         * Method to handle initiating the edit state of a single grouping.
         *
         * @method groupingSearchViewCtrl.editGrouping
         * @param {Object} grouping Grouping to edit
         */
        groupingSearchViewCtrl.editGrouping = function (grouping) {
            groupingSearchViewCtrl.selectedGrouping = grouping;
            groupingSearchViewCtrl.uiState.showDrilldown = true;
        };

        /**
         * Method to handle managing edit-state of groupings. This handler
         * is executed when the enter key is detected.
         *
         * @method groupingSearchViewCtrl.editGroupingOnKeyDown
         * @param {Object} Event Event object
         * @param {Object} grouping Grouping object
         */
        groupingSearchViewCtrl.editGroupingOnKeyDown = function (e, grouping) {
            if (e.keyCode === 13) {
                groupingSearchViewCtrl.editGrouping(grouping);
            }
        };

        /**
         * Method executes initialization process.
         *
         * @method initialize
         * @private
         */
        function initialize() {
            var t = $timeout(function () {
                if (($stateParams.searchPhrase || '').length > 0) {
                    // A search is executed when this view is first initialized and a search
                    // phrase has been entered prior to loading the Search view. In other words,
                    // when a user enters a search phrase in a different view other then on the
                    // Search screen and clicks the search button.
                    groupingSearchViewCtrl.searchPhrase = $stateParams.searchPhrase;
                    groupingSearchViewCtrl.onSearch(groupingSearchViewCtrl.searchPhrase);
                }

                $timeout.cancel(t);
            }, 0);
        }
        initialize();
    }
]);
