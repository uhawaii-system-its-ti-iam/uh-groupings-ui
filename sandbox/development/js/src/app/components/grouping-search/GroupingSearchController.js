angular.module('components.groupingSearch.GroupingSearchController', [
    'stack.location.LocationService'
])

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
    'LocationService',
    function ($attrs, $state, LocationService) {
        'use strict';

        // Define.
        var groupingSearchCtrl;

        /**
         * Property to reference controller instance.
         *
         * @property groupingSearchCtrl
         * @type {Object}
         */
        groupingSearchCtrl = this;

        /**
         * Property to contain the search phrase used in the query.
         *
         * @property groupingSearchCtrl.groupingSearchPhrase
         * @type {String}
         */
        groupingSearchCtrl.groupingSearchPhrase = groupingSearchCtrl.groupingSearchPhrase || '';

        /**
         * Method to allow executing the search when pressing enter key from within search box.
         *
         * @method groupingSearchCtrl.onKeyUp
         * @param {Object} event Event object
         */
        groupingSearchCtrl.onKeyUp = function (event) {
            var keycode = event.keyCode || event.which;

            if (keycode === 13) {
                groupingSearchCtrl.search();
            }
        };

        /**
         * Method performs the search with the provided search phrase.
         * If an 'onSearch' handler has been provided, it is used,
         * otherwise it defaults to navigating to the groupSearch state.
         *
         * @method search
         */
        groupingSearchCtrl.search = function () {
            // Directive can get reference to a function passed in as an event handler.
            // When a function is defined, use it.
            if ($attrs.onSearch && angular.isFunction($attrs.onSearch)) {
                // Pass in the search phrase via named parameter argument.
                // this.onSearch is always defined - it's a proxy to the scope of the passed in function reference.
                groupingSearchCtrl.onSearch({searchPhrase: groupingSearchCtrl.groupingSearchPhrase});
            } else {
                LocationService.redirect({
                    route: 'grouping-search',
                    params: {
                        searchPhrase: groupingSearchCtrl.groupingSearchPhrase
                    }
                });
            }
        };
    }
]);
