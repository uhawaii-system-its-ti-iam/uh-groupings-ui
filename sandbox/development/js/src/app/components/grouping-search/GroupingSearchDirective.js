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
     *         search-phrase="optional initial search phrase"
     *         on-search="someCtrl.someFunction">           // way to specify search handler
     *     </uhg-grouping-search>
     */
    .directive('uhgGroupingSearch', [
        '$state',
        function ($state) {
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

