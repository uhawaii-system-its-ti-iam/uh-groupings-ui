describe("AdminJsController", function () {

    beforeEach(module('adminApp'));

    var scope;
    var controller;
    var dataProvider;
    var dataDelete;
    var dataUpdater

    beforeEach(inject(function ($rootScope, $controller, dataProvider, dataDelete, dataUpdater) {
        scope = $rootScope.$new();
        controller = $controller('AdminJsController', {
            $scope: scope,
            dataProvider: dataProvider,
            deleteData: dataDelete,
            dataUpdater: dataUpdater
        });
    }));

    it("checkInitFunction", function () {

        var dummyElement = document.createElement('span');
        document.getElementById = jasmine.createSpy('HTML Element').and.returnValue(dummyElement);

        spyOn(scope, "init").and.callFake(function () {
            scope.list.push({
                "basis": "\u2716",
                "name": "Jake Nal",
                "uuid": 1021521,
                "username": "jnal"
            });
        });

        expect(controller).toBeDefined();
        expect(scope.list).toBeDefined();
        expect(scope.list.length).toEqual(0);

        // What we are testing
        scope.init();

        expect(scope.init).toHaveBeenCalled();
        expect(scope.list).toBeDefined();
        expect(scope.list.length).toEqual(1);

        expect(scope.list[0].name).toEqual("Jake Nal");
    });

    it("Check Add Function", function () {
        spyOn(scope, "init").and.callFake(function () {
            scope.list.push({
                "basis": "\u2716",
                "name": "Jake Nal",
                "uuid": 1021521,
                "username": "jnal"
            });
        });

        spyOn(scope, "add").and.callFake(function () {
            scope.list.push({
                "basis": "\u2716",
                "name": "Jacob Akek",
                "uuid": 1024321,
                "username": "jakek"
            });
        });

        // What we are testing
        scope.init();
        scope.add("jakek");

        expect(scope.add).toHaveBeenCalled();
        expect(scope.list.length).toEqual(2);
        expect(scope.list[1].name).toEqual("Jacob Akek");
    });


    it("Check Remove Function", function () {
        spyOn(scope, "init").and.callFake(function () {
            scope.list.push({
                "basis": "\u2716",
                "name": "Jake Nal",
                "uuid": 1021521,
                "username": "jnal"
            }, {
                "basis": "\u2716",
                "name": "Jacob Akek",
                "uuid": 1024321,
                "username": "jakek"
            });
        });

        spyOn(scope, "remove").and.callFake(function () {
            if (scope.list.length > 1) {
                scope.list.pop();
            }
        });

        expect(controller).toBeDefined();
        expect(scope.list).toBeDefined();
        expect(scope.list.length).toEqual(0);

        scope.init();
        expect(scope.init).toHaveBeenCalled();

        expect(scope.list.length).toEqual(2);
        scope.remove(1);
        expect(scope.remove).toHaveBeenCalled();
        expect(scope.list.length).toEqual(1);
        expect(scope.list[0].username).toEqual("jnal");
    });

    it("Check Fail Remove Function", function () {
        spyOn(scope, "init").and.callFake(function () {
            scope.list.push({
                "basis": "\u2716",
                "name": "Jake Nal",
                "uuid": 1021521,
                "username": "jnal"
            });
        });

        spyOn(scope, "remove").and.callFake(function () {
            if (scope.list.length > 1) {
                scope.list.pop();
            }
        });

        expect(controller).toBeDefined();
        expect(scope.list).toBeDefined();
        expect(scope.list.length).toEqual(0);

        scope.init();

        expect(scope.list.length).toEqual(1);
        scope.remove(0);
        expect(scope.list.length).toEqual(1);
    });


});