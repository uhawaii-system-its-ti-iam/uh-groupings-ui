angular.module('routes.groupingSearch.GroupingSearchController', [
        'stack.page-loader',
        'stack.i18n'
    ])

    /**
     * The GroupingSearchController manages view logic rendered to the
     * groupingSearch.html template for the /grouping-search route.
     *
     * @class GroupingSearchController
     * @constructor
     * @module routes.groupingSearch.GroupingSearchController
     **/
    .controller('GroupingSearchController', [
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
             * @param searchPhrase {String}
             */
            ctrl.search = function (searchPhrase) {
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

