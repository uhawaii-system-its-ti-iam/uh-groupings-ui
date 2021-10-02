describe("GroupingsService", function () {

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    let gs;
    let dp;
    let groupingPath;
    let onSuccess;
    let onError;
    let debugPrint;

    beforeEach(inject(function (groupingsService, dataProvider, BASE_URL) {
        gs = groupingsService;
        debugPrint = true;
        dp = dataProvider;
        groupingPath = "grouping:path";
        if (debugPrint) {
            onSuccess = function (res) {
                console.log(res);
            };
            onError = onSuccess;
        }
    }));

    it("should define groupingsService", function () {
        expect(gs).toBeDefined();
    });

    describe("hello", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.hello();
            expect(dp.loadData).toHaveBeenCalled();
        });
    });

    describe("getGrouping", function () {
        let page;
        let size;
        let sortString;
        let isAscending;

        beforeEach(function () {
            page = 0;
            size = 1;
            sortString = "name";
            isAscending = true;
        });

        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getGrouping(groupingPath, page, size, sortString, isAscending, onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });
    });

    describe("updateDescription", function () {
        let description;
        beforeAll(function () {
            description = "description";
        });
        it("should call dataProvider.updateDataWithBody", function () {
            spyOn(dp, "updateDataWithBody");
            gs.updateDescription(description, groupingPath, onSuccess, onError);
            expect(dp.updateDataWithBody).toHaveBeenCalled();
        });
    });

    describe("getAdminLists", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getAdminLists(onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });
    });

    describe("addMembersToInclude", function () {
        let usersToAdd;

        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.addMembersToInclude(usersToAdd, groupingPath, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
    });

    describe("addMembersToIncludeAsync", function () {
        let usersToAdd;
        let modal;
        it("should call dataProvider.updateDataWithTimeoutModal", function () {
            spyOn(dp, "updateDataWithTimeoutModal");
            gs.addMembersToIncludeAsync(usersToAdd, groupingPath, onSuccess, onError, modal);
            expect(dp.updateDataWithTimeoutModal).toHaveBeenCalled();
        });
    });

    describe("addMembersToExclude", function () {
        let usersToAdd;
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.addMembersToExclude(usersToAdd, groupingPath, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
    });

    describe("addMembersToExcludeAsync", function () {
        let usersToAdd;
        let modal;
        it("should call dataProvider.updateDataWithTimeoutModal", function () {
            spyOn(dp, "updateDataWithTimeoutModal");
            gs.addMembersToExcludeAsync(usersToAdd, groupingPath, onSuccess, onError, modal);
            expect(dp.updateDataWithTimeoutModal).toHaveBeenCalled();
        });
    });

    describe("assignOwnership", function () {
        let newOwner;
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.assignOwnership(groupingPath, newOwner, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
    });

    describe("addAdmin", function () {
        let adminToAdd;
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.addAdmin(adminToAdd, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
    });

    describe("removeFromGroups", function () {
        let groups;
        let member;
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.removeFromGroups(groups, member, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
    });

    describe("removeMembersFromInclude", function () {
        let members;
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.removeMembersFromInclude(groupingPath, members, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
    });

    describe("removeMembersFromExclude", function () {
        let members;
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.removeMembersFromExclude(groupingPath, members, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
    });

    describe("removeOwner", function () {
        let ownerToRemove;
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.removeOwner(groupingPath, ownerToRemove, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
    });

    describe("removeAdmin", function () {
        let adminToRemove;
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.removeAdmin(adminToRemove, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
    });

    describe("getMemberAttributes", function () {
        let member;
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getMemberAttributes(member, onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });
    });

    describe("optOut", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.optOut(groupingPath, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
    });

    describe("optIn", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.optIn(groupingPath, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
    });

    describe("getMembershipResults", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getMembershipResults(onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });
    });

    describe("getMembershipAssignmentForUser", function () {
        let username;
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getMembershipAssignmentForUser(onSuccess, onError, username);
            expect(dp.loadData).toHaveBeenCalled();
        });
    });

    describe("getOptInGroups", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getOptInGroups(onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });
    });

    describe("setOptIn", function () {
        let optInOn;
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.setOptIn(groupingPath, optInOn, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
    });

    describe("setOptOut", function () {
        let optOutOn;
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.setOptOut(groupingPath, optOutOn, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
    });

    describe("resetGroup", function () {
        let include;
        let exclude;
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.resetGroup(groupingPath, include, exclude, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
    });

    describe("getSyncDestList", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getSyncDestList(groupingPath, onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });
    });

    describe("setSyncDest", function () {
        let syncDestId;
        let turnOn;
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.setSyncDest(groupingPath, syncDestId, turnOn, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
    });

    describe("getGroupingsOwned", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getGroupingsOwned(onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });
    });
});