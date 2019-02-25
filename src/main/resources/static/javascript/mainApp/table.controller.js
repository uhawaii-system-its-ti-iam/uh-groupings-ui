(function () {

    /**
     * Controller for handling tables, including pagination, sorting by columns, and filtering.
     * @param $scope - binding between controller and HTML page
     * @param $filter - service for filtering and sorting
     */
    function TableJsController($scope, $filter) {

        $scope.columnSort = {};

        $scope.itemsPerPage = 20;
        $scope.gap = 2;

        var FILTER_COLUMNS_TO_IGNORE = ["basis", "$$hashKey"];
        var DEFAULT_COLUMN_NAME = "name";

        /**
         * Paginates a list of items.
         * @param {object[]} list - the unpaginated list
         * @returns {object[]} a paginated list
         */
        $scope.groupToPages = function (list) {
            if (!_.isArray(list) || $scope.itemsPerPage < 1) {
                return [];
            }
            var pagedList = [];
            for (var i = 0; i < list.length; i++) {
                if (i % $scope.itemsPerPage === 0) {
                    pagedList[Math.floor(i / $scope.itemsPerPage)] = [list[i]];
                } else {
                    pagedList[Math.floor(i / $scope.itemsPerPage)].push(list[i]);
                }
            }
            return pagedList;
        };

        /**
         * @param {string} key - the key/column name to check
         * @returns {boolean} false if values in this key/column should be ignored when filtering for members/groupings,
         * otherwise returns true.
         */
        function isFilterableColumn(key) {
            return !_.includes(FILTER_COLUMNS_TO_IGNORE, key);
        }

        /**
         * Checks if a string contains a substring (case insensitive).
         * @param {string} str - the string to check
         * @param {string} substr - the substring to find
         * @returns {boolean} true if the string contains the substring. Otherwise returns false.
         */
        function containsSubstring(str, substr) {
            if (!substr) return true;
            return str.toLowerCase().indexOf(substr.toLowerCase()) !== -1;
        }

        /**
         * Filters through a list given a user's query.
         * If query is empty, then this is the same as $scope.pagedListVar = $scope.groupToPages(list)
         * @param {object[]} list - the list to filter
         * @param {string} pagedListVar - the name of the variable containing the paginated list
         * @param {string} pageVar - the name of the variable containing the current page of the list
         * @param {string} query - the user's search query
         */
        $scope.filter = function (list, pagedListVar, pageVar, query, resetPage) {
            // Filters for items that match the user's query
            var filteredItems = $filter("filter")(list, function (item) {
                for (var key in item) {
                    if (_.has(item, key)
                        && isFilterableColumn(key)
                        && _.isString(item[key])
                        && containsSubstring(item[key], query)) {
                        return true;
                    }
                }
            });

            // Resets the page number
            if(resetPage) $scope[pageVar] = 0;
            // Paginates the filtered items
            $scope[pagedListVar] = $scope.groupToPages(filteredItems);
        };

        /**
         * @param {number} currentPage - the current page in the table
         * @param {number} totalPages - the total number of pages in the table
         * @param {number} gap - the max number of pages the user is able to see before and after the current page
         * @returns the page numbers (zero-indexed) that the user is able to see in the pagination control
         */
        $scope.pageRange = function (currentPage, totalPages, gap) {
            // Prevent pagination from starting and ending at nonexistent pages
            var start = (currentPage - gap < 0) ? 0 : currentPage - gap;
            var end = (currentPage + gap + 1 > totalPages) ? totalPages : currentPage + gap + 1;
            return _.range(start, end);
        };

        /**
         * Changes the current page for a paginated table.
         * @param {string} action - the action to take to change the page
         * @param {string} pageVar - the name of the variable containing the current page number
         * @param {string} pagedListVar - the name of the variable contaning the paginated list
         */
        $scope.setPage = function (action, pageVar, pagedListVar) {
            switch (action) {
                case "First":
                    $scope[pageVar] = 0;
                    break;
                case "Prev":
                    if ($scope[pageVar] > 0) {
                        $scope[pageVar]--;
                    }
                    break;
                case "Set":
                    if (this.n >= 0 && this.n <= $scope[pagedListVar].length - 1) {
                        $scope[pageVar] = this.n;
                    }
                    break;
                case "Next":
                    if ($scope[pageVar] < $scope[pagedListVar].length - 1) {
                        $scope[pageVar] = $scope[pageVar] + 1;
                    }
                    break;
                case "Last":
                    $scope[pageVar] = $scope[pagedListVar].length - 1;
                    break;
            }
        };

        /**
         * @param {number} currentPage - the current page in the table
         * @returns {boolean} true if the First and Prev buttons in the pagination controls should be disabled,
         * otherwise returns false
         */
        $scope.disableFirstAndPrev = function (currentPage) {
            return currentPage === 0;
        };

        /**
         * @param {object[]} pagedTable - the paginated table
         * @param {number} currentPage - the current page in the table
         * @returns {boolean} true if the Next and Last buttons in the pagination controls should be disabled, otherwise
         * returns false
         */
        $scope.disableNextAndLast = function (pagedTable, currentPage) {
            return (pagedTable.length === 0) || (currentPage === pagedTable.length - 1);
        };

        /**
         * Sorts a table by a given property.
         * @param {string} tableName - the variable name of the table to sort
         * @param {string} pagedTableName - the variable name of the paginated table
         * @param {string} propertyName - the property to sort by
         */
        $scope.sortBy = function (tableName, pagedTableName, propertyName) {
            // Table has not been sorted by any column yet
            if (!$scope.columnSort[tableName]) {
                if (propertyName === DEFAULT_COLUMN_NAME) {
                    $scope.columnSort[tableName] = { property: DEFAULT_COLUMN_NAME, reverse: true };
                } else {
                    // Otherwise, set the new property and sort in ascending order
                    $scope.columnSort[tableName] = { property: propertyName, reverse: false };
                }
            } else {
                // Clicking on the same property will just reverse the direction
                if (propertyName === $scope.columnSort[tableName].property) {
                    $scope.columnSort[tableName].reverse = !$scope.columnSort[tableName].reverse;
                } else {
                    // Otherwise, set the new property and sort in ascending order
                    $scope.columnSort[tableName].property = propertyName;
                    $scope.columnSort[tableName].reverse = false;
                }
            }
            var reverse = $scope.columnSort[tableName].reverse;
            $scope[tableName] = $filter("orderBy")($scope[tableName], propertyName, reverse);
            // Paginate the table again
            $scope[pagedTableName] = $scope.groupToPages($scope[tableName]);
        };

    }

    UHGroupingsApp.controller("TableJsController", TableJsController);

})();
