(function () {

    /**
     * Controller for handling tables, including pagination, sorting by columns, and filtering.
     * @param $scope - binding between controller and HTML page
     * @param $filter - service for filtering and sorting
     */
    function TableJsController($scope, $filter) {

        $scope.columnSort = {};

        /**
         * Paginates a list of items.
         * @param {object[]} list - the unpaginated list
         * @returns {object[]} a paginated list
         */
        $scope.groupToPages = function (list) {
            var pagedList = [];
            if (list === null) {
                console.log("There is nothing here");
            } else {
                for (var i = 0; i < list.length; i++) {
                    if (i % $scope.itemsPerPage === 0) {
                        pagedList[Math.floor(i / $scope.itemsPerPage)] = [list[i]];
                    } else {
                        pagedList[Math.floor(i / $scope.itemsPerPage)].push(list[i]);
                    }
                }
            }
            return pagedList;
        };

        /**
         * Checks if a string contains a substring (case insensitive).
         * @param {string} str - the string to check
         * @param {string} substr - the substring to find
         * @returns {boolean} true if the string contains the substring. Otherwise returns false.
         */
        var searchMatch = function (str, substr) {
            if (!substr) return true;
            return str.toLowerCase().indexOf(substr.toLowerCase()) !== -1;
        };

        /**
         * Filters through a list given a user's query.
         * @param {object[]} list - the list to filter
         * @param {string} pagedListVar - the name of the variable containing the paginated list
         * @param {string} pageVar - the name of the variable containing the current page of the list
         * @param {string} queryVar - the name of the variable containing the user's query
         */
        $scope.filter = function (list, pagedListVar, pageVar, queryVar) {
            var query = $scope[queryVar];
            // Filters for items that match the user's query
            var filteredItems = $filter("filter")(list, function (item) {
                for (var key in item) {
                    // Ignore the 'basis' and '$$hashKey' properties, as well as non-string items
                    if (item.hasOwnProperty(key) && key !== "basis" && key !== "$$hashKey" && typeof(item[key]) === "string") {
                        if (searchMatch(item[key], query)) return true;
                    }
                }
            });
            // Resets the page number
            $scope[pageVar] = 0;
            // Paginates the filtered items
            $scope[pagedListVar] = $scope.groupToPages(filteredItems);
        };

        /**
         * Creates an array of numbers in [start, end) with step 1.
         * @param {number} size - the desired size of the array
         * @param {number} start - the start number
         * @param {number} end - the end number
         * @returns an array of numbers from start to end - 1.
         */
        $scope.range = function (size, start, end) {
            var ret = [];
            if (size < end) {
                end = size;
            }
            if (start < 0) {
                start = 0;
            }
            for (var i = start; i < end; i++) {
                ret.push(i);
            }
            return ret;
        };

        /**
         * Changes the current page for a paginated table.
         * @param {string} action - the action to take to change the page
         * @param {string} pageVar - the name of the variable containing the current page number
         * @param {string} listVar - the name of the variable contaning the paginated list
         */
        $scope.currentPage = function (action, pageVar, listVar) {
            console.log(action + " " + pageVar + " " + listVar);
            switch (action) {
                case "Next":
                    if ($scope[pageVar] < $scope[listVar].length - 1) {
                        $scope[pageVar] = $scope[pageVar] + 1;
                    }
                    break;
                case "Set":
                    $scope[pageVar] = this.n;
                    break;
                case "Prev":
                    if ($scope[pageVar] > 0) {
                        $scope[pageVar]--;
                    }
                    break;
                case "First":
                    $scope[pageVar] = 0;
                    break;
                case "Last":
                    if ($scope[pageVar] >= 0) {
                        $scope[pageVar] = $scope[listVar].length - 1;
                    }
                    break;
            }
        };

        /**
         * Sorts a table by a given property.
         * @param {string} tableName - the variable name of the table to sort
         * @param {string} pagedTableName - the variable name of the paginated table
         * @param {string} propertyName - the property to sort by
         */
        $scope.sortBy = function (tableName, pagedTableName, propertyName) {
            if (!$scope.columnSort[tableName]) {
                // If the user sorts by name property (typically the first column), then just reverse the direction
                if (propertyName === "name") {
                    $scope.columnSort[tableName] = { property: "name", reverse: true };
                } else {
                    // Otherwise, set the new property and sort in ascending order
                    $scope.columnSort[tableName] = { property: propertyName, reverse: false };
                }
            } else {
                // If the property to sort by is the same as what is already stored, then just invert the direction
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
