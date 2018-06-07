describe("TableController", function () {

    beforeEach(module("UHGroupingsApp"));

    var scope;
    var controller;

    beforeEach(inject(function ($rootScope, $controller) {
        scope = $rootScope.$new();
        controller = $controller("TableJsController", {
            $scope: scope
        });
    }));

    describe("groupToPages", function () {

        it("should return three pages for a list of fifteen items, given five items per page", function () {
            var items = [];
            scope.itemsPerPage = 5;

            for (var i = 0; i < 15; i++) {
                items[i] = i;
            }

            var paginatedItems = scope.groupToPages(items);

            expect(paginatedItems.length).toEqual(3);

            expect(paginatedItems[0].length).toEqual(5);
            expect(paginatedItems[1].length).toEqual(5);
            expect(paginatedItems[2].length).toEqual(5);

            expect(paginatedItems[0][0]).toEqual(0);
            expect(paginatedItems[0][1]).toEqual(1);
            expect(paginatedItems[0][2]).toEqual(2);
            expect(paginatedItems[0][3]).toEqual(3);
            expect(paginatedItems[0][4]).toEqual(4);

            expect(paginatedItems[1][0]).toEqual(5);
            expect(paginatedItems[1][1]).toEqual(6);
            expect(paginatedItems[1][2]).toEqual(7);
            expect(paginatedItems[1][3]).toEqual(8);
            expect(paginatedItems[1][4]).toEqual(9);

            expect(paginatedItems[2][0]).toEqual(10);
            expect(paginatedItems[2][1]).toEqual(11);
            expect(paginatedItems[2][2]).toEqual(12);
            expect(paginatedItems[2][3]).toEqual(13);
            expect(paginatedItems[2][4]).toEqual(14);

        });

        it("should return zero pages if there are no items", function () {
            var items = [];
            scope.itemsPerPage = 5;

            var paginatedItems = scope.groupToPages(items);
            expect(paginatedItems.length).toEqual(0);
        });

        it("should return one page for a list of four items, given five items per page", function () {
            var items = [0, 1, 2, 3];
            scope.itemsPerPage = 5;

            var paginatedItems = scope.groupToPages(items);

            expect(paginatedItems.length).toEqual(1);

            expect(paginatedItems[0].length).toEqual(4);

            expect(paginatedItems[0][0]).toEqual(0);
            expect(paginatedItems[0][1]).toEqual(1);
            expect(paginatedItems[0][2]).toEqual(2);
            expect(paginatedItems[0][3]).toEqual(3);
        });

        it("should return two pages for a list of six items, given five items per page", function () {
            var items = [0, 1, 2, 3, 4, 5];
            scope.itemsPerPage = 5;

            var paginatedItems = scope.groupToPages(items);

            expect(paginatedItems.length).toEqual(2);

            expect(paginatedItems[0].length).toEqual(5);
            expect(paginatedItems[1].length).toEqual(1);

            expect(paginatedItems[0][0]).toEqual(0);
            expect(paginatedItems[0][1]).toEqual(1);
            expect(paginatedItems[0][2]).toEqual(2);
            expect(paginatedItems[0][3]).toEqual(3);
            expect(paginatedItems[0][4]).toEqual(4);

            expect(paginatedItems[1][0]).toEqual(5);
        });

        it("should return zero pages if a non-list is passed", function () {
            var item = "not an array";

            var paginatedItems = scope.groupToPages(item);

            expect(paginatedItems.length).toEqual(0);
        });

        it("should return zero pages if items per page is less than one", function () {
            var items = [0, 1, 2, 3, 4, 5];
            scope.itemsPerPage = 0;

            var paginatedItems = scope.groupToPages(items);

            expect(paginatedItems.length).toEqual(0);
        });

    });

    describe("filter", function () {

        beforeEach(function () {
            scope.items = [{
                "$$hashKey": "random",
                basis: "Include",
                firstName: "John",
                lastName: "Doe",
                username: "jdoe",
                uuid: "12345678"
            }, {
                "$$hashKey": "random",
                basis: "Exclude",
                firstName: "Jane",
                lastName: "Doe",
                username: "janed",
                uuid: "23456789"
            }];
            scope.itemsPerPage = 1;
            scope.pagedItems = scope.groupToPages(scope.items);
            scope.currentPage = 1;
        });

        it("should return entries where any property value matches the query", function () {
            scope.filter(scope.items, "pagedItems", "currentPage", "janed");

            expect(scope.pagedItems.length).toEqual(1);

            // Only the second item should be returned in the pagedItems
            expect(scope.pagedItems[0][0]).toEqual(scope.items[1]);
        });

        it("should ignore the $$hashKey column", function () {
            scope.filter(scope.items, "pagedItems", "currentPage", "random");

            expect(scope.pagedItems.length).toEqual(0);
        });

        it("should ignore the basis column", function () {
            scope.filter(scope.items, "pagedItems", "currentPage", "Exclude");
            expect(scope.pagedItems.length).toEqual(0);

            scope.filter(scope.items, "pagedItems", "currentPage", "Include");
            expect(scope.pagedItems.length).toEqual(0);
        });

        it("should call groupToPages so the table is repaginated", function () {
            spyOn(scope, "groupToPages").and.callThrough();

            scope.filter(scope.items, "pagedItems", "currentPage", "janed");

            expect(scope.groupToPages).toHaveBeenCalled();
        });

        it("should reset the page number to 0", function () {
            scope.filter(scope.items, "pagedItems", "currentPage", "janed");

            expect(scope.currentPage).toEqual(0);
        });

        it("should allow matching to be case insensitive", function () {
            scope.filter(scope.items, "pagedItems", "currentPage", "DOE");

            expect(scope.pagedItems.length).toEqual(2);

            expect(scope.pagedItems[0][0]).toEqual(scope.items[0]);
            expect(scope.pagedItems[1][0]).toEqual(scope.items[1]);
        });

    });

});
