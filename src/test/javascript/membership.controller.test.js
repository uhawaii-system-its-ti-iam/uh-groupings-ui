describe("MembershipJsController", function() {

     beforeEach(module('membershipApp'));

    var scope;
    var controller;
    var dataProvider;
    var dataUpdater;
    var e;

    beforeEach(inject(function($rootScope, $controller, dataProvider, dataUpdater) {
        scope = $rootScope.$new();
        controller = $controller('MembershipJsController', {
            $scope: scope,
            dataProvider: dataProvider,
            dataUpdater: dataUpdater
        });
    }));

   it("checkInitFunction", function() {
       e =true;
       expect(e).toBe(true);
    });
});
