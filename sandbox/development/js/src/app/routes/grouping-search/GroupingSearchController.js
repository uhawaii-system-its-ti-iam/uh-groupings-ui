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
                hasSearched: false
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
                console.log('GroupingSearchCtrl:editGrouping', grouping);
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

