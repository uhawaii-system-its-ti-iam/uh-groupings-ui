/* global inject */

describe("GroupingsService", () => {

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    let gs;
    let dp;
    let groupingPath;
    let onSuccess;
    let onError;
    let httpBackend;
    let BASE_URL;

    beforeEach(inject((groupingsService, dataProvider, _BASE_URL_, _$httpBackend_) => {
        gs = groupingsService;
        dp = dataProvider;
        BASE_URL = _BASE_URL_;
        httpBackend = _$httpBackend_;
        groupingPath = "grouping:path";
        onSuccess = () => {
        };
        onError = () => {
        };
    }));

    it("should define groupingsService", () => {
        expect(gs).toBeDefined();
    });

    describe("hello", () => {
        it("should call dataProvider.loadData", () => {
            spyOn(dp, "loadData");
            gs.hello();
            expect(dp.loadData).toHaveBeenCalled();
        });
    });

    describe("getGrouping", () => {
        let page;
        let size;
        let sortString;
        let isAscending;

        beforeEach(() => {
            page = 0;
            size = 1;
            sortString = "name";
            isAscending = true;
        });

        it("should call dataProvider.loadDataWithBodyRetry", () => {
            spyOn(dp, "loadDataWithBodyRetry");
            gs.getGrouping(groupingPath, page, size, sortString, isAscending, onSuccess, onError);
            expect(dp.loadDataWithBodyRetry).toHaveBeenCalled();
        });

        it("should call encodeParameterizedQueryString()", () => {
            spyOn(gs, "encodeParameterizedQueryString");
            gs.getGrouping(groupingPath, page, size, sortString, isAscending, onSuccess, onError);
            expect(gs.encodeParameterizedQueryString).toHaveBeenCalled();
        });
    });

    describe("getGroupingDescription", () => {
        it("should call dataProvider.loadData", () => {
            spyOn(dp, "loadData");
            gs.getGroupingDescription(groupingPath, onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });

        it("should use the correct path", () => {
            gs.getGroupingDescription(groupingPath, onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "groupings/" + groupingPath+ "/description").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getGroupingSyncDest", () => {
        it("should call dataProvider.loadData", () => {
            spyOn(dp, "loadData");
            gs.getGroupingSyncDest(groupingPath, onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });

        it("should use the correct path", () => {
            gs.getGroupingSyncDest(groupingPath, onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "groupings/" + groupingPath+ "/groupings-sync-destinations").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getGroupingOptAttributes", () => {
        it("should call dataProvider.loadData", () => {
            spyOn(dp, "loadData");
            gs.getGroupingOptAttributes(groupingPath, onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });

        it("should use the correct path", () => {
            gs.getGroupingOptAttributes(groupingPath, onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "groupings/" + groupingPath+ "/opt-attributes").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("updateDescription", () => {
        let description;
        beforeAll(() => {
            description = "description";
        });

        it("should call dataProvider.updateDataWithBody", () => {
            spyOn(dp, "updateDataWithBody");
            gs.updateDescription(description, groupingPath, onSuccess, onError);
            expect(dp.updateDataWithBody).toHaveBeenCalled();
        });

        it("should use the correct path", () => {
            gs.updateDescription(description, groupingPath, onSuccess, onError);
            httpBackend.expectPUT(BASE_URL + "groupings/" + groupingPath + "/description").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getAdminsGroupings", () => {

        it("should call dataProvider.loadData", () => {
            spyOn(dp, "loadData");
            gs.getAdminsGroupings(onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });

        it("should use the correct path", () => {
            gs.getAdminsGroupings(onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "adminsGroupings").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("addIncludeMembers", () => {
        let usersToAdd = [];
        let modal;

        it("should call dataProvider.updateDataWithBodyAndTimeoutModal", () => {
            spyOn(dp, "updateDataWithBodyAndTimeoutModal");
            gs.addIncludeMembers(usersToAdd, groupingPath, onSuccess, onError);
            expect(dp.updateDataWithBodyAndTimeoutModal).toHaveBeenCalled();
        });

        it("should use the correct path", () => {
            gs.addIncludeMembers(usersToAdd, groupingPath, onSuccess, onError, modal);
            httpBackend.expectPUT(BASE_URL + groupingPath + "/addIncludeMembers").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("addIncludeMembersAsync", () => {
        let usersToAdd = [];
        let modal;
        it("should call dataProvider.updateDataWithBodyAndTimeoutModalAsync", () => {
            spyOn(dp, "updateDataWithBodyAndTimeoutModalAsync");
            gs.addIncludeMembersAsync(usersToAdd, groupingPath, onSuccess, onError, modal);
            expect(dp.updateDataWithBodyAndTimeoutModalAsync).toHaveBeenCalled();
        });

        it("should use the correct path", () => {
            gs.addIncludeMembersAsync(usersToAdd, groupingPath, onSuccess, onError, modal);
            httpBackend.expectPUT(BASE_URL + groupingPath + "/addIncludeMembersAsync").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("addExcludeMembers", () => {
        let usersToAdd;
        let modal;

        it("should call dataProvider.updateDataWithBodyAndTimeoutModal", () => {
            spyOn(dp, "updateDataWithBodyAndTimeoutModal");
            gs.addExcludeMembers(usersToAdd, groupingPath, onSuccess, onError, modal);
            expect(dp.updateDataWithBodyAndTimeoutModal).toHaveBeenCalled();
        });
        it("should use the correct path", () => {
            gs.addExcludeMembers(usersToAdd, groupingPath, onSuccess, onError, modal);
            httpBackend.expectPUT(BASE_URL + groupingPath + "/addExcludeMembers").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("addExcludeMembersAsync", () => {
        let usersToAdd = [];
        let modal;
        it("should call dataProvider.updateDataWithBodyAndTimeoutModalAsync", () => {
            spyOn(dp, "updateDataWithBodyAndTimeoutModalAsync");
            gs.addExcludeMembersAsync(usersToAdd, groupingPath, onSuccess, onError, modal);
            expect(dp.updateDataWithBodyAndTimeoutModalAsync).toHaveBeenCalled();
        });
        it("should use the correct path", () => {
            gs.addExcludeMembersAsync(usersToAdd, groupingPath, onSuccess, onError, modal);
            httpBackend.expectPUT(BASE_URL + groupingPath + "/addExcludeMembersAsync").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("assignOwnership", () => {
        let newOwner;
        it("should call dataProvider.updateData", () => {
            spyOn(dp, "updateData");
            gs.addOwnerships(groupingPath, newOwner, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
        it("should use the correct path", () => {
            gs.addOwnerships(groupingPath, newOwner, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + groupingPath + "/" + newOwner + "/addOwnerships").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("addAdmin", () => {
        let adminToAdd;
        it("should call dataProvider.updateData", () => {
            spyOn(dp, "updateData");
            gs.addAdmin(adminToAdd, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
        it("should use the correct path", () => {
            gs.addAdmin(adminToAdd, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + adminToAdd + "/addAdmin").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("removeFromGroups", () => {
        let groups;
        let member = "";
        it("should call dataProvider.updateData", () => {
            spyOn(dp, "updateData");
            gs.removeFromGroups(groups, member, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
        it("should use the correct path", () => {
            gs.removeFromGroups(groups, member, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + groups + "/" + member + "/removeFromGroups").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("removeIncludeMembers", () => {
        let members;
        it("should call dataProvider.updateDataWithBody", () => {
            spyOn(dp, "updateDataWithBody");
            gs.removeIncludeMembers(groupingPath, members, onSuccess, onError);
            expect(dp.updateDataWithBody).toHaveBeenCalled();
        });
        it("should use the correct path", () => {
            gs.removeIncludeMembers(groupingPath, members, onSuccess, onError);
            httpBackend.expectPUT(BASE_URL + groupingPath + "/removeIncludeMembers").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("removeExcludeMembers", () => {
        let members;
        it("should call dataProvider.updateDataWithBody", () => {
            spyOn(dp, "updateDataWithBody");
            gs.removeExcludeMembers(groupingPath, members, onSuccess, onError);
            expect(dp.updateDataWithBody).toHaveBeenCalled();
        });
        it("should use the correct path", () => {
            gs.removeExcludeMembers(groupingPath, members, onSuccess, onError);
            httpBackend.expectPUT(BASE_URL + groupingPath + "/removeExcludeMembers").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("removeOwner", () => {
        let ownerToRemove;
        it("should call dataProvider.updateData", () => {
            spyOn(dp, "updateData");
            gs.removeOwnerships(groupingPath, ownerToRemove, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
        it("should use the correct path", () => {
            gs.removeOwnerships(groupingPath, ownerToRemove, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + groupingPath + "/" + ownerToRemove + "/removeOwnerships").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("removeAdmin", () => {
        let adminToRemove;
        it("should call dataProvider.updateData", () => {
            spyOn(dp, "updateData");
            gs.removeAdmin(adminToRemove, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
        it("should use the correct path", () => {
            gs.removeAdmin(adminToRemove, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + adminToRemove + "/removeAdmin").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("invalidUhIdentifiers", () => {
        let uhIdentifiers = "";
        it("should call dataProvider.loadDataWithBody", () => {
            spyOn(dp, "loadDataWithBody");
            gs.invalidUhIdentifiers(uhIdentifiers, onSuccess, onError);
            expect(dp.loadDataWithBody).toHaveBeenCalled();
        });
        it("should use the correct path", () => {
            gs.invalidUhIdentifiers(uhIdentifiers, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + "members/invalid").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("invalidUhIdentifiersAsync", () => {
        let uhIdentifiers = "";
        it("should call dataProvider.loadDataWithBodyAsync", () => {
            spyOn(dp, "loadDataWithBodyAsync");
            gs.invalidUhIdentifiersAsync(uhIdentifiers, onSuccess, onError);
            expect(dp.loadDataWithBodyAsync).toHaveBeenCalled();
        });
        it("should use the correct path", () => {
            gs.invalidUhIdentifiersAsync(uhIdentifiers, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + "members/invalidAsync").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getMemberAttributes", () => {
        let member = "";
        it("should call dataProvider.loadData", () => {
            spyOn(dp, "loadData");
            gs.getMemberAttributes(member, onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });
        it("should use the correct path", () => {
            gs.getMemberAttributes(member, onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "members/" + member).respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getMembersAttributes", () => {
        let member = "";
        it("should call dataProvider.loadDataWithBody", () => {
            spyOn(dp, "loadDataWithBody");
            gs.getMembersAttributes(member, onSuccess, onError);
            expect(dp.loadDataWithBody).toHaveBeenCalled();
        });
        it("should use the correct path", () => {
            gs.getMembersAttributes(member, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + "members").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("optOut", () => {
        it("should call dataProvider.updateData", () => {
            spyOn(dp, "updateData");
            gs.optOut(groupingPath, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
        it("should use the correct path", () => {
            gs.optOut(groupingPath, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + groupingPath + "/optOut").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("optIn", () => {
        it("should call dataProvider.updateData", () => {
            spyOn(dp, "updateData");
            gs.optIn(groupingPath, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });

        it("should use the correct path", () => {
            gs.optIn(groupingPath, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + groupingPath + "/optIn").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getMembershipResults", () => {
        it("should call dataProvider.loadData", () => {
            spyOn(dp, "loadData");
            gs.getMembershipResults(onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });

        it("should use the correct path", () => {
            gs.getMembershipResults(onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "members/memberships/").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("managePersonResults", () => {
        let member = "";
        it("should call dataProvider.loadData", () => {
            spyOn(dp, "loadData");
            gs.managePersonResults(member, onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });

        it("should use the correct path", () => {
            gs.managePersonResults(member, onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "members/" + member + "/groupings/").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getNumberOfMemberships", () => {
        it("should call dataProvider.loadData", () => {
            spyOn(dp, "loadData");
            gs.getNumberOfMemberships(onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });

        it("should use the correct path", () => {
            gs.getNumberOfMemberships(onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "members/memberships/count").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });

    });
    describe("getMembershipAssignmentForUser", () => {
        let username;
        it("should call dataProvider.loadData", () => {
            spyOn(dp, "loadData");
            gs.getMembershipAssignmentForUser(onSuccess, onError, username);
            expect(dp.loadData).toHaveBeenCalled();
        });

        it("should use the correct path", () => {
            gs.getMembershipAssignmentForUser(onSuccess, onError, username);
            httpBackend.expectGET(BASE_URL + "members/" + username + "/groupings/").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getOptInGroups", () => {
        it("should call dataProvider.loadData", () => {
            spyOn(dp, "loadData");
            gs.getOptInGroups(onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });

        it("should use the correct path", () => {
            gs.getOptInGroups(onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "groupings/optInGroups/").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("setOptIn", () => {
        let optInOn;
        it("should call dataProvider.updateData", () => {
            spyOn(dp, "updateData");
            gs.setOptIn(groupingPath, optInOn, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
        it("should use the correct path", () => {
            gs.setOptIn(groupingPath, optInOn, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + groupingPath + "/" + optInOn + "/setOptIn").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("setOptOut", () => {
        let optOutOn;
        it("should call dataProvider.updateData", () => {
            spyOn(dp, "updateData");
            gs.setOptOut(groupingPath, optOutOn, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
        it("should use the correct path", () => {
            gs.setOptOut(groupingPath, optOutOn, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + groupingPath + "/" + optOutOn + "/setOptOut").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("resetGroup", () => {
        let include;
        let exclude;
        it("should call dataProvider.updateData", () => {
            spyOn(dp, "updateData");
            gs.resetGroup(groupingPath, include, exclude, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });

        it("should use the correct path", () => {
            gs.resetGroup(groupingPath, include, exclude, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + groupingPath + "/" + include + "/" + exclude + "/resetGroup").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("resetIncludeGroupAsync", () => {
        it("should call dataProvider.updateDataAsync", () => {
            spyOn(dp, "updateDataAsync");
            gs.resetIncludeGroupAsync(groupingPath, onSuccess, onError);
            expect(dp.updateDataAsync).toHaveBeenCalled();
        });

        it("should use the correct path", () => {
            gs.resetIncludeGroupAsync(groupingPath, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + groupingPath + "/resetIncludeGroupAsync").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("resetExcludeGroupAsync", () => {
        it("should call dataProvider.updateDataAsync", () => {
            spyOn(dp, "updateDataAsync");
            gs.resetExcludeGroupAsync(groupingPath, onSuccess, onError);
            expect(dp.updateDataAsync).toHaveBeenCalled();
        });

        it("should use the correct path", () => {
            gs.resetExcludeGroupAsync(groupingPath, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + groupingPath + "/resetExcludeGroupAsync").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getSyncDestList", () => {
        it("should call dataProvider.loadData", () => {
            spyOn(dp, "loadData");
            gs.getSyncDestList(groupingPath, onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });

        it("should use the correct path", () => {
            gs.getSyncDestList(groupingPath, onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "groupings/" + groupingPath + "/sync-destinations").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("setSyncDest", () => {
        let syncDestId;
        let turnOn;
        it("should call dataProvider.updateData", () => {
            spyOn(dp, "updateData");
            gs.setSyncDest(groupingPath, syncDestId, turnOn, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });

        it("should use the correct path for enable", () => {
            gs.setSyncDest(groupingPath, syncDestId, true, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + "groupings/" + groupingPath + "/syncDests/" + syncDestId + "/enable").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });

        it("should use the correct path for disable", () => {
            gs.setSyncDest(groupingPath, syncDestId, false, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + "groupings/" + groupingPath + "/syncDests/" + syncDestId + "/disable").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("groupingOwners", () => {
        it("should call dataProvider.loadData", () => {
            spyOn(dp, "loadData");
            gs.groupingOwners("groupingPath", onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });

        it("should use the correct path", () => {
            let groupingPath = "some-path";
            gs.groupingOwners(groupingPath, onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "grouping/some-path/owners").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getGroupingsOwned", () => {
        it("should call dataProvider.loadData", () => {
            spyOn(dp, "loadData");
            gs.getGroupingsOwned(onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });
        it("should use the correct path", () => {
            gs.getGroupingsOwned(onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "owners/groupings").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getNumberOfGroupings", () => {
        it("should call dataProvider.loadData", () => {
            spyOn(dp, "loadData");
            gs.getNumberOfGroupings(onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });
        it("should use the correct path", () => {
            gs.getNumberOfGroupings(onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "owners/groupings/count").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("encodeParameterizedQueryString", () => {
        it("should encode a parameterized query string", () => {
            let params = {
                "page": 1,
                "size": 2,
                "sortString": "name",
                "isAscending": true
            };
            let result = gs.encodeParameterizedQueryString(params);
            expect(result).toEqual("page=1&size=2&sortString=name&isAscending=true");
        });
    });

    describe("getNumberOfGroupings", () => {
        it("should call dataProvider.loadData", () => {
            spyOn(dp, "loadData");
            gs.throwException(onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });
    });
});
