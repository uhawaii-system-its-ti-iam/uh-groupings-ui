angular.module('routes.groupingSearch.GroupingSearchViewController', [
        'stack.page-loader',
        'stack.i18n'
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
        function ($timeout, $stateParams) {
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
             * Method calls out to a service that queries back end for matching groupings
             *
             * @method onSearch
             * @param searchPhrase {String}
             */
            ctrl.onSearch = function (searchPhrase) {
                //TODO: build/utilize service to query groupings
                console.log('groupingSearch:search:' + searchPhrase);
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
                        ctrl.search(ctrl.searchPhrase);
                    }
                    $timeout.cancel(t);
                }, 0);
            }
            initialize();
        }
    ]);

