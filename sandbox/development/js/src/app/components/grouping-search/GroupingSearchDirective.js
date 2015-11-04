angular.module('components.groupingSearch.uhgGroupingSearch', [
        'components.groupingSearch.GroupingSearchController',
        'stack.i18n.stackLocalize',
        'stack.i18n.localize'
    ])

    /**
     * The GroupSearch directive houses the application-wide group search mechanism.
     *
     * @class GroupSearch
     * @constructor
     * @module components.groupingSearch.uhgGroupingSearch
     * @example
     *     <uhg-grouping-search
     *         search-phrase="optional initial search phrase"       // specify initial search phrase to use
     *         on-search="someCtrl.someFunction(searchPhrase)">     // specify search handler - searchPhrase is param
     *     </uhg-grouping-search>
     */
    .directive('uhgGroupingSearch', [
        function () {
            'use strict';

            return {
                restrict: 'EA',
                scope: {
                    onSearch: '&',
                    groupingSearchPhrase: '@searchPhrase'
                },
                controller: 'GroupingSearchController',
                controllerAs: 'groupingSearchDirCtrl',
                bindToController: true,
                templateUrl: 'js/src/app/components/grouping-search/groupingSearch.html'
            };
        }
    ]);

