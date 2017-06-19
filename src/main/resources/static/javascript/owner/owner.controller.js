(function () {

    /**
     * Owner controller for the groupings page
     *
     * @param $scope        : Binding variable between controller and html page.
     * @param dataProvider  : service function that acts as the AJAX get.
     * @param dataUpdater   : service function that acts as AJAX post, used mainly for adding or updating
     * @param dataDeleter    : service function that acts as AJAX psst, use function mainly for delete function.
     * @constructor
     */
    function OwnerJsController($scope, dataProvider, dataUpdater, dataDeleter) {
        var currentUser = document.getElementById("name").innerText;
        var groupingsOwned = "api/groupings/" + currentUser + "/myGroupings";
        var getUrl;

        $scope.ownedList = [];
        $scope.groupingsList = [];
        $scope.groupingsBasis = [];
        $scope.groupingInclude = [];
        $scope.groupingExclude = [];
        $scope.ownerList = [];
        $scope.pref = [];
        $scope.showGrouping = false;
        $scope.loading = true;
        $scope.groupingName = '';

        /**
         * Initialize function that retrieves the groupings you own.
         */
        $scope.init = function () {
            dataProvider.loadData(function (d) {
                var temp = [];
                //Assigns grouping name, folder directories and url used for api call.
                for (var i = 0; i < d.groupingsOwned.length; i++) {
                    temp[i] = d.groupingsOwned[i].path.substr(18).split(':');
                    var folder = '';
                    for (var j = 0; j < temp[i].length - 1; j++) {
                        folder += temp[i][j];
                        if (j != temp[i].length - 2) {
                            folder += "/";
                        }
                    }
                    $scope.ownedList.push({
                        'name': d.groupingsOwned[i].name,
                        'folder': folder,
                        'url': d.groupingsOwned[i].path
                    });
                }
                $scope.loading = false;
            }, groupingsOwned);
        };

        /**
         * Switches from showing that data of the grouping you own to the information about the grouping selected.
         *
         * @param row : row of the grouping with relation to the table.
         */
        $scope.showData = function (row) {
            $scope.groupingName = $scope.ownedList[row];
            //URLS being used in the api calls.
            if ($scope.showGrouping == false) {
                $scope.showGrouping = true;
                $scope.getData();
            }
            else {
                $scope.showGrouping = false;
            }
        };

        /**
         *  Retrieves the information about grouping selected.
         *  Assigns basis group, basisPlusIncludeMinusExclude group, include group, exclude group,
         *  owners list and grouping privileges.
         */
        $scope.getData = function () {
            getUrl = "api/groupings/" + $scope.groupingName.url + "/" + currentUser + "/grouping";
            $scope.loading = true;
            dataProvider.loadData(function (d) {
                console.log(d);
                $scope.basis = d.basis.members;

                //Gets members in grouping
                $scope.groupingsList = d.basisPlusIncludeMinusExclude.members;
                $scope.modify($scope.groupingsList);

                //Gets members in the basis group
                $scope.groupingsBasis = d.basis.members;
                 $scope.modify($scope.groupingsBasis);

                //Gets members in the include group
                $scope.groupingInclude = d.include.members;
                $scope.modify($scope.groupingInclude);

                //Gets members in the exclude group
                $scope.groupingExclude = d.exclude.members;
                $scope.modify($scope.groupingExclude);

                //Gets owners of the grouping
                $scope.ownerList = d.owners.members;

                $scope.pref = d.hasListserv;
                if ($scope.pref == true) {
                    $('#listserv').prop("checked", true);
                }

                //Stop loading spinner
                $scope.loading = false;
            }, getUrl);
        };

        /**
         * Modify the data from the grouping to be sorted, filter out hawaii.edu
         * and determines if a user is in the basis group or not.
         *
         * @param grouping : The name of the grouping of which its data will be modified.
         *
         * @returns returns
         *                1 for ascending
         *                -1 for descending
         *                0 for failed attempt
         */
        $scope.modify = function (grouping) {
            //sorts data in alphabetic order
            grouping.sort(function (a, b) {
                var nameA = a.name.toLowerCase(), nameB = b.name.toLowerCase();
                if (nameA < nameB) //sort string ascending
                    return -1;
                if (nameA > nameB)
                    return 1;
                return 0
            });

            //Filter out names with hawaii.edu and adds basis object.
            for (var i = 0; i < grouping.length; i++) {
                grouping[i].basis = "\u2716";
                if (grouping[i].name.includes("hawaii.edu")) {
                    grouping.splice(i, 1);
                    i--;
                }
            }

            //Determines if member is in the basis or not
            for (var l = 0; l < $scope.basis.length; l++) {
                for (var m = 0; m < grouping.length; m++) {
                    if ($scope.basis[l].name === grouping[m].name) {
                        grouping[m].basis = "\u2714";
                    }
                }
            }
        };

        /**
         *  Function that switches from the view of a single grouping
         *  to the list of groupings that you own.
         */
        $scope.showGroups = function () {
            if ($scope.showGrouping == false) {
                $scope.showGrouping = true;
            }
            else {
                $scope.showGrouping = false;
                $scope.groupingName = ''
            }
        };

        /**
         * Adds member to the include or exclude group.
         * If user is successful in adding, then alerts success.
         * Otherwise alert that the user does not exist
         *
         * @param type : the type of group that the user is being added into. Include or exclude.
         */
        $scope.addMember = function (type) {
            var addUrl = "api/groupings/" + $scope.groupingName.url + "/" + currentUser + "/" + $scope.addUser + "/addMemberTo" + type + "Group";
            dataUpdater.addData(function (d) {
                if (d.resultCode === "SUCCESS") {
                    console.log("success in adding " + $scope.addUser);
                    alert("SUCCESS IN ADDING " + $scope.addUser);
                    $scope.getData();
                }
                else if (typeof d.resultsCode === 'undefined') {
                    console.log($scope.addUser + " this user does not exist.");
                    alert($scope.addUser + " this user does not exist.");
                }
            }, addUrl);
            $scope.addUser = '';
        };

        /**
         * Removes member from the include group or exclude groups. Completely removes them from the group.
         *
         * @param type : type of group that the user is being added into. Include or exclude.
         * @param row  : index of the user in the respected group array.
         */
        $scope.removeMember = function (type, row) {
            var user;
            if(type === 'Include'){
                user = $scope.groupingInclude[row].username;
            }
            if(type === 'Exclude'){
                user = $scope.groupingExclude[row].username;
            }

            var URL = "api/groupings/" + $scope.groupingName.url + "/" + currentUser + "/" + user + "/deleteMemberFrom" + type + "Group";
            dataDeleter.deleteData(function (d) {
                console.log(d);
                $scope.getData();
            }, URL);
        };

        /**
         * Removes ownership of a grouping from an user
         *
         * @param index : The index of the member in the ownerList array.
         */
        $scope.removeOwner = function (index) {
            var removeOwner = $scope.ownerList[index].username;
            var removeOwnerUrl = "api/groupings/" + $scope.groupingName.url + "/" + currentUser + "/" + removeOwner + "/removeOwnership";
            if ($scope.ownerList.length > 1) {
                dataDeleter.deleteData(function (d) {
                    $scope.getData();
                }, removeOwnerUrl);
            }
        };

        /**
         * Gives a user ownership of a grouping.
         * If the user is successfully assigned ownership, then alerts success.
         * Otherwise alerts that the user does not exist.
         */
        $scope.addOwner = function () {
            var addOwnerUrl = "api/groupings/" + $scope.groupingName.url + "/" + currentUser + "/" + $scope.ownerUser + "/assignOwnership";
            dataUpdater.addData(function (d) {
                if (d[0].resultCode === "SUCCESS") {
                    console.log("Assigned " + $scope.ownerUser + " as an owner");
                    alert("Assigned " + $scope.ownerUser + " as an owner");
                    $scope.getData();
                }
                else if (typeof d[0].resultsCode === 'undefined') {
                    console.log($scope.ownerUser + " this user does not exist.");
                    alert($scope.ownerUser + " this user does not exist.");
                }
            }, addOwnerUrl);
            $scope.ownerUser = '';
        };

        /**
         * Saves changes made to grouping privileges
         */
        $scope.savePref = function () {
            if (confirm("Are you sure you want to save")) {
                if ($('#addOption').is(':checked')) {
                    console.log("You are allowing members to opt in your grouping")
                }
                else {
                    console.log("You are not allowing members to opt in your grouping")
                }
                if ($('#removeOption').is(':checked')) {
                    console.log("You are allowing members to exclude themselves from your groupings")
                }
                else {
                    console.log("You are not allowing members to exclude themselves from your groupings")
                }
                if ($('#listserv').is(':checked')) {
                    console.log("LISTSERV is true")
                }
                else {
                    console.log("LISTSERV is false")
                }
            }
        };

        /**
         * Export data in table to a CSV file
         *
         * @param type : type of group being exported
         * @param name : name of the group. i.e. include or exclude
         */
        $scope.export = function (type, name) {
            var data, filename, link;

            var csv = $scope.convertArrayOfObjectsToCSV(type);
            if (csv == null) return;

            filename = name + '_export.csv';

            if (!csv.match(/^data:text\/csv/i)) {
                csv = 'data:text/csv;charset=utf-8,' + csv;
            }
            data = encodeURI(csv);

            link = document.createElement('a');
            link.setAttribute('href', data);
            link.setAttribute('download', filename);
            link.click();
        };

        /**
         * Converts the data in the table into data that is usable for a csv file.
         *
         * @param type : type of group to retrieve data.
         * @returns a string of converted array to be usable for the csv file.
         */
        $scope.convertArrayOfObjectsToCSV = function (type) {
            var str = "Name, Username, Email \r\n";

            for (var i = 0; i < type.length; i++) {
                var line = '';
                //for (var index in type[i]) {
                if (line != '')
                    line += ',';
                line += type[i].name + ', ' + type[i].username + ', ' + type[i].username + "@hawaii.edu,";
                //}
                str += line + '\r\n';
            }
            return str;
        };
    }
    ownerApp.controller("OwnerJsController", OwnerJsController);
})();
