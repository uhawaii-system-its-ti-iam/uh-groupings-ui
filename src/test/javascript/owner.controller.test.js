describe("OwnerController", function () {

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    let scope;
    let controller;
    let httpBackend;
    let BASE_URL;

    beforeEach(inject(function ($rootScope, $controller, _BASE_URL_, _$httpBackend_) {
        scope = $rootScope.$new();
        controller = $controller("OwnerJsController", {
            $scope: scope
        });
        httpBackend = _$httpBackend_;
        BASE_URL = _BASE_URL_;
    }));

    it("should define the owner controller", function () {
        expect(controller).toBeDefined();
    });

});
