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

    it("checkInitFunction", function() {
        spyOn(scope, "loadData").and.callFake(function() {
            for(var i = 1; i <= 100 ; i++){
            scope.membersList.push({
                "name": "Member "+ i
            });
            }
        });

        /**
        expected it to be somewhat defined before calling the init
        **/
        expect(controller).toBeDefined();
        expect(membersList).toBeDefined();


        /**
        calling the init function to start the testing
        **/
        scope.init();
        
        expect(scope.loadData).toHaveBeenCalled();
    });
});
