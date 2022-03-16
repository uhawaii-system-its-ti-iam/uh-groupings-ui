describe("GeneralController", () => {

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    let scope;
    let controller;
    let httpBackend;
    let BASE_URL;
    let gs;
    beforeEach(inject(($rootScope, $controller, _BASE_URL_, _$httpBackend_, groupingsService) => {
        scope = $rootScope.$new();
        controller = $controller("GeneralJsController", {
            $scope: scope
        });
        httpBackend = _$httpBackend_;
        BASE_URL = _BASE_URL_;
        gs = groupingsService;
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
        let mockUser;
        let mockResponse;
        beforeEach(() => {
            mockUser = { username: "jdoe" };
            mockResponse = 999;
            httpBackend.whenGET(BASE_URL + "currentUser")
                .respond(200, mockUser);

            httpBackend.whenGET(BASE_URL + "members/memberships/")
                .respond(200, mockResponse);

            httpBackend.whenGET(BASE_URL + "owners/grouping/")
                .respond(200, mockResponse);
        });

        it("should make an API call to getCurrentUser", () => {
            httpBackend.expectGET(BASE_URL + "currentUser").respond(200, mockUser);
            expect(httpBackend.flush).not.toThrow();
        });

        it("should initialize currentUser", () => {
            httpBackend.expectGET(BASE_URL + "currentUser").respond(200, mockUser);
            httpBackend.flush();

            expect(scope.currentUser).toEqual("jdoe");
        });

        it("should make an API call to getNumberOfMembeships", () => {
            httpBackend.expectGET(BASE_URL + "members/memberships/").respond(200, mockResponse);
            expect(httpBackend.flush).not.toThrow();
        });

        it("should initialize numberOfMemberships", () => {
            httpBackend.expectGET(BASE_URL + "members/memberships/").respond(200, mockResponse);
            httpBackend.flush();

            expect(scope.numberOfMemberships).toEqual(999);
        });

        it("should make an API call to getNumberOfGroupings", () => {
            httpBackend.expectGET(BASE_URL + "owners/grouping/").respond(200, mockResponse);
            expect(httpBackend.flush).not.toThrow();
        });

        it("should initialize numberOfGroupings", () => {
            httpBackend.expectGET(BASE_URL + "owners/grouping/").respond(200, mockResponse);
            httpBackend.flush();

            expect(scope.numberOfGroupings).toEqual(999);
        });
    });

    // Set up mock data
    beforeEach( () => {
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
        beforeEach( () => {
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
            spyOn(scope, "getGroupingInformation");
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
            spyOn(gs, "getSyncDestList");
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
            spyOn(gs, "getGrouping");
            scope.getGroupingInformation();
            expect(gs.getGrouping).toHaveBeenCalled();
        });
        it("should set descriptionLoaded & paginatingProgress to true & paginatingComplete to false after calling gs.getGrouping", () => {
            spyOn(gs, "getGrouping");
            scope.getGroupingInformation();
            //maybe implement this another way??
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
            scope.getPages(pagesOfGrouping.groupingPath, pagesOfGrouping.currentPage, pagesOfGrouping.PAGE_SIZE, pagesOfGrouping.sortString, pagesOfGrouping.isAscending).then((result) => {
                expect(result).toBe(true);
                done();
            });
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
            spyOn(gs, "updateDescription");
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

    describe("closeApiError", () => {
        beforeEach(() => {
           scope.createApiErrorModal();
        });

        it("should close modal", () => {
            spyOn(scope.apiErrorModalInstance, 'close');
            scope.closeApiError();
            expect(scope.apiErrorModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("proceedAddMembers", () => {
       beforeEach(() => {
           scope.launchImportModal();
       });

        it("should close confirmImportInstance modal", () => {
            spyOn(scope.confirmImportInstance, 'close');
            scope.proceedAddMembers();
            expect(scope.confirmImportInstance.close).toHaveBeenCalled();
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

    // describe("launchImportModal", () => {
    //     it ("should check that the import modal is launched", () => {
    //         spyOn(scope, "confirmImportInstance");
    //         scope.launchImportModal("list");
    //             expect(scope.listName).toBe("list");
    //         expect(scope.confirmImportInstance).toHaveBeenCalled();
    //     });
    // });

    describe("validateAndAddUser", () => {
        describe("user adds 'validUser', who is a valid user and is not in any list, to the Include list", () => {
            const validUser = {
                name: "Valid User",
                username: "validUser",
                uhUuid: "00000010",
                firstName: "Valid",
                lastName: "User"
            };
            beforeEach( () => {
                scope.userToAdd = "validUser";
                httpBackend.whenGET(BASE_URL + "currentUser")
                    .respond(200);
                httpBackend.whenGET(BASE_URL + "members/memberships/")
                    .respond(200);
                httpBackend.whenGET(BASE_URL + "owners/grouping/")
                    .respond(200);
                httpBackend.whenGET(BASE_URL + "members/" + validUser.username)
                    .respond(200, validUser);
            });

            it("user should be validated", () => {
                spyOn(scope, "createConfirmAddModal").and.callThrough();
                scope.validateAndAddUser(validUser.username, "Include");
                httpBackend.expectGET(BASE_URL + "members/" + validUser.username)
                    .respond(200, validUser);

                expect(httpBackend.flush).not.toThrow();
                expect(scope.createConfirmAddModal).toHaveBeenCalled();
            });
        });

        describe("user adds 'invalidUser', who is not in the Grouper database", () => {
            const invalidUser = {
                name: null,
                username: null,
                uhUuid: null,
                firstName: null,
                lastName: null
            };
            beforeEach( () => {
                httpBackend.whenGET(BASE_URL + "currentUser")
                    .respond(200);
                httpBackend.whenGET(BASE_URL + "members/memberships/")
                    .respond(200);
                httpBackend.whenGET(BASE_URL + "owners/grouping/")
                    .respond(200);
                httpBackend.whenGET(BASE_URL + "members/" + "invalidUser")
                    .respond(200, invalidUser);
            });

            it("user should be invalidated", () => {
                spyOn(scope, "createCheckModal").and.callThrough();
                scope.validateAndAddUser("invalidUser", "Include");
                httpBackend.expectGET(BASE_URL + "members/" + "invalidUser")
                    .respond(200, invalidUser);

                expect(httpBackend.flush).not.toThrow();
                expect(scope.resStatus).toEqual(404);
                expect(scope.createCheckModal).not.toHaveBeenCalled();
            });
        });
    });

    describe("addMember", () => {
        describe("user adds 'user8', who is not in any list, to the Include list", () => {
            beforeEach( () => {
                scope.userToAdd = "user8";
            });

            it("should create a confirmation modal to add the user", () => {
                spyOn(scope, "createConfirmAddModal").and.callThrough();
                scope.addMember("Include");

                expect(scope.createConfirmAddModal).toHaveBeenCalled();
            });

            it("should not create a modal asking if the user wants to remove 'user8' from a list", () => {
                spyOn(scope, "createCheckModal").and.callThrough();
                scope.addMember("Include");

                expect(scope.createCheckModal).not.toHaveBeenCalled();
            });
        });

        describe("user adds 'user1' to the Exclude list, who is currently in the Include list", () => {
            beforeEach( () => {
                scope.userToAdd = "user1";
            });

            /*
            it("should return true since 'user1' is currently in the Include list", () => {
                spyOn(scope, "isInAnotherList").and.callThrough();
                scope.addMember("Exclude");
                expect(scope.isInAnotherList).toHaveBeenCalled();
                expect(scope.isInAnotherList("user1", "Exclude")).toBe(true);
            });
             */

            it("should create a modal asking if the user wants to remove 'user1' from the Include list", () => {
                    spyOn(scope, "createCheckModal").and.callThrough();
                    scope.addMember("Exclude");
                    expect(scope.createCheckModal).toHaveBeenCalled();
                }
            );
        });

        describe("user tries to add a blank username to a list", () => {
            beforeEach( () => {
                scope.userToAdd = "";
            });

            it("should create an error message saying to enter a username", () => {
                spyOn(scope, "createConfirmAddModal").and.callThrough();
                scope.addMembers("Include");
                expect(scope.emptyInput).toBe(true);
                expect(scope.createConfirmAddModal).not.toHaveBeenCalled();
            });
        });

        describe("user tries to add 'user5', who is currently in the Exclude list, to the Exclude list", () => {
            beforeEach( () => {
                scope.userToAdd = "user5";
            });

            it("should create an error message saying the user already exists in the list", () => {
                spyOn(scope, "existInList").and.callThrough();
                scope.addMember("Include");
                expect(scope.existInList).toHaveBeenCalled();
                expect(scope.existInList("user5", "Exclude")).toBe(true);
            });
        });

        describe("user tries to add 'user7', who is currently in the Basis list, to the Include list", () => {
            beforeEach( () => {
                scope.userToAdd = "user7";
            });

            it("should create a modal asking if the user wants to add 'user7', who is in Basis, in the Include list", () => {
                spyOn(scope, "createBasisWarningModal").and.callThrough();
                scope.addMember("Include");
                expect(scope.createBasisWarningModal).toHaveBeenCalled();
            });
        });

        describe("user tries to add 'user8', who is currently not in the Basis list, to the Exclude list", () => {
            beforeEach( () => {
                scope.userToAdd = "user8";
            });

            it("should create a modal asking if the user wants to add 'user8', who not in Basis, in the Exclude list", () => {
                spyOn(scope, "createBasisWarningModal").and.callThrough();
                scope.addMember("Exclude");
                expect(scope.createBasisWarningModal).toHaveBeenCalled();
            });
        });
    });

    describe("isInAnotherList", () => {
        describe("user tries to add 'user1', who is currently in the Include list, to the Exclude list", () => {
            beforeEach( () => {
                scope.userToAdd = "user1";
            });
            it("should return true since 'user1' is currently in the Include list", () => {
                spyOn(scope, "isInAnotherList").and.callThrough();
                scope.addMember("Exclude");

                //  expect(scope.isInAnotherList).toHaveBeenCalled();
                expect(scope.isInAnotherList("user1", "Exclude")).toBe(true);
            });
        });
        describe("user tries to add 'user5', who is currently in the Exclude list, to the Include list", () => {
            beforeEach( () => {
                scope.userToAdd = "user5";
            });
            it("should return true since 'user5' is currently in the Exclude list", () => {
                spyOn(scope, "isInAnotherList").and.callThrough();
                scope.addMember("Include");

                // expect(scope.isInAnotherList).toHaveBeenCalled();
                expect(scope.isInAnotherList("user5", "Include")).toBe(true);
            });
        });
        describe("user tries to add 'user8', who is not in any list", () => {
            beforeEach( () => {
                scope.userToAdd = "user8";
            });
            it("should return false if the user tries to add 'user8' to the Include list", () => {
                spyOn(scope, "isInAnotherList").and.callThrough();
                scope.addMember("Include");

                //expect(scope.isInAnotherList).not.toHaveBeenCalled();
                expect(scope.isInAnotherList("user8", "Include")).toBe(false);
            });
            it("should return false if the user tries to add 'user8' to the Exclude list", () => {
                spyOn(scope, "isInAnotherList").and.callThrough();
                scope.addMember("Exclude");

                //expect(scope.isInAnotherList).not.toHaveBeenCalled();
                expect(scope.isInAnotherList("user8", "Exclude")).toBe(false);
            });

        });
    });

    describe("existInList", () => {
        describe("user tries to add 'user5', who is currently in the Exclude list, to the Exclude list", () => {
            it("should return true since user5 is being added to the same list", () => {
                expect(scope.existInList("user5", "Exclude")).toBe(true);
            });
        });

        describe("user tries to add 'user1', who is currently in the Include list, to the Include list", () => {
            it("should return true since user1 is being added ot the same list", () => {
                expect(scope.existInList("user1", "Include")).toBe(true);
            });
        });

        describe("user tries to add 'user8', who is not in any list", () => {
            it("should return false if trying the user tries to add 'user8' to the Include list", () => {
                expect(scope.existInList("user8", "Include")).toBe(false);
            });

            it("should return false if trying the user tries to add 'user8' to the Exclude list", () => {
                expect(scope.existInList("user8", "Exclude")).toBe(false);
            });
        });

        describe("user tries to add 'user2', who is currently in the Include list, to the Exclude list", () => {
            it("should return false since 'user2' is not being added to the same list", () => {
                expect(scope.existInList("user2", "Exclude")).toBe(false);
            });
        });

        describe("user tries to add 'user4', who is currently in the Exclude list, to the Include list", () => {
            it("should return false since 'user4' is not being added to the same list", () => {
                expect(scope.existInList("user4", "Include")).toBe(false);
            });
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

    describe("getPersonProps", () => {
        beforeEach( () => {
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
            beforeEach( () => {
                scope.currentUser = "jdoe";
                scope.userToRemove = {
                    username: "jdoe",
                    name: "John Doe",
                    uhUuid: "12345678"
                };
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

    describe("parseAddRemoveInputStr", () => {
        let spaceSeparated = "Hello I love you";
        let commaSeparated = "Hello,I,love,you";
        let commaAndSpaceSeparated = "Hello, I love,you";
        let single = "Hello";

        it("should take a space separated string and replace the spaces with ','", () => {
            expect(scope.parseAddRemoveInputStr(spaceSeparated)).toEqual(commaSeparated);
        });
        it("should take a comma separated string and do nothing", () => {
            expect(scope.parseAddRemoveInputStr(commaSeparated)).toEqual(commaSeparated);
        });
        it("should take a comma and space separated string and replace the spaces amd commas with ','", () => {
            expect(scope.parseAddRemoveInputStr(commaAndSpaceSeparated)).toEqual(commaSeparated);
        });
        it("should do nothing with a string that has no commas or spaces", () => {
            expect(scope.parseAddRemoveInputStr(single)).toEqual(single);
        });
        it("should return an empty string if value passed is not a string", () => {
            expect(scope.parseAddRemoveInputStr(true)).toEqual("");

        });
    });


    describe("initMemberDisplayName", () => {

        it("should set user to fullName if fullName.length > 0", () => {
            let attributes = {
                name: "testName",
                firstName: "testFirstName",
                uhUuid: "testuhUuid",
                username: "testUsername"
            };

            scope.initMemberDisplayName(attributes);
            expect(scope.user).toBe(scope.fullName);
        });

        it("should set user to uid if fullName.Length == 0 and uid > 0", () => {
            let attributes = {
                name: "",
                firstName: "testFirstName",
                uhUuid: "testuhUuid",
                username: "testUsername"
            };

            scope.initMemberDisplayName(attributes);

            expect(scope.user).toBe(scope.uid);
        });

        it("should set user to uhUuid if fullName & uid both == 0", () => {
            let attributes = {
                name: "",
                firstName: "testFirstName",
                uhUuid: "testuhUuid",
                username: ""
            };

            scope.initMemberDisplayName(attributes);

            expect(scope.user).toBe(scope.uhUuid);
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

    describe("resetFields", () => {

        it("should call getGroupingInformation", () => {
            spyOn(scope, "getGroupingInformation");
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

        it("should set swap to true", () => {
            scope.swap = false;
            scope.resetErrors();
            expect(scope.swap).toBeTrue();
        });

        it("should inGrouper to false", () => {
            scope.inGrouper = true;
            scope.resetErrors();
            expect(scope.inGrouper).toBeFalse();
        });
    });

    describe("proceedCheckModal", () => {
       beforeEach(() => {
           scope.createCheckModal('testUser', 'testList','testSwap','testInBasis');
       });
        it("should close the checkModalInstance modal", () => {
            spyOn(scope.checkModalInstance, 'close');
            scope.proceedCheckModal();
            expect(scope.checkModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("closeCheckModal", () => {
        beforeEach(() => {
            scope.createCheckModal('testUser', 'testList','testSwap','testInBasis');
        });
        it("should dismiss the checkModalInstance modal", () => {
            spyOn(scope.checkModalInstance, 'dismiss');
            scope.closeCheckModal();
            expect(scope.checkModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe("proceedConfirmAddUser", () => {
       beforeEach(() => {
           scope.createConfirmAddMembersModal(scope.listName);
       });

        it("should close confirmAddModalInstance", () => {
            spyOn(scope.confirmAddModalInstance, 'close');
            scope.proceedConfirmAddUser();
            expect(scope.confirmAddModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("cancelConfirmAddUser", () => {
       beforeEach(() => {
          scope.createConfirmAddMembersModal(scope.listName);
       });

        it("should dismiss confirmAddModalInstance", () => {
            spyOn(scope.confirmAddModalInstance, 'dismiss');
            scope.cancelConfirmAddUser();
            expect(scope.confirmAddModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe("closeSuccessfulAddModal", () => {
       beforeEach(() => {
           scope.createSuccessfulAddModal('testString');
       });

        it("should close addModalInstance", () => {
            spyOn(scope.addModalInstance, 'close');
            scope.closeSuccessfulAddModal();
            expect(scope.addModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("closeResetNotifModal", () => {
       beforeEach(() => {
           scope.createResetNotifModal(scope.group);
       });

        it("should close resetNotifModalInstance", () => {
            spyOn(scope.resetNotifModalInstance, 'close');
            scope.closeResetNotifModal();
            expect(scope.resetNotifModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("batchRemovePromptModalAccept", () => {
       beforeEach(() => {
           scope.multiRemovePromptModalInstance = {
               close: () => {
                   // Mock $uib modal close
               },
           };
       });

        it("should close batchRemovePromptModalAccept", () => {
            spyOn(scope.multiRemovePromptModalInstance, 'close');
            scope.batchRemovePromptModalAccept();
            expect(scope.multiRemovePromptModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("batchRemovePromptModalCancel", () => {
        beforeEach(() => {
            scope.multiRemovePromptModalInstance = {
                // Mock $uib dismiss
                dismiss: ('cancel'),
            };
        });

        it("should dismiss multiRemovePromptModalInstance", () => {
            spyOn(scope.multiRemovePromptModalInstance, 'dismiss');
            scope.batchRemovePromptModalCancel();
            expect(scope.multiRemovePromptModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe("closeBatchRemoveConfirmationModalInstance", () => {
       beforeEach(() => {
          scope.batchRemoveConfirmationModal(scope.listName);
       });

        it("should close multiRemoveConfirmationModalInstance", () => {
            spyOn(scope.multiRemoveConfirmationModalInstance, 'close');
            scope.closeBatchRemoveConfirmationModalInstance();
            expect(scope.multiRemoveConfirmationModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("proceedRemoveUser", () => {
       beforeEach(() => {
           scope.removeModalInstance = {
               close: {
                   // Mock $uib modal close
               },
           };
       });

        it("should close removeModalInstance", () => {
            spyOn(scope.removeModalInstance, 'close');
            scope.proceedRemoveUser();
            expect(scope.removeModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("extractSelectedUsersFromCheckboxes",  () => {
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
});