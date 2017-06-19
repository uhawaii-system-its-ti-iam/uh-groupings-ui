(function () {
    /**
     * Admin controller for the entire admin page.
     *
     * @param $scope        : Binding variable between controller and html page.
     * @param dataProvider  : service function that acts as the AJAX get.
     * @param dataUpdater   : service function that acts as AJAX post, used mainly for adding or updating
     * @param dataDelete    : service function that acts as AJAX psst, use function mainly for delete function.
     * @constructor
     */
    function AdminJsController($scope, dataProvider, dataUpdater, dataDelete) {
        var currentUser = "aaron";//document.getElementById("name").innerHTML;
        var url = "api/groupings/tmp:win-aux/" + currentUser + "/grouping";
        $scope.list = [];
        //Adds the loading spinner
        $scope.loading = true;

        /**
         * Initializing function for the admin page.
         * Calls dataProvider service to retrieve a list of admins.
         * @returns returns
         *                1 for ascending
         *                -1 for descending
         *                0 for failed attempt
         */
        $scope.init = function () {
            dataProvider.loadData(function (d) {
                var tempList = d.basisPlusIncludeMinusExclude.members;

                //sorts the data by name
                tempList.sort(function (a, b) {
                    var nameA = a.name.toLowerCase(), nameB = b.name.toLowerCase();
                    if (nameA < nameB) //sort string ascending
                        return -1;
                    if (nameA > nameB)
                        return 1;
                    return 0
                });

                var basis = d.basis.members;

                //Default add everyone as not in basis
                for (var k = 0; k < tempList.length; k++) {
                    tempList[k].basis = "\u2716";
                }

                //Adds whether or not a member is in the basis group or not
                for (var l = 0; l < basis.length; l++) {
                    for (var m = 0; m < tempList.length; m++) {
                        if (basis[l].name === tempList[m].name) {
                            tempList[m].basis = "\u2714";
                        }
                    }
                }

                $scope.list = tempList;
                $scope.loading = false;
                console.log($scope.list);
            }, url);
        };


        /**
         * Adds function that adds an member to the admin grouping.
         * Uses dataUpdater service to post the user that is being added.
         * If user is successfully added, dataProvider will return a Success in result Code.
         * Else if resultCode is undefined, then user was not successfully added.
         */
        $scope.add = function () {
            var addUrl = "api/groupings/hawaii.edu:custom:test:aaronvil:aaronvil-test/" + currentUser + "/" + $scope.username + "/addMemberToIncludeGroup";
            $scope.testdata = [];

            if (confirm("You are adding " + $scope.username + " to the include list of this grouping")) {
                dataUpdater.updateData(function (d) {
                    console.log(d);
                    if (d.resultCode === 'SUCCESS') {
                        console.log("Success In Adding");
                        //reload data table
                        $scope.loading = true;
                        $scope.init();
                    }
                    else if (typeof d.resultCode === 'undefined') {
                        console.log("Failure In Adding");
                    }
                }, addUrl);
            }
        };

        /**
         * Remove function uses dataDelete Service to remove user from admin grouping.
         * Will not delete admin if there is only one admin in the list.
         *
         * @param index, the index of the user based on the html table.
         */
        $scope.remove = function (index) {
            var deleteUser = $scope.list[index].username;
            var deleteUrl = "api/groupings/hawaii.edu:custom:test:aaronvil:aaronvil-test/" + currentUser + "/" + deleteUser + "/deleteMemberFromIncludeGroup";
            if ($scope.list.length > 1) {
                dataDelete.deleteData(function (d) {
                    $scope.list.splice(index, 1);
                    $scope.init();
                }, deleteUrl);
            }
        };
    }

    adminApp.controller("AdminJsController", AdminJsController);
})();
