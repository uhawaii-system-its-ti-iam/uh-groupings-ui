describe("OwnerController", function () {

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
        controller = $controller("OwnerJsController", {
            $scope: scope
        });
        httpBackend = _$httpBackend_;
        BASE_URL = _BASE_URL_;
    }));

    it("should define the owner controller", function () {
        expect(controller).toBeDefined();
    });

    it("should correctly set the currentUser", function () {
        expect(scope.currentUser).toEqual("jdoe");
    });

});
