/* global inject */

describe("TimeoutController", function () {

    beforeEach(module("UHGroupingsApp"));

    let scope;
    let controller;
    let window;
    let uibModal;
    let BASE_URL;
    let interval;
    let dp;
    let timeout;
    let httpBackend;

    beforeEach(inject(($rootScope, _$window_, $uibModal, $controller, _BASE_URL_, $timeout, $interval, dataProvider, _$httpBackend_) => {
        scope = $rootScope.$new(true);

        window = {
            location: {
                pathname: "/uhgroupings/",
                href: _$window_
            }
        };

        controller = $controller("TimeoutJsController", {
            $scope: scope,
            $window: window
        });

        uibModal = $uibModal;
        BASE_URL = _BASE_URL_;
        interval = $interval;
        timeout = $timeout;
        dp = dataProvider;
        uibModal = $uibModal;
        httpBackend = _$httpBackend_;
    }));


    it("should define the timeout controller", () => {
        expect(controller).toBeDefined();
    });

    describe("idleTimeReached", () => {
        it("should logout user when idle time is reached", () => {

            spyOn(scope,"displayTimeoutModal");
            spyOn(scope,"logoutOnIdle");

            let mockResponse  = {data: "mock"};
            httpBackend.expectGET('currentUser').respond(200, mockResponse);

            scope.displayTimeoutModal();

            timeout.flush();

            expect(scope.displayTimeoutModal).toHaveBeenCalled();
            httpBackend.flush();

            scope.secondsRemaining = 0;
            scope.timer();
            expect(scope.logoutOnIdle).toHaveBeenCalled();
        });

    });

    describe("timer", () => {
        it("should decrement seconds remaining", () => {
            scope.secondsRemaining = 10;
            scope.timer();
            expect(scope.secondsRemaining).toEqual(9);
        });

        it("should call logoutOnIdle when seconds remaining <= 0", () => {
            spyOn(scope,"logoutOnIdle");
            scope.secondsRemaining = 0;
            scope.timer();
            expect(scope.logoutOnIdle).toHaveBeenCalled();
        });
    });

    describe("displayTimeoutModal", () => {
        it("should check the timeoutModalInstance is displayed", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.displayTimeoutModal();
            expect(uibModal.open).toHaveBeenCalled();
        });

    });

    describe("closeTimeoutModal", () => {
        beforeEach(() => {
            scope.timeoutModalInstance = {
                close: () => {}
            };
        });

        it("should close timeoutModalInstance", () => {
            spyOn(scope.timeoutModalInstance, "close").and.callThrough();
            scope.closeTimeoutModal();
            expect(scope.timeoutModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("timer", () => {
        it("should decrement seconds remaining", () => {
            scope.secondsRemaining = 10;
            scope.timer();
            expect(scope.secondsRemaining).toEqual(9);
        });

        it("should call logoutOnIdle when seconds remaining <= 0", () => {
            spyOn(scope,"logoutOnIdle");
            scope.secondsRemaining = 0;
            scope.timer();
            expect(scope.logoutOnIdle).toHaveBeenCalled();
        });
    });
});
