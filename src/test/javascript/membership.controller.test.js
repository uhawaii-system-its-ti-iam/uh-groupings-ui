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
            scope.membersList.push({
                "name": "zknobel-test",
            });
            scope.membersList.push({
                "name": "aaronvil-test",
            });
        });
        expect(controller).toBeDefined();
        //expect(scope.membershipList).toBeDefined();
        expect(scope.membersList.length).toEqual(0);
        expect(scope.optInList.length).toEqual(0);
        expect(scope.optOutList.length).toEqual(0);
        expect(scope.optedIn.length).toEqual(0);
        expect(scope.optedOut.length).toEqual(0);

        scope.init();

        expect(scope.init).toHaveBeenCalled();
        expect(scope.membersList).toBeDefined();
        expect(scope.membersList.length).toEqual(3);

        expect(scope.membersList[0].name).toEqual("ksanidad-test");
        expect(scope.membersList[1].name).toEqual("zknobel-test");
        expect(scope.membersList[2].name).toEqual("aaronvil-test");
    });

    it("checkPageGroupFunction", function() {
        spyOn(scope, "init").and.callFake(function() {
            for(var i = 1; i < 101; i++) {
                scope.membersList.push({
                    "name": "group "+i
                });
                scope.pagedItems.push({
                    "name": "group "+i
                });
            }
        });


        scope.init();
        // scope.groupToPages();
        expect(scope.init).toHaveBeenCalled();
        // expect(scope.groupToPages).toHaveBeenCalled();
        expect(scope.membersList.length).toEqual(100);
        expect(scope.itemsPerPage).toEqual(25);
        expect(scope.pagedItems.length).toEqual(100);
        expect(scope.currentPageOptIn).toEqual(0);
        scope.prevPageOptIn();
        expect(scope.currentPageOptIn).toEqual(0);
        scope.nextPageOptIn();
        expect(scope.currentPageOptIn).toEqual(1);
    });

    it("gettingMockDataForOpts", function() {
        spyOn(scope, "init").and.callFake(function() {
            for(var i = 1; i < 101; i++) {
                scope.optInList.push({
                    "name": "group "+i
                });
                scope.optOutList.push({
                    "name": "group "+i
                });
                scope.optedIn.push({
                    "name": "group "+i
                });
                scope.optedOut.push({
                    "name": "group "+i
                });
            }
        });


        expect(scope.optInList).toBeDefined();
        expect(scope.optOutList).toBeDefined();
        expect(scope.optedIn).toBeDefined();
        expect(scope.optedOut).toBeDefined();
        expect(scope.optInList.length).toEqual(0);
        expect(scope.optOutList.length).toEqual(0);
        expect(scope.optedIn.length).toEqual(0);
        expect(scope.optedOut.length).toEqual(0);

        scope.init();
        expect(scope.optInList.length).toEqual(100);
        expect(scope.optOutList.length).toEqual(100);
        expect(scope.optedIn.length).toEqual(100);
        expect(scope.optedOut.length).toEqual(100);
    });

});
