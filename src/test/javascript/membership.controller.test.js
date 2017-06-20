describe("MembershipJsController", function () {

    beforeEach(module('membershipApp'));

    var scope;
    var testing;
    var controller;
    var dataProvider;
    var dataUpdater;

    beforeEach(inject(function ($rootScope, $controller, dataProvider, dataUpdater) {
        testing = false;
        scope = $rootScope.$new();
        // expect(scope).toBeDefined();
        controller = $controller('MembershipJsController', {
            $scope: scope,
            dataProvider: dataProvider,
            dataUpdater:dataUpdater
        });
    }));

    it("checkInitFunction", function () {
        spyOn(scope, "init").and.callFake(function () {
            scope.membersList.push({
                "name": "ksanidad-test",
            });
        });
        expect(controller).toBeDefined();
    });

});
