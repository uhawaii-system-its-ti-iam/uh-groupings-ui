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
                scope.pagedItems1.push({
                    "name": "group "+i
                });
            }
        });


        scope.init();
        // scope.groupToPages();
        expect(scope.init).toHaveBeenCalled();
        // expect(scope.groupToPages).toHaveBeenCalled();
        expect(scope.membersList.length).toEqual(100);
        expect(scope.itemsPerPage).toEqual(5);
        expect(scope.pagedItems1.length).toEqual(100);
        expect(scope.currentPageOptIn).toEqual(0);
        scope.currentPage('Page Opt In Prev');
        expect(scope.currentPageOptIn).toEqual(0);
        scope.currentPage('Page Opt In Next');
        expect(scope.currentPageOptIn).toEqual(1);
    });

    it("gettingMockDataForOpts", function() {
        spyOn(scope, "init").and.callFake(function() {
            for(var i = 1; i < 101; i++) {
                scope.optInList.push({
                    "name": "Grouped "+i
                });
                scope.membersList.push({
                    "name": "Member "+i
                });
                scope.optedIn.push({
                    "name": "Opted In "+i
                });
                scope.optedOut.push({
                    "name": "Opted Out "+i
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
        expect(scope.membersList.length).toEqual(100);
        expect(scope.optedIn.length).toEqual(100);
        expect(scope.optedOut.length).toEqual(100);

        expect(scope.membersList[0].name).toEqual("Member 1");
        expect(scope.optInList[25].name).toEqual("Grouped 26");
        expect(scope.optedIn[56].name).toEqual("Opted In 57");
        expect(scope.optedOut[17].name).toEqual("Opted Out 18");
    });

    it("groupToPagesTesting", function(){
        /**
        *  Generic placeholders
        **/
        var testOptInList = ["ora","ora","ora","ora","ora"];
        var testMembersList = ["ora","ora","ora","ora","ora"];
        var testOptedIn = ["ora","ora","ora","ora","ora"];
        var testOptedOut = ["ora","ora","ora","ora","ora"];
        var n = 2;

        scope.pagedItems1 = testOptInList;
        scope.pagedItems3 = testMembersList;
        scope.pagedItems4 = testOptedIn;
        scope.pagedItems5 = testOptedOut;

        expect(testOptInList.length).toEqual(5);
        expect(scope.pagedItems1.length).toEqual(5);
        expect(scope.currentPageOptIn).toEqual(0);
        expect(scope.currentPageOptOut).toEqual(0);
        expect(scope.currentPageCancelOptIn).toEqual(0);
        expect(scope.currentPageCancelOptOut).toEqual(0);

        scope.currentPage("Page Opt In Next");
        expect(scope.currentPageOptIn).toEqual(1);
        scope.currentPage("Page Opt In Next");
        scope.currentPage("Page Opt In Next");
        expect(scope.currentPageOptIn).toEqual(3);
        scope.currentPage("Page Opt In Prev");
        expect(scope.currentPageOptIn).toEqual(2);

        scope.currentPage("Cancel Opt In Next");
        expect(scope.currentPageCancelOptIn).toEqual(1);
        scope.currentPage("Cancel Opt In Next");
        expect(scope.currentPageCancelOptIn).toEqual(2);
        scope.currentPage("Cancel Opt In Next");
        expect(scope.currentPageCancelOptIn).toEqual(3);
        scope.currentPage("Cancel Opt In Next");
        expect(scope.currentPageCancelOptIn).toEqual(4);
        scope.currentPage("Cancel Opt In Next");
        expect(scope.currentPageCancelOptIn).toEqual(4);
        scope.currentPage("Cancel Opt In Prev");
        expect(scope.currentPageCancelOptIn).toEqual(3);
        // scope.currentPage("Cancel Opt In Prev");
        // expect(scope.currentPageCancelOptIn).toEqual(4);

        scope.currentPage("Cancel Opt Out set");
        expect(scope.currentPageCancelOptOut).toEqual(2);


    });

    it("testingRangem", function(){

        var testingRange = [];

        expect(testingRange).toBeDefined();
        testingRange = scope.range(20,14,19);
        expect(testingRange.length).toEqual(5);
        expect(testingRange[0]).toEqual(14);
        testingRange = scope.range(20,15,20);
        expect(testingRange.length).toEqual(5);
        expect(testingRange[0]).toEqual(15);
        testingRange = scope.range(20,16,21);
        expect(testingRange.length).toEqual(4);
        expect(testingRange[0]).toEqual(16);
    });

    it("testing Sort", function () {
      spyOn(scope, "init").and.callFake(function () {
        scope.membersList.push({
          "name": "ksanidad-test"
        });
        scope.membersList.push({
          "name": "zknobel-test"
        });
        scope.membersList.push({
          "name": "aaronvil-test"
        });
      });

      spyOn(_, 'sortBy').and.callFake(function () {
        scope.membersList.push({
          "name": "ksanidad-test"
        });
      });

      expect(controller).toBeDefined();
      expect(scope.membersList).toBeDefined();
      expect(scope.membersList.length).toEqual(0);

      // What we are testing
      scope.init();

      expect(scope.init).toHaveBeenCalled();
      expect(scope.membersList).toBeDefined();
      expect(scope.membersList.length).toEqual(3);

      expect(scope.membersList[0].name).toEqual("ksanidad-test");

      var list = scope.membersList;

      scope.sort(list, 'name', 'pagedItems1', 'symbol');

      expect(scope.sort).toHaveBeenCalled();
    })

});
