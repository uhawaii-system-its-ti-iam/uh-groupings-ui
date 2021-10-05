describe("AdminController", function () {

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    let scope;
    let controller;
    let httpBackend;
    let BASE_URL;
    let window;

    beforeEach(inject(function ($rootScope, $controller, _BASE_URL_, _$httpBackend_) {
        scope = $rootScope.$new();
        controller = $controller("AdminJsController", {
            $scope: scope,
            $window: window
        });
        httpBackend = _$httpBackend_;
        BASE_URL = _BASE_URL_;
    }));

    it("should define the admin controller", function () {
        expect(controller).toBeDefined();
    });

    describe("displayAdmins", function () {
        it("should call resetGroupingInformation", function () {
            spyOn(scope, "resetGroupingInformation").and.callThrough();
            scope.displayAdmins();

            expect(scope.resetGroupingInformation).toHaveBeenCalled();
        });

        it("should repaginate the groupings list table (since the filter is reset)", function () {
            spyOn(scope, "groupToPages").and.callThrough();
            scope.displayAdmins();

            expect(scope.groupToPages).toHaveBeenCalledWith(scope.groupingsList);
        });

        describe("a user is currently looking at a selected grouping", function () {
            beforeEach(function () {
                scope.showGrouping = true;
            });

            it("should no longer show the selected grouping", function () {
                scope.displayAdmins();

                expect(scope.showGrouping).toBe(false);
            });
        });
    });

    describe("searchForUserGroupingInformation", function () {
        beforeEach(function () {
            scope.personToLookup = "test";
        });
        it("should search for person", function () {
            spyOn(scope, "searchForUserGroupingInformation").and.callThrough();
            scope.searchForUserGroupingInformation();
            expect(scope.searchForUserGroupingInformation).toHaveBeenCalled();
        });
    });

    describe("mergeManagePersonDuplicateValues", function () {
        beforeEach(function () {
            scope.personList = [
                {
                    name: "test1",
                    inInclude: 0,
                    inExclude: 0,
                    inBasis: 1,
                    inOwner: 0,
                    inBasisAndInclude: 0
                },
                {
                    name: "test1",
                    inInclude: 1,
                    inExclude: 0,
                    inBasis: 1,
                    inOwner: 0,
                    inBasisAndInclude: 1
                },
                {
                    name: "test2",
                    inInclude: 0,
                    inExclude: 1,
                    inBasis: 0,
                    inOwner: 0,
                    inBasisAndInclude: 0
                },
                {
                    name: "test3",
                    inInclude: 1,
                    inExclude: 0,
                    inBasis: 0,
                    inOwner: 0,
                    inBasisAndInclude: 0
                },
                {
                    name: "test3",
                    inInclude: 0,
                    inExclude: 0,
                    inBasis: 1,
                    inOwner: 0,
                    inBasisAndInclude: 1
                },
                {
                    name: "test3",
                    inInclude: 0,
                    inExclude: 0,
                    inBasis: 0,
                    inOwner: 1,
                    inBasisAndInclude: 0
                }
            ];
        });
        it("should merge the grouping values into each unique Grouping", function () {
            let mergeManagePersonDuplicateValues = jasmine.createSpy()
                .and.callFake(function (dups) {
                    let result = [];
                    dups.forEach((membership, index) => {
                        dups.forEach((m, index2) => {
                            if (membership.name === m.name && index !== index2) {
                                membership.inInclude |= m.inInclude;
                                membership.inExclude |= m.inExclude;
                                membership.inBasis |= m.inBasis;
                                membership.inOwner |= m.inOwner;
                                membership.inBasisAndInclude |= m.inBasisAndInclude;
                            }
                        });
                    });
                    dups.forEach((membership, index) => {
                        let index1 = dups.findIndex((e) => {
                            return e.name === membership.name;
                        });
                        if (index1 === index) {
                            // Push the merged result from the duplicates
                            result.push({
                                "name": membership.name,
                                "inInclude": membership.inInclude,
                                "inExclude": membership.inExclude,
                                "inBasis": membership.inBasis,
                                "inOwner": membership.inOwner,
                                "inBasisAndInclude": membership.inBasisAndInclude
                            });
                        }
                    });
                    return result;
                });
            let arr = [];
            arr = mergeManagePersonDuplicateValues(scope.personList);
            expect(mergeManagePersonDuplicateValues).toHaveBeenCalledWith(scope.personList);
            expect(arr).not.toEqual(scope.personList);
            expect(arr).toContain({
                name: "test1",
                inInclude: 1,
                inExclude: 0,
                inBasis: 1,
                inOwner: 0,
                inBasisAndInclude: 1
            });
            expect(arr).toContain({
                name: "test2",
                inInclude: 0,
                inExclude: 1,
                inBasis: 0,
                inOwner: 0,
                inBasisAndInclude: 0
            });
            expect(arr).toContain({
                name: "test3",
                inInclude: 1,
                inExclude: 0,
                inBasis: 1,
                inOwner: 1,
                inBasisAndInclude: 1
            });
        });
    });

});
