describe("MembershipJsController", function () {

    beforeEach(module('membershipApp'));

    var scope;
    var controller;
    var dataProvider;
    var dataDelete;
    var dataUpdater

    beforeEach(inject(function ($rootScope, $controller, dataProvider, dataDelete, dataUpdater) {
        scope = $rootScope.$new();
        controller = $controller('MembershipJsController', {
            $scope: scope,
            dataProvider: dataProvider,
            deleteData: dataDelete,
            dataUpdater: dataUpdater
        });
    }));

    // it("checkInitFunction", function () {
    //
    //     var dummyElement = document.createElement('span');
    //     document.getElementById = jasmine.createSpy('HTML Element').and.returnValue(dummyElement);
    //
    //     spyOn(scope, "init").and.callFake(function () {
    //         scope.list.push({
    //             "basis": "\u2716",
    //             "name": "Jake Nal",
    //             "uuid": 1021521,
    //             "username": "jnal"
    //         });
    //     });
    // });

});
