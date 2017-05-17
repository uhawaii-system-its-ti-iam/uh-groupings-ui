(function () {

    function OwnerJsController($scope, dataProvider, dataUpdater, dataDeleter) {
        var currentUser = document.getElementById("name").innerText;
        var groupingsOwned = "api/groupings/" + currentUser + "/myGroupings";
        var getUrl;
        var ownerUrl;

        $scope.ownedList = [];
        $scope.groupingsList = [];
        $scope.groupingsBasis = [];
        $scope.groupingInclude = [];
        $scope.groupingExclude = [];
        $scope.ownerList = [];
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
                    $scope.ownedList.push({'name': d.groupingsOwned[i].name, 'folder': folder, 'url': d.groupingsOwned[i].path});
                }
                $scope.loading = false;
            }, groupingsOwned);
        };

        //Shows page containing groupings information
        $scope.showData = function (row) {
            $scope.groupingName = $scope.ownedList[row];
            $scope.loading = true;
            //URLS being used in the api calls.
            getUrl = "api/groupings/" + $scope.groupingName.url + "/" + currentUser + "/grouping";
            ownerUrl = "api/groupings/" + $scope.groupingName.url + "/" + currentUser + "/grouping";

            if ($scope.showList == false) {
                $scope.showList = true;
                //Gets list of Members
                $scope.getMembers(getUrl);
                $scope.getBasis(getUrl);
                $scope.getInclude(getUrl);
                $scope.getExclude(getUrl);
                $scope.getPref(getUrl);

                //Gets List of Owners
                $scope.getOwners(getUrl);
            }
            else {
                $scope.showList = false;
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

        //Gets the list of members minus excluded
        $scope.getMembers = function (URL) {

            dataProvider.loadData(function (d) {
                var temp;
                temp = d.basisPlusIncludeMinusExclude.members;

                for (var k = 0; k < temp.length; k++) {
                    temp[k].basis = "\u2716";
                }

                //sorts the data by name
                temp.sort(function (a, b) {
                    var nameA = a.name.toLowerCase(), nameB = b.name.toLowerCase();
                    if (nameA < nameB) //sort string ascending
                        return -1;
                    if (nameA > nameB)
                        return 1;
                    return 0
                });

                //assigns which member is in basis group
                var basis = d.basis.members;

                for (var l = 0; l < basis.length; l++) {
                    for (var m = 0; m < temp.length; m++) {
                        if (basis[l].name === temp[m].name) {
                            temp[m].basis = "\u2714";
                        }
                    }
                }
                $scope.groupingsList = temp;
            }, URL);
        };

        //Gets the list of basis members
        $scope.getBasis = function (URL) {
            dataProvider.loadData(function (d) {
                var temp;
                temp = d.basis.members;

                //sorts the data by name
                temp.sort(function (a, b) {
                    var nameA = a.name.toLowerCase(), nameB = b.name.toLowerCase();
                    if (nameA < nameB) //sort string ascending
                        return -1;
                    if (nameA > nameB)
                        return 1;
                    return 0
                });

                //Filters out names with hawaii.edu
                for (var i = 0; i < temp.length; i++) {
                    if (temp[i].name.includes("hawaii.edu")) {
                        temp.splice(i, 1);
                        i--;
                    }
                }

                $scope.groupingsBasis = temp;
            }, URL);
        };

        //Gets the list of excluded members
        $scope.getExclude = function (URL) {
            $scope.groupingExclude = '';
            dataProvider.loadData(function (d) {
                var temp;
                temp = d.exclude.members;

                //sorts the data by name
                temp.sort(function (a, b) {
                    var nameA = a.name.toLowerCase(), nameB = b.name.toLowerCase();
                    if (nameA < nameB) //sort string ascending
                        return -1;
                    if (nameA > nameB)
                        return 1;
                    return 0
                });

                for (var k = 0; k < temp.length; k++) {
                    temp[k].basis = "\u2716";
                }

                var basis = d.basis.members;

                for (var l = 0; l < basis.length; l++) {
                    for (var m = 0; m < temp.length; m++) {
                        if (basis[l].name === temp[m].name) {
                            temp[m].basis = '\u2714';
                        }
                    }
                }

                //Filters out names with hawaii.edu
                for (var i = 0; i < temp.length; i++) {
                    if (temp[i].name.includes("hawaii.edu")) {
                        temp.splice(i, 1);
                        i--;
                    }
                }
                $scope.groupingExclude = temp;
                $scope.loading = false;
            }, URL);
        };

        //Gets the list of included members
        $scope.getInclude = function (URL) {
            $scope.groupingInclude = '';
            dataProvider.loadData(function (d) {
                var temp;
                temp = d.include.members;

                //sorts the data by name
                temp.sort(function (a, b) {
                    var nameA = a.name.toLowerCase(), nameB = b.name.toLowerCase();
                    if (nameA < nameB) //sort string ascending
                        return -1;
                    if (nameA > nameB)
                        return 1;
                    return 0
                });

                for (var k = 0; k < temp.length; k++) {
                    temp[k].basis = "\u2716";
                }

                var basis = d.basis.members;

                for (var l = 0; l < basis.length; l++) {
                    for (var m = 0; m < temp.length; m++) {
                        if (basis[l].name === temp[m].name) {
                            temp[m].basis = '\u2714';
                        }
                    }
                }

                //Filters out names with hawaii.edu
                for (var n = 0; n < temp.length; n++) {
                    if (temp[n].name.includes("hawaii.edu")) {
                        temp.splice(n, 1);
                        n--;
                    }
                }
                $scope.groupingInclude = temp;
            }, URL);
        };

        //Adds a member to the included list
        $scope.addInclude = function () {
            var addUrl = "api/groupings/" + $scope.groupingName.url + "/" + currentUser + "/" + $scope.includeUsername + "/addMemberToIncludeGroup";
            dataUpdater.addData(function (d) {
                if(d.resultCode === "SUCCESS"){
                    console.log("success in adding " + $scope.includeUsername);
                    alert("SUCCESS IN ADDING " + $scope.includeUsername);
                    $scope.loading = true;
                    $scope.getMembers(getUrl);
                    $scope.getInclude(getUrl);
                    $scope.getExclude(getUrl);
                }
                else if(typeof d.resultsCode === 'undefined'){
                    console.log($scope.includeUsername + " this user does not exist.");
                    alert($scope.includeUsername + " this user does not exist.");
                }
            }, addUrl);
            $scope.includeUsername = '';
        };

        //Move the user to the exclude list
        $scope.removeInclude = function (row) {
            var user = $scope.groupingInclude[row].username;
            var URL = "api/groupings/" + $scope.groupingName.url + "/" + currentUser + "/" + user + "/deleteMemberFromIncludeGroup";

            dataDeleter.deleteData(function (d) {
                console.log(d);
                $scope.loading = true;
                $scope.getMembers(getUrl);
                $scope.getInclude(getUrl);
                $scope.getExclude(getUrl);
            }, URL);
        };

        //Adds a member to the excluded list
        $scope.addExclude = function () {
            var excludeUrl = "api/groupings/" + $scope.groupingName.url + "/" + currentUser + "/" + $scope.excludeUser + "/addMemberToExcludeGroup";

            dataUpdater.addData(function (d) {
                if(d.resultCode === "SUCCESS"){
                    console.log("success in adding " + $scope.excludeUser);
                    alert("SUCCESS IN ADDING " + $scope.excludeUser);
                    $scope.loading = true;
                    $scope.getMembers(getUrl);
                    $scope.getInclude(getUrl);
                    $scope.getExclude(getUrl);
                }
                else if(typeof d.resultsCode === 'undefined'){
                    console.log($scope.excludeUser + " this user does not exist.");
                    alert($scope.excludeUser + " this user does not exist.");
                }
            }, excludeUrl);
            $scope.excludeUser = '';
        };

        //Move the member to the include list
        $scope.removeExclude = function (row) {
            var user = $scope.groupingExclude[row].username;
            var URL = "api/groupings/" + $scope.groupingName.url + "/" + currentUser + "/" + user + "/deleteMemberFromExcludeGroup";

            dataDeleter.deleteData(function (d) {
                console.log(d);
                $scope.loading = true;
                $scope.getMembers(getUrl);
                $scope.getInclude(getUrl);
                $scope.getExclude(getUrl);
            }, URL);
        };


        //Gets the list of owners of the grouping
        $scope.getOwners = function (URL) {
            dataProvider.loadData(function (d) {
                $scope.ownerList = d.owners.members;
                console.log($scope.ownerList);
                $scope.loading = false;
            }, URL);
        };

        //Removes ownership for member
        $scope.removeOwner = function (index) {
            var removeOwner = $scope.ownerList[index].username;
            var removeOwnerUrl = "api/groupings/" + $scope.groupingName.url + "/" + currentUser + "/" + removeOwner + "/removeOwnership";
            if ($scope.ownerList.length > 1) {
                console.log(removeOwnerUrl);
                dataDeleter.deleteData(function (d) {
                    console.log(d);
                    $scope.loading = true;
                    $scope.getOwners(getUrl);
                }, removeOwnerUrl);
            }
        };

        //Adds an owner to the grouping
        $scope.addOwner = function () {
            var addOwnerUrl = "api/groupings/" + $scope.groupingName.url + "/" + currentUser + "/" + $scope.ownerUser + "/assignOwnership";
            console.log(addOwnerUrl);
            dataUpdater.addData(function (d) {
                if(d[0].resultCode === "SUCCESS"){
                    console.log("Assigned " + $scope.ownerUser + " as an owner");
                    alert("Assigned " + $scope.ownerUser + " as an owner");
                    $scope.loading = true;
                    $scope.getOwners(getUrl);
                }
                else if(typeof d[0].resultsCode === 'undefined'){
                    console.log($scope.ownerUser + " this user does not exist.");
                    alert($scope.ownerUser + " this user does not exist.");
                }
            }, addOwnerUrl);
            $scope.ownerUser = '';
        };

        $scope.getPref = function(URL) {
            dataProvider.loadData(function (d) {
                console.log(d);
            }, URL);
        };

        $scope.savePref = function () {
            if ($('#addOption').is(':checked')) {
                console.log("You are allowing members to opt in your grouping")
            }
            if ($('#removeOption').is(':checked')) {
                console.log("You are allowing members to exclude themselves from your groupings")
            }
            if ($('#listserv').is(':checked')) {
                console.log("LISTSERV is true")
            }
        };

        $scope.export = function(type, name) {
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

        $scope.convertArrayOfObjectsToCSV = function(type) {
            console.log(type);
            var str = "Name, Username, Email \r\n";

            for (var i = 0; i < type.length; i++) {
                var line = '';
                //for (var index in type[i]) {
                    if (line != '')
                        line += ',';
                    line += type[i].name + ', ' + type[i].username + ', ' + type[i].username + "@hawaii.edu," ;
                //}
                str += line + '\r\n';
            }
            return str;
        };
    }

    ownerApp.controller("OwnerJsController", OwnerJsController);
})();