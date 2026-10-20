/* global inject */

describe("TimeoutController", function () {

    beforeEach(module("UHGroupingsApp"));

    // userService caches the current user in sessionStorage, where another spec may have left a different one.
    beforeEach(() => sessionStorage.removeItem("currentUserDataSession"));

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
            httpBackend.whenGET("modal/timeoutModal").respond(200);

            scope.displayTimeoutModal();

            expect(scope.displayTimeoutModal).toHaveBeenCalled();

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

    describe("asyncJobPolled", () => {
        const MINUTE = 60 * 1000;
        let rootScope;

        beforeEach(inject(($rootScope) => {
            rootScope = $rootScope;
            httpBackend.whenGET("currentUser").respond(200, {});
            httpBackend.whenGET("modal/timeoutModal").respond(200, "");
        }));

        // The idle timer is started once the document is ready.
        beforeEach((done) => setTimeout(done));

        const pollJob = () => {
            rootScope.$broadcast("asyncJobPolled");
            rootScope.$digest();
        };

        it("should not display the inactivity warning while an async job is being polled", () => {
            spyOn(scope, "displayTimeoutModal");

            for (let elapsed = 0; elapsed < 60 * MINUTE; elapsed += 5 * MINUTE) {
                timeout.flush(5 * MINUTE);
                pollJob();
            }
            expect(scope.displayTimeoutModal).not.toHaveBeenCalled();

            timeout.flush(25 * MINUTE);
            expect(scope.displayTimeoutModal).toHaveBeenCalled();
        });

        it("should reset the timer when an async job is polled, but not while the inactivity warning is open", () => {
            spyOn(timeout, "cancel").and.callThrough();
            pollJob();
            expect(timeout.cancel).toHaveBeenCalledTimes(1);

            scope.displayTimeoutModal();
            httpBackend.flush();
            rootScope.$digest();
            timeout.cancel.calls.reset();

            pollJob();

            expect(timeout.cancel).not.toHaveBeenCalled();
        });
    });

    describe("$destroy", () => {
        beforeEach(() => {
            httpBackend.whenGET("currentUser").respond(200, {});
            httpBackend.whenGET("modal/timeoutModal").respond(200, "");
        });

        it("should stop the countdown of an open inactivity warning", () => {
            scope.displayTimeoutModal();
            httpBackend.flush();
            spyOn(interval, "cancel").and.callThrough();

            expect(() => scope.$destroy()).not.toThrow();
            expect(interval.cancel).toHaveBeenCalled();
        });
    });
});
