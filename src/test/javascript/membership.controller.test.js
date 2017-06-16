describe("MembershipJsController", function() {

    beforeEach(module('membershipApp'));

    var scope;
    var controller;
    var dataProvider;

    beforeEach(inject(function($rootScope, $controller, dataProvider) {
        scope = $rootScope.$new();
        controller = $controller('MembershipJsController', {
            $scope: scope,
            dataProvider: dataProvider
        });
    }));

    
})
