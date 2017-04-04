(function () {

    function OwnerJsController($scope, dataProvider) {
        var currentUser = document.getElementById("name").innerText;
        var groupingsOwned = "groupingsOwned?username=" + currentUser;
        var getUrl;
        var ownerUrl;

        $scope.ownedList = [];
        $scope.groupingsList = [];
        $scope.groupingsBasis = [];
        $scope.groupingInclude = [];
        $scope.groupingExclude = [];
        $scope.ownerList = [];
        $scope.showList = false;
        $scope.groupingName = '';

        $scope.init = function () {
            dataProvider.loadData(function (d) {
                var temp = [];

                //Assigns grouping name, folder directories and url used for api call.
                for (var i = 0; i < d.length; i++) {
                    temp[i] = d[i].substr(18).split(':');

                    var folder = '';
                    for (var j = 0; j < temp[i].length - 1; j++) {
                        folder += temp[i][j];
                        if (j != temp[i].length - 2) {
                            folder += "/";
                        }
                    }

                    $scope.ownedList.push({'name': temp[i][temp[i].length - 1], 'folder': folder, 'url': d[i]});
                }
            }, groupingsOwned)
        };

        //Shows page containing groupings information
        $scope.showData = function (row) {
            $scope.groupingName = $scope.ownedList[row];

            //URLS being used in the api calls.
            getUrl = "getMembers?grouping=" + $scope.groupingName.url + "&username=" + currentUser;
            ownerUrl = "getOwners?grouping=" + $scope.groupingName.url + "&username=" + currentUser;

            if ($scope.showList == false) {
                $scope.showList = true;

                //Gets list of Members
                $scope.getMembers(getUrl);
                $scope.getBasis(getUrl);
                $scope.getInclude(getUrl);
                $scope.getExclude(getUrl);

                //Gets List of Owners
                $scope.getOwners(ownerUrl);
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
                console.log(d);
                temp = d.basisPlusIncludeMinusExclude;

                for(var k = 0; k < temp.length; k++)
                {
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

                //Filters out names with hawaii.edu
                for (var i = 0; i < temp.length; i++) {
                    if (temp[i].name.includes("hawaii.edu")) {
                        temp.splice(i, 1);
                        i--;
                    }
                }

                var basis = d.basis;

                for(var l = 0; l < basis.length; l++)
                {
                    for(var m = 0; m < temp.length; m++)
                    {
                        if(basis[l].name === temp[m].name)
                        {
                            temp[m].basis = '\u2714';
                        }
                    }
                }

                //gets the username from the attributeValues array
                for (var i = 0; i < temp.length; i++) {
                    temp[i].attributeValues = _.pluck(_.pluck(temp, "attributeValues"), 0)[i];
                }
                $scope.groupingsList = temp;
            }, URL)
        };

        //Gets the list of basis members
        $scope.getBasis = function (URL) {
            dataProvider.loadData(function (d) {
                var temp;
                temp = d.basis;
                console.log(temp);

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

                //gets the username from the attributeValues array
                for (var i = 0; i < temp.length; i++) {
                    temp[i].attributeValues = _.pluck(_.pluck(temp, "attributeValues"), 0)[i];
                }

                $scope.groupingsBasis = temp;
            }, URL)
        };

        //Gets the list of excluded members
        $scope.getExclude = function (URL) {
            dataProvider.loadData(function (d) {
                var temp;
                temp = d.exclude;

                //sorts the data by name
                temp.sort(function (a, b) {
                    var nameA = a.name.toLowerCase(), nameB = b.name.toLowerCase();
                    if (nameA < nameB) //sort string ascending
                        return -1;
                    if (nameA > nameB)
                        return 1;
                    return 0
                });

                for(var k = 0; k < temp.length; k++)
                {
                    temp[k].basis = "\u2716";
                }

                var basis = d.basis;

                for(var l = 0; l < basis.length; l++)
                {
                    for(var m = 0; m < temp.length; m++)
                    {
                        if(basis[l].name === temp[m].name)
                        {
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

                //gets the username from the attributeValues array
                for (var j = 0; j < temp.length; j++) {
                    temp[j].attributeValues = _.pluck(_.pluck(temp, "attributeValues"), 0)[j];
                }

                $scope.groupingExclude = temp;
            }, URL)
        };

        //Gets the list of included members
        $scope.getInclude = function (URL) {
            dataProvider.loadData(function (d) {
                var temp;
                temp = d.include;

                //sorts the data by name
                temp.sort(function (a, b) {
                    var nameA = a.name.toLowerCase(), nameB = b.name.toLowerCase();
                    if (nameA < nameB) //sort string ascending
                        return -1;
                    if (nameA > nameB)
                        return 1;
                    return 0
                });

                for(var k = 0; k < temp.length; k++)
                {
                    temp[k].basis = "\u2716";
                }

                var basis = d.basis;

                for(var l = 0; l < basis.length; l++)
                {
                    for(var m = 0; m < temp.length; m++)
                    {
                        if(basis[l].name === temp[m].name)
                        {
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

                //gets the username from the attributeValues array
                for (var i = 0; i < temp.length; i++) {
                    temp[i].attributeValues = _.pluck(_.pluck(temp, "attributeValues"), 0)[i];
                }

                $scope.groupingInclude = temp;
            }, URL)
        };

        //Adds a member to the included list
        $scope.addInclude = function () {
            var addUrl;
            $scope.testdata = [];
            console.log($scope.includeUsername);
            addUrl = "addMember?userToAdd=" + $scope.includeUsername + "&grouping=" + $scope.groupingName.url + "&username=" + currentUser;

            console.log(addUrl);

            dataProvider.loadData(function (d) {
                console.log(d);
                const pluck = _.pluck(d, "results");

                console.log(pluck);
                console.log(pluck[0]);
                if (typeof pluck[0] === 'undefined') {
                    console.log($scope.includeUsername + " this user does not exist.");
                    alert($scope.includeUsername + " this user does not exist.");
                }
                else {
                    const meta = pluck[0][0].resultMetadata;

                    console.log(meta.resultCode);

                    if (meta.resultCode === 'SUCCESS') {
                        console.log("Successfully added " + $scope.includeUsername);
                        alert("Successfully added " + $scope.includeUsername);
                        $scope.getMembers(getUrl);
                        $scope.getInclude(getUrl);
                        $scope.getExclude(getUrl);
                    }
                    else if (meta.resultCode === 'SUCCESS_ALREADY_EXISTED') {
                        console.log($scope.includeUsername + " already exists in this groupings.");
                        alert($scope.includeUsername + " already exists in this groupings.");
                    }
                }

                $scope.testdata = d;
                $scope.includeUsername = '';
            }, addUrl);
        };

        $scope.removeInclude = function(row) {
            console.log("Removing member at row: " + row);
            var user =  $scope.groupingInclude[row].attributeValues;
            var URL = "deleteMember?username=" + currentUser + "&userToDelete=" + user + "&grouping=" + $scope.groupingName.url;
            console.log(user);
            console.log(URL);

            $.ajax({
                url: URL,
                method: 'GET',
                success: function () {
                    console.log("Success In Deletion");
                    alert($scope.groupingInclude[row].attributeValues + " successfully removed from included");
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

            excludeUrl = "deleteMember?username=" + currentUser + "&userToDelete=" + $scope.excludeUser + "&grouping=" + $scope.groupingName.url;

            console.log(excludeUrl);

            /*dataProvider.loadData(function (d) {
                console.log(d);

                $scope.testdata = d;
            }, excludeUrl);*/

            $.ajax({
                url: excludeUrl,
                method: 'GET',
                success: function () {
                    alert("Added " + $scope.excludeUser+ " to excluded list");
                    //reload data table
                    $scope.getMembers(getUrl);
                    $scope.getInclude(getUrl);
                    $scope.getExclude(getUrl);
                    $scope.excludeUser = '';
                },
                error: function () {
                    console.log("Failed To Delete");
                    alert("Error in adding user to excluded");
                }
            });

        };

        $scope.removeExclude = function(row) {
            console.log("Adding member at row: " + row);
            var user =  $scope.groupingExclude[row].attributeValues;
            var URL = "addMember?userToAdd=" + user + "&grouping=" + $scope.groupingName.url + "&username=" + currentUser;
            console.log(user);
            console.log(URL);

            dataProvider.loadData(function (d) {
                console.log(d);
                const pluck = _.pluck(d, "results");

                console.log(pluck);
                console.log(pluck[0]);
                if (typeof pluck[0] === 'undefined') {
                    console.log($scope.includeUsername + " this user does not exist.");
                    alert($scope.includeUsername + " this user does not exist.");
                }
                else {
                    const meta = pluck[0][0].resultMetadata;

                    console.log(meta.resultCode);

                    if (meta.resultCode === 'SUCCESS') {
                        console.log("Successfully added " + $scope.groupingExclude[row].attributeValues);
                        alert("Successfully added " + $scope.groupingExclude[row].attributeValues);
                        $scope.getMembers(getUrl);
                        $scope.getInclude(getUrl);
                        $scope.getExclude(getUrl);
                    }
                    else if (meta.resultCode === 'SUCCESS_ALREADY_EXISTED') {
                        console.log($scope.groupingExclude[row].attributeValues + " already exists in this groupings.");
                        alert($scope.groupingExclude[row].attributeValues + " already exists in this groupings.");
                    }
                }

                $scope.testdata = d;
            }, URL);

        };

        //Gets the list of owners of the grouping
        $scope.getOwners = function (URL) {
            dataProvider.loadData(function (d) {
                for (var i = 0; i < d.length; i++) {
                    d[i].attributeValues = _.pluck(_.pluck(d, "attributeValues"), 0)[i];
                }
                $scope.ownerList = d;
            }, URL);
        };

        //Removes ownership for member
        $scope.removeOwner = function (index) {
            console.log("You want to remove owner at index " + index);
            var removeOwnerUrl;
            var removeOwner = $scope.ownerList[index].attributeValues;
            console.log(removeOwner);
            if ($scope.ownerList.length > 1) {
                removeOwnerUrl = "removeOwnership?groupings=" + $scope.groupingName.url + "&username=" + removeOwner;

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
        }
    }

    ownerApp.controller("OwnerJsController", OwnerJsController);
})();