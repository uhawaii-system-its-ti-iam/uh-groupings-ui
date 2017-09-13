(function () {

    /**
     * Owner controller for the groupings page
     *
     * @param $scope        : A Binding variable between controller and html page.
     * @param dataProvider  : service function that acts as the AJAX get.
     * @param dataUpdater   : service function that acts as AJAX post, used mainly for adding or updating
     * @param dataDeleter    : service function that acts as AJAX psst, use function mainly for delete function.
     * @constructor
     */
    function OwnerJsController($scope, $window,$filter, dataProvider, dataUpdater, dataDeleter) {
        $scope.currentUsername = "";
        $scope.initCurrentUsername = function() {
            $scope.currentUsername = $window.document.getElementById("name").innerHTML;
        };

        $scope.getCurrentUsername = function() {
            return $scope.currentUsername;
        };

        var groupingsOwned;
        var getUrl;

        $scope.ownedList = [];
        $scope.groupingsList = [];
        $scope.groupingsBasis = [];
        $scope.groupingInclude = [];
        $scope.groupingExclude = [];
        $scope.ownerList = [];
        $scope.pref = [];
        $scope.allowOptIn = [];
        $scope.allowOptOut = [];
        $scope.showGrouping = false;
        $scope.loading = true;
        $scope.groupingName = '';

        /*
        *pagination variables
        */
        $scope.gap=2;
        $scope.itemsPerPage = 25;
        //figure out how much pages to paginate. so far lets do one
        $scope.pagedItemsInclude = [];
        $scope.currentPageInclude = 0;

        $scope.pagedItemsGroupings = [];
        $scope.currentPageGroupings = 0;

        $scope.pagedItemsBasis = [];
        $scope.currentPageBasis = 0;

        $scope.pagedItemsExclude = [];
        $scope.currentPageExclude = 0;

        $scope.pagedItemsOwners = [];
        $scope.currentPageOwners = 0;

        $scope.pagedItemsList = [];
        $scope.currentPageList = 0;

        /**
         * Initialize function that retrieves the groupings you own.
         */
        $scope.init = function () {
            $scope.initCurrentUsername();

            groupingsOwned = "api/groupings/" + $scope.getCurrentUsername() + "/groupingAssignment";
            dataProvider.loadData(function (d) {
                var temp = [];
                console.log(d);
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
            getUrl = "api/groupings/" + $scope.groupingName.url + "/" + $scope.getCurrentUsername() + "/grouping";
            $scope.loading = true;
            dataProvider.loadData(function (d) {
                console.log(d);
                $scope.basis = d.basis.members;

                //Gets members in grouping
                $scope.groupingsList = d.composite.members;
                $scope.modify($scope.groupingsList);
                $scope.pagedItemsList = $scope.groupToPages($scope.groupingsList,$scope.pagedItemsList);

                //Gets members in the basis group
                $scope.groupingsBasis = d.basis.members;
                $scope.modify($scope.groupingsBasis);
                $scope.pagedItemsBasis = $scope.groupToPages($scope.groupingsBasis,$scope.pagedItemsBasis);

                //Gets members in the include group
                $scope.groupingInclude = d.include.members;
                $scope.modify($scope.groupingInclude);
                $scope.pagedItemsInclude = $scope.groupToPages($scope.groupingInclude, $scope.pagedItemsInclude);

                //Gets members in the exclude group
                $scope.groupingExclude = d.exclude.members;
                $scope.modify($scope.groupingExclude);
                $scope.pagedItemsExclude = $scope.groupToPages($scope.groupingExclude,$scope.pagedItemsExclude);

                //Gets owners of the grouping
                $scope.ownerList = d.owners.members;
                $scope.modify($scope.ownerList);
                $scope.pagedItemsOwners = $scope.groupToPages($scope.ownerList, $scope.pagedItemsOwners);

                $scope.pref = d.listservOn;
                $scope.allowOptIn = d.optInOn;
                $scope.allowOptOut = d.optOutOn;

                if ($scope.pref == true) {
                    $('#listserv').prop("checked", true);
                }
                else {
                    $('#listserv').prop("checked", false);
                }
                if ($scope.allowOptIn == true) {
                    $('#optInOption').prop("checked", true);
                }
                else {
                    $('#optInOption').prop("checked", false);
                }
                if ($scope.allowOptOut == true) {
                    $('#optOutOption').prop("checked", true);
                }
                else {
                    $('#optOutOption').prop("checked", false);
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
                    if ($scope.basis[l].uuid === grouping[m].uuid) {
                        grouping[m].basis = "\u2714";
                    }
                }
            }

            //sorts data in alphabetic order
            grouping.sort(function (a, b) {
                var nameA = a.name.toLowerCase(), nameB = b.name.toLowerCase();
                if (nameA < nameB) //sort string ascending
                    return -1;
                if (nameA > nameB)
                    return 1;
                return 0
            });
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
                $scope.groupingName = '';
                $scope.groupingsList = [];
                $scope.groupingsBasis = [];
                $scope.groupingInclude = [];
                $scope.groupingExclude = [];
                $scope.ownerList = [];
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
            var addUrl = "api/groupings/" + $scope.groupingName.url + "/" + $scope.getCurrentUsername() + "/" + $scope.addUser + "/addMemberTo" + type + "Group";
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
            if (type === 'Include') {
                user = $scope.groupingInclude[row].username;
            }
            if (type === 'Exclude') {
                user = $scope.groupingExclude[row].username;
            }

            var URL = "api/groupings/" + $scope.groupingName.url + "/" + $scope.getCurrentUsername() + "/" + user + "/deleteMemberFrom" + type + "Group";
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
            var removeOwnerUrl = "api/groupings/" + $scope.groupingName.url + "/" + $scope.getCurrentUsername() + "/" + removeOwner + "/removeOwnership";
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
            var addOwnerUrl = "api/groupings/" + $scope.groupingName.url + "/" + $scope.getCurrentUsername() + "/" + $scope.ownerUser + "/assignOwnership";
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
            var prefUrls = [];
            if (confirm("Are you sure you want to save")) {
                if ($('#optInOption').is(':checked')) {
                    $scope.allowOptIn = true;
                }
                else {
                    $scope.allowOptIn = false;
                }
                if ($('#optOutOption').is(':checked')) {
                    $scope.allowOptOut = true;
                }
                else {
                    $scope.allowOptOut = false;
                }
                if ($('#listserv').is(':checked')) {
                    $scope.pref = true;
                }
                else {
                    $scope.pref = false;
                }
                prefUrls.push({"url" : "api/groupings/" + $scope.groupingName.url + "/" + $scope.getCurrentUsername() + "/" + $scope.pref + "/setListserv", "name" : "Listserv"});
                prefUrls.push({"url" : "api/groupings/" + $scope.groupingName.url + "/" + $scope.getCurrentUsername() + "/" + $scope.allowOptIn + "/setOptIn", "name" : "optInOption"});
                prefUrls.push({"url" : "api/groupings/" + $scope.groupingName.url + "/" + $scope.getCurrentUsername() + "/" + $scope.allowOptOut + "/setOptOut", "name" : "optOutOption"});

                for (var i = 0; i < prefUrls.length; i++) {
                    dataUpdater.addData(function (d) {
                        console.log(d);
                        if (d.resultCode === "SUCCESS") {
                            console.log("preference successfully updated");
                            alert("preference successfully updated");
                            $scope.getData();
                        }
                        else if (typeof d.resultsCode === 'undefined') {
                            console.log("preference did not change");
                            alert("preference did not change");
                        }
                    }, prefUrls[i].url);
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
    //Pagination code


    /**gives you a true or false if it finds the match
    **@param haystack - the thing to be checked
    **@param needle - the check against
    **
    **/
    var searchMatch = function (haystack, needle) {
        if (!needle) {
            return true;
        }
        return haystack.toLowerCase().indexOf(needle.toLowerCase()) !== -1;
    };

    /**searches through the array to find matches and then fixes the list
    **@param list - gives the whole list to sort out
    **@param whatList - it gives you the list you need to search through
    **@param whatQuery - it gives the search bar its seperate search function.
    **/
    $scope.search = function (list, whatList,whatQuery) {
        var query = "";
        query = $scope[whatQuery];
        console.log(query);
        //console.log($scope[whatList]);
        $scope.filteredItems = [];
        $scope.filteredItems = $filter('filter')(list, function (item) {
            if(searchMatch(item.name, query)){
                return true;
            }
        });
        // console.log($scope.filteredItems);
        page = 0;
        // now group by pages
        var emptyList = [];
        $scope[whatList] = $scope.groupToPagesChanged(emptyList);
    };

    $scope.groupToPagesChanged = function(pagedList){
        var pagedList = [];
        for(var i = 0; i < $scope.filteredItems.length ; i++){
            if(i % $scope.itemsPerPage === 0){
                pagedList[Math.floor(i/$scope.itemsPerPage)] = [ $scope.filteredItems[i]];
            }else{
                pagedList[Math.floor(i/$scope.itemsPerPage)].push( $scope.filteredItems[i]);
            }
        }
        return pagedList;
    };


    /**groups all the items to pages
       have sepperate arrays (hopefully)
       @param
    **/
    $scope.groupToPages=function(theList , pagedList){
        var pagedList = [];
        if(theList == null){
            console.log("I AM NULL ... WHY?!");
        }
        if(theList != null){
        for(var i = 0; i < theList.length ; i++){
            if(i % $scope.itemsPerPage === 0){
                pagedList[Math.floor(i/$scope.itemsPerPage)] = [ theList[i]];
            }else{
                pagedList[Math.floor(i/$scope.itemsPerPage)].push( theList[i]);
            }
        }
        }
        return pagedList;
    };

    /**shows the range between the start and end
     *checks for negative numbers
     *
     * @param size
     * @param start
     * @param end
     *  all the param are self explanitory
     * @return ret
     *     everything within the range of start,
     *       end, and making sure it's that size
     **/
    $scope.range = function (size, start, end) {
        var ret = [];

        if (size < end) {
            end = size;
            // start = size - $scope.gap;
        }
        if (start < 0) {
            start = 0;
        }
        for (var i = start; i < end; i++) {
            ret.push(i);
        }
        return ret;
    };

    $scope.currentPage = function(pages){
        switch(pages){
            //TODO we need  [next, first, prev, last, set], the $scope[whatList], $scope[whatPage]
            case 'Include Next':
                if ($scope.currentPageInclude < $scope.pagedItemsInclude.length - 1) {
                    $scope.currentPageInclude = $scope.currentPageInclude + 1;
                }
                break;

            case 'Include Set':
                $scope.currentPageInclude = this.n;
                break;

            case 'Include Prev':
                if ($scope.currentPageInclude > 0) {
                    $scope.currentPageInclude--;
                }
                break;
            case 'Include First':
                $scope.currentPageInclude = 0;
                break;

            case 'Include Last':
                if ($scope.currentPageInclude >= 0) {
                    $scope.currentPageInclude = $scope.pagedItemsInclude.length - 1;
                }
                break;
                // Split for the exclude
            case 'Exclude Next':
                if ($scope.currentPageExclude < $scope.pagedItemsExclude.length - 1) {
                    $scope.currentPageExclude = $scope.currentPageExclude + 1;
                }
                break;
            case 'Exclude Set':
                $scope.currentPageExclude = this.n;
                break;

            case 'Exclude Prev':
                if ($scope.currentPageExclude > 0) {
                    $scope.currentPageExclude--;
                }
                break;
            case 'Exclude First':
                $scope.currentPageExclude = 0;
                break;
            case 'Exclude Last':
                if ($scope.currentPageExclude >= 0) {
                    $scope.currentPageExclude = $scope.pagedItemsExclude.length - 1;
                }
                break;
                // Cases for the basis
            case 'Basis Next':
                if ($scope.currentPageBasis < $scope.pagedItemsBasis.length - 1) {
                    $scope.currentPageBasis = $scope.currentPageBasis + 1;
                }
                break;
            case 'Basis Set':
                $scope.currentPageBasis = this.n;
                break;
            case 'Basis Prev':
                if ($scope.currentPageBasis > 0) {
                    $scope.currentPageBasis--;
                }
                break;
            case 'Basis First':
                $scope.currentPageBasis = 0;
                break;
            case 'Basis Last':
                if ($scope.currentPageBasis >= 0) {
                    $scope.currentPageBasis = $scope.pagedItemsBasis.length - 1;
                }
                break;
                // Cases for Owners
            case 'Owners Next':
                if ($scope.currentPageOwners < $scope.pagedItemsOwners.length - 1) {
                    $scope.currentPageOwners = $scope.currentPageOwners + 1;
                }
                break;
            case 'Owners Set':
                $scope.currentPageOwners = this.n;
                break;
            case 'Owners Prev':
                if ($scope.currentPageOwners > 0) {
                    $scope.currentPageOwners--;
                }
                break;
            case 'Owners First':
                $scope.currentPageOwners = 0;
                break;
            case 'Owners Last':
                if ($scope.currentPageOwners >= 0) {
                    $scope.currentPageOwners = $scope.pagedItemsOwners.length - 1;
                }
                break;
                // Cases for List
            case 'List Next':
                if ($scope.currentPageList < $scope.pagedItemsList.length - 1) {
                    $scope.currentPageList = $scope.currentPageList + 1;
                }
                break;
            case 'List Set':
                $scope.currentPageList = this.n;
                break;
            case 'List Prev':
                if ($scope.currentPageList > 0) {
                    $scope.currentPageList--;
                }
                break;
            case 'List First':
                $scope.currentPageList = 0;
                break;
            case 'List Last':
                if ($scope.currentPageList >= 0) {
                    $scope.currentPageList = $scope.pagedItemsList.length - 1;
                }
                break;
        }
    };

}

    ownerApp.controller("OwnerJsController", OwnerJsController);
})();
