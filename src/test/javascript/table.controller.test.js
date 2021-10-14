describe("TableController", function () {

    beforeEach(module("UHGroupingsApp"));

    let scope;
    let controller;

    beforeEach(inject(function ($rootScope, $controller) {
        scope = $rootScope.$new();
        controller = $controller("TableJsController", {
            $scope: scope
        });
    }));

    it("should define the table controller", function () {
        expect(controller).toBeDefined();
    });

    describe("groupToPages", function () {

        it("should return three pages for a list of fifteen items, given five items per page", function () {
            const items = _.range(15);
            scope.itemsPerPage = 5;

            const paginatedItems = scope.groupToPages(items);

            expect(paginatedItems.length).toEqual(3);

            expect(paginatedItems[0].length).toEqual(5);
            expect(paginatedItems[1].length).toEqual(5);
            expect(paginatedItems[2].length).toEqual(5);

            expect(paginatedItems[0]).toEqual([0, 1, 2, 3, 4]);
            expect(paginatedItems[1]).toEqual([5, 6, 7, 8, 9]);
            expect(paginatedItems[2]).toEqual([10, 11, 12, 13, 14]);

        });

        it("should return zero pages if there are no items", function () {
            const items = [];
            scope.itemsPerPage = 5;

            const paginatedItems = scope.groupToPages(items);
            expect(paginatedItems.length).toEqual(0);
        });

        it("should return one page for a list of four items, given five items per page", function () {
            const items = [0, 1, 2, 3];
            scope.itemsPerPage = 5;

            const paginatedItems = scope.groupToPages(items);

            expect(paginatedItems.length).toEqual(1);

            expect(paginatedItems[0].length).toEqual(4);

            expect(paginatedItems[0]).toEqual([0, 1, 2, 3]);
        });

        it("should return two pages for a list of six items, given five items per page", function () {
            const items = [0, 1, 2, 3, 4, 5];
            scope.itemsPerPage = 5;

            const paginatedItems = scope.groupToPages(items);

            expect(paginatedItems.length).toEqual(2);

            expect(paginatedItems[0].length).toEqual(5);
            expect(paginatedItems[1].length).toEqual(1);

            expect(paginatedItems[0]).toEqual([0, 1, 2, 3, 4]);
            expect(paginatedItems[1]).toEqual([5]);
        });

        it("should return zero pages if a non-list is passed", function () {
            const item = "not an array";

            const paginatedItems = scope.groupToPages(item);

            expect(paginatedItems.length).toEqual(0);
        });

        it("should return zero pages if items per page is less than one", function () {
            const items = [0, 1, 2, 3, 4, 5];
            scope.itemsPerPage = 0;

            const paginatedItems = scope.groupToPages(items);

            expect(paginatedItems.length).toEqual(0);
        });

    });

    describe("filter", function () {

        beforeEach(function () {
            scope.items = [
                {
                    $$hashKey: "random",
                    firstName: "John",
                    lastName: "Doe",
                    username: "jdoe",
                    uhUuid: "12345678"
                },
                {
                    $$hashKey: "random",
                    firstName: "Jane",
                    lastName: "Doe",
                    username: "janed",
                    uhUuid: "23456789"
                },
                {
                    $$hashKey: "something",
                    firstName: "Dennis",
                    lastName: "Jefferson",
                    username: "dj",
                    uhUuid: "65478912"
                },
                {
                    $$hashKey: "another",
                    firstName: "Dave",
                    lastName: "Williams",
                    username: "dave",
                    uhUuid: "72897556"
                }
            ];
            scope.itemsPerPage = 1;
            scope.pagedItems = scope.groupToPages(scope.items);
            scope.currentPage = 1;
        });

        it("should return entries where any property value matches the query", function () {
            scope.filter(scope.items, "pagedItems", "currentPage", "janed", true);

            expect(scope.pagedItems.length).toEqual(1);

            // Only the second item should be returned in the pagedItems
            expect(scope.pagedItems[0][0]).toEqual(scope.items[1]);
        });

        it("should ignore the $$hashKey column", function () {
            scope.filter(scope.items, "pagedItems", "currentPage", "random", true);

            expect(scope.pagedItems.length).toEqual(0);
        });

        it("should ignore the basis column", function () {
            scope.filter(scope.items, "pagedItems", "currentPage", "Exclude", true);
            expect(scope.pagedItems.length).toEqual(0);

            scope.filter(scope.items, "pagedItems", "currentPage", "Include", true);
            expect(scope.pagedItems.length).toEqual(0);
        });

        it("should call groupToPages so the table is repaginated", function () {
            spyOn(scope, "groupToPages").and.callThrough();

            scope.filter(scope.items, "pagedItems", "currentPage", "janed", true);

            expect(scope.groupToPages).toHaveBeenCalled();
        });

        it("should reset the page number to 0", function () {
            scope.filter(scope.items, "pagedItems", "currentPage", "janed", true);

            expect(scope.currentPage).toEqual(0);
        });

        it("should allow matching to be case insensitive", function () {
            scope.filter(scope.items, "pagedItems", "currentPage", "DOE", true);

            expect(scope.pagedItems.length).toEqual(2);

            expect(scope.pagedItems[0][0]).toEqual(scope.items[0]);
            expect(scope.pagedItems[1][0]).toEqual(scope.items[1]);
        });

        it("should be equivalent to groupToPages if query is empty", function () {
            scope.filter(scope.items, "pagedItems", "currentPage", "", true);
            var pagedItemsUsingGtp = scope.groupToPages(scope.items);

            expect(scope.pagedItems).toEqual(pagedItemsUsingGtp);
        });

    });

    describe("setPage", function () {
        beforeEach(function () {
            scope.items = _.range(30);
            scope.itemsPerPage = 5;
            scope.pagedItems = scope.groupToPages(scope.items);
            scope.currentPage = 3;
        });


        describe("First", function () {
            it("should go back to the first page", function () {
                scope.setPage("First", "currentPage", "pagedItems");

                expect(scope.currentPage).toEqual(0);
            });
        });

        describe("Prev", function () {
            it("should go back to the previous page", function () {
                scope.setPage("Prev", "currentPage", "pagedItems");

                expect(scope.currentPage).toEqual(2);
            });

            it("should not go before the first page", function () {
                scope.currentPage = 0;
                scope.setPage("Prev", "currentPage", "pagedItems");

                expect(scope.currentPage).toEqual(0);
            });
        });

        describe("Set", function () {

            it("should go to page 3 if page 3 is clicked", function () {
                scope.n = 2;

                scope.setPage("Set", "currentPage", "pagedItems");

                expect(scope.currentPage).toEqual(2);
            });

            it("should not go to a negative page number", function () {
                scope.n = -1;

                scope.setPage("Set", "currentPage", "pagedItems");

                expect(scope.currentPage).toEqual(3);
            });

            it("should not go past the last page", function () {
                scope.n = 6;

                scope.setPage("Set", "currentPage", "pagedItems");

                expect(scope.currentPage).toEqual(3);
            });
        });

        describe("Next", function () {
            it("should go to the next page", function () {
                scope.setPage("Next", "currentPage", "pagedItems");

                expect(scope.currentPage).toEqual(4);
            });

            it("should not go past the last page", function () {
                scope.currentPage = 5;
                scope.setPage("Next", "currentPage", "pagedItems");

                expect(scope.currentPage).toEqual(5);
            });
        });

        describe("Last", function () {
            it("should go to the last page", function () {
                scope.setPage("Last", "currentPage", "pagedItems");

                expect(scope.currentPage).toEqual(5);
            });
        });

    });

    describe("disableFirstAndPrev", function () {
        it("should return true if you are on the first page", function () {
            scope.currentPage = 0;

            expect(scope.disableFirstAndPrev(scope.currentPage)).toBe(true);
        });

    });

    describe("disableNextAndLast", function () {
        it("should return true if you are on the last page", function () {
            scope.items = _.range(30);
            scope.itemsPerPage = 5;
            scope.pagedItems = scope.groupToPages(scope.items);
            scope.currentPage = 5;

            expect(scope.disableNextAndLast(scope.pagedItems, scope.currentPage)).toBe(true);
        });

        it("should return true if there are no pages in the table", function () {
            scope.pagedItems = [];
            scope.currentPage = 0;

            expect(scope.disableNextAndLast(scope.pagedItems, scope.currentPage)).toBe(true);
        });

    });


    describe("sortBy", function () {
        beforeEach(function () {
            scope.items = [
                {
                    name: "a",
                    id: 0
                },
                {
                    name: "b",
                    id: 12
                },
                {
                    name: "c",
                    id: 4
                },
                {
                    name: "d",
                    id: 9
                },
                {
                    name: "e",
                    id: 13
                },
                {
                    name: "f",
                    id: 17
                }
            ];
            scope.itemsPerPage = 2;
            scope.pagedItems = scope.groupToPages(scope.items);
        });

        it("should call groupToPages so the table is repaginated", function () {
            spyOn(scope, "groupToPages").and.callThrough();
            scope.sortBy("items", "pagedItems", "name");

            expect(scope.groupToPages).toHaveBeenCalledWith(scope.items);
        });

        describe("on first load, no columns were clicked yet", function () {
            it("should sort the items in descending order by name if the name column is clicked", function () {
                scope.sortBy("items", "pagedItems", "name");

                expect(scope.items[0]).toEqual({ name: "f", id: 17 });
                expect(scope.items[1]).toEqual({ name: "e", id: 13 });
                expect(scope.items[2]).toEqual({ name: "d", id: 9 });
                expect(scope.items[3]).toEqual({ name: "c", id: 4 });
                expect(scope.items[4]).toEqual({ name: "b", id: 12 });
                expect(scope.items[5]).toEqual({ name: "a", id: 0 });
            });

            it("should sort the items in ascending order by id if the id column is clicked", function () {
                scope.sortBy("items", "pagedItems", "id");

                expect(scope.items[0]).toEqual({ name: "a", id: 0 });
                expect(scope.items[1]).toEqual({ name: "c", id: 4 });
                expect(scope.items[2]).toEqual({ name: "d", id: 9 });
                expect(scope.items[3]).toEqual({ name: "b", id: 12 });
                expect(scope.items[4]).toEqual({ name: "e", id: 13 });
                expect(scope.items[5]).toEqual({ name: "f", id: 17 });
            });
        });

        describe("after the name property has been clicked", function () {
            beforeEach(function () {
                scope.sortBy("items", "pagedItems", "name");
            });

            it("should sort items back in ascending order by name if the name clicked is clicked", function () {
                scope.sortBy("items", "pagedItems", "name");

                expect(scope.items[0]).toEqual({ name: "a", id: 0 });
                expect(scope.items[1]).toEqual({ name: "b", id: 12 });
                expect(scope.items[2]).toEqual({ name: "c", id: 4 });
                expect(scope.items[3]).toEqual({ name: "d", id: 9 });
                expect(scope.items[4]).toEqual({ name: "e", id: 13 });
                expect(scope.items[5]).toEqual({ name: "f", id: 17 });
            });
        });

    });

    describe("pageRange", function () {
        let gap;
        let totalPages;

        describe("given a table with 7 pages and a gap size of 2", function () {
            beforeEach(function () {
                gap = 2;
                totalPages = 7;
            });

            it("should return [0, 1, 2] if you are on the first page", function () {
                const range = scope.pageRange(0, totalPages, gap);

                expect(range).toEqual([0, 1, 2]);
            });

            it("should return [0, 1, 2, 3] if you are on page 2", function () {
                const range = scope.pageRange(1, totalPages, gap);

                expect(range).toEqual([0, 1, 2, 3]);
            });

            it("should return [2, 3, 4, 5, 6] if you are on page 5", function () {
                const range = scope.pageRange(4, totalPages, gap);

                expect(range).toEqual([2, 3, 4, 5, 6]);
            });

            it("should return [3, 4, 5, 6] if you are on page 6", function () {
                const range = scope.pageRange(5, totalPages, gap);

                expect(range).toEqual([3, 4, 5, 6]);
            });

            it("should return [4, 5, 6] if you are on the last page", function () {
                const range = scope.pageRange(6, totalPages, gap);

                expect(range).toEqual([4, 5, 6]);
            });
        });

    });

});
