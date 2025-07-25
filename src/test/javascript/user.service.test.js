/* global inject */

describe("UserService", function () {

    beforeEach(module("UHGroupingsApp"));

    let userService;
    let groupingsService;
    let $q;
    let $rootScope;
    let $window;
    let $injector;

    const mockUser = {
        uid: "testiwta",
        uhUuid: "99997010"
    };

    beforeEach(inject((_groupingsService_, _$q_, _$rootScope_, _$window_, _$injector_) => {
        groupingsService = _groupingsService_;
        $q = _$q_;
        $rootScope = _$rootScope_;
        $window = _$window_;
        $injector = _$injector_; // Store the injector to get new service instances.

        spyOn(groupingsService, "getCurrentUser").and.callFake((onSuccess) => {
            onSuccess(mockUser);
        });

        spyOn($window.sessionStorage, "setItem").and.callThrough();
    }));

    beforeEach(() => {
        $window.sessionStorage.removeItem("currentUserDataSession");
        $window.sessionStorage.setItem.calls.reset();
        groupingsService.getCurrentUser.calls.reset();
    });

    describe("getCurrentUser()", () => {

        it("should be defined", inject((_userService_) => {
            expect(_userService_.getCurrentUser).toBeDefined();
        }));

        describe("when no user is cached in memory or session storage", () => {

            // Get a fresh instance of the service before these tests run.
            beforeEach(() => {
                userService = $injector.get("userService");
            });

            it("should call groupingsService.getCurrentUser", () => {
                userService.getCurrentUser();
                expect(groupingsService.getCurrentUser).toHaveBeenCalled();
            });

            it("should save the fetched user to session storage", () => {
                userService.getCurrentUser();
                $rootScope.$apply(); // Resolve the promise

                expect($window.sessionStorage.setItem).toHaveBeenCalledWith("currentUserDataSession", JSON.stringify(mockUser));
            });

            it("should only call the API once for concurrent requests", () => {
                userService.getCurrentUser();
                userService.getCurrentUser(); // Second call

                expect(groupingsService.getCurrentUser.calls.count()).toBe(1);
            });
        });

        describe("when a user is already in memory", () => {

            // Get a fresh instance and then preload the user into memory.
            beforeEach(() => {
                userService = $injector.get("userService");
                // Preload a user into the fresh service instance
                userService.getCurrentUser();
                $rootScope.$apply();
                // Reset spies after the setup call
                groupingsService.getCurrentUser.calls.reset();
            });

            it("should not call groupingsService.getCurrentUser", () => {
                userService.getCurrentUser();
                expect(groupingsService.getCurrentUser).not.toHaveBeenCalled();
            });

            it("should return an immediately resolved promise with the cached user", (done) => {
                let resolvedUser = null;
                userService.getCurrentUser().then((res) => {
                    resolvedUser = res;
                });
                $rootScope.$apply();
                expect(resolvedUser).toEqual(mockUser);
                done();
            });
        });

        describe("when a user is in session storage but not in memory", () => {

            // Get a fresh instance, which will automatically load from sessionStorage.
            beforeEach(() => {
                $window.sessionStorage.setItem("currentUserDataSession", JSON.stringify(mockUser));
                userService = $injector.get("userService");
            });

            it("should load the user from session storage and not call the API", (done) => {
                let resolvedUser = null;
                userService.getCurrentUser().then((res) => {
                    resolvedUser = res;
                });
                $rootScope.$apply();
                expect(groupingsService.getCurrentUser).not.toHaveBeenCalled();
                expect(resolvedUser).toEqual(mockUser);
                done();
            });
        });
    });
});