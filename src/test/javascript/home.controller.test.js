/* global inject */
describe("HomeController", () => {

    beforeEach(module("UHGroupingsApp"));

    let scope;
    let controller;
    let httpBackend;
    let BASE_URL;
    let gs;

    beforeEach(inject(($rootScope, $controller, _BASE_URL_, _$httpBackend_, groupingsService) => {
        scope = $rootScope.$new(true);
        controller = $controller("HomeJsController", {
            $scope: scope,
        });
        httpBackend = _$httpBackend_;
        BASE_URL = _BASE_URL_;
        gs = groupingsService;
    }));

    it("should define the home controller", () => {
        expect(controller).toBeDefined();
    });

    describe("init", () => {
        it("should call groupingsService.getNumberOfMemberships", () => {
            spyOn(gs, "getNumberOfMemberships").and.callThrough();
            scope.init();
            expect(gs.getNumberOfMemberships).toHaveBeenCalled();
        });

        it("should call groupingsService.getNumberOfGroupings", () => {
            spyOn(gs, "getNumberOfGroupings").and.callThrough();
            scope.init();
            expect(gs.getNumberOfGroupings).toHaveBeenCalled();
        });
    });
});
