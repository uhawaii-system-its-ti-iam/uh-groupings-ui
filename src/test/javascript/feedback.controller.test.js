/* global inject */
describe("FeedbackController", () => {

    beforeEach(module("UHGroupingsApp"));

    let scope;
    let controller;
    const mockUser = {
        uid: "testiwta",
        uhUuid: "99997010"
    };

    beforeEach(inject(($rootScope, $controller) => {
        scope = $rootScope.$new(true);
        scope.currentUser = mockUser;
        controller = $controller("FeedbackJsController", {
            $scope: scope
        });
    }));

    it("should define the feedback controller", () => {
        expect(controller).toBeDefined();
    });

    describe("checkEmail", () => {
        it("should call $scope.displayDynamicModal() when $scope.email is an invalid email", () => {
            scope.feedbackEmail = "invalid_email";
            const mockEvent = jasmine.createSpyObj('event', ['preventDefault']);
            spyOn(scope, "displayDynamicModal").and.callThrough();
            scope.checkEmail(mockEvent);
            expect(scope.displayDynamicModal).toHaveBeenCalled();
        });

        it("shouldn't call $scope.displayDynamicModal() when $scope.email is valid email", () => {
            scope.feedbackEmail = "example@hawaii.edu";
            const mockEvent = jasmine.createSpyObj('event', ['preventDefault']);
            spyOn(scope, "displayDynamicModal").and.callThrough();
            scope.checkEmail(mockEvent);
            expect(scope.displayDynamicModal).not.toHaveBeenCalled();
        });
    });

});
