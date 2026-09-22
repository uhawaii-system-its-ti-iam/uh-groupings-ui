/* global inject */
describe("HomeController", () => {

    beforeEach(module("UHGroupingsApp"));

    let scope;
    let controller;
    let httpBackend;
    let BASE_URL;
    let gs;
    let us;

    beforeEach(inject(($rootScope, $controller, _BASE_URL_, _$httpBackend_, groupingsService, userService) => {
        scope = $rootScope.$new(true);
        controller = $controller("HomeJsController", {
            $scope: scope,
        });
        httpBackend = _$httpBackend_;
        BASE_URL = _BASE_URL_;
        gs = groupingsService;
        us = userService;
    }));

    it("should define the home controller", () => {
        expect(controller).toBeDefined();
    });

    describe("init", () => {
        beforeEach(() => {
            spyOn(us, "refresh").and.callThrough();
            spyOn(gs, "getNumberOfMemberships").and.callFake((callback) => {
                callback(5);
            });
            spyOn(gs, "getNumberOfGroupings").and.callFake((callback) => {
                callback(3);
            });
            scope.init();
        });

        it("should call userService.refresh()", () => {
            expect(us.refresh).toHaveBeenCalled();
        });

        it("should set the number of memberships in the scope", () => {
            expect(gs.getNumberOfMemberships).toHaveBeenCalledWith(jasmine.any(Function), jasmine.any(Function));
            expect(scope.numberOfMemberships).toEqual(5);
        });

        it("should set the number of groupings in the scope", () => {
            expect(gs.getNumberOfGroupings).toHaveBeenCalledWith(jasmine.any(Function), jasmine.any(Function));
            expect(scope.numberOfGroupings).toEqual(3);
        });
    });

    describe("count error handling", () => {
        const backendUnavailableResponse = {
            data: {
                resultCode: "BACKEND_UNAVAILABLE"
            }
        };

        it("should show a fallback when the membership count is unavailable", () => {
            spyOn(us, "refresh").and.callThrough();
            spyOn(gs, "getNumberOfMemberships").and.callFake((callback, callError) => {
                callError(backendUnavailableResponse);
            });
            spyOn(gs, "getNumberOfGroupings").and.callFake((callback) => {
                callback(3);
            });

            scope.init();

            expect(scope.numberOfMemberships).toEqual("--");
            expect(scope.numberOfGroupings).toEqual(3);
        });

        it("should show a fallback when the owner grouping count is unavailable", () => {
            spyOn(us, "refresh").and.callThrough();
            spyOn(gs, "getNumberOfMemberships").and.callFake((callback) => {
                callback(5);
            });
            spyOn(gs, "getNumberOfGroupings").and.callFake((callback, callError) => {
                callError(backendUnavailableResponse);
            });

            scope.init();

            expect(scope.numberOfMemberships).toEqual(5);
            expect(scope.numberOfGroupings).toEqual("--");
        });
    });
});
