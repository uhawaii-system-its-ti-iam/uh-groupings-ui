/* global _, inject */

describe("GeneralController", () => {

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    let scope;
    let controller;
    let httpBackend;
    let BASE_URL;
    let gs;
    let uibModal;

    beforeEach(inject(($rootScope, $controller, _BASE_URL_, _$httpBackend_, groupingsService, $uibModal, $window) => {
        scope = $rootScope.$new();
        controller = $controller("GeneralJsController", {
            $scope: scope
        });
        httpBackend = _$httpBackend_;
        BASE_URL = _BASE_URL_;
        gs = groupingsService;
        uibModal = $uibModal;
        window = $window;
    }));

    it("should define the general controller", () => {
        expect(controller).toBeDefined();
    });

    it("should define the variables and methods in the table controller", () => {
        expect(scope.columnSort).toBeDefined();
        expect(scope.groupToPages).toBeDefined();
        expect(scope.filter).toBeDefined();
        expect(scope.pageRange).toBeDefined();
        expect(scope.setPage).toBeDefined();
        expect(scope.disableFirstAndPrev).toBeDefined();
        expect(scope.disableNextAndLast).toBeDefined();
        expect(scope.sortBy).toBeDefined();
    });

    describe("get current user and home page numbers: ", () => {
        const mockUser = {username: "iamtst01", uhUuid: "iamtst01"};
        const mockResponse = 999;
        beforeEach(() => {
            httpBackend.whenGET(BASE_URL + "currentUser")
                .respond(200, mockUser);

            httpBackend.whenGET(BASE_URL + "members/memberships/count")
                .respond(200, mockResponse);

            httpBackend.whenGET(BASE_URL + "owners/groupings/count")
                .respond(200, mockResponse);
        });

        it("should make an API call to getCurrentUser", () => {
            httpBackend.expectGET(BASE_URL + "currentUser").respond(200, mockUser);
            expect(httpBackend.flush).not.toThrow();
        });

        it("should initialize currentUser", () => {
            httpBackend.expectGET(BASE_URL + "currentUser").respond(200, mockUser);
            httpBackend.flush();

            expect(scope.currentUser).toEqual({uid: "iamtst01", uhUuid: "iamtst01"});
        });

        it("should make an API call to getNumberOfMemberships", () => {
            httpBackend.expectGET(BASE_URL + "members/memberships/count").respond(200, mockResponse);
            expect(httpBackend.flush).not.toThrow();
        });

        it("should initialize numberOfMemberships", () => {
            httpBackend.expectGET(BASE_URL + "members/memberships/count").respond(200, mockResponse);
            httpBackend.flush();

            expect(scope.numberOfMemberships).toEqual(999);
        });

        it("should make an API call to getNumberOfGroupings", () => {
            httpBackend.expectGET(BASE_URL + "owners/groupings/count").respond(200, mockResponse);
            expect(httpBackend.flush).not.toThrow();
        });

        it("should initialize numberOfGroupings", () => {
            httpBackend.expectGET(BASE_URL + "owners/groupings/count").respond(200, mockResponse);
            httpBackend.flush();

            expect(scope.numberOfGroupings).toEqual(999);
        });
    });

    // Set up mock data
    beforeEach(() => {
        scope.selectedGrouping = { name: "grouping1", path: "path:path2:grouping1" };
        scope.itemsPerPage = 20;

        scope.groupingBasis = [
            {
                name: "User One",
                username: "user1",
                uhUuid: "00000001",
                firstName: "User",
                lastName: "One"
            },
            {
                name: "User Four",
                username: "user4",
                uhUuid: "00000004",
                firstName: "User",
                lastName: "Four"
            },
            {
                name: "User Seven",
                username: "user7",
                uhUuid: "00000007",
                firstName: "User",
                lastName: "Seven"
            }
        ];
        scope.pagedItemsBasis = scope.groupToPages(scope.groupingBasis);

        scope.groupingInclude = [
            {
                name: "User One",
                username: "user1",
                uhUuid: "00000001",
                firstName: "User",
                lastName: "One"
            },
            {
                name: "User Two",
                username: "user2",
                uhUuid: "00000002",
                firstName: "User",
                lastName: "Two"
            },
            {
                name: "User Three",
                username: "user3",
                uhUuid: "00000003",
                firstName: "User",
                lastName: "Three"
            }
        ];
        scope.pagedItemsInclude = scope.groupToPages(scope.groupingInclude);

        scope.groupingExclude = [
            {
                name: "User Four",
                username: "user4",
                uhUuid: "00000004",
                firstName: "User",
                lastName: "Four"
            },
            {
                name: "User Five",
                username: "user5",
                uhUuid: "00000005",
                firstName: "User",
                lastName: "Five"
            },
            {
                name: "User Nine",
                username: "",
                uhUuid: "00000009",
                firstName: "User",
                lastName: "Nine"
            }
        ];
        scope.pagedItemsExclude = scope.groupToPages(scope.groupingExclude);

        scope.groupingMembers = _.cloneDeep(scope.groupingInclude);
        scope.groupingMembers.push({
            name: "User Seven",
            username: "user7",
            uhUuid: "00000007",
            firstName: "User",
            lastName: "Seven"
        });
        scope.pagedItemsMembers = scope.groupToPages(scope.groupingMembers);

        scope.groupingOwners = [
            {
                name: "User Six",
                username: "user6",
                uhUuid: "00000006",
                firstName: "User",
                lastName: "Six"
            }
        ];
        scope.pagedItemsOwners = scope.groupToPages(scope.groupingOwners);

        scope.listserv = true;
        scope.allowOptIn = true;
        scope.allowOptOut = true;
    });

    describe("displayGrouping", () => {
        beforeEach(() => {
            scope.pagedItemsGroupings = [["zzzz"]];
        });
        it("should set selectedGrouping to 'zzzz'", () => {
            scope.selectedGrouping = 0;
            scope.displayGrouping(0, 0);
            expect(scope.selectedGrouping).toEqual("zzzz");
        });
        it("should set showgrouping to true", () => {
            scope.showGrouping = false;
            scope.displayGrouping(0, 0);
            expect(scope.showGrouping).toBeTrue();
        });
        it("should call getGroupingInformation", () => {
            spyOn(scope, "getGroupingInformation").and.callThrough();
            scope.displayGrouping(0, 0);
            expect(scope.getGroupingInformation).toHaveBeenCalled();
        });
    });

    describe("toggleShowAdminTab", () => {
        it("should set showShowAdminTab to true when false", () => {
            scope.showAdminTab = false;
            scope.toggleShowAdminTab();
            expect(scope.showAdminTab).toBeTrue();
        });
        it("should set showShowAdminTab to false when true", () => {
            scope.showAdminTab = true;
            scope.toggleShowAdminTab();
            expect(scope.showAdminTab).toBeFalse();
        });
    });

    describe("getAllSyncDestinations", () => {
        it("should call getSyncDestList", () => {
            spyOn(gs, "getSyncDestList").and.callThrough();
            scope.getAllSyncDestinations();
            expect(gs.getSyncDestList).toHaveBeenCalled();
        });
    });

    describe("getGroupingInformation", () => {
        let pagesOfGrouping;
        beforeEach(() => {
            pagesOfGrouping = {
                groupingPath: "somePath",
                currentPage: 1,
                PAGE_SIZE: 1,
                sortString: "name",
                isAscending: true
            };
        });

        it("should set loading to true", () => {
            scope.getGroupingInformation();
            expect(scope.loading).toBeTrue();
        });
        it("should call getGrouping when asyncThreadCount is 1", () => {
            spyOn(gs, "getGrouping").and.callThrough();
            scope.getGroupingInformation();
            expect(gs.getGrouping).toHaveBeenCalled();
        });
        it("should set descriptionLoaded & paginatingProgress to true & paginatingComplete to false after calling gs.getGrouping", () => {
            spyOn(gs, "getGrouping").and.callThrough();
            scope.getGroupingInformation();
            expect(gs.getGrouping).toHaveBeenCalled();
        });
    });

    describe("getPages", () => {
        let pagesOfGrouping;
        beforeEach(() => {
            pagesOfGrouping = {
                groupingPath: "somePath",
                currentPage: 1,
                PAGE_SIZE: 1,
                sortString: "name",
                isAscending: true
            };
        });

        it("should return a promise", () => {
            spyOn(gs, "getGrouping").and.returnValue(Promise.resolve(true));
            scope.getPages(pagesOfGrouping.groupingPath, pagesOfGrouping.currentPage, pagesOfGrouping.PAGE_SIZE, pagesOfGrouping.sortString, pagesOfGrouping.isAscending);
            expect(gs.getGrouping).toHaveBeenCalled();
        });
    });

    describe("descriptionLengthWarning", () => {
        beforeEach(() => {
            scope.modelDescription = "theModelDescription";
        });
        it("should check that the modelDescription length doesn't go over the max", () => {
            expect(scope.descriptionLengthWarning()).toBeFalse();
        });
    });

    describe("editDescription", () => {
        it("should set descriptionForm to false when true", () => {
            scope.descriptionForm = true;
            scope.editDescription();
            expect(scope.descriptionForm).toBeFalse();
        });
        it("should set descriptionForm to true when false", () => {
            scope.descriptionForm = false;
            scope.editDescription();
            expect(scope.descriptionForm).toBeTrue();
        });
    });

    describe("cancelDescriptionEdit", () => {
        it("should set the modelDescription to the groupingDesciption", () => {
            scope.cancelDescriptionEdit();
            expect(scope.modelDescription).toBe("");
        });
        it("should set descriptionForm to false when true", () => {
            scope.descriptionForm = true;
            scope.cancelDescriptionEdit();
            expect(scope.descriptionForm).toBeFalse();
        });
    });

    describe("saveDescription", () => {
        it("should return the cancelDescriptionEdit function when localeCompare is 0", () => {
            expect(scope.saveDescription()).toBe(scope.cancelDescriptionEdit());
        });
        beforeEach(() => {
            scope.modelDescription = "descriptionOfAModal";
        });
        it("should update the description for a grouping", () => {
            spyOn(gs, "updateDescription").and.callThrough();
            scope.saveDescription();
            expect(scope.modelDescription).toBe("descriptionOfAModal");
            expect(gs.updateDescription).toHaveBeenCalled();
        });
    });

    describe("getCSVToolTipMessage", () => {
        it("should display loaded message when paginatingComplete has loaded", () => {
            scope.paginatingComplete = true;
            scope.getCSVToolTipMessage();
            expect(scope.getCSVToolTipMessage()).toBe(scope.groupLoaded);
        });
        it("should display not loaded message when paginatingComplete has not loaded", () => {
            scope.paginatingComplete = false;
            scope.getCSVToolTipMessage();
            expect(scope.getCSVToolTipMessage()).toBe(scope.groupNotLoaded);
        });
    });

    describe("descriptionDisplay", () => {
        it("should set descriptionLoaded as empty string if false", () => {
            scope.descriptionLoaded = false;
            expect(scope.descriptionDisplay()).toBe("");
        });
        it("should set modelDescription as empty string and display tracker to 0 when " +
            "showGrouping is true and displayTracker is 1", () => {
            scope.showGrouping = true;
            scope.displayTracker = 1;
            scope.descriptionLoaded = true;
            scope.descriptionDisplay();
            expect(scope.modelDescription).toBe(scope.groupingDescription);
            expect(scope.displayTracker).toBe(0);
        });
        it("should return groupingDescription if groupingDescription.length > 0,", () => {
            scope.descriptionLoaded = true;
            scope.groupingDescription = "123";
            expect(scope.descriptionDisplay()).toBe(scope.groupingDescription);
        });
        it("should return noDescriptionMessage if groupingDescription.length < 0", () => {
            scope.descriptionLoaded = true;
            scope.groupingDescription.length = -1;
            expect(scope.descriptionDisplay()).toBe(scope.noDescriptionMessage);
        });
    });

    describe("createApiErrorModal", () => {
        it("should check that the createApiErrorModal is launched", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.createApiErrorModal();
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("closeApiError", () => {
        beforeEach(() => {
            scope.createApiErrorModal();

            it("should close modal", () => {
                spyOn(scope.apiErrorModalInstance, "close").and.callThrough();
                scope.createApiErrorModal();
            });

            it("should close modal", () => {
                spyOn(scope.apiErrorModalInstance, "close").and.callThrough();
                scope.closeApiError();
                expect(scope.apiErrorModalInstance.close).toHaveBeenCalled();
            });
        });
    });

    // For reference (in index order):
    // Members: User One, User Two, User Three, User Seven, User Eight
    // Basis: User One, User Four, User Seven
    // Include: User One, User Two, User Three
    // Exclude: User Four, User Five, User Nine
    // Owners: User Six
    describe("addInBasis", () => {
        it("should add a key called 'inBasis' for all members in the group passed", () => {
            scope.addInBasis(scope.groupingInclude);
            expect(_.has(scope.groupingInclude[0], "inBasis")).toBe(true);
            expect(_.has(scope.groupingInclude[1], "inBasis")).toBe(true);
            expect(_.has(scope.groupingInclude[2], "inBasis")).toBe(true);

            scope.addInBasis(scope.groupingExclude);
            expect(_.has(scope.groupingExclude[0], "inBasis")).toBe(true);
            expect(_.has(scope.groupingExclude[1], "inBasis")).toBe(true);
        });

        it("should have a value of 'Yes' for the 'inBasis' key if the member is in the basis group", () => {
            scope.addInBasis(scope.groupingInclude);
            expect(scope.groupingInclude[0].inBasis).toEqual("Yes");

            scope.addInBasis(scope.groupingExclude);
            expect(scope.groupingExclude[0].inBasis).toEqual("Yes");
        });

        it("should have a value of 'No' for the 'inBasis' key if the member is not in the basis group", () => {
            scope.addInBasis(scope.groupingInclude);
            expect(scope.groupingInclude[1].inBasis).toEqual("No");
            expect(scope.groupingInclude[2].inBasis).toEqual("No");

            scope.addInBasis(scope.groupingExclude);
            expect(scope.groupingExclude[1].inBasis).toEqual("No");
        });
    });

    describe("addInInclude", () => {
        it("should add a key called 'inInclude' for all members in the group passed", () => {
            scope.addInInclude(scope.groupingBasis);
            expect(_.has(scope.groupingBasis[0], "inInclude")).toBe(true);
            expect(_.has(scope.groupingBasis[1], "inInclude")).toBe(true);
            expect(_.has(scope.groupingBasis[2], "inInclude")).toBe(true);
        });

        it("should have a value of 'Yes' for the 'inInclude' key if the member is in the include group", () => {
            scope.addInInclude(scope.groupingBasis);
            expect(scope.groupingBasis[0].inInclude).toEqual("Yes");
        });

        it("should have a value of 'No' for the 'inInclude' key if the member is not in the include group", () => {
            scope.addInInclude(scope.groupingBasis);
            expect(scope.groupingBasis[1].inInclude).toEqual("No");
            expect(scope.groupingBasis[2].inInclude).toEqual("No");
        });
    });

    describe("addInExclude", () => {
        it("should add a key called 'inExclude' for all members in the group passed", () => {
            scope.addInExclude(scope.groupingBasis);
            expect(_.has(scope.groupingBasis[0], "inExclude")).toBe(true);
            expect(_.has(scope.groupingBasis[1], "inExclude")).toBe(true);
            expect(_.has(scope.groupingBasis[2], "inExclude")).toBe(true);
        });

        it("should have a value of 'Yes' for the 'inExclude' key if the member is in the exclude group", () => {
            scope.addInExclude(scope.groupingBasis);
            expect(scope.groupingBasis[1].inExclude).toEqual("Yes");
        });

        it("should have a value of 'No' for the 'inExclude' key if the member is not in the exclude group", () => {
            scope.addInExclude(scope.groupingBasis);
            expect(scope.groupingBasis[0].inExclude).toEqual("No");
            expect(scope.groupingBasis[2].inExclude).toEqual("No");
        });
    });

    describe("addInGroups", () => {
        it("should call addInBasis, addInInclude, and addInExclude", () => {
            spyOn(scope, "addInBasis").and.callThrough();
            spyOn(scope, "addInInclude").and.callThrough();
            spyOn(scope, "addInExclude").and.callThrough();

            scope.addInGroups(scope.groupingInclude);
            expect(scope.addInBasis).toHaveBeenCalled();
            expect(scope.addInInclude).toHaveBeenCalled();
            expect(scope.addInExclude).toHaveBeenCalled();
        });
    });

    describe("addWhereListed", () => {
        it("should add a key called 'whereListed' for all members in the grouping", () => {
            scope.addWhereListed(scope.groupingMembers);

            expect(_.has(scope.groupingMembers[0], "whereListed")).toBe(true);
            expect(_.has(scope.groupingMembers[1], "whereListed")).toBe(true);
            expect(_.has(scope.groupingMembers[2], "whereListed")).toBe(true);
        });

        it("should have a value of 'Basis' for the 'whereListed' key if the member is only in the basis group", () => {
            scope.addWhereListed(scope.groupingMembers);

            expect(scope.groupingMembers[3].whereListed).toEqual("Basis");
        });

        it("should have a value of 'Include' for the 'whereListed' key if the member is only in the include group", () => {
            scope.addWhereListed(scope.groupingMembers);

            expect(scope.groupingMembers[1].whereListed).toEqual("Include");
            expect(scope.groupingMembers[2].whereListed).toEqual("Include");
        });

        it("should have a value of 'Basis & Include' for the 'whereListed' key if the member is in both the basis and include group", () => {
            scope.addWhereListed(scope.groupingMembers);

            expect(scope.groupingMembers[0].whereListed).toEqual("Basis & Include");
        });
    });

    describe("launchImportModal", () => {
        it("should set listName to listName passed in", () => {
            scope.listName = "";
            scope.launchImportModal("admin");
            expect(scope.listName).toBe("admin");
        });

        it("should check that the import modal is launched", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.launchImportModal(scope.listName);
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("addMembers", () => {
        it("should use $scope.manageMembers when membersToAdd parameter is null", () => {
            scope.manageMembers = "iamtst01";
            spyOn(gs, "invalidUhIdentifiers").and.callThrough();
            scope.addMembers("Include");
            expect(gs.invalidUhIdentifiers).toHaveBeenCalledWith([scope.manageMembers],
                jasmine.any(Function), jasmine.any(Function));
        });

        it("should ignore $scope.manageMembers when a membersToAdd parameter is passed in", () => {
            scope.manageMembers = "iamtst01";
            spyOn(gs, "invalidUhIdentifiers").and.callThrough();
            scope.addMembers("Include", ["iamtst02"]);
            expect(gs.invalidUhIdentifiers).toHaveBeenCalledWith(["iamtst02"],
                jasmine.any(Function), jasmine.any(Function));
        });

        it("should set $scope.listName to the listName that we are passing in", () => {
            scope.listName = "";
            scope.addMembers("admin", []);
            expect((scope.listName)).toBe("admin");
        });

        it("should set $scope.emptyInput to true if the members to add are empty", () => {
            scope.emptyInput = false;
            scope.addMembers("Include", []);
            expect(scope.emptyInput).toBeTrue();

            scope.emptyInput = false;
            scope.addMembers("Include", null);
            expect(scope.emptyInput).toBeTrue();
        });

        it("should call $scope.launchDynamicModal() when the members we are adding are above the maxImport", () => {
            spyOn(scope, "launchDynamicModal").and.callThrough();
            let arr = [];
            for (let i = 0; i < 100002; i++) {
                arr.push("iamtst01");
            }

            scope.addMembers("Include", arr);
            expect(scope.launchDynamicModal).toHaveBeenCalled();
        });

        it("should call gs.invalidUhIdentifiers and set $scope.waitingForImportResponse to true", () => {
            scope.waitingForImportResponse = false;
            spyOn(gs, "invalidUhIdentifiers").and.callThrough();
            scope.addMembers("Include", ["iamtst01"]);

            expect(scope.waitingForImportResponse).toBeTrue();
            expect(gs.invalidUhIdentifiers).toHaveBeenCalled();
        });

        describe("gs.invalidUhIdentifiers callbacks", () => {
            const uhIdentifiers = ["iamtst01"];

            beforeEach(() => {
                httpBackend.whenGET(BASE_URL + "currentUser").passThrough();
                httpBackend.whenGET(BASE_URL + "members/memberships/count").passThrough();
                httpBackend.whenGET(BASE_URL + "owners/groupings/count").passThrough();
                httpBackend.whenGET("modal/addModal").passThrough();
                httpBackend.whenGET("modal/importConfirmationModal").passThrough();
                httpBackend.whenGET("modal/importErrorModal").passThrough();
                httpBackend.whenGET("modal/dynamicModal").passThrough();
            });

            describe("onSuccess", () => {
                it("should set $scope.waitingForImportResponse to false", () => {
                    scope.waitingForImportResponse = true;
                    scope.addMembers("Include", uhIdentifiers);

                    httpBackend.expectPOST(BASE_URL + "members/invalid", uhIdentifiers).respond(200, uhIdentifiers);
                    httpBackend.flush();

                    expect(scope.waitingForImportResponse).toBeFalse();
                });

                it("should call $scope.existsInList and $scope.launchAddModal", () => {
                    spyOn(scope, "existsInList").and.callThrough();
                    spyOn(scope, "launchAddModal");
                    scope.addMembers("Include", uhIdentifiers);

                    httpBackend.expectPOST(BASE_URL + "members/invalid", uhIdentifiers).respond(200, []);
                    httpBackend.flush();

                    expect(scope.existsInList).toHaveBeenCalled();
                    expect(scope.launchAddModal).toHaveBeenCalled();
                });

                it("should call $scope.launchDynamicModal when all members pre-exist and is a batch import", () => {
                    for (let i = 0; i < 100; i++) {
                        scope.groupingInclude.push({
                            name: `iamtst${i}`,
                            uhUuid: `iamtst${i}`,
                            uid: `iamtst${i}`
                        });
                    }
                    const arr = scope.groupingInclude.map((member) => member.uhUuid);
                    spyOn(scope, "launchDynamicModal").and.callThrough();
                    scope.addMembers("Include", arr);
                    expect(scope.launchDynamicModal).toHaveBeenCalled();
                });

                it("should filter out the members to add that already exist in the list", () => {
                    scope.groupingInclude = [{
                        name: "iamtst01",
                        uhUuid: "iamtst01",
                        username: "iamtst01"
                    }];

                    spyOn(scope, "launchAddModal");
                    scope.addMembers("Include", ["iamtst01"]);

                    httpBackend.expectPOST(BASE_URL + "members/invalid", []).respond(200, []);
                    httpBackend.flush();

                    expect(scope.launchAddModal).toHaveBeenCalledWith({
                        membersToAdd: [],
                        listName: "Include"
                    });
                });

                it("should call $scope.launchImportConfirmationModal when adding more than multi-add threshold", () => {
                    spyOn(scope, "launchImportConfirmationModal").and.callThrough();
                    let arr = [];
                    for (let i = 0; i < 102; i++) {
                        arr.push("iamtst01");
                    }
                    scope.addMembers("Include", arr);

                    httpBackend.expectPOST(BASE_URL + "members/invalid", arr).respond(200, []);
                    httpBackend.flush();

                    expect(scope.launchImportConfirmationModal).toHaveBeenCalled();
                });

                it("should set $scope.invalidMembers and $scope.addInputError when res has invalid uhIdentifiers", () => {
                    scope.addInputError = false;
                    scope.addMembers("Include", uhIdentifiers);

                    httpBackend.expectPOST(BASE_URL + "members/invalid", uhIdentifiers).respond(200, uhIdentifiers);
                    httpBackend.flush();

                    expect(scope.invalidMembers).toEqual(uhIdentifiers);
                    expect(scope.addInputError).toBeTrue();
                });

                it("should set $scope.invalidMembers and call $scope.launchImportErrorModal when res has uhIdentifiers", () => {
                    spyOn(scope, "launchImportErrorModal").and.callThrough();
                    let arr = [];
                    for (let i = 0; i < 102; i++) {
                        arr.push("iamtst01");
                    }
                    scope.addMembers("Include", arr);

                    httpBackend.expectPOST(BASE_URL + "members/invalid", arr).respond(200, uhIdentifiers);
                    httpBackend.flush();

                    expect(scope.invalidMembers).toEqual(uhIdentifiers);
                    expect(scope.launchImportErrorModal).toHaveBeenCalled();
                });
            });

            describe("onError", () => {
                it("should set $scope.waitingForImportResponse, $scope.resStatus and call $scope.launchDynamicModal", () => {
                    const resStatus = 404;
                    spyOn(scope, "launchDynamicModal").and.callThrough();
                    scope.addMembers("Include", uhIdentifiers);

                    httpBackend.expectPOST(BASE_URL + "members/invalid", uhIdentifiers).respond(resStatus);
                    httpBackend.flush();

                    expect(scope.waitingForImportResponse).toBeFalse();
                    expect(scope.resStatus).toBe(resStatus);
                    expect(scope.launchDynamicModal).toHaveBeenCalled();
                });
            });
        });
    });

    describe("launchAddModal", () => {
        const member = ["iamtst01"];
        const members = ["iamtst01", "iamtst02"];
        const mockResponseSingle = [{
            name: "tst01name",
            uid: "iamtst01",
            uhUuid: "iamtst01"
        }];
        const mockResponseMulti = [
            {
                name: "tst01name",
                uid: "iamtst01",
                uhUuid: "iamtst01"
            },
            {
                name: "tst02name",
                uid: "iamtst02",
                uhUuid: "iamtst02"
            }
        ];

        it("should set $scope.listName to the parameter passed in", () => {
            scope.listName = "";
            scope.launchAddModal({
                membersToAdd: member,
                listName: "Include"
            });

            expect(scope.listName).toBe("Include");
        });

        it("should set $scope.containsInput to true and return when membersToAdd is empty", () => {
            spyOn(gs, "getMembersAttributes");
            scope.launchAddModal({
                membersToAdd: [],
                listName: "Include"
            });

            expect(scope.containsInput).toBeTrue();
            expect(gs.getMembersAttributes).not.toHaveBeenCalled();
        });

        it("should call gs.getMembersAttributes and set $scope.waitingForImportResponse to true", () => {
            scope.waitingForImportResponse = false;
            spyOn(gs, "getMembersAttributes").and.callThrough();
            scope.launchAddModal({
                membersToAdd: member,
                listName: "Include"
            });

            expect(scope.waitingForImportResponse).toBeTrue();
            expect(gs.getMembersAttributes).toHaveBeenCalled();
        });

        describe("gs.getMembersAttributes callbacks", () => {
            beforeEach(() => {
                spyOn(gs, "getMembersAttributes").and.callThrough();

                httpBackend.whenGET(BASE_URL + "currentUser").passThrough();
                httpBackend.whenGET(BASE_URL + "members/memberships/count").passThrough();
                httpBackend.whenGET(BASE_URL + "owners/groupings/count").passThrough();
                httpBackend.whenGET("modal/addModal").passThrough();
            });

            describe("onSuccess", () => {
                it("should set $scope.multiAddResults and set $scope.waitingForImportResponse to false", () => {
                    spyOn(scope, "addInGroups");
                    spyOn(scope, "initMemberDisplayName");
                    scope.waitingForImportResponse = true;
                    scope.launchAddModal({
                        membersToAdd: member,
                        listName: "Include"
                    });

                    httpBackend.expectPOST(BASE_URL + "members", member).respond(200, mockResponseSingle);
                    httpBackend.flush();

                    expect(scope.waitingForImportResponse).toBeFalse();
                    expect(scope.multiAddResults).toEqual(mockResponseSingle);
                });

                it("should call $scope.addInGroups and set $scope.initMemberDisplayName", () => {
                    spyOn(scope, "addInGroups").and.callThrough();
                    spyOn(scope, "initMemberDisplayName").and.callThrough();
                    scope.launchAddModal({
                        membersToAdd: member,
                        listName: "Include"
                    });

                    httpBackend.expectPOST(BASE_URL + "members", member).respond(200, mockResponseSingle);
                    httpBackend.flush();

                    expect(scope.addInGroups).toHaveBeenCalled();
                    expect(scope.initMemberDisplayName).toHaveBeenCalled();
                });

                it("should open addModal.html", () => {
                    spyOn(scope, "initMemberDisplayName").and.callThrough();

                    scope.launchAddModal({
                        membersToAdd: member,
                        listName: "Include"
                    });

                    httpBackend.expectPOST(BASE_URL + "members", member).respond(200, mockResponseSingle);
                    httpBackend.expectGET("modal/addModal").respond(200);
                    httpBackend.flush();

                    expect(scope.initMemberDisplayName).toHaveBeenCalled();
                });

                it("should open multiAddModal.html", () => {
                    for (const mockResponse of mockResponseMulti) {
                        mockResponse["inBasis"] = "No";
                        mockResponse["inInclude"] = "No";
                        mockResponse["inExclude"] = "No";
                    }
                    scope.launchAddModal({
                        membersToAdd: members,
                        listName: "Include"
                    });

                    httpBackend.expectPOST(BASE_URL + "members", members).respond(200, mockResponseMulti);
                    httpBackend.expectGET("modal/multiAddModal").respond(200);
                    httpBackend.flush();

                    expect(scope.isMultiAdd).toBeTrue();
                    expect(scope.multiAddResults).toEqual(mockResponseMulti);
                });

                describe("Pressing 'add' or 'cancel' on add/multiAddModal", () => {
                    const mockModal = {
                        result: {
                            then(confirmCallback) {
                                this.confirmCallBack = confirmCallback;
                            },
                            finally(confirmCallback) {
                                this.confirmCallBack = confirmCallback;
                            }
                        },
                        close() {
                            this.result.confirmCallBack();
                        }
                    };

                    beforeEach(() => {
                        spyOn(gs, "addMembersToIncludeAsync").and.callThrough();
                        spyOn(gs, "addMembersToExcludeAsync").and.callThrough();
                        spyOn(gs, "addOwnerships").and.callThrough();
                        spyOn(gs, "addAdmin").and.callThrough();
                    });

                    it("should not make any groupingsService call when the user presses 'cancel' on addModal.html", () => {
                        scope.launchAddModal({
                            membersToAdd: member,
                            listName: "Include"
                        });

                        httpBackend.expectPOST(BASE_URL + "members", member).respond(200, mockResponseSingle);
                        httpBackend.expectGET("modal/addModal").respond(200);
                        httpBackend.flush();

                        scope.cancelAddModal();
                        expect(scope.waitingForImportResponse).toBeFalse();
                        expect(gs.addMembersToIncludeAsync).not.toHaveBeenCalled();
                        expect(gs.addMembersToExcludeAsync).not.toHaveBeenCalled();
                        expect(gs.addOwnerships).not.toHaveBeenCalled();
                        expect(gs.addAdmin).not.toHaveBeenCalled();
                    });

                    it("should not make any groupingsService call when the user presses 'cancel' on multiAddModal.html", () => {
                        scope.launchAddModal({
                            membersToAdd: members,
                            listName: "Include"
                        });

                        httpBackend.expectPOST(BASE_URL + "members", members).respond(200, mockResponseMulti);
                        httpBackend.expectGET("modal/multiAddModal").respond(200);
                        httpBackend.flush();

                        scope.cancelAddModal();
                        expect(scope.waitingForImportResponse).toBeFalse();
                        expect(gs.addMembersToIncludeAsync).not.toHaveBeenCalled();
                        expect(gs.addMembersToExcludeAsync).not.toHaveBeenCalled();
                        expect(gs.addOwnerships).not.toHaveBeenCalled();
                        expect(gs.addAdmin).not.toHaveBeenCalled();
                    });

                    it("should call gs.addMembersToIncludeAsync when the user presses 'add' in addModal.html", () => {
                        spyOn(uibModal, "open").and.returnValue(mockModal);
                        scope.launchAddModal({
                            membersToAdd: member,
                            listName: "Include"
                        });

                        httpBackend.expectPOST(BASE_URL + "members", member).respond(200, mockResponseSingle);
                        httpBackend.flush();

                        scope.proceedAddModal();
                        expect(scope.waitingForImportResponse).toBeTrue();
                        expect(gs.addMembersToIncludeAsync).toHaveBeenCalled();
                    });

                    it("should call gs.addMembersToIncludeAsync when the user presses 'add' in multiAddModal.html", () => {
                        spyOn(uibModal, "open").and.returnValue(mockModal);
                        scope.launchAddModal({
                            membersToAdd: members,
                            listName: "Include"
                        });

                        httpBackend.expectPOST(BASE_URL + "members", members).respond(200, mockResponseMulti);
                        httpBackend.flush();

                        scope.proceedAddModal();
                        expect(scope.waitingForImportResponse).toBeTrue();
                        expect(gs.addMembersToIncludeAsync).toHaveBeenCalled();
                    });

                    it("should call gs.addMembersToExcludeAsync when the user presses 'add' in addModal.html", () => {
                        spyOn(uibModal, "open").and.returnValue(mockModal);
                        scope.launchAddModal({
                            membersToAdd: member,
                            listName: "Exclude"
                        });

                        httpBackend.expectPOST(BASE_URL + "members", member).respond(200, mockResponseSingle);
                        httpBackend.flush();

                        scope.proceedAddModal();
                        expect(scope.waitingForImportResponse).toBeTrue();
                        expect(gs.addMembersToExcludeAsync).toHaveBeenCalled();
                    });

                    it("should call gs.addMembersToExcludeAsync when the user presses 'add' in multiAddModal.html", () => {
                        spyOn(uibModal, "open").and.returnValue(mockModal);
                        scope.launchAddModal({
                            membersToAdd: members,
                            listName: "Exclude"
                        });

                        httpBackend.expectPOST(BASE_URL + "members", members).respond(200, mockResponseMulti);
                        httpBackend.flush();

                        scope.proceedAddModal();
                        expect(scope.waitingForImportResponse).toBeTrue();
                        expect(gs.addMembersToExcludeAsync).toHaveBeenCalled();
                    });

                    it("should call gs.addOwnerships when the user presses 'add' in addModal.html", () => {
                        spyOn(uibModal, "open").and.returnValue(mockModal);
                        scope.launchAddModal({
                            membersToAdd: member,
                            listName: "owners"
                        });

                        httpBackend.expectPOST(BASE_URL + "members", member).respond(200, mockResponseSingle);
                        httpBackend.flush();

                        scope.proceedAddModal();
                        expect(scope.waitingForImportResponse).toBeTrue();
                        expect(gs.addOwnerships).toHaveBeenCalled();
                    });

                    it("should call gs.addOwnerships when the user presses 'add' in multiAddModal.html", () => {
                        spyOn(uibModal, "open").and.returnValue(mockModal);
                        scope.launchAddModal({
                            membersToAdd: members,
                            listName: "owners"
                        });

                        httpBackend.expectPOST(BASE_URL + "members", members).respond(200, mockResponseMulti);
                        httpBackend.flush();

                        scope.proceedAddModal();
                        expect(scope.waitingForImportResponse).toBeTrue();
                        expect(gs.addOwnerships).toHaveBeenCalled();
                    });

                    it("should call gs.addAdmin when the user presses 'add' in addModal.html", () => {
                        spyOn(uibModal, "open").and.returnValue(mockModal);
                        scope.launchAddModal({
                            membersToAdd: member,
                            listName: "admins"
                        });

                        httpBackend.expectPOST(BASE_URL + "members", member).respond(200, mockResponseSingle);
                        httpBackend.flush();

                        scope.proceedAddModal();
                        expect(scope.waitingForImportResponse).toBeTrue();
                        expect(gs.addAdmin).toHaveBeenCalled();
                    });
                });
            });

            describe("onError", () => {
                it("should set $scope.resStatus and set $scope.waitingForImportResponse to false", () => {
                    const resStatus = 404;
                    scope.launchAddModal({
                        membersToAdd: member,
                        listName: "Include"
                    });

                    httpBackend.expectPOST(BASE_URL + "members", member).respond(resStatus);
                    httpBackend.flush();

                    expect(scope.resStatus).toBe(resStatus);
                });
            });
        });
    });

    describe("proceedAddModal", () => {
        beforeEach(() => {
            scope.addModalInstance = {
                close: () => {
                    // Mock $uib modal close
                }
            };
        });

        it("should close addModalInstance", () => {
            spyOn(scope.addModalInstance, "close").and.callThrough();
            scope.proceedAddModal();
            expect(scope.addModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("cancelAddModal", () => {
        beforeEach(() => {
            scope.addModalInstance = {
                dismiss: () => {
                    // Mock $uib modal dismiss
                }
            };
        });

        it("should dismiss addModalInstance", () => {
            spyOn(scope.addModalInstance, "dismiss").and.callThrough();
            scope.cancelAddModal();
            expect(scope.addModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe("launchImportConfirmationModal", () => {
        const mockModal = {
            result: {
                then(confirmCallback) {
                    this.confirmCallBack = confirmCallback;
                }
            },
            close() {
                this.result.confirmCallBack();
            }
        };

        it("should set $scope.listName and $scope.importSize", () => {
            scope.listName = "";
            scope.importSize = 0;
            scope.launchImportConfirmationModal("Include", ["iamtst01", "iamtst02"]);
            expect(scope.listName).toBe("Include");
            expect(scope.importSize).toBe(2);
        });

        it("should open $uibModal with modal/importConfirmationModal", () => {
            spyOn(uibModal, "open").and.returnValue(mockModal);
            scope.launchImportConfirmationModal("Include", ["iamtst01", "iamtst02"]);
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/importConfirmationModal",
                scope
            });
        });

        it("should make the correct groupingsService and member to Include/Exclude", () => {
            spyOn(uibModal, "open").and.returnValue(mockModal);
            spyOn(gs, "addMembersToIncludeAsync").and.callThrough();
            spyOn(gs, "addMembersToExcludeAsync").and.callThrough();

            scope.launchImportConfirmationModal("Include", ["iamtst01", "iamtst02"]);
            scope.proceedImportConfirmationModal();
            expect(gs.addMembersToIncludeAsync).toHaveBeenCalled();

            scope.launchImportConfirmationModal("Exclude", ["iamtst01", "iamtst02"]);
            scope.proceedImportConfirmationModal();
            expect(gs.addMembersToExcludeAsync).toHaveBeenCalled();
        });
    });

    describe("proceedImportConfirmationModal", () => {
        beforeEach(() => {
            scope.importConfirmationModalInstance = {
                close: () => {
                    // Mock $uib modal close
                }
            };
        });

        it("should close importConfirmationModalInstance", () => {
            spyOn(scope.importConfirmationModalInstance, "close").and.callThrough();
            scope.proceedImportConfirmationModal();
            expect(scope.importConfirmationModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("cancelImportConfirmationModal", () => {
        beforeEach(() => {
            scope.importConfirmationModalInstance = {
                dismiss: () => {
                    // Mock $uib modal dismiss
                }
            };
        });

        it("should dismiss importConfirmationModalInstance", () => {
            spyOn(scope.importConfirmationModalInstance, "dismiss").and.callThrough();
            scope.cancelImportConfirmationModal();
            expect(scope.importConfirmationModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe("sanitizer", () => {
        let goodFile, badFile, parseFile;
        beforeEach(() => {
            let bad1, bad2, bad3, bad4, bad5, bad6, bad7, bad8, bad9, bad10, bad11;
            bad1 = "<img src onerror=alert(\"Gavin is testing\")/>\n";
            bad2 = "<IMG SRC=\"javascript:alert('XSS');\">\n";
            bad3 = "<IMG SRC=javascript:alert('XSS')>\n";
            bad4 = "<IMG SRC=JaVaScRiPt:alert('XSS')>\n";
            bad5 = "<IMG SRC=javascript:alert(&quot;XSS&quot;)>\n";
            bad6 = "<IMG SRC=`javascript:alert(\"RSnake says, 'XSS'\")`>\n";
            bad7 = "\<a onmouseover=\"alert(document.cookie)\"\>xxs link\</a\>\n";
            bad8 = "\<a onmouseover=alert(document.cookie)\>xxs link\</a\>\n";
            bad9 = "<img src=x onerror=\"&#0000106&#0000097&#0000118&#0000097&#0000115&#0000099&#0000114&#0000105&#0000112&#0000116&#0000058&#0000097&#0000108&#0000101&#0000114&#0000116&#0000040&#0000039&#0000088&#0000083&#0000083&#0000039&#0000041\">";
            bad10 = "<div id=\"init_data\" style=\"display: none\">\n" +
                " <%= html_encode(data.to_json) %>\n" +
                "</div>\n";

            goodFile = "iamtst01\niamtst02\niamtst03\niamtst04\niamtst05\niamtst06\n22222222\n12345678\nbogusname\nfakename\n_1234455\n_iamtst01\n_test_123-abc";
            badFile = `${bad1}${bad2}${bad3}${bad4}${bad5}${bad6}${bad7}${bad8}${bad9}${bad10}`;
            parseFile = (file) => {
                scope.manageMembers = file.split(/[\r\n]+/);
                return scope.sanitizer(scope.manageMembers);
            };
        });

        it("should return an empty string when given a string with harmful input", () => {
            let invalidNames = scope.sanitizer("https://google.com");
            expect(invalidNames).toEqual("");

            invalidNames = scope.sanitizer("<IMG SRC=\"javascript:alert('XSS');\">");
            expect(invalidNames).toEqual("");

            invalidNames = scope.sanitizer("uhmanoa@hawaii.edu");
            expect(invalidNames).toEqual("");
        });

        it("should return an empty array when given an array with harmful input", () => {
            let arrayOfValidNames = parseFile(badFile);
            expect(arrayOfValidNames.length).toEqual(0);
            expect(arrayOfValidNames).toEqual([]);

            arrayOfValidNames = scope.sanitizer(["https://google.com", "iamtst01", "uhmanoa@hawaii.edu"]);
            expect(arrayOfValidNames.length).toEqual(1);
            expect(arrayOfValidNames).toEqual(["iamtst01"]);
        });

        it("should return an array of usernames that match the definition of a uhuuid or a uid", () => {
            const arrayOfValidNames = parseFile(goodFile);
            expect(arrayOfValidNames.length).toEqual(13);
            expect(arrayOfValidNames.toString()).toEqual("iamtst01,iamtst02,iamtst03,iamtst04,iamtst05,iamtst06,22222222,12345678,bogusname,fakename,_1234455,_iamtst01,_test_123-abc");
        });
    });

    describe("getPersonProps", () => {
        beforeEach(() => {
            scope.personProps = "";
        });
        describe("get and modify person props", () => {
            it("should return all the user's attributes", () => {
                let propsArray = Object.keys(scope.groupingBasis[0]);
                expect(propsArray).toEqual(["name", "username", "uhUuid", "firstName", "lastName"]);
            });
            it("should return a list with only the user's username, uhUuid, and name", () => {
                spyOn(scope, "getPersonProps").and.callThrough();
                let propsArray = Object.keys(scope.groupingBasis[0]);
                scope.getPersonProps(propsArray);
                expect(propsArray).not.toEqual(["name", "username", "uhUuid", "firstName", "lastName"]);
                expect(scope.personProps).toEqual([["username"], ["uhUuid"], ["name"]]);
            });
        });
    });

    describe("readTextFile", () => {
        it("should check for the MIME type and match it with text/plain", () => {
            let fakeNames = ["iamtst01","iamtst02","iamtst03","iamtst04","iamtst05","iamtst06","22222222","12345678","bogusname","fakename","_1234455","_iamtst01","_test_123-abc"];
            const fakeFile = new File(fakeNames, "file.txt", {type: "text/plain"});
            expect(scope.verifyImportFileType).toBeDefined();

            expect(scope.verifyImportFileType(fakeFile)).toBeTrue();
            expect(scope.verifyImportFileType({type: "text/plain"})).toBeTrue();
            expect(scope.verifyImportFileType({type: "TEXT/PLAIN"})).toBeTrue();

            expect(scope.verifyImportFileType({type: null})).toBeFalse();
            expect(scope.verifyImportFileType({type: "text/csv"})).toBeFalse();
            expect(scope.verifyImportFileType(null)).toBeFalse();
        });

        it("should check for the file size to be within 5MB",() => {
            let fakeNames = ["iamtst01","iamtst02","iamtst03","iamtst04","iamtst05","iamtst06","22222222","12345678","bogusname","fakename","_1234455","_iamtst01","_test_123-abc"];
            const fakeFile = new File(fakeNames, "file.txt", {type: "text/plain"});
            expect(scope.verifyImportFileSize).toBeDefined();

            expect(scope.verifyImportFileSize(fakeFile)).toBeTrue();
            expect(scope.verifyImportFileSize({size: 1000000})).toBeTrue();
            expect(scope.verifyImportFileSize({size: 5242880})).toBeTrue();

            expect(scope.verifyImportFileSize({size: 5242881})).toBeFalse();
        });

        it("should check for file name size to be under 50 characters.", () => {
            let fakeNames = ["iamtst01","iamtst02","iamtst03","iamtst04","iamtst05","iamtst06","22222222","12345678","bogusname","fakename","_1234455","_iamtst01","_test_123-abc"];
            const fakeFile = new File(fakeNames, "file.txt", {type: "text/plain"});
            expect(scope.verifyImportFileSize).toBeDefined();

            expect(scope.verifyImportFileNameSize(fakeFile)).toBeTrue();
            expect(scope.verifyImportFileNameSize({name: "ThisIsALongNameOverFiftyCharactersaaaaaaaaaaaaaaaa.txt"}));

            expect(scope.verifyImportFileNameSize({name: "ThisIsALongNameOverFiftyCharactersaaaaaaaaaaaaaaaa.txt"})).toBeFalse();
        });

        it("should check for the file name for any illegal characters.",() => {
            let fakeNames = ["iamtst01","iamtst02","iamtst03","iamtst04","iamtst05","iamtst06","22222222","12345678","bogusname","fakename","_1234455","_iamtst01","_test_123-abc"];
            const fakeFile = new File(fakeNames, "file.txt", {type: "text/plain"});
            expect(scope.verifyImportFileName).toBeDefined();

            expect(scope.verifyImportFileName(fakeFile)).toBeTrue();
            expect(scope.verifyImportFileName({name: "THISISATEXTFILE.txt"})).toBeTrue();
            expect(scope.verifyImportFileName({name: "thisisatextfile.txt"})).toBeTrue();
            expect(scope.verifyImportFileName({name: "this_is_a_text_file.txt"})).toBeTrue();
            expect(scope.verifyImportFileName({name: "this is a text file.txt"})).toBeTrue();

            expect(scope.verifyImportFileName({name: "badtextfile%.txt"})).toBeFalse();
        });
    });

    describe("launchImportErrorModal", () => {
        beforeEach(() => {
            scope.importErrorModalInstance = {
                dismiss: () => {
                    // Mock $uib modal dismiss
                }
            };
        });

        it("should open importErrorModalInstance", () => {
            spyOn(uibModal, "open");
            scope.launchImportErrorModal();
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("closeImportErrorModal", () => {
        beforeEach(() => {
            scope.launchImportErrorModal();
        });

        it("should dismiss confirmImportInstance", () => {
            spyOn(scope.importErrorModalInstance, "dismiss").and.callThrough();
            scope.closeImportErrorModal();
            expect(scope.importErrorModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe("cancelImportModal", () => {
        beforeEach(() => {
            scope.launchImportModal(scope.listName);
        });

        it("should dismiss confirmImportInstance", () => {
            spyOn(scope.importModalInstance, "dismiss").and.callThrough();
            scope.cancelImportModal();
            expect(scope.importModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe("launchDynamicModal", () => {
        beforeEach(() => {
            scope.createDynamicModal = {
                dismiss: () => {
                    // Mock $uib modal dismiss
                }
            };
        });

        it("should set currentModalTitle & currentModalBody to passed in title & body", () => {
            scope.launchDynamicModal("testTitle", "testBody", 5000);
            expect(scope.currentModalTitle).toBe("testTitle");
            expect(scope.currentModalBody).toBe("testBody");
        });

        it("should open createDynamicModal modal", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.launchDynamicModal("testTitle", "testBody", 5000);
            expect(uibModal.open).toHaveBeenCalled();
        });

        it("should call dismissDynamicModal to dismiss modal", () => {
            spyOn(scope.createDynamicModal, "dismiss").and.callThrough();
            scope.launchDynamicModal("testTitle", "testBody", 5000);
            expect(scope.createDynamicModal.dismiss()).toBeTrue();
        });
    });

    describe("removeItemsFromArray", () => {
        it("should remove items from the pendingList array", () => {
            let pendingList = [1, 1, 2, 3, 4, 5];
            let itemsToRemove = [1, 1];
            pendingList = scope.removeItemsFromArray(pendingList, itemsToRemove);
            expect(pendingList).toEqual([2, 3, 4, 5]);
        });
    });

    describe("existsInList", () => {
        beforeEach(() => {
            scope.groupingInclude = [
                {
                    name: "iamtst01",
                    uhUuid: "iamtst01",
                    username: "iamtst01"
                },
                {
                    name: "iamtst02",
                    uhUuid: "iamtst02",
                    username: "iamtst02"
                }
            ];

            scope.groupingExclude = [
                {
                    name: "iamtst03",
                    uhUuid: "iamtst03",
                    username: "iamtst03"
                },
                {
                    name: "iamtst04",
                    uhUuid: "iamtst04",
                    username: "iamtst04"
                }
            ];

            scope.groupingOwners = [{
                name: "iamtst05",
                uhUuid: "iamtst05",
                username: "iamtst05"
            }];

            scope.adminsList = [{
                name: "iamtst06",
                uhUuid: "iamtst06",
                username: "iamtst06"
            }];
        });

        it("should set $scope.membersInList to a comma-seperated string of members are in the list", () => {
            scope.existsInList("Include", ["iamtst01", "iamtst02"]);
            expect(scope.membersInList).toBe("iamtst01, iamtst02");

            scope.existsInList("Exclude", ["iamtst03", "iamtst04"]);
            expect(scope.membersInList).toBe("iamtst03, iamtst04");

            scope.existsInList("owners", ["iamtst05"]);
            expect(scope.membersInList).toBe("iamtst05");
        });

        it("should set $scope.membersInList to an empty string when none of the members passed in are in the list", () => {
            scope.existsInList("Include", ["iamtst11", "iamtst12"]);
            expect(scope.membersInList).toBe("");

            scope.existsInList("Exclude", ["iamtst13", "iamtst14"]);
            expect(scope.membersInList).toBe("");

            scope.existsInList("owners", ["iamtst15"]);
            expect(scope.membersInList).toBe("");
        });

        it("should set $scope.containsInput to true when the members passed in are ALL in the list", () => {
            scope.containsInput = false;
            scope.existsInList("Include", ["iamtst01", "iamtst02"]);
            expect(scope.containsInput).toBeTrue();

            scope.containsInput = false;
            scope.existsInList("Exclude", ["iamtst03", "iamtst04"]);
            expect(scope.containsInput).toBeTrue();

            scope.containsInput = false;
            scope.existsInList("owners", ["iamtst05"]);
            expect(scope.containsInput).toBeTrue();
        });
    });

    describe("initMemberDisplayName", () => {
        it("should set $scope.fullName, $scope.givenName, $scope.uhUuid and $scope.uid to the appropriate values", () => {
            let attributes = {
                name: "testName",
                firstName: "testFirstName",
                uhUuid: "testuhUuid",
                username: "testUsername"
            };

            scope.initMemberDisplayName(attributes);
            expect(scope.fullName).toBe(attributes.name);
            expect(scope.givenName).toBe(attributes.firstName);
            expect(scope.uhUuid).toBe(attributes.uhUuid);
            expect(scope.uid).toBe(attributes.username);

            attributes = {
                name: "testName",
                firstName: "testFirstName",
                uhUuid: "testuhUuid",
                uid: "testUsername"
            };

            scope.initMemberDisplayName(attributes);
            expect(scope.fullName).toBe(attributes.name);
            expect(scope.givenName).toBe(attributes.firstName);
            expect(scope.uhUuid).toBe(attributes.uhUuid);
            expect(scope.uid).toBe(attributes.uid);
        });

        it("should set $scope.inBasis, $scope.inInclude, and $scope.inExclude to the appropriate values", () => {
            let attributes = {
                name: "testName",
                firstName: "testFirstName",
                uhUuid: "testuhUuid",
                username: "testUsername"
            };

            scope.initMemberDisplayName(attributes);
            expect(scope.inBasis).toBe("No");
            expect(scope.inInclude).toBe("No");
            expect(scope.inExclude).toBe("No");

            attributes = {
                name: "testName",
                firstName: "testFirstName",
                uhUuid: "testuhUuid",
                username: "testUsername",
                inBasis: "Yes",
                inInclude: "Yes",
                inExclude: "Yes"
            };

            scope.initMemberDisplayName(attributes);
            expect(scope.inBasis).toBe("Yes");
            expect(scope.inInclude).toBe("Yes");
            expect(scope.inExclude).toBe("Yes");

            attributes = {
                name: "testName",
                firstName: "testFirstName",
                uhUuid: "testuhUuid",
                username: "testUsername",
                inBasis: "No",
                inInclude: "No",
                inExclude: "No"
            };

            scope.initMemberDisplayName(attributes);
            expect(scope.inBasis).toBe("No");
            expect(scope.inInclude).toBe("No");
            expect(scope.inExclude).toBe("No");
        });

        it("should set $scope.member to fullName if fullName.length > 0", () => {
            const attributes = {
                name: "testName",
                firstName: "testFirstName",
                uhUuid: "testuhUuid",
                username: "testUsername"
            };

            scope.initMemberDisplayName(attributes);
            expect(scope.member).toBe(scope.fullName);
        });

        it("should set $scope.member to uid if fullName.Length == 0 and uid > 0", () => {
            const attributes = {
                name: "",
                firstName: "testFirstName",
                uhUuid: "testuhUuid",
                username: "testUsername"
            };

            scope.initMemberDisplayName(attributes);
            expect(scope.member).toBe(scope.uid);
        });

        it("should set $scope.member to uhUuid if fullName & uid both == 0", () => {
            const attributes = {
                name: "",
                firstName: "testFirstName",
                uhUuid: "testuhUuid",
                username: ""
            };

            scope.initMemberDisplayName(attributes);
            expect(scope.member).toBe(scope.uhUuid);
        });
    });

    describe("launchAddResultModal", () => {
        let options = { userToAdd: "testUser", response: "testResponse", listName: "testList" };

        it("should set scope.listName to passed in option object's listName", () => {
            scope.launchAddResultModal(options);
            expect(scope.listName).toBe("testList");
        });

        it("should open addResultModalInstance modal", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.launchAddResultModal(options);
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("closeAddResultModal", () => {
        beforeEach(() => {
            scope.launchAddResultModal("testString");
        });

        it("should close addResultModalInstance", () => {
            spyOn(scope.addResultModalInstance, "close").and.callThrough();
            scope.closeAddResultModal();
            expect(scope.addResultModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("launchMultiAddResultModal", () => {
        it("should check that the multiAddResultModalInstance is opened", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.launchMultiAddResultModal(scope.listName);
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("closeMultiAddResultModal", () => {
        beforeEach(() => {
            scope.multiAddResultModalInstance = {
                close: () => {
                    // Mock $uib modal close
                }
            };

            scope.launchMultiAddResultModal();
        });

        it("should close multiAddResultModalInstance", () => {
            spyOn(scope.multiAddResultModalInstance, "close").and.callThrough();
            scope.closeMultiAddResultModal();
            expect(scope.multiAddResultModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("launchRemoveResultModal", () => {
        it("should open removeResultModalInstance modal", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.launchRemoveResultModal();
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("closeRemoveResultModal", () => {
        beforeEach(() => {
            scope.removeResultModalInstance = {
                close: () => {
                    // Mock $uib modal close
                }
            };

            scope.launchRemoveResultModal();
        });

        it("should close removeResultModalInstance", () => {
            spyOn(scope.removeResultModalInstance, "close").and.callThrough();
            scope.closeRemoveResultModal();
            expect(scope.removeResultModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("launchMultiRemoveResultModal", () => {
        it("should open multiRemoveResultModalInstance modal", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.launchMultiRemoveResultModal();
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("closeMultiRemoveResultModal", () => {
        beforeEach(() => {
            scope.multiRemoveResultModalInstance = {
                close: () => {
                    // Mock $uib modal close
                }
            };

            scope.launchMultiRemoveResultModal();
        });

        it("should close multiRemoveResultModalInstance", () => {
            spyOn(scope.multiRemoveResultModalInstance, "close").and.callThrough();
            scope.closeMultiRemoveResultModal();
            expect(scope.multiRemoveResultModalInstance.close).toHaveBeenCalled();
        });
    });
    
    describe("closeResetNotifModal", () => {
        beforeEach(() => {
            scope.createResetNotifModal(scope.group);
        });

        it("should close resetNotifModalInstance", () => {
            spyOn(scope.resetNotifModalInstance, "close").and.callThrough();
            scope.closeResetNotifModal();
            expect(scope.resetNotifModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("createRoleErrorModal", () => {
        it("should set scope.loading to false", () => {
            scope.loading = true;
            scope.createRoleErrorModal();
            expect(scope.loading).toBeFalse();
        });

        it("should check that createRoleErrorModal is launched", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.createRoleErrorModal();
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("removeMemberWithTrashcan", () => {
        it("should call launchRemoveModal", () => {
            let listName = "Include";
            spyOn(scope, "launchRemoveModal");
            scope.removeMemberWithTrashcan(listName, 0, 0);
            expect(scope.launchRemoveModal).toHaveBeenCalled();
        });
    });

    describe("transferMembersFromPageToCheckboxObject", () => {
        let currentPage;
        beforeEach(() => {
            currentPage = [
                {
                    firstName: "Monir",
                    inBasis: "No",
                    lastName: "Hodges",
                    name: "Monir F Hodges",
                    uhUuid: "1111",
                    username: "monir"
                },
                {
                    firstName: "Monir",
                    inBasis: "No",
                    lastName: "Hodges",
                    name: "Monir F Hodges",
                    uhUuid: "1112",
                    username: "monir"
                }
            ]; 
            scope.membersInCheckboxList = {
                1112: true,
                1111: true,
            };
        });
        it("should make the members in the checkbox list false", () => {
            scope.transferMembersFromPageToCheckboxObject(currentPage);
            expect(scope.membersInCheckboxList).toEqual({
                1112: false,
                1111: false,
            });
        });
    });

    describe("toggleCheckAllSelection", () => {
        beforeEach(() => {
            scope.pagedItemsInclude = [
                [
                    {
                        firstName: "iamtst01",
                        inBasis: "No",
                        lastName: "iamtst01",
                        name: "iamtst01",
                        uhUuid: "12345678",
                        username: "monir",
                    }
                ],
            ];
            scope.membersInCheckboxList = scope.pagedItemsInclude;
        });

        it("should set allSelected to true if false", () => {
            scope.allSelected = false;
            scope.toggleCheckAllSelection("Include");
            expect(scope.allSelected).toBeTrue();
        });
        it("should set allSelected to false if true", () => {
            scope.allSelected = true;
            scope.toggleCheckAllSelection("Include");
            expect(scope.allSelected).toBeFalse();
        });
        it("should set the membersInCheckboxList.uhUuid to the value of scope.allSelected", () => {
            scope.allSelected = false;
            scope.toggleCheckAllSelection("Include");
            expect(scope.membersInCheckboxList[((scope.pagedItemsInclude[0][0]).uhUuid)]).toEqual(true);
        });
        it("should set the membersInCheckboxList.uhUuid to the value of scope.allSelected", () => {
            scope.allSelected = true;
            scope.toggleCheckAllSelection("Include");
            expect(scope.membersInCheckboxList[((scope.pagedItemsInclude[0][0]).uhUuid)]).toEqual(false);
        });
    });

    describe("extractSelectedUsersFromCheckboxes", () => {
        let obj = {};
        let str = "test";
        let expectedResult = "";
        for (let i = 0; i < 10; i++) {
            let ident = str + i.toString();
            let val = Boolean(i % 2);
            obj[ident] = val;
            if (val) {
                expectedResult += ident + ",";
            }
        }
        expectedResult = expectedResult.slice(0, -1);

        it("should create a comma separated string of all object identifiers set to true", () => {
            expect(scope.extractSelectedUsersFromCheckboxes(obj)).toEqual(expectedResult);
        });
        it("should return an empty string if object contains anything but boolean values", () => {
            expect(scope.extractSelectedUsersFromCheckboxes({ test1: "test1", test2: true, test3: false })).toEqual("");
        });
    });

    describe("returnMemberObject", () => {
        beforeEach(() => {
            scope.groupingInclude = [{
                name: "iamtst01",
                uhUuid: "iamtst01",
                username: "iamtst01"
            }];

            scope.groupingExclude = [{
                name: "iamtst02",
                uhUuid: "iamtst02",
                username: "iamtst02"
            }];

            scope.groupingOwners = [{
                name: "iamtst03",
                uhUuid: "iamtst03",
                username: "iamtst03"
            }];

            scope.adminsList = [{
                name: "iamtst04",
                uhUuid: "iamtst04",
                username: "iamtst04"
            }];
        });

        it("should return the member object found in the Include list", () => {
            expect(scope.returnMemberObject("iamtst01", "Include")).toEqual(scope.groupingInclude[0]);
        });

        it("should return the member object found in the Exclude list", () => {
            expect(scope.returnMemberObject("iamtst02", "Exclude")).toEqual(scope.groupingExclude[0]);
        });

        it("should return the member object found in the owners list", () => {
            expect(scope.returnMemberObject("iamtst03", "owners")).toEqual(scope.groupingOwners[0]);
        });

        it("should return the member object found in the admins list", () => {
            expect(scope.returnMemberObject("iamtst04", "admins")).toEqual(scope.adminsList[0]);
        });

        it("should return undefined when member is not found in the list", () => {
            expect(scope.returnMemberObject("iamtst01", "Exclude")).toBeUndefined();
        });

        it("should return undefined with incorrect listName", () => {
            expect(scope.returnMemberObject("iamtst01", "bogus")).toBeUndefined();
        });
    });

    describe("parseAddRemoveInputStr", () => {
        let spaceSeparated = "Hello I love you";
        let commaSeparated = "Hello,I,love,you";
        let commaAndSpaceSeparated = "Hello, I love,you";
        let single = "Hello";
        let arrayCreated = ["Hello", "I", "love", "you"];
        let singleArrayCreated = ["Hello"];

        it("should take a space separated string and create an array", () => {
            expect(scope.parseAddRemoveInputStr(spaceSeparated)).toEqual(arrayCreated);
        });
        it("should take a comma separated string and create an array", () => {
            expect(scope.parseAddRemoveInputStr(commaSeparated)).toEqual(arrayCreated);
        });
        it("should take a comma and space separated string and create an array", () => {
            expect(scope.parseAddRemoveInputStr(commaAndSpaceSeparated)).toEqual(arrayCreated);
        });
        it("should take a string that has no commas or spaces and create an array", () => {
            expect(scope.parseAddRemoveInputStr(single)).toEqual(singleArrayCreated);
        });
        it("should return an empty string if value passed is not a string", () => {
            expect(scope.parseAddRemoveInputStr(true)).toEqual("");
        });
    });

    describe("fetchMemberProperties", () => {
        let members = [];
        beforeEach(() => {
            scope.groupingInclude = [
                {
                    name: "iamtst01",
                    uhUuid: "iamtst01",
                    username: "iamtst01"
                },
                {
                    name: "iamtst02",
                    uhUuid: "iamtst02",
                    username: "iamtst02"
                }
            ];
        });

        it("should return true when members are in the list", () => {
            members = ["iamtst01"];
            expect(scope.fetchMemberProperties(members, "Include")).toBeTrue();
            expect(scope.membersNotInList).toEqual("");
        });

        it("should return true when all members are in the list", () => {
            members = ["iamtst01", "iamtst02"];
            expect(scope.fetchMemberProperties(members, "Include")).toBeTrue();
            expect(scope.membersNotInList).toEqual("");
        });

        it("should return true although some members are not in the list", () => {
            members = ["iamtst01", "iamtst03", "iamtst04"];
            expect(scope.fetchMemberProperties(members, "Include")).toBeTrue();
            expect(scope.membersNotInList).toEqual("iamtst03, iamtst04");
        });

        it("should return false when members are not in the list", () => {
            members = ["iamtst03", "iamtst04"];
            expect(scope.fetchMemberProperties(members, "Include")).toBeFalse();
            expect(scope.membersNotInList).toEqual("iamtst03, iamtst04");
        });
    });

    describe("removeMembers", () => {
        it("should set the appropriate $scope.listName", () => {
            scope.listName = "";
            scope.membersToModify = "iamtst01";
            scope.removeMembers("Include");
            expect(scope.listName).toEqual("Include");
        });

        it("should set membersToModify to manageMembers", () => {
            scope.membersInCheckboxList = {
                "iamtst01": true,
                "iamtst02": false,
                "iamtst03": true
            };
            scope.manageMembers = "iamtst01, iamtst02";
            scope.removeMembers(scope.listName);
            expect(scope.membersToModify).toBe(scope.manageMembers);
        });

        it("should set membersToModify to extractSelectedUsersFromCheckboxes", () => {
            scope.membersInCheckboxList = {
                "iamtst01": true,
                "iamtst02": false,
                "iamtst03": true
            };
            scope.removeMembers(scope.listName);
            expect(scope.membersToModify).toBe(scope.extractSelectedUsersFromCheckboxes(scope.membersInCheckboxList));
        });

        it("should set emptyInput to true if membersToModify is empty", () => {
            scope.manageMembers = "";
            scope.membersToModify = "";
            scope.emptyInput = false;
            scope.removeMembers(scope.listName);
            expect(scope.emptyInput).toBeTrue();
        });

        it("should call launchRemoveModal", () => {
            scope.groupingInclude = [{
                name: "iamtst01",
                username: "iamtst01",
                uhUuid: "iamtst01"
            }];
            scope.manageMembers = "iamtst01";
            spyOn(scope, "launchRemoveModal");
            spyOn(scope, "launchDynamicModal");
            scope.removeMembers("Include");
            expect(scope.membersToModify).toEqual("iamtst01");
            expect(scope.launchRemoveModal).toHaveBeenCalled();
        });

        it("should call launchDynamicModal when member does not exist in list", () => {
            scope.groupingInclude = [{
                name: "iamtst01",
                username: "iamtst01",
                uhUuid: "iamtst01"
            }];
            scope.manageMembers = "iamtst02";
            spyOn(scope, "launchRemoveModal");
            spyOn(scope, "launchDynamicModal");
            scope.removeMembers("Include");
            expect(scope.membersToModify).toEqual("iamtst02");
            expect(scope.launchRemoveModal).not.toHaveBeenCalled();
            expect(scope.launchDynamicModal).toHaveBeenCalled();
        });

        it("should call launchRemoveErrorModal when the listName is owners", () => {
            scope.groupingOwners = [{
                name: "iamtst03",
                username: "iamtst03",
                uhUuid: "iamtst03"
            }];
            scope.manageMembers = "iamtst03";
            spyOn(scope, "launchRemoveErrorModal");
            scope.removeMembers("owners");
            expect(scope.launchRemoveErrorModal).toHaveBeenCalled();
        });
    });

    describe("removeOwnerWithTrashcan", () => {
        it("should create the remove modal if groupingOwners length > 1", () => {
            scope.groupingOwners = [
                {
                    name: "iamtst01",
                    username: "iamtst01",
                    uhUuid: "iamtst01"
                },
                {
                    name: "iamtst02",
                    username: "iamtst02",
                    uhUuid: "iamtst02"
                }
            ];
            spyOn(scope, "launchRemoveModal");
            scope.removeOwnerWithTrashcan(0, 0);
            expect(scope.launchRemoveModal).toHaveBeenCalled();
        });

        it("should create the remove error modal if groupingOwners < 1", () => {
            scope.groupingOwners = [{
                name: "iamtst01",
                username: "iamtst01",
                uhUuid: "iamtst01"
            }];
            spyOn(scope, "launchRemoveErrorModal");
            scope.removeOwnerWithTrashcan(0, 0);
            expect(scope.launchRemoveErrorModal).toHaveBeenCalledWith("owner");
        });
    });

    describe("createEmptyGroupModal", () => {
        it("should check that the createEmptyGroupModal is launched", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.createEmptyGroupModal();
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("createResetNotifModal", () => {
        it("should set group to group that is passed in", () => {
            expect(scope.group).toBe("");
            scope.createResetNotifModal("admin");
            expect(scope.group).toBe("admin");
        });

        it("should check that the resetNotifModalInstance is launched", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.createResetNotifModal();
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("launchRemoveModal", () => {
        let options;
        const mockModal = {
            result: {
                then(confirmCallback) {
                    this.confirmCallBack = confirmCallback;
                }
            },
            close() {
                this.result.confirmCallBack();
            }
        };

        beforeEach(() => {
            scope.groupingInclude = [
                {
                    name: "iamtst01",
                    username: "iamtst01",
                    uhUuid: "iamtst01"
                },
                {
                    name: "iamtst02",
                    username: "iamtst02",
                    uhUuid: "iamtst02"
                }
            ];

            scope.groupingExclude = [
                {
                    name: "iamtst03",
                    username: "iamtst03",
                    uhUuid: "iamtst03"
                },
                {
                    name: "iamtst04",
                    username: "iamtst04",
                    uhUuid: "iamtst04"
                }
            ];

            scope.groupingOwners = [
                {
                    name: "iamtst05",
                    username: "iamtst05",
                    uhUuid: "iamtst05"
                },
                {
                    name: "iamtst06",
                    username: "iamtst06",
                    uhUuid: "iamtst06"
                }
            ];
        });

        it("should set scope.removeInputError to true when no users are given", () => {
            options = { listName: "Include" };
            scope.removeInputError = false;
            scope.launchRemoveModal(options);
            expect(scope.removeInputError).toBeTrue();
        });

        it("should open uibModal with removeModal template when removing 1 user", () => {
            spyOn(scope, "initMemberDisplayName");
            spyOn(uibModal, "open").and.returnValue(mockModal);

            options = { membersToRemove: { name: "iamtst01", username: "iamtst01", uhUuid: "iamtst01" }, listName: "Include" };
            scope.launchRemoveModal(options);
            expect(scope.initMemberDisplayName).toHaveBeenCalled();
            expect(scope.membersToRemove).toEqual(["iamtst01"]);
            expect(scope.isMultiRemove).toBeFalse();
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/removeModal",
                windowClass: "",
                backdrop: "static",
                scope
            });
            spyOn(gs, "removeMembersFromInclude").and.callThrough();
            scope.proceedRemoveModal();
            expect(gs.removeMembersFromInclude).toHaveBeenCalled();

            options = { membersToRemove: ["iamtst03"], listName: "Exclude" };
            scope.launchRemoveModal(options);
            expect(scope.initMemberDisplayName).toHaveBeenCalled();
            expect(scope.membersToRemove).toEqual(["iamtst03"]);
            expect(scope.isMultiRemove).toBeFalse();
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/removeModal",
                windowClass: "",
                backdrop: "static",
                scope
            });
            spyOn(gs, "removeMembersFromExclude").and.callThrough();
            scope.proceedRemoveModal();
            expect(gs.removeMembersFromExclude).toHaveBeenCalled();
        });

        it("should open uibModal with multiRemoveModal template when removing more than 1 user", () => {
            spyOn(uibModal, "open").and.returnValue(mockModal);
            spyOn(gs, "removeMembersFromInclude").and.callThrough();
            spyOn(gs, "removeMembersFromExclude").and.callThrough();

            options = { membersToRemove: ["iamtst01", "iamtst02"], listName: "Include" };
            scope.launchRemoveModal(options);
            expect(scope.membersToRemove).toEqual(["iamtst01", "iamtst02"]);
            expect(scope.isMultiRemove).toBeTrue();
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/multiRemoveModal",
                windowClass: "",
                backdrop: "static",
                scope
            });
            scope.proceedRemoveModal();
            expect(gs.removeMembersFromInclude).toHaveBeenCalled();

            options = { membersToRemove: ["iamtst03", "iamtst04"], listName: "Exclude" };
            scope.launchRemoveModal(options);
            expect(scope.membersToRemove).toEqual(["iamtst03", "iamtst04"]);
            expect(scope.isMultiRemove).toBeTrue();
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/multiRemoveModal",
                windowClass: "",
                backdrop: "static",
                scope
            });
            scope.proceedRemoveModal();
            expect(gs.removeMembersFromExclude).toHaveBeenCalled();
        });

        it("should open uibModal with windowClass modal-danger when removing the yourself from owners list", () => {
            spyOn(uibModal, "open").and.returnValue(mockModal);
            spyOn(gs, "removeOwnerships").and.callThrough();

            options = { membersToRemove: { name: "iamtst05", username: "iamtst05", uhUuid: "iamtst05" }, listName: "owners" };
            scope.currentUser = {uid: "iamtst05", uhUuid: "iamtst05"};
            scope.launchRemoveModal(options);
            expect(scope.membersToRemove).toEqual(["iamtst05"]);
            expect(scope.isMultiRemove).toBeFalse();
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/removeModal",
                windowClass: "modal-danger",
                backdrop: "static",
                scope
            });
            scope.proceedRemoveModal();
            expect(gs.removeOwnerships).toHaveBeenCalled();

            options = { membersToRemove: ["iamtst05", "iamtst06"], listName: "owners" };
            scope.launchRemoveModal(options);
            expect(scope.membersToRemove).toEqual(["iamtst05", "iamtst06"]);
            expect(scope.isMultiRemove).toBeTrue();
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/multiRemoveModal",
                windowClass: "modal-danger",
                backdrop: "static",
                scope
            });
            scope.proceedRemoveModal();
            expect(gs.removeOwnerships).toHaveBeenCalled();
        });
    });

    describe("proceedRemoveModal", () => {
        beforeEach(() => {
            scope.removeModalInstance = {
                close: () => {
                    // Mock $uib modal close
                }
            };
        });

        it("should close removeModalInstance", () => {
            spyOn(scope.removeModalInstance, "close").and.callThrough();
            scope.proceedRemoveModal();
            expect(scope.removeModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("proceedResetGroup", () => {
        beforeEach(() => {
            scope.createResetGroupModal(scope.group);
        });

        it("should close resetModalInstance", () => {
            spyOn(scope.resetModalInstance, "close").and.callThrough();
            scope.proceedResetGroup();
            expect(scope.resetModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("cancelRemoveModal", () => {
        beforeEach(() => {
            scope.removeModalInstance = {
                dismiss: () => {
                    // Mock $uib modal dismiss
                }
            };
        });

        it("should dismiss removeModalInstance", () => {
            spyOn(scope.removeModalInstance, "dismiss").and.callThrough();
            scope.cancelRemoveModal();
            expect(scope.removeModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe("cancelResetGroup", () => {
        beforeEach(() => {
            scope.createResetGroupModal(scope.group);
        });

        it("should dismiss resetModalInstance", () => {
            spyOn(scope.resetModalInstance, "dismiss").and.callThrough();
            scope.cancelResetGroup();
            expect(scope.resetModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe("closeEmptyGroupModal", () => {
        beforeEach(() => {
            scope.createEmptyGroupModal();
        });

        it("should dismiss emptyGroupModalInstance", () => {
            spyOn(scope.emptyGroupModalInstance, "dismiss").and.callThrough();
            scope.closeEmptyGroupModal();
            expect(scope.emptyGroupModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe("launchRemoveErrorModal", () => {
        it("should set userType to userType that is passed in", () => {
            expect(scope.userType).toBeUndefined();
            scope.launchRemoveErrorModal("admin");
            expect(scope.userType).toBe("admin");
        });

        it("should check that removeErrorModalInstance is launched", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.launchRemoveErrorModal("test");
            expect(uibModal.open).toHaveBeenCalled();
        });
    });
    
    describe("closeRemoveErrorModal", () => {
        beforeEach(() => {
            scope.launchRemoveErrorModal("testString");
        });

        it("should close removeErrorModalInstance", () => {
            spyOn(scope.removeErrorModalInstance, "close").and.callThrough();
            scope.closeRemoveErrorModal();
            expect(scope.removeErrorModalInstance.close).toHaveBeenCalled();
        });
    });
    
    describe("returnToGroupingsList", () => {
        it("should call resetGroupingInformation", () => {
            spyOn(scope, "resetGroupingInformation").and.callThrough();
            scope.returnToGroupingsList();

            expect(scope.resetGroupingInformation).toHaveBeenCalled();
        });

        it("should reset scope.columnSort to a blank object", () => {
            scope.returnToGroupingsList();

            expect(scope.columnSort).toEqual({});
        });

        it("should go back to the groupings list view", () => {
            scope.returnToGroupingsList();

            expect(scope.showGrouping).toBe(false);
        });
    });

    describe("resetGroupingInformation", () => {
        it("should reset columnSort object", () => {
            scope.columnSort = { adminsList: { property: "name" } };
            scope.resetGroupingInformation();
            expect(scope.columnSort).toEqual({});
        });

        it("should reset syncDestArray, resetResults, and personProps arrays", () => {
            scope.syncDestArray = ["test"];
            scope.resetResults = ["test"];
            scope.personProps = ["test"];

            scope.resetGroupingInformation();

            expect(scope.syncDestArray).toEqual([]);
            expect(scope.resetResults).toEqual([]);
            expect(scope.personProps).toEqual([]);
        });
        
        it("should reset every nonpaginated list pertaining to a grouping", () => {
            scope.resetGroupingInformation();

            expect(scope.groupingMembers).toEqual([]);
            expect(scope.groupingBasis).toEqual([]);
            expect(scope.groupingInclude).toEqual([]);
            expect(scope.groupingExclude).toEqual([]);
            expect(scope.groupingOwners).toEqual([]);
        });

        it("should reset every paginated list pertaining to a grouping", () => {
            scope.resetGroupingInformation();

            expect(scope.pagedItemsMembers).toEqual([]);
            expect(scope.pagedItemsBasis).toEqual([]);
            expect(scope.pagedItemsInclude).toEqual([]);
            expect(scope.pagedItemsExclude).toEqual([]);
            expect(scope.pagedItemsOwners).toEqual([]);
        });

        it("should reset all page numbers for lists pertaining to a grouping", () => {
            scope.resetGroupingInformation();

            expect(scope.currentPageMembers).toEqual(0);
            expect(scope.currentPageBasis).toEqual(0);
            expect(scope.currentPageInclude).toEqual(0);
            expect(scope.currentPageExclude).toEqual(0);
            expect(scope.currentPageOwners).toEqual(0);
        });

        it("should reset all search filters", () => {
            scope.resetGroupingInformation();

            expect(scope.basisQuery).toEqual("");
            expect(scope.excludeQuery).toEqual("");
            expect(scope.includeQuery).toEqual("");
            expect(scope.membersQuery).toEqual("");
            expect(scope.adminsQuery).toEqual("");
            expect(scope.optInQuery).toEqual("");
            expect(scope.ownersQuery).toEqual("");
            expect(scope.personQuery).toEqual("");
        });
    });
    
    describe("createPreferenceInfoModal", () => {
        it("should set preferenceInfo to preferenceInfo that is passed in", () => {
            expect(scope.preferenceInfo).toBeUndefined();
            scope.createPreferenceInfoModal("test");
            expect(scope.preferenceInfo).toBe("test");
        });

        it("should check that the infoModalInstance is launched", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.createPreferenceInfoModal("test");
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("closePreferenceInfo", () => {
        beforeEach(() => {
            scope.createPreferenceInfoModal("testString");
        });

        it("should close infoModalInstance", () => {
            spyOn(scope.infoModalInstance, "close").and.callThrough();
            scope.closePreferenceInfo();
            expect(scope.infoModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("updateAllowOptOut", () => {
        it("should call groupings service", () => {
            spyOn(gs, "setOptOut").and.callThrough();
            scope.updateAllowOptOut();
            expect(gs.setOptOut).toHaveBeenCalled();
        });
    });

    describe("createResetGroupModal", () => {
        let options = { user: "testUser", group: "testGroup", listNames: "testList" };

        it("should set scope.groupReset/listNames to passed in option's object group/listNames", () => {
            scope.groupReset = "";
            scope.listNames = "";
            scope.createResetGroupModal(options);
            expect(scope.groupReset).toBe("testGroup");
            expect(scope.listNames).toBe("testList");
        });

        it("should open resetModalInstance modal", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.createResetGroupModal(options);
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("resetGroup", () => {
        it("should set scope.resetInclude to 'empty' if entries in groupingInclude is 0 or scope.includeCheck is false", function () {
            scope.resetInclude = "test";
            scope.includeCheck = false;
            scope.resetGroup();
            expect(scope.resetInclude).toEqual("empty");
        });

        it("should set scope.resetInclude to 'empty' if entries in groupingInclude is 0 or scope.includeCheck is false", function () {
            scope.resetInclude = "test";
            scope.includeCheck = true;
            scope.groupingInclude = [];
            scope.resetGroup();
            expect(scope.resetInclude).toEqual("empty");
        });

        it("should push uhUuid from scope.groupingInclude into scope.resetInclude", () => {
            scope.includeCheck = true;
            scope.groupingInclude = [{ uhUuid: "testId1" }, { uhUuid: "testId2" }];
            scope.resetGroup();
            expect(scope.resetInclude).toEqual(["testId1", "testId2"]);
        });

        it("should set scope.resetResults to groupingInclude concat w/ groupingExclude if exclude & include checks are true", () => {
            scope.excludeCheck = true;
            scope.includeCheck = true;
            scope.resetGroup();
            expect(scope.resetResults).toEqual(scope.groupingInclude.concat(scope.groupingExclude));
        });

        it("should set scope.resetResults to groupingExclude if exclude is true and include is false", () => {
            scope.excludeCheck = true;
            scope.includeCheck = false;
            scope.resetGroup();
            expect(scope.resetResults).toEqual(scope.groupingExclude);
        });

        it("should set scope.resetResults to groupingInclude if exclude is false and include is true", () => {
            scope.excludeCheck = false;
            scope.includeCheck = true;
            scope.resetGroup();
            expect(scope.resetResults).toEqual(scope.groupingInclude);
        });

        it("should set scope.resetResults to '' if both exclude and include is false", () => {
            scope.excludeCheck = false;
            scope.includeCheck = false;
            scope.resetGroup();
            expect(scope.resetResults).toEqual("");
        });

        it("should call scope.getPersonProps if resetResults[0] is not undefined", () => {
            scope.groupingInclude = ["username", "uhUuid", "name"];
            scope.excludeCheck = false;
            scope.includeCheck = true;
            spyOn(scope, "getPersonProps").and.callThrough();
            scope.resetGroup();
            expect(scope.getPersonProps).toHaveBeenCalled();
        });

        it("should call createResetGroupModal", () => {
            spyOn(scope, "createResetGroupModal").and.callThrough();
            scope.resetGroup();
            expect(scope.createResetGroupModal).toHaveBeenCalled();
        });
    });

    describe("updateIncludeCheck", () => {
        it("should set includeCheck to true if includeCheck is false", () => {
            scope.includeCheck = false;
            scope.updateIncludeCheck();
            expect(scope.includeCheck).toBeTrue();
        });

        it("should set includeCheck to false if includeCheck is true", () => {
            scope.includeCheck = true;
            scope.updateIncludeCheck();
            expect(scope.includeCheck).toBeFalse();
        });
    });

    describe("updateExcludeCheck", () => {
        it("should set excludeCheck to true if excludeCheck is false", () => {
            scope.excludeCheck = false;
            scope.updateExcludeCheck();
            expect(scope.excludeCheck).toBeTrue();
        });

        it("should set excludeCheck to false if excludeCheck is true", () => {
            scope.excludeCheck = true;
            scope.updateExcludeCheck();
            expect(scope.excludeCheck).toBeFalse();
        });
    });

    describe("updateAllowOptIn", () => {
        it("should call groupings service setOptIn", () => {
            spyOn(gs, "setOptIn").and.callThrough();
            scope.updateAllowOptIn();
            expect(gs.setOptIn).toHaveBeenCalled();
        });
    });

    describe("getSyncDestValueInArray", () => {
        let testSync;
        beforeEach(() => {
            testSync = {
                description: "Google-Group: #uh-iam-group",
                hidden: false,
                name: "testSyncDest",
                synced: false,
                tooltip: "Synchronize the grouping's membership with a corresponding GOOGLE-GROUP list, which will be created as needed."
            };
            scope.syncDestArray.push(testSync);
        });

        it("should not alter the value of syncDest", () => {
            let result = scope.getSyncDestValueInArray(testSync.name);
            expect(result).toBeFalse();
        });
    });

    describe("getEntireSyncDestInArray", () => {
        let testSyncDest, testSyncDest1, testSyncDest2;
        beforeEach(() => {
            testSyncDest = {
                description: "",
                hidden: true,
                name: "testSyncDest",
                synced: false,
                tooltip: "Synchronize the grouping's membership with a corresponding GOOGLE-GROUP list, which will be created as needed."
            };
            testSyncDest1 = {
                description: "",
                hidden: true,
                name: "testSyncDest1",
                synced: true,
                tooltip: "Synchronize the grouping's membership with a corresponding GOOGLE-GROUP list, which will be created as needed."
            };
            testSyncDest2 = {
                description: "",
                hidden: false,
                name: "testSyncDest2",
                synced: false,
                tooltip: "Synchronize the grouping's membership with a corresponding GOOGLE-GROUP list, which will be created as needed."
            };
            scope.syncDestArray.push(testSyncDest);
            scope.syncDestArray.push(testSyncDest1);
            scope.syncDestArray.push(testSyncDest2);
        });

        it("should return the entire sync dest object of the testSyncDest", () => {
            let result = scope.getEntireSyncDestInArray(testSyncDest.name);
            expect(result).toEqual(testSyncDest);
        });

        it("should return the entire sync dest object of the testSyncDest1", () => {
            let result = scope.getEntireSyncDestInArray(testSyncDest1.name);
            expect(result).toEqual(testSyncDest1);
        });

        it("should return the entire sync dest object of the testSyncDest2", () => {
            let result = scope.getEntireSyncDestInArray(testSyncDest2.name);
            expect(result).toEqual(testSyncDest2);
        });
    });

    describe("setSyncDestInArray", () => {
        let testSyncDest;
        beforeEach(() => {
            testSyncDest = {
                description: "Google-Group: #uh-iam-group",
                hidden: true,
                name: "testSyncDest",
                synced: false,
                tooltip: "Synchronize the grouping's membership with a corresponding GOOGLE-GROUP list, which will be created as needed."
            };
        });

        it("should set the given sync dest from true to false", () => {
            scope.syncDestArray.push(testSyncDest);
            scope.setSyncDestInArray(testSyncDest.name, true);
            expect(scope.syncDestArray[0].synced).toBeTrue();
        });
        it("should set the given sync dest from false to true", () => {
            scope.syncDestArray.push(testSyncDest);
            scope.setSyncDestInArray(testSyncDest.name, false);
            expect(scope.syncDestArray[0].synced).toBeFalse();
        });
    });

    describe("updateSingleSyncDest", () => {
        let testSync;
        beforeEach(() => {
            testSync = {
                description: "Google-Group: #uh-iam-group",
                hidden: true,
                name: "testSyncDest",
                synced: false,
                tooltip: "Synchronize the grouping's membership with a corresponding GOOGLE-GROUP list, which will be created as needed."
            };
        });

        it("should call the setSyncDest", () => {
            spyOn(gs, "setSyncDest");
            scope.syncDestArray.push(testSync);
            scope.updateSingleSyncDest(testSync.name);
            expect(gs.setSyncDest).toHaveBeenCalled();
        });

        it("should call getSyncDestValueInArray", () => {
            spyOn(scope, "getSyncDestValueInArray").and.callThrough();
            scope.syncDestArray.push(testSync);
            scope.updateSingleSyncDest(testSync.name);
            expect(scope.getSyncDestValueInArray).toHaveBeenCalled();
        });
    });

    describe("createPreferenceErrorModal", () => {
        it("should check that the preferenceErrorModalInstance is launched", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.createPreferenceErrorModal();
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("closePreferenceError", () => {
        beforeEach(() => {
            scope.createPreferenceErrorModal();
        });

        it("should close preferenceErrorModalInstance", () => {
            spyOn(scope.preferenceErrorModalInstance, "close").and.callThrough();
            scope.closePreferenceError();
            expect(scope.preferenceErrorModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("resetFields", () => {

        it("should call getGroupingInformation", () => {
            spyOn(scope, "getGroupingInformation").and.callThrough();
            scope.resetFields();
            expect(scope.getGroupingInformation).toHaveBeenCalled();
        });

        it("should reset userToAdd string", () => {
            scope.userToAdd = "test";
            scope.resetFields();
            expect(scope.userToAdd).toBe("");
        });

        it("should reset membersInCheckboxList array", () => {
            scope.membersInCheckboxList = [1, 2, 3];
            scope.resetFields();
            expect(scope.membersInCheckboxList).toEqual({});
        });

        it("should set allSelected to false", () => {
            scope.allSelected = true;
            scope.resetFields();
            expect(scope.allSelected).toBeFalse();
        });

        it("should set waitingForImportResponse to false", () => {
            scope.waitingForImportResponse = true;
            scope.resetFields();
            expect(scope.waitingForImportResponse).toBeFalse();
        });
    });

    describe("resetErrors", () => {
        it("should set resStatus to 0", () => {
            scope.resStatus = 1;
            scope.resetErrors();
            expect(scope.resStatus).toBe(0);
        });

        it("should set emptyInput to false", () => {
            scope.emptyInput = true;
            scope.resetErrors();
            expect(scope.emptyInput).toBeFalse();
        });

        it("should set emptySelect to false", () => {
            scope.emptySelect = true;
            scope.resetErrors();
            expect(scope.emptySelect).toBeFalse();
        });

        it("should set containsInput to false", () => {
            scope.containsInput = true;
            scope.resetErrors();
            expect(scope.containsInput).toBeFalse();
        });

        it("should inGrouper to false", () => {
            scope.inGrouper = true;
            scope.resetErrors();
            expect(scope.inGrouper).toBeFalse();
        });

        it("should set removeInputError to false", () => {
            scope.removeInputError = true;
            scope.resetErrors();
            expect(scope.removeInputError).toBeFalse();
        });
    });

    describe("addOnClick", () => {
        it("should call addMembers with Include", () => {
            spyOn(scope, "addMembers");
            scope.addOnClick("Include");
            expect(scope.addMembers).toHaveBeenCalledWith("Include");
        });

        it("should call addMembers with Exclude", () => {
            spyOn(scope, "addMembers");
            scope.addOnClick("Exclude");
            expect(scope.addMembers).toHaveBeenCalledWith("Exclude");
        });

        it("should call addMembers with owners", () => {
            spyOn(scope, "addMembers");
            scope.addOnClick("owners");
            expect(scope.addMembers).toHaveBeenCalledWith("owners");
        });

        it("should set errorDismissed to false", () => {
            scope.errorDismissed = true;
            scope.addOnClick("owners");
            expect(scope.errorDismissed).toBeFalse();
        });
    });

    describe("removeOnClick", () => {

        it("should call resetErrors", () => {
            spyOn(scope, "resetErrors");
            scope.removeOnClick("Include");
            expect(scope.resetErrors).toHaveBeenCalled();
        });

        it("should set errorDismissed to false", () => {
            scope.errorDismissed = true;
            scope.removeOnClick("Include");
            expect(scope.errorDismissed).toBeFalse();
        });

        it("should call removeMembers with owners", () => {
            spyOn(scope, "removeMembers");
            scope.removeOnClick("owners");
            expect(scope.removeMembers).toHaveBeenCalledWith("owners");
        });

        it("should call removeMembers with Include", () => {
            spyOn(scope, "removeMembers");
            scope.removeOnClick("Include");
            expect(scope.removeMembers).toHaveBeenCalledWith("Include");
        });

        it("should call removeMembers with Exclude", () => {
            spyOn(scope, "removeMembers");
            scope.removeOnClick("Exclude");
            expect(scope.removeMembers).toHaveBeenCalledWith("Exclude");
        });
    });

    describe("dismissErrors", () => {
        it("should set errorDismissed to true", () => {
            scope.errorDismissed = false;
            scope.dismissErrors();
            expect(scope.errorDismissed).toBeTrue();
        });

        it("should call resetErrors", () => {
            spyOn(scope, "resetErrors");
            scope.dismissErrors();
            expect(scope.resetErrors).toHaveBeenCalled();
        });
    });

    describe("createSyncDestModal", () => {
        let testSyncDest;
        beforeEach(() => {
            testSyncDest = {
                description: "Google-Group: #uh-iam-group",
                hidden: true,
                name: "testSyncDest",
                synced: false,
                tooltip: "Synchronize the grouping's membership with a corresponding GOOGLE-GROUP list, which will be created as needed."
            };
        });

        it("should create syncDestInstance and set selectedSyncDest", () => {
            spyOn(scope, "setSyncDestInArray").and.callThrough();
            spyOn(uibModal, "open").and.callThrough();
            scope.syncDestArray.push(testSyncDest);
            scope.setSyncDestInArray(testSyncDest.name, true);

            scope.createSyncDestModal(testSyncDest.name);

            expect(scope.setSyncDestInArray).toHaveBeenCalled();
            expect(uibModal.open).toHaveBeenCalled();
            expect(scope.selectedSyncDest).toEqual(scope.getEntireSyncDestInArray(testSyncDest.name));
        });
    });

    describe("proceedSyncDestModal", () => {
        beforeEach(() => {
            scope.syncDestInstance = {
                close: () => {
                    // Mock $uib modal close
                }
            };
        });

        it("should close syncDestInstance", () => {
            spyOn(scope.syncDestInstance, "close").and.callThrough();
            scope.proceedSyncDestModal();
            expect(scope.syncDestInstance.close).toHaveBeenCalled();
        });
    });

    describe("closeSyncDestModal", () => {
        beforeEach(() => {
            scope.syncDestInstance = {
                dismiss: () => {
                    // Mock $uib modal dismiss
                }
            };
        });

        it("should dismiss syncDestInstance", () => {
            spyOn(scope.syncDestInstance, "dismiss").and.callThrough();
            scope.closeSyncDestModal();
            expect(scope.syncDestInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe("createOwnerErrorModal", () => {
        it("should set loading to false", () => {
            scope.loading = true;
            scope.createOwnerErrorModal();
            expect(scope.loading).toBeFalse();
        });

        it("should check that the OwnerErrorModalInstance is launched", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.createOwnerErrorModal();
            expect(uibModal.open).toHaveBeenCalled();
        });
    });
    
    describe("convertListToCsv", () => {
        describe("user exports a list with members", () => {
            it("should start with the correct column headers", () => {
                const csv = scope.convertListToCsv(scope.groupingExclude);

                expect(csv.indexOf("Last,First,Username,UH Number,Email\r\n")).toEqual(0);
            });

            it("should contain the information of every member in the list", () => {
                const csv = scope.convertListToCsv(scope.groupingExclude);

                expect(csv).toContain("Four,User,user4,00000004,user4@hawaii.edu");
                expect(csv).toContain("Five,User,user5,00000005,user5@hawaii.edu");
                expect(csv).toContain("Nine,User,,00000009,\r\n");
            });
        });
    });

    describe("showWarningRemovingSelf", () => {
        describe("removing self from a list", () => {
            beforeEach(() => {
                scope.currentUser = {uid: "iamtst01", uhUuid: "iamtst01"};
                scope.membersToRemove = ["iamtst01"];
            });

            it("should warn the user if removing from the owners list", () => {
                scope.listName = "owners";
                expect(scope.showWarningRemovingSelf()).toBe(true);
            });

            it("should warn the user if removing from the admins list", () => {
                scope.listName = "admins";
                expect(scope.showWarningRemovingSelf()).toBe(true);
            });

            it("should not warn the user if removing from the Include list", () => {
                scope.listName = "Include";
                expect(scope.showWarningRemovingSelf()).toBe(false);
            });

            it("should not warn the user if removing from the Exclude list", () => {
                scope.listName = "Exclude";
                expect(scope.showWarningRemovingSelf()).toBe(false);
            });
        });
    });

    describe("getCookie", () => {
        let result;

        it("should return empty string when cookie not found", () => {
            result = scope.getCookie("badCookie");
            expect(result).toEqual("");
        });
    });
    
    describe("proceedRedirectApiError", () => {
        let testWindowLocationHref = "/testURL";
        beforeEach(() => {
            scope.proceedRedirectApiError = {
                create: scope.createApiErrorModal(),
                close: () => {
                    scope.apiErrorModalInstance.close();
                },
                setUrl: () => {
                    testWindowLocationHref = "/uhgroupings/feedback";
                }
            };
        });

        it("should close apiErrorModalInstance", () => {
            spyOn(scope.apiErrorModalInstance, "close").and.callThrough();
            scope.proceedRedirectApiError.close();
            expect(scope.apiErrorModalInstance.close).toHaveBeenCalled();
        });

        it("should set $window.location.href to proper path", () => {
            expect(testWindowLocationHref).toBe("/testURL");
            scope.proceedRedirectApiError.setUrl();
            expect(testWindowLocationHref).toBe("/uhgroupings/feedback");
        });
    });

    describe("proceedRedirect", () => {
        let testWindowLocationHref = "/testURL";
        beforeEach(() => {
            scope.proceedRedirect = {
                create: scope.createOwnerErrorModal(),
                close: () => {
                    scope.OwnerErrorModalInstance.close();
                },
                setUrl: () => {
                    testWindowLocationHref = "/uhgroupings/";
                }
            };
        });

        it("should close OwnerErrorModalInstance", () => {
            spyOn(scope.OwnerErrorModalInstance, "close").and.callThrough();
            scope.proceedRedirect.close();
            expect(scope.OwnerErrorModalInstance.close).toHaveBeenCalled();
        });

        it("should set $window.location.href to proper path", () => {
            testWindowLocationHref = "/badURL";
            expect(testWindowLocationHref).toBe("/badURL");
            scope.proceedRedirect.setUrl();
            expect(testWindowLocationHref).toBe("/uhgroupings/");
        });
    });

    describe("hideColumn", () => {
        let columnToHide;

        afterEach(() => {
            localStorage.clear();
        });

        it("should use the default values", () => {
            localStorage.clear();
            expect(localStorage.getItem("columnDisplaySetting")).toBe(null);
            expect(localStorage.getItem("showDescriptionColumn")).toBe(null);
            expect(localStorage.getItem("showPathColumn")).toBe(null);

            expect(scope.columnDisplaySetting).toBe("first");
            expect(scope.showDescriptionColumn).toBe(true);
            expect(scope.showPathColumn).toBe(false);
        });

        it("should hide the grouping path column", () => {
            columnToHide = "path";
            scope.hideColumn(columnToHide);
            expect(scope.columnDisplaySetting).toBe("first");
            expect(scope.showDescriptionColumn).toBe(true);
            expect(scope.showPathColumn).toBe(false);

            expect(localStorage.getItem("columnDisplaySetting")).toBe("first");
            expect(localStorage.getItem("showDescriptionColumn")).toBe("true");
            expect(localStorage.getItem("showPathColumn")).toBe("false");
        });

        it("should hide the description column", () => {
            columnToHide = "description";
            scope.hideColumn(columnToHide);
            expect(scope.columnDisplaySetting).toBe("second");
            expect(scope.showDescriptionColumn).toBe(false);
            expect(scope.showPathColumn).toBe(true);

            expect(localStorage.getItem("columnDisplaySetting")).toBe("second");
            expect(localStorage.getItem("showDescriptionColumn")).toBe("false");
            expect(localStorage.getItem("showPathColumn")).toBe("true");
        });

        it("should show both grouping path and description columns", () => {
            columnToHide = "none";
            scope.hideColumn(columnToHide);
            expect(scope.columnDisplaySetting).toBe("third");
            expect(scope.showDescriptionColumn).toBe(true);
            expect(scope.showPathColumn).toBe(true);

            expect(localStorage.getItem("columnDisplaySetting")).toBe("third");
            expect(localStorage.getItem("showDescriptionColumn")).toBe("true");
            expect(localStorage.getItem("showPathColumn")).toBe("true");
        });
    });
});
