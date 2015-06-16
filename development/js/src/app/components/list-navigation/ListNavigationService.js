angular.module('components.list-navigation.ListNavigationService', [])

/**
 * The ListNavigationService provides methods and parsing functionality
 * for the ListNavigation directive.
 *
 * @class ListNavigationService
 * @constructor
 * @module components.list-navigation.ListNavigationService
 */
.factory('ListNavigationService', [
    function () {
        'use strict';

        var service = {
            /**
             * Method returns the index position of a navigation object
             * given the passed route name and the collection of navigation
             * objects contained within the items array.
             *
             * @method getNavigationPositionFromRoute
             * @param {String} route Route name
             * @param {Array} items Collection of navigation objects
             * @return {Number} Index position of navigation object
             */
            getNavigationPositionFromRoute: function (route, items) {
                var position = 0;

                angular.forEach(items, function (obj, index) {
                    if (obj.route === route) {
                        position = index;
                    }
                });

                return position;
            }
        };

        return service;
    }
]);
