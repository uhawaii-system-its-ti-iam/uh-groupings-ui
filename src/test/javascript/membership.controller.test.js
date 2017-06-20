describe("MembershipJsController", function () {

    beforeEach(module('membershipApp'));

    var scope;
    var controller;
    var dataProvider;
    var dataUpdater;

    beforeEach(inject(function ($rootScope, $controller, dataProvider, dataUpdater) {
        scope = $rootScope.$new();
        controller = $controller('MembershipJsController', {
            $scope: scope,
            dataProvider: dataProvider,
            dataUpdater:dataUpdater
        });
    }));

    it("checkInitFunction", function () {
        // spyOn(scope, "init").and.callFake(function () {
        //     scope.membersList.push({
        //         "name": "ksanidad-test",
        //     });
        // });
        expect(controller).toBeDefined();
        // expect(scope.membersList).toBeDefined();
    });

});
