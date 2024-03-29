/* global inject */
describe("AnnouncementsJsController", function () {

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    let scope;
    let controller;
    let httpBackend;
    let gs;

    beforeEach(inject(($rootScope, $controller, _$httpBackend_, groupingsService) => {
        scope = $rootScope.$new(true);
        controller = $controller("AnnouncementsJsController", {
            $scope: scope
        });
        httpBackend = _$httpBackend_;
        gs = groupingsService;
    }));

    const announcementsRes = {
        "resultCode": "SUCCESS",
        "announcements": [
            {
                "message": "old message",
                "start": "20230607T000000",
                "end": "20230615T000000",
                "state": "Expired"
            },
            {
                "message": "Test will be down for migration to new VMs featuring Java 17 (required for Spring Boot 3)",
                "start": "20231206T000000",
                "end": "20231208T110000",
                "state": "Expired"
            },
            {
                "message": "Test is now running on VMs featuring Java 17 (hello Spring Boot3)",
                "start": "20231208T110000",
                "end": "20240215T000000",
                "state": "Active"
            }
        ]
    }

    it("should define the announcements controller", () => {
        expect(controller).toBeDefined();
    });

    describe("init", () => {
        it("should call getAnnouncements from groupingsService", () => {
            spyOn(gs, "getAnnouncements").and.callThrough();
            scope.init();
            expect(gs.getAnnouncements).toHaveBeenCalled();
        });

        it("should call $scope.handleActiveAnnouncements", () => {
            spyOn(scope, "handleActiveAnnouncements");
            scope.init();

            httpBackend.whenGET("currentUser").passThrough();
            httpBackend.expectGET("announcements").respond(200, announcementsRes);
            httpBackend.flush()

            expect(scope.handleActiveAnnouncements).toHaveBeenCalledWith(announcementsRes.announcements);
            expect(scope.activeAnnouncements).toBe(scope.handleActiveAnnouncements(announcementsRes.announcements))
        })
    });

    describe("handleActiveAnnouncements", () => {
        it("should filter by active announcements and map to list of messages", () => {
            const activeAnnouncements = scope.handleActiveAnnouncements(announcementsRes.announcements);
            expect(activeAnnouncements.length).toBe(1);
            expect(activeAnnouncements[0]).toBe("Test is now running on VMs featuring Java 17 (hello Spring Boot3)");
        });
    });
});
