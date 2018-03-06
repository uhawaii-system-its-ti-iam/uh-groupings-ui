(function () {

    /**
     * This controller contains functions specific to the groupings page.
     * @param $scope - binding between controller and HTML page
     * @param $controller - service for instantiating controllers
     * @param $uibModal - the UI Bootstrap service for creating modals
     * @param dataProvider - service function that provides GET and POST requests for getting or updating data
     */
    function OwnerJsController($scope, $window, $controller, $uibModal, dataProvider) {

        $scope.gap = 2;
        $scope.itemsPerPage = 20;

        // Allow this controller to use functions from the General Controller
        angular.extend(this, $controller('GeneralJsController', {$scope: $scope}));

        /**
         * Initialize function that retrieves the groupings you own.
         */
        $scope.init = function () {
            $scope.loading = true;
            var groupingsOwned = "api/groupings/groupingAssignments";

            dataProvider.loadData(function (d) {
                var temp = [];
                if (typeof d.groupingsIn === 'undefined') {
                    $scope.createApiErrorModal();
                } else {
                    // Assigns grouping name and url used for api call.
                    for (var i = 0; i < d.groupingsOwned.length; i++) {
                        $scope.groupingsList.push({
                            'name': d.groupingsOwned[i].name,
                            'path': d.groupingsOwned[i].path
                        });
                    }
                    $scope.pagedItemsGroupings = $scope.groupToPages($scope.groupingsList, $scope.pagedItemsGroupings);
                }
                $scope.pagedItemsGroupings = $scope.groupToPages($scope.groupingsList, $scope.pagedItemsGroupings);

                $scope.loading = false;
            }, function (error) {
                console.log(error);
                console.log('Error in loadData; status: ' + error.status);

                //Sends JAVA


                //$window.location.href = 'feedback';
            }, groupingsOwned);
        };

        /**
         * Creates a modal for errors in loading data from the API.
         */
        $scope.createApiErrorModal = function () {
            $scope.apiErrorModalInstance = $uibModal.open({
                templateUrl: 'modal/apiError.html',
                scope: $scope
            });
        };

        /**
         * Closes the API error modal.
         */
        $scope.closeApiError = function () {
            $scope.apiErrorModalInstance.close();
        };

        /**
         * Gets information about the grouping clicked by the user.
         * @param {number} index - the index of the grouping selected, with the current page and items per page taken
         * into account
         */
        $scope.showData = function (index) {
            $scope.selectedGrouping = $scope.groupingsList[index];
            if (!$scope.showGrouping) {
                $scope.showGrouping = true;
                $scope.getData();
            } else {
                $scope.showGrouping = false;
            }
        };

        /**
         *  Retrieves the information about grouping selected.
         *  Assigns basis group, basisPlusIncludeMinusExclude group, include group, exclude group,
         *  owners list and grouping privileges.
         */
        $scope.getData = function () {
            getUrl = "api/groupings/" + $scope.selectedGrouping.path + "/grouping";
            $scope.loading = true;
            dataProvider.loadData(function (d) {
                console.log(d);
                $scope.basis = d.basis.members;

                //Gets members in grouping
                $scope.groupingMembers = d.composite.members;
                $scope.modify($scope.groupingMembers, 'members');
                $scope.pagedItemsMembers = $scope.groupToPages($scope.groupingMembers, $scope.pagedItemsMembers);

                //Gets members in the basis group
                $scope.groupingBasis = d.basis.members;
                $scope.modify($scope.groupingBasis);
                $scope.pagedItemsBasis = $scope.groupToPages($scope.groupingBasis, $scope.pagedItemsBasis);

                //Gets members in the include group
                $scope.groupingInclude = d.include.members;
                $scope.modify($scope.groupingInclude);
                $scope.pagedItemsInclude = $scope.groupToPages($scope.groupingInclude, $scope.pagedItemsInclude);

                //Gets members in the exclude group
                $scope.groupingExclude = d.exclude.members;
                $scope.modify($scope.groupingExclude);
                $scope.pagedItemsExclude = $scope.groupToPages($scope.groupingExclude, $scope.pagedItemsExclude);

                //Gets owners of the grouping
                $scope.groupingOwners = d.owners.members;
                $scope.modify($scope.groupingOwners);
                $scope.pagedItemsOwners = $scope.groupToPages($scope.groupingOwners, $scope.pagedItemsOwners);

                $scope.allowOptIn = d.optInOn;
                $scope.allowOptOut = d.optOutOn;
                $scope.listserv = d.listservOn;

                //Stop loading spinner
                $scope.loading = false;
            }, function (error) {
                console.log(error);
                console.log('Error in loadData; status: ' + error.status);

                //Sends JAVA


                $window.location.href = 'feedback';
            }, getUrl);
        };

        /**
         * Modify the data from the grouping to be sorted, filter out hawaii.edu
         * and determines if a user is in the basis group or not.
         *
         * @param grouping - The name of the grouping of which its data will be modified.
         *
         * @returns returns
         *                1 for ascending
         *                -1 for descending
         *                0 for failed attempt
         */
        $scope.modify = function (grouping, list) {
            //Filter out names with hawaii.edu and adds basis object.
            for (var i = 0; i < grouping.length; i++) {
                if (list === 'members') grouping[i].basis = "Include";
                else grouping[i].basis = "No";

                if (grouping[i].name.indexOf("hawaii.edu") > -1) {
                    grouping.splice(i, 1);
                    i--;
                }
            }

            //Determines if member is in the basis or not
            for (var l = 0; l < $scope.basis.length; l++) {
                for (var m = 0; m < grouping.length; m++) {
                    if ($scope.basis[l].uuid === grouping[m].uuid) {
                        if (list === 'members') {
                            grouping[m].basis = "Basis";
                            for (var k = 0; k < $scope.groupingInclude.length; k++) {
                                if ($scope.groupingInclude[k].uuid === grouping[m].uuid) {
                                    grouping[m].basis = "Basis / Include";
                                }
                            }
                        }
                        else grouping[m].basis = "Yes";
                    }
                }
            }

            grouping.sort(function (a, b) {
                var nameA = a.name.toLowerCase(), nameB = b.name.toLowerCase();
                if (nameA < nameB) //sort string ascending
                    return -1;
                if (nameA > nameB)
                    return 1;
                return 0
            });

            $scope.replaceBlankUsernames(grouping);
        };

        /**
         *  Sorts the data in the table in ascending or descending order based on
         *  the list and column being sorted.
         *
         * @param list - The data list to which will be sorted
         * @param col - The object to name to determine how it will be sorted by.
         * @param listPaged - The paged data list to which the sorted list will go into.
         * @param symbol - The symbol to tell user if they are sorting in ascending or descending order.
         */
        $scope.sortCol = function (list, col, listPaged, symbol) {
            $scope.symbol = {'name': '', 'url': '', 'uuid': '', 'username': ''};

            if ($scope[symbol] === '\u25B2' || typeof $scope[symbol] == 'undefined') {
                list = _.sortBy(list, col);
                $scope[listPaged] = $scope.groupToPages(list, $scope[listPaged]);
                $scope[symbol] = '\u25BC';
            } else {
                list = _.sortBy(list, col).reverse();
                $scope[listPaged] = $scope.groupToPages(list, $scope[listPaged]);
                $scope[symbol] = '\u25B2';
            }
            switch (col) {
                case 'name':
                    $scope.symbol.name = '\u21c5';
                    break;
                case 'url':
                    $scope.symbol.url = '\u21c5';
                    break;
                case 'uuid':
                    $scope.symbol.uuid = '\u21c5';
                    break;
                case 'username':
                    $scope.symbol.username = '\u21c5';
                    break;
                case 'basis':
                    $scope.symbol.basis = '\u21c5';
            }
        };

        /**
         * Creates a modal telling the user whether or not the user was successfully added into the grouping/admin list.
         * @param {string} user - the user being added
         * @param {boolean} wasSuccessful - whether or not the user was successfully added
         * @param {string} path - the path to the grouping
         */
        $scope.createAddModal = function (user, wasSuccessful, path) {
            $scope.user = user;
            $scope.wasSuccessful = wasSuccessful;

            $scope.addModalInstance = $uibModal.open({
                templateUrl: 'modal/addModal.html',
                scope: $scope,
            });

            $scope.addModalInstance.result.finally(function () {
                if (wasSuccessful) {
                    $scope.loading = true;
                    $scope.getData(path);
                }
            });
        };

        /**
         * Creates a modal that prompts the user whether they want to delete the user or not. If 'Yes' is pressed, then
         * a request is made to delete the user.
         * @param {string} user - the user to delete
         * @param {string} url - the URL used to make the request
         * @param {string} path - the path to the grouping
         */
        $scope.createRemoveModal = function (user, url, path) {
            $scope.userToDelete = user;
            $scope.removeModalInstance = $uibModal.open({
                templateUrl: 'modal/removeModal.html',
                scope: $scope
            });

            $scope.removeModalInstance.result.then(function () {
                $scope.loading = true;
                // Remove the user, then reload the grouping
                dataProvider.updateData(function () {
                    $scope.getData(path);
                }, url);
            });
        };

    }

    UHGroupingsApp.controller("OwnerJsController", OwnerJsController);

})();
