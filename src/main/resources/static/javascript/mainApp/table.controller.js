/* global _, UHGroupingsApp */

(() => {

    /**
     * Controller for handling tables, including pagination, sorting by columns, and filtering.
     * @param $scope - binding between controller and HTML page
     * @param $filter - service for filtering and sorting
     */
    function TableJsController($scope, $filter) {

        //Variables used to fill the tables:

        // Admin table variables
        $scope.adminsList = [];
        $scope.pagedItemsAdmins = [];
        $scope.currentPageAdmins = 0;

        // Person table variables
        $scope.personList = [];
        $scope.pagedItemsPerson = [];
        $scope.currentPagePerson = 0;

        // Manage memberships table variables
        $scope.membershipsList = [];
        $scope.pagedItemsMemberships = [];
        $scope.currentPageMemberships = 0;

        // Available memberships table variables
        $scope.optInList = [];
        $scope.pagedItemsOptInList = [];
        $scope.currentPageOptIn = 0;

        // Groupings table variables
        $scope.groupingsList = [];
        $scope.pagedItemsGroupings = [];
        $scope.currentPageGroupings = 0;

        // A grouping's members table variables
        $scope.groupingMembers = [];
        $scope.pagedItemsMembers = [];
        $scope.currentPageMembers = 0;

        // A grouping's basis table variables
        $scope.groupingBasis = [];
        $scope.pagedItemsBasis = [];
        $scope.currentPageBasis = 0;

        // A grouping's include table variables
        $scope.groupingInclude = [];
        $scope.pagedItemsInclude = [];
        $scope.currentPageInclude = 0;

        // A grouping's exclude table variables
        $scope.groupingExclude = [];
        $scope.pagedItemsExclude = [];
        $scope.currentPageExclude = 0;

        // A grouping's owners table variables
        $scope.groupingOwners = [];
        $scope.pagedItemsOwners = [];
        $scope.currentPageOwners = 0;

        $scope.columnSort = {};

        $scope.itemsPerPage = 20;
        $scope.gap = 2;

        const FILTER_COLUMNS_TO_IGNORE = ["basis", "$$hashKey"];
        const DEFAULT_COLUMN_NAME = "name";

        /**
         * Chunk an array of objects into an array of paged object arrays.
         * [{},{},{},{}] into [[{},{}],[{},{}]]
         */
        $scope.objToPageArray = (obj, size) => {
            let i = 0;
            let arr = [];
            while (i < obj.length) {
                arr.push(obj.slice(i, size + i));
                i += size;
            }
            return arr;
        };
        /**
         * Paginate a list of items.
         * @param {object} list The un-paginated list.
         * @returns {array} A paginated list, an array of arrays of objects with each sub array having a maximum of $scope.itemsperpage objects.
         */
        $scope.groupToPages = (list) => {
            if (!_.isArray(list) || $scope.itemsPerPage < 1) {
                return [];
            }
            let pagedList = [];
            for (let i = 0; i < list.length; i++) {
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
        const isFilterableColumn = (key) => {
            return !_.includes(FILTER_COLUMNS_TO_IGNORE, key);
        };

        /**
         * Check if a string contains a substring (case insensitive).
         * @param {string} str - the string to check
         * @param {string} substr - the substring to find
         * @returns {boolean} true if the string contains the substring. Otherwise returns false.
         */
        const containsSubstring = (str, substr) => {
            if (!substr) {
                return true;
            }
            return str.toLowerCase().indexOf(substr.toLowerCase()) !== -1;
        };

        /**
         * Filter through a list given a user's query.
         * If query is empty, then this is the same as $scope.pagedListVar = $scope.groupToPages(list)
         * @param {object[]} list - the list to filter
         * @param {string} pagedListVar - the name of the variable containing the paginated list
         * @param {string} pageVar - the name of the variable containing the current page of the list
         * @param {string} query - the user's search query
         * @param resetPage
         */
        $scope.filter = (list, pagedListVar, pageVar, query, resetPage) => {
            let filteredItems = $filter("filter")(list, (item) => {
                for (let key in item) {
                    if (_.has(item, key)
                        && isFilterableColumn(key)
                        && _.isString(item[key])
                        && containsSubstring(item[key], query)) {
                        return true;
                    }
                }
            });

            // Resets the page number
            if (resetPage) {
                $scope[pageVar] = 0;
            }
            // Paginates the filtered items
            $scope[pagedListVar] = $scope.groupToPages(filteredItems);
        };

        /**
         * @param {number} currentPage - the current page in the table
         * @param {number} totalPages - the total number of pages in the table
         * @param {number} gap - the max number of pages the user is able to see before and after the current page
         * @returns the page numbers (zero-indexed) that the user is able to see in the pagination control
         */
        $scope.pageRange = (currentPage, totalPages, gap) => {
            // Prevent pagination from starting and ending at nonexistent pages
            let start = (currentPage - gap < 0) ? 0 : currentPage - gap;
            let end = (currentPage + gap + 1 > totalPages) ? totalPages : currentPage + gap + 1;
            return _.range(start, end);
        };

        /**
         * Change the current page for a paginated table.
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
        $scope.disableFirstAndPrev = (currentPage) => {
            return currentPage === 0;
        };

        /**
         * @param {object[]} pagedTable - the paginated table
         * @param {number} currentPage - the current page in the table
         * @returns {boolean} true if the Next and Last buttons in the pagination controls should be disabled, otherwise
         * returns false
         */
        $scope.disableNextAndLast = (pagedTable, currentPage) => {
            return (pagedTable.length === 0) || (currentPage === pagedTable.length - 1);
        };

        /**
         * Sort a table by a given property.
         * @param {string} tableName - the variable name of the table to sort
         * @param {string} pagedTableName - the variable name of the paginated table
         * @param {string} propertyName - the property to sort by
         */
        $scope.sortBy = (tableName, pagedTableName, propertyName) => {
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
            let reverse = $scope.columnSort[tableName].reverse;
            $scope[tableName] = $filter("orderBy")($scope[tableName], propertyName, reverse);

            const tableNames = {
                adminsList: { pagedListVar: "pagedItemsAdmins", pageVar: "currentPageAdmins" , query: $scope.adminsQuery },
                groupingsList: { pagedListVar: "pagedItemsGroupings", pageVar: "currentPageGroupings", query: $scope.groupingsQuery },
                membershipsList: { pagedListVar: "pagedItemsMemberships", pageVar: "currentPageMemberships", query: $scope.membersQuery },
                optInList: { pagedListVar: "pagedItemsOptInList", pageVar: "currentPageOptIn", query: $scope.optInQuery },
                groupingMembers: { pagedListVar: "pagedItemsMembers", pageVar: "currentPageMembers", query: $scope.membersQuery },
                groupingBasis: { pagedListVar: "pagedItemsBasis", pageVar: "currentPageBasis", query: $scope.basisQuery },
                groupingInclude: { pagedListVar: "pagedItemsInclude", pageVar: "currentPageInclude", query: $scope.includeQuery },
                groupingExclude: { pagedListVar: "pagedItemsExclude", pageVar: "currentPageExclude", query: $scope.excludeQuery },
                groupingOwners: { pagedListVar: "pagedItemsOwners", pageVar: "currentPageOwners", query: $scope.ownersQuery }
            };

            if (!tableNames.hasOwnProperty(tableName)) {
                $scope[pagedTableName] = $scope.groupToPages($scope[tableName]);
                return;
            }
            const { pagedListVar, pageVar, query } = tableNames[`${tableName}`];
            $scope.filter($scope[tableName], pagedListVar, pageVar, query, false);
        };
    }

    UHGroupingsApp.controller("TableJsController", TableJsController);
})();
