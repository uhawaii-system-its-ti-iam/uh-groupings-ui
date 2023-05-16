/* global inject */
describe("AppService", () => {

        beforeEach(module("UHGroupingsApp"));
        beforeEach(module("ngMockE2E"));

        let dp;
        beforeEach(inject((dataProvider, BASE_URL) => {
            dp = dataProvider;
        }));
        it("should define dataProvider", () => {
            expect(dp).toBeDefined();
        });

    }
);