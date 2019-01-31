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
                ]
            };

            scope.itemsPerPage = 2;

            httpBackend.whenGET(BASE_URL + "members/groupings")
                .respond(200, mockResponse);

            scope.init();
        });

        it("should make an API call to groupingAssignment", function () {
            httpBackend.expectGET(BASE_URL + "members/groupings").respond(200, mockResponse);
            expect(httpBackend.flush).not.toThrow();
        });

        it("should initialize membershipsList", function () {
            httpBackend.expectGET(BASE_URL + "members/groupings").respond(200, mockResponse);
            httpBackend.flush();

            expect(scope.membershipsList.length).toEqual(3);
            expect(scope.pagedItemsMemberships.length).toEqual(2);
            expect(scope.membershipsList).toContain(mockResponse.groupingsIn[0]);
            expect(scope.membershipsList).toContain(mockResponse.groupingsIn[1]);
            expect(scope.membershipsList).toContain(mockResponse.groupingsIn[2]);
        });

        it("should initialize optInList", function () {
            httpBackend.expectGET(BASE_URL + "members/groupings").respond(200, mockResponse);
            httpBackend.flush();

            expect(scope.optInList.length).toEqual(1);
            expect(scope.pagedItemsOptInList.length).toEqual(1);
            expect(scope.optInList).toContain(mockResponse.groupingsToOptInTo[0]);
        });

    });

    // Set up mock data for optIn, optOut, and membershipRequired
    beforeEach(function () {
        scope.itemsPerPage = 2;

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
        scope.pagedItemsMemberships = scope.groupToPages(scope.membershipsList);

        scope.optInList = [
            {
                path: "path1:path4:grouping5",
                name: "grouping5"
            }
        ];
        scope.pagedItemsOptInList = scope.groupToPages(scope.optInList);

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

    describe("optOut", function () {
        var mockResponse;

        beforeEach(function () {
            mockResponse = [{
                action: "delete user from path:path2:path3:grouping4:include",
                resultCode: "SUCCESS"
            }];

            httpBackend.whenPOST(BASE_URL + "path:path2:path3:grouping4/optOut")
                .respond(200, mockResponse);

            httpBackend.whenGET(BASE_URL + "members/groupings")
                .respond(200);
        });

        it("should call init() on success", function () {
            spyOn(scope, "init").and.callThrough();

            // path:path2:path3:grouping4
            scope.optOut(1, 1);
            httpBackend.expectPOST(BASE_URL + "path:path2:path3:grouping4/optOut").respond(200, mockResponse);
            httpBackend.flush();

            expect(scope.init).toHaveBeenCalled();
            httpBackend.expectGET(BASE_URL + "members/groupings").respond(200);
        });

    });

    describe("optIn", function () {
        var mockResponse;

        beforeEach(function () {
            mockResponse = [{
                action: "add users to path1:path4:grouping5:include",
                resultCode: "SUCCESS"
            }];

            httpBackend.whenPOST(BASE_URL + "path1:path4:grouping5/optIn")
                .respond(200, mockResponse);

            httpBackend.whenGET(BASE_URL + "members/groupings")
                .respond(200);
        });

        it("should call init() on success", function () {
            spyOn(scope, "init").and.callThrough();

            // path1:path4:grouping4
            scope.optIn(0, 0);
            httpBackend.expectPOST(BASE_URL + "path1:path4:grouping5/optIn").respond(200, mockResponse);
            httpBackend.flush();

            expect(scope.init).toHaveBeenCalled();
            httpBackend.expectGET(BASE_URL + "members/groupings").respond(200);
        });

    });

});
