/* global inject */
describe("AppService", function () {

        beforeEach(module("UHGroupingsApp"));
        beforeEach(module("ngMockE2E"));

        let dp;
        beforeEach(inject(function (dataProvider, BASE_URL) {
            dp = dataProvider;
        }));
        it("should define dataProvider", function () {
            expect(dp).toBeDefined();
        });

    }
);