describe("MembershipController", function () {

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    var scope;
    var controller;
    var httpBackend;
    var BASE_URL;

    beforeEach(inject(function ($rootScope, $controller, _BASE_URL_, _$httpBackend_) {
        scope = $rootScope.$new();
        controller = $controller("MembershipJsController", {
            $scope: scope
        });
        httpBackend = _$httpBackend_;
        BASE_URL = _BASE_URL_;
    }));

    it("should define the membership controller", function () {
        expect(controller).toBeDefined();
    });

    it("should define the variables and methods in the table controller", function () {
        expect(scope.columnSort).toBeDefined();
        expect(scope.groupToPages).toBeDefined();
        expect(scope.filter).toBeDefined();
        expect(scope.pageRange).toBeDefined();
        expect(scope.setPage).toBeDefined();
        expect(scope.disableFirstAndPrev).toBeDefined();
        expect(scope.disableNextAndLast).toBeDefined();
        expect(scope.sortBy).toBeDefined();
    });

    describe("init", function () {
        var mockResponse;

        beforeEach(function () {
            mockResponse = {
                groupingsIn: [
                    {
                        path: "path1:path2:grouping1",
                        name: "grouping1"
                    },
                    {
                        path: "path1:path3:grouping2",
                        name: "grouping2"
                    },
                    {
                        path: "path1:path2:grouping3",
                        name: "grouping3"
                    }
                ],
                groupingsToOptInTo: [
                    {
                        path: "path1:path4:grouping4",
                        name: "grouping4"
                    }
                ],
                groupingsToOptOutOf: [
                    {
                        path: "path1:path3:grouping2",
                        name: "grouping2"
                    }
                ]
            };

            scope.itemsPerPage = 2;

            httpBackend.whenGET(BASE_URL + "groupingAssignment")
                .respond(200, mockResponse);
        });

        it("should initialize the memberships list and opt in lists", function () {
            scope.init();
            httpBackend.expectGET(BASE_URL + "groupingAssignment").respond(200, mockResponse);
            httpBackend.flush();

            expect(scope.membershipsList.length).toEqual(3);
            expect(scope.pagedItemsMemberships.length).toEqual(2);
            expect(scope.membershipsList[0]).toEqual(mockResponse.groupingsIn[0]);
            expect(scope.membershipsList[1]).toEqual(mockResponse.groupingsIn[1]);
            expect(scope.membershipsList[2]).toEqual(mockResponse.groupingsIn[2]);

            expect(scope.optInList.length).toEqual(1);
            expect(scope.pagedItemsOptInList.length).toEqual(1);
            expect(scope.optInList[0]).toEqual(mockResponse.groupingsToOptInTo[0]);
        });

    });

});
