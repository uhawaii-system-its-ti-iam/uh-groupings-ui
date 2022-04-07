describe("AdminController", function () {

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    let scope;
    let controller;
    let gs;

    beforeEach(inject(function ($rootScope, $controller, groupingsService) {
        scope = $rootScope.$new();
        controller = $controller("AdminJsController", {
            $scope: scope
        });
        gs = groupingsService;
    }));

    it("should define the admin controller", function () {
        expect(controller).toBeDefined();
    });

    describe("init", function () {
        it("should call groupingsService.getAdminLists", function () {
            spyOn(gs, "getAdminLists");
            scope.init();
            expect(gs.getAdminLists).toHaveBeenCalled();
        });
    });
    describe("getAdminListsCallbackOnSuccess", function () {
        let res = {};
        beforeEach(function () {
            res = {
                "allGroupingPaths": [
                    {
                        "path": "path",
                        "name": "name"
                    },
                    {
                        "path": "path",
                        "name": "name"
                    }
                ],
                "adminGroup": {
                    "members": [
                        {
                            "username": "username",
                            "uhUuid": "uhUuid",
                            "firstName": "firstName",
                            "lastName": "lastName",
                            "name": "name"
                        },
                        {
                            "username": "username",
                            "uhUuid": "uhUuid",
                            "firstName": "firstName",
                            "lastName": "lastName",
                            "name": "name"
                        }
                    ]
                }
            };
        });
        it("should call objToPageArray", function () {
            spyOn(scope, "objToPageArray");
            scope.getAdminListsCallbackOnSuccess(res);
            expect(scope.objToPageArray).toHaveBeenCalled();
        });
        it("should instantiate scope.pagedItemsAdmins", function () {
            scope.getAdminListsCallbackOnSuccess(res);
            expect(scope.pagedItemsAdmins).toBeDefined();
        });
        it("should instantiate scope.groupingsList", function () {
            scope.getAdminListsCallbackOnSuccess(res);
            expect(scope.groupingsList).toBeDefined();
        });
        it("should set scope.loading to false", function () {
            scope.getAdminListsCallbackOnSuccess(res);
            expect(scope.loading).toBeFalse();
        });
    });

    describe("searchForUserGroupingInformationOnSuccessCallback", function () {
        let res;
        beforeEach(function () {
            res = [
                {
                    "identifier": null,
                    "person": null,
                    "group": null,
                    "path": "path-to-grouping-name",
                    "name": "grouping-name",
                    "inBasis": false,
                    "inInclude": false,
                    "inExclude": false,
                    "inOwner": true,
                    "inBasisAndInclude": false,
                    "optOutEnabled": true,
                    "optInEnabled": false,
                    "selfOpted": false
                }];

        });
        it("should call scope.filter", function () {
            spyOn(scope, "filter");
            scope.searchForUserGroupingInformationOnSuccessCallback(res);
            expect(scope.filter).toHaveBeenCalled();
        });
        it("should set scope.personList equal to api response", function () {
            scope.searchForUserGroupingInformationOnSuccessCallback(res);
            expect(scope.personList).toEqual(res);
        });
        it("should set scope.user equal to scope.personToLookup", function () {
            scope.searchForUserGroupingInformationOnSuccessCallback(res);
            expect(expect(scope.user).toEqual(scope.personToLookup));
        });
        it("should set scope.loading to be false", function () {
            scope.searchForUserGroupingInformationOnSuccessCallback(res);
            expect(scope.loading).toBeFalse();
        });
    });
    describe("searchForUserGroupingInformationOnErrorCallback", function () {
        beforeEach(function () {
            scope.searchForUserGroupingInformationOnErrorCallback([]);
        });
        it("should set scope.loading to be false", function () {
            expect(scope.loading).toBeFalse();
        });
        it("should set scope.user equal to scope.personToLookup", function () {
            expect(expect(scope.user).toEqual(scope.personToLookup));
        });
    });

    describe("searchForUserGroupingInformation", function () {
        beforeEach(function () {
            scope.personToLookup = "iamtst01";
        });
        it("should call groupingsService.getMembershipAssignmentForUser", function () {
            spyOn(gs, "getMembershipAssignmentForUser").and.callThrough();
            scope.searchForUserGroupingInformation();
            expect(gs.getMembershipAssignmentForUser).toHaveBeenCalled();
        });
    });

    describe("removeFromGroupsCallbackOnSuccess", function () {
        let res;
        beforeEach(function () {
            scope.personToLookup = "iamtst01";
            res = {
                "uid": "iamtst01",
                "uhUuid": "iamtst01",
                "givenName": "tst01name",
                "cn": "tst01name",
                "sn": "tst01name"
            };
        });
        it("should set scope.emptySelect to true when scope.selectedGroupingsPaths is empty", function () {
            scope.removeFromGroupsCallbackOnSuccess(res);
            expect(scope.emptySelect).toBeTrue();
        });
    });

    describe("removeFromGroups", function () {
        beforeEach(function () {
            scope.personToLookup = "";
        });

        it("should call groupingsService.getMemberAttributes", function () {
            spyOn(gs, "getMemberAttributes");
            scope.removeFromGroups();
            expect(gs.getMemberAttributes).toHaveBeenCalled();
        });
    });

    describe("updateCheckBoxes", function () {
        beforeEach(function () {
            scope.pagedItemsPerson[scope.currentPagePerson] = {
                inBasis: false,
                inExclude: false,
                inInclude: true,
                isSelected: false
            };
        });
        it("should negate scope.checkAll", function () {
            let checkAll = scope.checkAll;
            scope.updateCheckBoxes();
            expect(scope.checkAll).toEqual(!checkAll);
        });
    });
    describe("addAdmin", function () {

        it("should set waitingForImportResponse to false", function () {
            scope.waitingForImportResponse = true;
            scope.addAdmin();
            expect(scope.waitingForImportResponse).toBeFalse();

        });
    });

    describe("removeAdmin", function () {
        beforeEach(function () {
            scope.pagedItemsAdmins[0] = "zzzz";
        });
        it("should call scope.createRemoveErrorModal", function () {
            spyOn(scope, "createRemoveErrorModal");
            scope.removeAdmin(0, 0);
            expect(scope.createRemoveErrorModal).toHaveBeenCalled();
        });
    });

    describe("createGroupPathsAndNames", function () {
        let selectedGroupingsNames, selectedGroupingsPaths, selectedOwnedGroupings, selectedOwnedGroupingsNames, currentPage;
        beforeEach(function () {
            selectedGroupingsNames = [];
            selectedGroupingsPaths = [];
            selectedOwnedGroupings = [];
            selectedOwnedGroupingsNames = [];
            currentPage = [
                {
                    inBasis: false,
                    inInclude: false,
                    inExclude: false,
                    inOwner: true,
                    isSelected: true,
                    path: "grouping:grouping-name-group0",
                    name: "grouping-name0"
                },
                {
                    inBasis: false,
                    inInclude: true,
                    inExclude: false,
                    inOwner: false,
                    isSelected: true,
                    path: "grouping:grouping-name-group1",
                    name: "grouping-name1"
                },
                {
                    inBasis: false,
                    inInclude: false,
                    inExclude: false,
                    inOwner: true,
                    isSelected: false,
                    path: "grouping:grouping-name-group2",
                    name: "grouping-name2"
                }
            ];
        });
        it("should concatenate the subgroup onto the grouping paths", function () {
            scope.createGroupPathsAndNames(currentPage, selectedGroupingsNames, selectedGroupingsPaths, selectedOwnedGroupings, selectedOwnedGroupingsNames);
            expect(selectedGroupingsPaths[0]).toEqual("grouping:grouping-name-group0:owners");
            expect(selectedGroupingsPaths[1]).toEqual("grouping:grouping-name-group1:include");
        });
        it("should create the same amount of paths as names", function () {
            scope.createGroupPathsAndNames(currentPage, selectedGroupingsNames, selectedGroupingsPaths, selectedOwnedGroupings, selectedOwnedGroupingsNames);
            expect(selectedGroupingsPaths.length).toEqual(selectedGroupingsNames.length);
        });
        it("should fetch the grouping names", function () {
            scope.createGroupPathsAndNames(currentPage, selectedGroupingsNames, selectedGroupingsPaths, selectedOwnedGroupings, selectedOwnedGroupingsNames);
            expect(selectedGroupingsNames[0]).toEqual("grouping-name0");
            expect(selectedGroupingsNames[1]).toEqual("grouping-name1");
        });
        it("should not fetch data that is not selected", function () {
            scope.createGroupPathsAndNames(currentPage, selectedGroupingsNames, selectedGroupingsPaths, selectedOwnedGroupings, selectedOwnedGroupingsNames);
            expect(selectedGroupingsNames[2]).toBeUndefined();
            expect(selectedGroupingsPaths[2]).toBeUndefined();
            expect(selectedGroupingsNames.length).toEqual(2);
            expect(selectedGroupingsPaths.length).toEqual(2);
        });

    });

});