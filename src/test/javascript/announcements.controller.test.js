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

    const multipleAnnouncementsRes = {
        "resultCode": "SUCCESS",
        "announcements": [
            { "message": "First test announcement" },
            { "message": "Second test announcement" }
        ]
    };

    const emptyAnnouncementsRes = {
        "resultCode": "SUCCESS",
        "announcements": []
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

        it("should map multiple announcements to messages in order", () => {
            scope.init();

            httpBackend.expectGET("announcements").respond(200, multipleAnnouncementsRes);
            httpBackend.flush();

            expect(scope.activeAnnouncements.length).toBe(2);
            expect(scope.activeAnnouncements[0]).toBe("First test announcement");
            expect(scope.activeAnnouncements[1]).toBe("Second test announcement");
        });

        it("should leave activeAnnouncements empty when API returns no announcements", () => {
            scope.init();

            httpBackend.expectGET("announcements").respond(200, emptyAnnouncementsRes);
            httpBackend.flush();

            expect(scope.activeAnnouncements.length).toBe(0);
        });

        it("should map messages without filtering on state", () => {
            const responseWithState = {
                "resultCode": "SUCCESS",
                "announcements": [
                    { "message": "active message", "state": "Active" },
                    { "message": "expired message", "state": "Expired" }
                ]
            };

            scope.init();

            httpBackend.expectGET("announcements").respond(200, responseWithState);
            httpBackend.flush();

            expect(scope.activeAnnouncements).toEqual(["active message", "expired message"]);
        });

        it("should display the API error modal when getAnnouncements fails", () => {
            spyOn(scope, "displayApiErrorModal");

            scope.init();

            httpBackend.expectGET("announcements").respond(500);
            httpBackend.flush();

            expect(scope.displayApiErrorModal).toHaveBeenCalled();
        });

        it("should display the API error modal when an authenticated user's announcement request fails", () => {
            scope.currentUser = {
                uid: "testiwta",
                uhUuid: "99997010"
            };
            spyOn(scope, "displayApiErrorModal");

            scope.init();

            httpBackend.expectGET("announcements").respond(500, {
                message: "Unable to retrieve announcements"
            });
            httpBackend.flush();

            expect(scope.displayApiErrorModal).toHaveBeenCalled();
            expect(scope.activeAnnouncements).toEqual([]);
        });

        it("should not display the API error modal when an anonymous user's announcement request fails", () => {
            scope.currentUser = {};

            spyOn(scope, "displayApiErrorModal");
            spyOn(console, "error");

            scope.init();

            httpBackend.expectGET("announcements").respond(500, {
                message: "Unable to retrieve announcements"
            });
            httpBackend.flush();

            expect(scope.displayApiErrorModal).not.toHaveBeenCalled();
            expect(scope.activeAnnouncements).toEqual([]);
            expect(console.error).toHaveBeenCalled();
        });

        it("should log an error and set activeAnnouncements to empty when the response has an unexpected shape", () => {
            spyOn(console, "error");

            scope.init();

            httpBackend.expectGET("announcements").respond(200, {
                resultCode: "SUCCESS"
            });
            httpBackend.flush();

            expect(console.error).toHaveBeenCalledWith(
                "Unexpected announcements response; expected {announcements: []} but received:",
                jasmine.anything()
            );
            expect(scope.activeAnnouncements).toEqual([]);
        });
    });
});
