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
    }));

    it("checkInitFunction", function () {

        var dummyElement = document.createElement('span');
        document.getElementById = jasmine.createSpy('HTML Element').and.returnValue(dummyElement);

        spyOn(scope, "init").and.callFake(function () {
            scope.ownedList.push({
                "name": "test name",
                "folder": "test folder",
                "url": "test url"
            });
        });

        expect(controller).toBeDefined();
        expect(scope.ownedList).toBeDefined();
        expect(scope.ownedList.length).toEqual(0);

        // What we are testing
        scope.init();
        
        expect(scope.init).toHaveBeenCalled();
        expect(scope.ownedList).toBeDefined();
    });
});