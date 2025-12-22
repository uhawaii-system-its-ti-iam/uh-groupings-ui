/* global inject */
describe("AnnouncementsJsController", function () {

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    let scope;
    let controller;
    let httpBackend;
    let gs;
    let mockUserService;
    const mockUser = {
        data: {
            uid: "testiwta", uhUuid: "99997010"
        }
    };

    beforeEach(() => {
        mockUserService = {
            getCurrentUser: jasmine.createSpy("getCurrentUser")
        };

        module(($provide) => {
            $provide.value("userService", mockUserService);
        });
    });

    beforeEach(inject(($rootScope, $controller, _$httpBackend_, _$q_, groupingsService) => {
        scope = $rootScope.$new(true);
        httpBackend = _$httpBackend_;
        gs = groupingsService;

        mockUserService.getCurrentUser.and.returnValue(_$q_.when(mockUser));
        controller = $controller("AnnouncementsJsController", {
            $scope: scope
        });
        scope.$apply();
    }));

    const announcementsRes = {
        "resultCode": "SUCCESS", "announcements": [{
            "message": "Test is now running on VMs featuring Java 17 (hello Spring Boot3)"
        }]
    };

    it("should define the announcements controller", () => {
        expect(controller).toBeDefined();
    });

    describe("init", () => {
        it("should call getAnnouncements from groupingsService", () => {
            spyOn(gs, "getAnnouncements").and.callThrough();
            scope.init();
            expect(gs.getAnnouncements).toHaveBeenCalled();
        });

        it("should map announcements to messages directly (API filters to Active only)", () => {
            scope.init();

            expect(mockUserService.getCurrentUser).toHaveBeenCalled();
            httpBackend.expectGET("announcements").respond(200, announcementsRes);
            httpBackend.flush();

            expect(scope.activeAnnouncements.length).toBe(1);
            expect(scope.activeAnnouncements[0]).toBe("Test is now running on VMs featuring Java 17 (hello Spring Boot3)");
        });
    });
});
