describe("AdminController", function () {

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    let scope;
    let controller;

    beforeEach(inject(function ($rootScope, $controller, _BASE_URL_, _$httpBackend_) {
        scope = $rootScope.$new();
        controller = $controller("AdminJsController", {
            $scope: scope,
        });
    }));

    it("should define the admin controller", function () {
        expect(controller).toBeDefined();
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
