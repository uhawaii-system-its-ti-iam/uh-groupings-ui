angular.module('components.groupingSearch.GroupingSearchController', [])

    /**
     * The GroupingSearchController houses state and view logic
     * for the uhg-group-search directive.
     *
     * @class GroupingSearchController
     * @constructor
     * @module components.groupingSearch.GroupingSearchController
     **/
    .controller('GroupingSearchController', [
        '$state',
        function ($state) {
            'use strict';

            /**
             * Property to contain the search phrase used in the query.
             *
             * @property groupingSearchPhrase
             * @type {String}
             */
            this.groupingSearchPhrase = this.groupingSearchPhrase || '';

            /**
             * Method performs the search with the provided search phrase.
             * If an 'onSearch' handler has been provided, it is used,
             * otherwise it defaults to navigating to the groupSearch state.
             * @method search
             */
            this.search = function () {
                //directive can get reference to a function passed in as evt handler
                //if it is defined and is a function, use it.
                if (this.onSearch && angular.isFunction(this.onSearch)) {
                    //pass in the search phrase
                    this.onSearch({searchPhrase: this.groupingSearchPhrase});
                } else {
                    $state.go('groupingSearch', {searchPhrase: this.groupingSearchPhrase});
                }
            };
        }
    ]);

