describe("AdminController", function () {

    // Set up mock element for setting the current user
    var mockElement = document.createElement("div");
    mockElement.innerHTML = "jdoe";
    document.getElementById = jasmine.createSpy("name").and.returnValue(mockElement);

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    var scope;
    var controller;
    var httpBackend;
    var BASE_URL;

    beforeEach(inject(function ($rootScope, $controller, _BASE_URL_, _$httpBackend_) {
        scope = $rootScope.$new();
        controller = $controller("AdminJsController", {
            $scope: scope
        });
        httpBackend = _$httpBackend_;
        BASE_URL = _BASE_URL_;
    }));

    it("should define the admin controller", function () {
        expect(controller).toBeDefined();
    });

    it("should correctly set the currentUser", function () {
        expect(scope.currentUser).toEqual("jdoe");
    });

    describe("displayAdmins", function () {
        it("should call resetGroupingInformation", function () {
            spyOn(scope, "resetGroupingInformation").and.callThrough();
            scope.displayAdmins();

            expect(scope.resetGroupingInformation).toHaveBeenCalled();
        });

        it("should repaginate the groupings list table (since the filter is reset)", function () {
            spyOn(scope, "groupToPages").and.callThrough();
            scope.displayAdmins();

            expect(scope.groupToPages).toHaveBeenCalledWith(scope.groupingsList);
        });

        describe("a user is currently looking at a selected grouping", function () {
            beforeEach(function () {
                scope.showGrouping = true;
            });

            it("should no longer show the selected grouping", function () {
                scope.displayAdmins();

                expect(scope.showGrouping).toBe(false);
            });
        });
    });


});
