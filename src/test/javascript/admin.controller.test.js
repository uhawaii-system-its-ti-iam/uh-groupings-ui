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

});
