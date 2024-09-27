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
        beforeEach(() => {
            spyOn(gs, "getCurrentUser").and.callFake((callback) => {
                callback({
                    data: {
                        uid: "user123",
                        uhUuid: "uuid456"
                    }
                });
            });
            spyOn(gs, "getNumberOfMemberships").and.callFake((callback) => {
                callback(5);
            });
            spyOn(gs, "getNumberOfGroupings").and.callFake((callback) => {
                callback(3);
            });
            scope.init();
        });

        it("should initialize currentUser in scope", () => {
            expect(gs.getCurrentUser).toHaveBeenCalled();
            expect(scope.currentUser.uid).toEqual("user123");
            expect(scope.currentUser.uhUuid).toEqual("uuid456");
        });

        it("should set the number of memberships in the scope", () => {
            expect(gs.getNumberOfMemberships).toHaveBeenCalled();
            expect(scope.numberOfMemberships).toEqual(5);
        });

        it("should set the number of groupings in the scope", () => {
            expect(gs.getNumberOfGroupings).toHaveBeenCalled();
            expect(scope.numberOfGroupings).toEqual(3);
        });
    });
});
