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
