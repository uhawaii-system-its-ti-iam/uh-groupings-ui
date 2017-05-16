(function () {

    function OwnerJsController($scope, dataProvider) {
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
                $scope.loading = false;
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
            }, URL);
        };

        //Gets the list of included members
        $scope.getInclude = function (URL) {
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
            var addUrl;
            $scope.testdata = [];
            console.log($scope.includeUsername);
            addUrl = "addMemberToIncludeGroup?userToAdd=" + $scope.includeUsername + "&grouping=" + $scope.groupingName.url + "&username=" + currentUser;

            console.log(addUrl);
            dataProvider.loadData(function (d) {
                console.log(d);
                var result = d.results;

                if(result[0].wsSubject.resultCode === 'SUCCESS') {
                    console.log("Successfully added " + $scope.includeUsername);
                    alert("Successfully added " + $scope.includeUsername);
                    $scope.loading = true;
                    $scope.getMembers(getUrl);
                    $scope.getInclude(getUrl);
                    $scope.getExclude(getUrl);
                    $scope.getPref(getUrl);
                }
                else if(typeof d.results === 'undefined'){
                    console.log($scope.username + " this user does not exist.");
                    alert($scope.username + " this user does not exist.");
                }

                $scope.testdata = d;
                $scope.includeUsername = '';
            }, addUrl);
        };

        //Move the user to the exclude list
        $scope.removeInclude = function (row) {
            console.log("Removing member at row: " + row);
            var user = $scope.groupingInclude[row].username;
            var URL = "deleteMemberFromIncludeGroup?username=" + currentUser + "&userToDelete=" + user + "&grouping=" + $scope.groupingName.url;
            console.log(user);
            console.log(URL);

            $.ajax({
                url: URL,
                method: 'GET',
                success: function () {
                    console.log("Success In Deletion");
                    alert($scope.groupingInclude[row].username + " successfully removed from included");
                    //reload data table
                    $scope.getMembers(getUrl);
                    $scope.getInclude(getUrl);
                    $scope.getExclude(getUrl);
                },
                error: function () {
                    console.log("Failed To Delete");
                    alert("Error in removing user from included");
                }
            });
        };

        //Adds a member to the excluded list
        $scope.addExclude = function () {
            var excludeUrl;
            $scope.testdata = [];
            console.log($scope.excludeUser);

            excludeUrl = "addMemberToExcludeGroup?username=" + currentUser + "&userToAdd=" + $scope.excludeUser + "&grouping=" + $scope.groupingName.url;

            dataProvider.loadData(function (d) {
                console.log(d);
                var result = d.results;

                if(result[0].wsSubject.resultCode === 'SUCCESS') {
                    console.log("Successfully added " + $scope.excludeUser);
                    alert("Successfully added " + $scope.excludeUser);
                    $scope.loading = true;
                    $scope.getMembers(getUrl);
                    $scope.getInclude(getUrl);
                    $scope.getExclude(getUrl);
                }
                else if(typeof d.results === 'undefined'){
                    console.log($scope.username + " this user does not exist.");
                    alert($scope.username + " this user does not exist.");
                }

                $scope.testdata = d;
                $scope.excludeUser = '';
            }, excludeUrl);

        };

        //Move the member to the include list
        $scope.removeExclude = function (row) {
            console.log("Adding member at row: " + row);
            var user = $scope.groupingExclude[row].username;
            var URL = "deleteMemberFromExcludeGroup?userToDelete=" + user + "&grouping=" + $scope.groupingName.url + "&username=" + currentUser;
            console.log(user);
            console.log(URL);

            $.ajax({
                url: URL,
                method: 'GET',
                success: function () {
                    console.log("Success In Deletion");
                    alert($scope.groupingInclude[row].username + " successfully removed from included");
                    //reload data table
                    $scope.getMembers(getUrl);
                    $scope.getInclude(getUrl);
                    $scope.getExclude(getUrl);
                },
                error: function () {
                    console.log("Failed To Delete");
                    alert("Error in removing user from included");
                }
            });
        };


        //Gets the list of owners of the grouping
        $scope.getOwners = function (URL) {
            dataProvider.loadData(function (d) {
                $scope.ownerList = d.owners.members;
                console.log($scope.ownerList);
            }, URL);
        };

        //Removes ownership for member
        $scope.removeOwner = function (index) {
            console.log("You want to remove owner at index " + index);
            var removeOwnerUrl;
            var removeOwner = $scope.ownerList[index].username;
            console.log(removeOwner);
            if ($scope.ownerList.length > 1) {
                removeOwnerUrl = "removeOwnership?ownerToRemove=" + removeOwner + "&grouping=" + $scope.groupingName.url + "&username=" + currentUser;

                console.log(removeOwnerUrl);
                $.ajax({
                    url: removeOwnerUrl,
                    method: 'GET',
                    success: function () {
                        console.log("Success In Deletion");
                        //reload data table
                        $scope.getOwners(ownerUrl);
                    },
                    error: function () {
                        console.log("Failed To Delete")
                    }
                });
            }
        };

        //Adds an owner to the grouping
        $scope.addOwner = function () {
            var addOwnerUrl;
            $scope.testdata = [];
            addOwnerUrl = "assignOwnership?newOwner=" + $scope.ownerUser + "&grouping=" + $scope.groupingName.url + "&username=" + currentUser;

            console.log(addOwnerUrl);
            dataProvider.loadData(function (d) {
                console.log(d);
                $scope.testdata = d;
                console.log("Success");
                $scope.getOwners(ownerUrl);
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
            var str = '';

            console.log(type);
            str = "Name, Username, Email \r\n";
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