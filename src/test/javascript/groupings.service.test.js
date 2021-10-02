describe("GroupingsService", function () {

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    let gs;
    let dp;

    beforeEach(inject(function (groupingsService, dataProvider) {
        gs = groupingsService;
        dp = dataProvider;
        console.log(gs);
    }));

    it("should define groupingsService", function () {
        expect(gs).toBeDefined();
    });
    describe("updateDescription", function () {
        it("should call dataProvider.updateDataWithBody", function () {
            spyOn(dp, "updateDataWithBody");
            gs.updateDescription("data", "path", {}, {});
            expect(dp.updateDataWithBody).toHaveBeenCalled();
        });
    });
    describe("getAdminLists", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getAdminLists({}, {});
            expect(dp.loadData).toHaveBeenCalled();
        });
    });
    describe("addMembersToInclude", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.addMembersToInclude({}, {});
            expect(dp.updateData).toHaveBeenCalled();
        });
    });
    describe("addMembersToIncludeAsync", function () {
        it("should call dataProvider.updateDataWithTimeoutModal", function () {
            spyOn(dp, "updateDataWithTimeoutModal");
            gs.addMembersToIncludeAsync({}, {});
            expect(dp.updateDataWithTimeoutModal).toHaveBeenCalled();
        });
    });
    describe("addMembersToExclude", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.addMembersToExclude({}, {});
            expect(dp.updateData).toHaveBeenCalled();
        });
    });
    describe("addMembersToExcludeAsync", function () {
        it("should call dataProvider.updateDataWithTimeoutModal", function () {
            spyOn(dp, "updateDataWithTimeoutModal");
            gs.addMembersToExcludeAsync({}, {});
            expect(dp.updateDataWithTimeoutModal).toHaveBeenCalled();
        });
    });
    describe("assignOwnership", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.assignOwnership({}, {});
            expect(dp.updateData).toHaveBeenCalled();
        });
    });
    describe("addAdmin", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.addAdmin({}, {});
            expect(dp.updateData).toHaveBeenCalled();
        });
    });
    describe("removeFromGroups", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.removeFromGroups({}, {});
            expect(dp.updateData).toHaveBeenCalled();
        });
    });
    describe("removeMembersFromInclude", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.removeMembersFromInclude({}, {});
            expect(dp.updateData).toHaveBeenCalled();
        });
    });
    describe("removeMembersFromExclude", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.removeMembersFromExclude({}, {});
            expect(dp.updateData).toHaveBeenCalled();
        });
    });
    describe("removeOwner", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.removeOwner({}, {});
            expect(dp.updateData).toHaveBeenCalled();
        });
    });
    describe("removeAdmin", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.removeAdmin({}, {});
            expect(dp.updateData).toHaveBeenCalled();
        });
    });
    describe("getMemberAttributes", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getMemberAttributes({}, {});
            expect(dp.loadData).toHaveBeenCalled();
        });
    });
    describe("optOut", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.optOut({}, {});
            expect(dp.updateData).toHaveBeenCalled();
        });
    });
    describe("optIn", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.optIn({}, {});
            expect(dp.updateData).toHaveBeenCalled();
        });
    });
    describe("getMembershipResults", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getMembershipResults({}, {});
            expect(dp.loadData).toHaveBeenCalled();
        });
    });
    describe("getMembershipAssignmentForUser", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getMembershipAssignmentForUser({}, {});
            expect(dp.loadData).toHaveBeenCalled();
        });
    });
    describe("getOptInGroups", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getOptInGroups({}, {});
            expect(dp.loadData).toHaveBeenCalled();
        });
    });
    describe("setOptIn", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.setOptIn({}, {});
            expect(dp.updateData).toHaveBeenCalled();
        });
    });
    describe("setOptOut", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.setOptOut({}, {});
            expect(dp.updateData).toHaveBeenCalled();
        });
    });
    describe("resetGroup", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.resetGroup({}, {});
            expect(dp.updateData).toHaveBeenCalled();
        });
    });
    describe("getSyncDestList", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getSyncDestList({}, {});
            expect(dp.loadData).toHaveBeenCalled();
        });
    });
    describe("setSyncDest", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.setSyncDest({}, {});
            expect(dp.updateData).toHaveBeenCalled();
        });
    });
    describe("getGroupingsOwned", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getGroupingsOwned({}, {});
            expect(dp.loadData).toHaveBeenCalled();
        });
    });
});