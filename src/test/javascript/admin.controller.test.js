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

    describe("searchForUserGroupingInformation", function () {
        beforeEach(function () {
            scope.personToLookup = "test";
        });
        it("should search for person", function () {
            spyOn(scope, "searchForUserGroupingInformation").and.callThrough();
            scope.searchForUserGroupingInformation();
            expect(scope.searchForUserGroupingInformation).toHaveBeenCalled();
        });
    });

});
