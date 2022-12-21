/* global inject */

describe("GroupingsService", function () {

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    let gs;
    let dp;
    let groupingPath;
    let onSuccess;
    let onError;
    let httpBackend;
    let BASE_URL;

    beforeEach(inject(function (groupingsService, dataProvider, _BASE_URL_, _$httpBackend_) {
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
        it("should call encodeParameterizedQueryString()", function () {
            spyOn(gs, "encodeParameterizedQueryString");
            gs.getGrouping(groupingPath, page, size, sortString, isAscending, onSuccess, onError);
            expect(gs.encodeParameterizedQueryString).toHaveBeenCalled();
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

        it("should use the correct path", function () {
            gs.updateDescription(description, groupingPath, onSuccess, onError);
            httpBackend.expectPUT(BASE_URL + "groupings/" + groupingPath + "/description").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getAdminLists", function () {

        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getAdminLists(onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });

        it("should use the correct path", function () {
            gs.getAdminLists(onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "adminLists").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("addMembersToInclude", function () {
        let usersToAdd;

        it("should call dataProvider.updateDataWithBody", function () {
            spyOn(dp, "updateDataWithBody");
            gs.addMembersToInclude(usersToAdd, groupingPath, onSuccess, onError);
            expect(dp.updateDataWithBody).toHaveBeenCalled();
        });

        it("should use the correct path", function () {
            gs.addMembersToInclude(usersToAdd, groupingPath, onSuccess, onError);
            httpBackend.expectPUT(BASE_URL + groupingPath + "/addMembersToIncludeGroup").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("addMembersToIncludeAsync", function () {
        let usersToAdd;
        let modal;
        it("should call dataProvider.updateDataWithBodyAndTimeoutModal", function () {
            spyOn(dp, "updateDataWithBodyAndTimeoutModal");
            gs.addMembersToIncludeAsync(usersToAdd, groupingPath, onSuccess, onError, modal);
            expect(dp.updateDataWithBodyAndTimeoutModal).toHaveBeenCalled();
        });

        it("should use the correct path", function () {
            gs.addMembersToInclude(usersToAdd, groupingPath, onSuccess, onError);
            httpBackend.expectPUT(BASE_URL + groupingPath + "/addMembersToIncludeGroup").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("addMembersToExclude", function () {
        let usersToAdd;
        it("should call dataProvider.updateDataWithBody", function () {
            spyOn(dp, "updateDataWithBody");
            gs.addMembersToExclude(usersToAdd, groupingPath, onSuccess, onError);
            expect(dp.updateDataWithBody).toHaveBeenCalled();
        });
        it("should use the correct path", function () {
            gs.addMembersToExclude(usersToAdd, groupingPath, onSuccess, onError);
            httpBackend.expectPUT(BASE_URL + groupingPath + "/addMembersToExcludeGroup").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("addMembersToExcludeAsync", function () {
        let usersToAdd;
        let modal;
        it("should call dataProvider.updateDataWithBodyAndTimeoutModal", function () {
            spyOn(dp, "updateDataWithBodyAndTimeoutModal");
            gs.addMembersToExcludeAsync(usersToAdd, groupingPath, onSuccess, onError, modal);
            expect(dp.updateDataWithBodyAndTimeoutModal).toHaveBeenCalled();
        });
        it("should use the correct path", function () {
            gs.addMembersToExclude(usersToAdd, groupingPath, onSuccess, onError);
            httpBackend.expectPUT(BASE_URL + groupingPath + "/addMembersToExcludeGroup").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("assignOwnership", function () {
        let newOwner;
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.addOwnerships(groupingPath, newOwner, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
        it("should use the correct path", function () {
            gs.addOwnerships(groupingPath, newOwner, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + groupingPath + "/" + newOwner + "/addOwnerships").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("addAdmin", function () {
        let adminToAdd;
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.addAdmin(adminToAdd, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
        it("should use the correct path", function () {
            gs.addAdmin(adminToAdd, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + adminToAdd + "/addAdmin").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("removeFromGroups", function () {
        let groups;
        let member = "";
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.removeFromGroups(groups, member, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
        it("should use the correct path", function () {
            gs.removeFromGroups(groups, member, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + groups + "/" + member + "/removeFromGroups").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("removeMembersFromInclude", function () {
        let members;
        it("should call dataProvider.updateDataWithBody", function () {
            spyOn(dp, "updateDataWithBody");
            gs.removeMembersFromInclude(groupingPath, members, onSuccess, onError);
            expect(dp.updateDataWithBody).toHaveBeenCalled();
        });
        it("should use the correct path", function () {
            gs.removeMembersFromInclude(groupingPath, members, onSuccess, onError);
            httpBackend.expectPUT(BASE_URL + groupingPath + "/removeMembersFromIncludeGroup").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("removeMembersFromExclude", function () {
        let members;
        it("should call dataProvider.updateDataWithBody", function () {
            spyOn(dp, "updateDataWithBody");
            gs.removeMembersFromExclude(groupingPath, members, onSuccess, onError);
            expect(dp.updateDataWithBody).toHaveBeenCalled();
        });
        it("should use the correct path", function () {
            gs.removeMembersFromExclude(groupingPath, members, onSuccess, onError);
            httpBackend.expectPUT(BASE_URL + groupingPath + "/removeMembersFromExcludeGroup").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("removeOwner", function () {
        let ownerToRemove;
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.removeOwnerships(groupingPath, ownerToRemove, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
        it("should use the correct path", function () {
            gs.removeOwnerships(groupingPath, ownerToRemove, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + groupingPath + "/" + ownerToRemove + "/removeOwnerships").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("removeAdmin", function () {
        let adminToRemove;
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.removeAdmin(adminToRemove, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
        it("should use the correct path", function () {
            gs.removeAdmin(adminToRemove, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + adminToRemove + "/removeAdmin").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getMemberAttributes", function () {
        let member = "";
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getMemberAttributes(member, onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });
        it("should use the correct path", function () {
            gs.getMemberAttributes(member, onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "members/" + member).respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("optOut", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.optOut(groupingPath, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
        it("should use the correct path", function () {
            gs.optOut(groupingPath, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + groupingPath + "/optOut").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("optIn", function () {
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.optIn(groupingPath, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });

        it("should use the correct path", function () {
            gs.optIn(groupingPath, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + groupingPath + "/optIn").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getMembershipResults", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getMembershipResults(onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });

        it("should use the correct path", function () {
            gs.getMembershipResults(onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "members/groupings/").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("managePersonResults", function () {
        let member = "";
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.managePersonResults(member, onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });

        it("should use the correct path", function () {
            gs.managePersonResults(member, onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "members/" + member + "/groupings/all").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getNumberOfMemberships", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getNumberOfMemberships(onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });

        it("should use the correct path", function () {
            gs.getNumberOfMemberships(onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "members/memberships/").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });

    });
    describe("getMembershipAssignmentForUser", function () {
        let username;
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getMembershipAssignmentForUser(onSuccess, onError, username);
            expect(dp.loadData).toHaveBeenCalled();
        });

        it("should use the correct path", function () {
            gs.getMembershipAssignmentForUser(onSuccess, onError, username);
            httpBackend.expectGET(BASE_URL + "members/" + username + "/groupings/").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getOptInGroups", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getOptInGroups(onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });

        it("should use the correct path", function () {
            gs.getOptInGroups(onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "groupings/optInGroups/").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("setOptIn", function () {
        let optInOn;
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.setOptIn(groupingPath, optInOn, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
        it("should use the correct path", function () {
            gs.setOptIn(groupingPath, optInOn, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + groupingPath + "/" + optInOn + "/setOptIn").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("setOptOut", function () {
        let optOutOn;
        it("should call dataProvider.updateData", function () {
            spyOn(dp, "updateData");
            gs.setOptOut(groupingPath, optOutOn, onSuccess, onError);
            expect(dp.updateData).toHaveBeenCalled();
        });
        it("should use the correct path", function () {
            gs.setOptOut(groupingPath, optOutOn, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + groupingPath + "/" + optOutOn + "/setOptOut").respond(200);
            expect(httpBackend.flush).not.toThrow();
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

        it("should use the correct path", function () {
            gs.resetGroup(groupingPath, include, exclude, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + groupingPath + "/" + include + "/" + exclude + "/resetGroup").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getSyncDestList", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getSyncDestList(groupingPath, onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });

        it("should use the correct path", function () {
            gs.getSyncDestList(groupingPath, onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "groupings/" + groupingPath + "/sync-destinations").respond(200);
            expect(httpBackend.flush).not.toThrow();
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

        it("should use the correct path for enable", function () {
            gs.setSyncDest(groupingPath, syncDestId, true, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + "groupings/" + groupingPath + "/syncDests/" + syncDestId + "/enable").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });

        it("should use the correct path for disable", function () {
            gs.setSyncDest(groupingPath, syncDestId, false, onSuccess, onError);
            httpBackend.expectPOST(BASE_URL + "groupings/" + groupingPath + "/syncDests/" + syncDestId + "/disable").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getGroupingsOwned", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getGroupingsOwned(onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });
        it("should use the correct path", function () {
            gs.getGroupingsOwned(onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "owners/groupings").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });

    describe("getNumberOfGroupings", function () {
        it("should call dataProvider.loadData", function () {
            spyOn(dp, "loadData");
            gs.getNumberOfGroupings(onSuccess, onError);
            expect(dp.loadData).toHaveBeenCalled();
        });
        it("should use the correct path", function () {
            gs.getNumberOfGroupings(onSuccess, onError);
            httpBackend.expectGET(BASE_URL + "owners/grouping/").respond(200);
            expect(httpBackend.flush).not.toThrow();
        });
    });
    describe("encodeParameterizedQueryString", function () {
        it("should encode a parameterized query string", function () {
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
});
