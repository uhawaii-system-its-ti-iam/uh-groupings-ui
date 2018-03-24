(function () {

    /**
     * This controller contains functions specific to the groupings page.
     * @param $scope - binding between controller and HTML page
     * @param $controller - service for instantiating controllers
     * @param $uibModal - the UI Bootstrap service for creating modals
     * @param dataProvider - service function that provides GET and POST requests for getting or updating data
     */
    function OwnerJsController($scope, $controller, $window, $uibModal, dataProvider) {

        $scope.gap = 2;
        $scope.itemsPerPage = 20;

        // Allow this controller to use functions from the General Controller
        angular.extend(this, $controller('GeneralJsController', { $scope: $scope }));

        /**
         * Initialize function that retrieves the groupings you own.
         */
        $scope.init = function () {
            $scope.loading = true;
            var groupingsOwned = "api/groupings/groupingAssignment";

            dataProvider.loadData(function (d) {
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
                    $scope.pagedItemsGroupings = $scope.groupToPages($scope.groupingsList);
                }
                $scope.loading = false;
            }, function (d) {
                    console.log("error has occurred");
                    console.log(d);
                    var error = encodeURI(d.message);
                    $window.location.href = "/uhgroupings/feedback/" + error;
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
         * Switches from showing that data of the grouping you own to the information about the grouping selected.
         *
         * @param row - row of the grouping with relation to the table.
         */
        $scope.showData = function (row) {
            $scope.selectedGrouping = $scope.pagedItemsGroupings[$scope.currentPageGroupings][row];
            //URLS being used in the api calls.
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
            }, function(d){
                console.log("error has occured");
                console.log(d);
                var error = encodeURI(d);
                $window.location.href = "/uhgroupings/feedback/" + error;
            }, getUrl);
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

            $scope.addModalInstance.result.finally(function() {
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
