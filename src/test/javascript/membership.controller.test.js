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

            scope.init();
        });

        it("should make an API call to groupingAssignment", function () {
            httpBackend.expectGET(BASE_URL + "groupingAssignment").respond(200, mockResponse);
            expect(httpBackend.flush).not.toThrow();
        });

        it("should initialize membershipsList", function () {
            httpBackend.expectGET(BASE_URL + "groupingAssignment").respond(200, mockResponse);
            httpBackend.flush();

            expect(scope.membershipsList.length).toEqual(3);
            expect(scope.pagedItemsMemberships.length).toEqual(2);
            expect(scope.membershipsList).toContain(mockResponse.groupingsIn[0]);
            expect(scope.membershipsList).toContain(mockResponse.groupingsIn[1]);
            expect(scope.membershipsList).toContain(mockResponse.groupingsIn[2]);
        });

        it("should initialize optInList", function () {
            httpBackend.expectGET(BASE_URL + "groupingAssignment").respond(200, mockResponse);
            httpBackend.flush();

            expect(scope.optInList.length).toEqual(1);
            expect(scope.pagedItemsOptInList.length).toEqual(1);
            expect(scope.optInList).toContain(mockResponse.groupingsToOptInTo[0]);
        });

        it("should initialize optOutList", function () {
            httpBackend.expectGET(BASE_URL + "groupingAssignment").respond(200, mockResponse);
            httpBackend.flush();

            expect(scope.optOutList.length).toEqual(1);
            expect(scope.optOutList).toContain(mockResponse.groupingsToOptOutOf[0]);
        });

    });

    describe("membershipRequired", function () {
        beforeEach(function () {
            scope.membershipsList = [
                {
                    path: "path:path2:path3:grouping1",
                    name: "grouping1"
                },
                {
                    path: "path:path2:path4:grouping2",
                    name: "grouping2"
                },
                {
                    path: "path:path2:path5:grouping3",
                    name: "grouping3"
                },
                {
                    path: "path:path2:path3:grouping4",
                    name: "grouping4"
                }
            ];
            scope.itemsPerPage = 2;
            scope.pagedItemsMemberships = scope.groupToPages(scope.membershipsList);

            scope.optOutList = [
                {
                    path: "path:path2:path3:grouping1",
                    name: "grouping1"
                },
                {
                    path: "path:path2:path3:grouping4",
                    name: "grouping4"
                }
            ];
        });

        it("should return false for groupings in both membershipsList and optOutList", function () {
            // path:path2:path3:grouping1
            expect(scope.membershipRequired(0, 0)).toBe(false);
            // path:path2:path3:grouping4
            expect(scope.membershipRequired(1, 1)).toBe(false);
        });

        it("should return true for groupings in membershipsList, but not in optOutList", function () {
            // path:path2:path4:grouping2
            expect(scope.membershipRequired(0, 1)).toBe(true);
            // path:path2:path5:grouping3
            expect(scope.membershipRequired(1, 0)).toBe(true);
        });
    });

});
