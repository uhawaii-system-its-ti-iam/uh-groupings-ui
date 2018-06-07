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

        it("should return four pages for a list of twenty items, given five items per page", function () {
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
            expect(paginatedItems.length).toEqual(0)
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

        it("should return zero pages if items per page is less than one", function () {
            var items = [0, 1, 2, 3, 4, 5];
            scope.itemsPerPage = 0;

            var paginatedItems = scope.groupToPages(items);

            expect(paginatedItems.length).toEqual(0);
        });

    });

});
