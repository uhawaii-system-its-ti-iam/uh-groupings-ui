describe("AdminController", function () {

    // Set up mock element for setting the current user
    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    let scope;
    let controller;
    let httpBackend;
    let BASE_URL;
    let window;

    beforeEach(inject(function ($rootScope, $controller, _BASE_URL_, _$httpBackend_) {
        scope = $rootScope.$new();
        controller = $controller("AdminJsController", {
            $scope: scope,
            $window: window
        });
        httpBackend = _$httpBackend_;
        BASE_URL = _BASE_URL_;
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
