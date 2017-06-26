describe("OwnerJsController", function () {

    beforeEach(module('ownerApp'));

    var scope;
    var controller;
    var dataProvider;
    var dataDeleter;
    var dataUpdater;

    beforeEach(inject(function ($rootScope, $controller, dataProvider, dataDeleter, dataUpdater) {
        scope = $rootScope.$new();
        controller = $controller('OwnerJsController', {
            $scope: scope,
            dataProvider: dataProvider,
            deleteData: dataDeleter,
            dataUpdater: dataUpdater
        });

        spyOn(scope, "init").and.callFake(function () {
            scope.ownedList.push({
                "name": "test name",
                "folder": "test folder",
                "url": "test url"
            });
        });
    }));

    var dummyElement = document.createElement('span');
    document.getElementById = jasmine.createSpy('HTML Element').and.returnValue(dummyElement);

    it("checkInitFunction", function () {
        expect(controller).toBeDefined();
        expect(scope.ownedList).toBeDefined();
        expect(scope.ownedList.length).toEqual(0);

        // What we are testing
        scope.init();

        expect(scope.init).toHaveBeenCalled();
        expect(scope.ownedList).toBeDefined();
    });

    it("Check Get Data", function () {

        spyOn(scope, "showData").and.callFake(function () {
            scope.groupingName.push({ "url": "test Url" });
        });

        expect(controller).toBeDefined();
        expect(scope.groupingName).toBeDefined();
        expect(scope.getData).toBeDefined();
        expect(scope.modify).toBeDefined();
    });

    it("Check Add Member", function () {
        spyOn(scope, "addMember").and.callFake(function () {
            scope.ownedList.push({
                "name": "test 2 name",
                "folder": "test 2 folder",
                "url": "test 2 url"
            });
        });
        spyOn(scope, "getData").and.callThrough();

        scope.init();
        expect(scope.ownedList.length).toEqual(1);
        scope.addMember();
        expect(scope.ownedList.length).toEqual(2);
        expect(scope.modify).toBeDefined();

    });

    it("Check Remove Member", function () {
        spyOn(scope, "removeMember").and.callFake(function () {
            scope.ownedList.pop();
        });
        spyOn(scope, "addMember").and.callFake(function () {
            scope.ownedList.push({
                "name": "test 2 name",
                "folder": "test 2 folder",
                "url": "test 2 url"
            });
        });
        scope.init();
        scope.addMember();

        expect(scope.ownedList.length).toEqual(2);
        scope.removeMember();
        expect(scope.ownedList.length).toEqual(1);

    });

});