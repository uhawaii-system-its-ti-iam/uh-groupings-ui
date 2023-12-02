/* global inject */
describe("AnnouncementsJsController", function () {

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    let scope;
    let controller;
    let gs;

    beforeEach(inject(($rootScope, $controller, groupingsService) => {
        scope = $rootScope.$new(true);
        controller = $controller("AnnouncementsJsController", {
            $scope: scope
        });
        gs = groupingsService;
    }));

    it("should define the announcements controller", () => {
        expect(controller).toBeDefined();
    });

    describe("init", () => {
        it("should call getActiveAnnouncements from groupingsService", () => {
            spyOn(gs, "getActiveAnnouncements").and.callThrough();
            scope.init();
            expect(gs.getActiveAnnouncements).toHaveBeenCalled();
        });
    });
});