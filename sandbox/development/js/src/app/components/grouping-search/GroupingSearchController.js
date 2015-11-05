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
        '$attrs',
        '$state',
        function ($attrs, $state) {
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
                //if it is defined, use it.
                if ($attrs.onSearch) {
                    //pass in the search phrase via named parameter argument
                    //this.onSearch is always defined - it's a proxy to the scope of the passed in function reference
                    this.onSearch({searchPhrase: this.groupingSearchPhrase});
                } else {
                    $state.go('groupingSearch', {searchPhrase: this.groupingSearchPhrase});
                }
            };
        }
    ]);

