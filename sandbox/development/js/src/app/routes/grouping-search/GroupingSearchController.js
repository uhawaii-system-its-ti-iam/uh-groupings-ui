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
    function ($timeout, $stateParams, GroupingsService) {
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
         * Property houses search result.
         * @type {Array}
         */
        ctrl.searchResults = [];

        /**
         * Property houses flags representing the current UI State.
         * @type {Object}
         */
        ctrl.uiState = {
            isSearching: false,
            hasSearched: false,
            showDrilldown: false
        };

        /**
         * Method to close editor view and go back to result-set from last search.
         *
         * @method backToResults
         */
        ctrl.backToResults = function () {
            ctrl.uiState.showDrilldown = false;
        };

        /**
         * Method calls out to a service that queries back-end for matching groupings.
         *
         * @method onSearch
         * @param {String} searchPhrase Pharse to search
         */
        ctrl.onSearch = function (searchPhrase) {
            ctrl.uiState.isSearching = true;
            ctrl.searchPhrase = searchPhrase;
            
            // Implementation only depicts the happy path.
            // Error handling was not implemented due to time constraints.
            GroupingsService.query(searchPhrase).then(function (groups) {
                // Using a timeout to make sure the loading spinner displays
                // long enough to not just be a flicker.
                $timeout(function () {
                    ctrl.searchResults = groups;
                    ctrl.uiState.isSearching = false;
                    ctrl.uiState.hasSearched = true;
                }, 1000);
            });
        };

        /**
         * Method to reset the search.
         *
         * @method clearSearchResults
         */
        ctrl.clearSearchResults = function () {
            ctrl.searchResults = [];
            ctrl.searchPhrase = '';
            ctrl.uiState.showDrilldown = ctrl.uiState.hasSearched = false;
        };

        /**
         * Method to handle initiating the edit state of a single grouping.
         *
         * @method editGrouping
         * @param {Object} grouping Grouping to edit
         */
        ctrl.editGrouping = function (grouping) {
            ctrl.selectedGrouping = grouping;
            ctrl.uiState.showDrilldown = true;
        };

        /**
         * Method to handle managing edit-state of groupings. This handler
         * is executed when the enter key is detected.
         *
         * @method editGroupingOnKeyDown
         * @param {Object} Event Event object
         * @param {Object} grouping Grouping object
         */
        ctrl.editGroupingOnKeyDown = function (e, grouping) {
            if (e.keyCode === 13) {
                ctrl.editGrouping(grouping);
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
                    ctrl.searchPhrase = $stateParams.searchPhrase;
                    ctrl.onSearch(ctrl.searchPhrase);
                }

                $timeout.cancel(t);
            }, 0);
        }
        initialize();
    }
]);
