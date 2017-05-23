(function () {

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
        $scope.showList = false;
        $scope.loading = true;
        $scope.groupingName = '';

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

        //Shows page containing groupings information
        $scope.showData = function (row) {
            $scope.groupingName = $scope.ownedList[row];
            //URLS being used in the api calls.
            if ($scope.showList == false) {
                $scope.showList = true;
                $scope.getData();
            }
            else {
                $scope.showList = false;
            }
        };

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

        //Returns back to list of groupings you own.
        $scope.showGroups = function () {
            if ($scope.showList == false) {
                $scope.showList = true;
            }
            else {
                $scope.showList = false;
                $scope.groupingName = ''
            }
        };

        //Adds a member to a include/exclude group
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

        //removes member from the include/exclude group
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

        //Removes ownership for member
        $scope.removeOwner = function (index) {
            var removeOwner = $scope.ownerList[index].username;
            var removeOwnerUrl = "api/groupings/" + $scope.groupingName.url + "/" + currentUser + "/" + removeOwner + "/removeOwnership";
            if ($scope.ownerList.length > 1) {
                console.log(removeOwnerUrl);
                dataDeleter.deleteData(function (d) {
                    $scope.getData();
                }, removeOwnerUrl);
            }
        };

        //Adds an owner to the grouping
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

        $scope.convertArrayOfObjectsToCSV = function (type) {
            console.log(type);
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