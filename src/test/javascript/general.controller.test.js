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
        it("should set listName to the listName that we are passing in", () => {
            scope.listName = "admin";
            scope.addMembers(scope.listName);
            expect((scope.listName)).toBe("admin");
        });

        it("should make scope.emptyInput true if scope.manageMembers is empty", () => {
            scope.emptyInput = false;
            scope.manageMembers = null;
            scope.addMembers(scope.listName);
            expect(scope.emptyInput).toBeTrue();
        });

        it("should call addMultipleMembers when the usersToAdd is below our maxImport", () => {
            spyOn(scope, "addMultipleMembers");
            scope.manageMembers = "iamtst01 iamtst02";
            scope.addMembers(scope.listName);
            expect(scope.addMultipleMembers).toHaveBeenCalled();
        });

        it("should call launchDynamicModal when the usersToAdd is above the multiAddThreshold", () => {
            spyOn(scope, "launchDynamicModal");
            let arr = [];
            for (let i = 0; i < 102; i++) {
                arr.push("iamtst01");
            }
            scope.manageMembers = arr.toString().split(",").join(" ");
            scope.addMembers(scope.listName);
            expect(scope.launchDynamicModal).toHaveBeenCalled();
        });

        it("should call launchDynamicModal when the members we are adding are above the maxImport", () => {
            spyOn(scope, "launchDynamicModal");
            let arr = [];
            for (let i = 0; i < 100002; i++) {
                arr.push("iamtst01");
            }
            scope.manageMembers = arr.toString().split(",").join(" ");
            scope.addMembers(scope.listName);
            expect(scope.launchDynamicModal).toHaveBeenCalled();
        });

        it("should call validateAndAddUser when numMembers is less than 0", () => {
            spyOn(scope, "validateAndAddUser");
            scope.manageMembers = "iamtst01";
            scope.addMembers(scope.listName);
            expect(scope.manageMembers).toBe("iamtst01");
            expect(scope.validateAndAddUser).toHaveBeenCalled();
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

    describe("successfulAddHandler", () => {
        let membersToAdd,
            res,
            invalidRes,
            duplicateRes,
            validPeople,
            invalidMembersToAdd,
            duplicateMembersToAdd,
            duplicatePeople;
        beforeEach(() => {
            membersToAdd = "iamtst01,iamtst02,iamtst03,iamtst04";
            invalidMembersToAdd = "invalidName01,invalidName02,invalidName03,invalidName04";
            duplicateMembersToAdd = "iamtst01,iamtst01,iamtst03";
            res = [
                {
                    name: "iamtst01",
                    pathOfAdd: "hawaii.edu:custom:test:grouping-path:include",
                    pathOfRemoved: "hawaii.edu:custom:test:grouping-path:exclude",
                    result: "SUCCESS",
                    uhUuid: "iamtst01",
                    uid: "iamtst01",
                    userIdentifier: "iamtst01",
                    userWasAdded: true,
                    userWasRemoved: false
                },
                {
                    name: "iamtst02",
                    pathOfAdd: "hawaii.edu:custom:test:grouping-path:include",
                    pathOfRemoved: "hawaii.edu:custom:test:grouping-path:exclude",
                    result: "SUCCESS",
                    uhUuid: "iamtst02",
                    uid: "iamtst02",
                    userIdentifier: "iamtst02",
                    userWasAdded: true,
                    userWasRemoved: false
                },
                {
                    name: "iamtst03",
                    pathOfAdd: "hawaii.edu:custom:test:grouping-path:include",
                    pathOfRemoved: "hawaii.edu:custom:test:grouping-path:exclude",
                    result: "SUCCESS",
                    uhUuid: "iamtst03",
                    uid: "iamtst03",
                    userIdentifier: "iamtst03",
                    userWasAdded: true,
                    userWasRemoved: false
                },
                {
                    name: "iamtst04",
                    pathOfAdd: "hawaii.edu:custom:test:grouping-path:include",
                    pathOfRemoved: "hawaii.edu:custom:test:grouping-path:exclude",
                    result: "SUCCESS",
                    uhUuid: "iamtst04",
                    uid: "iamtst04",
                    userIdentifier: "iamtst04",
                    userWasAdded: true,
                    userWasRemoved: false
                }
            ];
            duplicateRes = [
                {
                    name: "iamtst01",
                    pathOfAdd: "hawaii.edu:custom:test:grouping-path:include",
                    pathOfRemoved: "hawaii.edu:custom:test:grouping-path:exclude",
                    result: "SUCCESS",
                    uhUuid: "iamtst01",
                    uid: "iamtst01",
                    userIdentifier: "iamtst01",
                    userWasAdded: true,
                    userWasRemoved: false
                },
                {
                    name: "iamtst01",
                    pathOfAdd: "hawaii.edu:custom:test:grouping-path:include",
                    pathOfRemoved: "hawaii.edu:custom:test:grouping-path:exclude",
                    result: "SUCCESS",
                    uhUuid: "iamtst01",
                    uid: "iamtst01",
                    userIdentifier: "iamtst01",
                    userWasAdded: false,
                    userWasRemoved: false
                },
                {
                    name: "iamtst03",
                    pathOfAdd: "hawaii.edu:custom:test:grouping-path:include",
                    pathOfRemoved: "hawaii.edu:custom:test:grouping-path:exclude",
                    result: "SUCCESS",
                    uhUuid: "iamtst03",
                    uid: "iamtst03",
                    userIdentifier: "iamtst03",
                    userWasAdded: true,
                    userWasRemoved: false
                }
            ];
            invalidRes = [
                {
                    name: "invalidName01",
                    pathOfAdd: "hawaii.edu:custom:test:grouping-path:include",
                    pathOfRemoved: "hawaii.edu:custom:test:grouping-path:exclude",
                    result: "FAILURE",
                    uhUuid: "invalidName01",
                    uid: "invalidName01",
                    userIdentifier: "invalidName01",
                    userWasAdded: false,
                    userWasRemoved: false
                },
                {
                    name: "invalidName02",
                    pathOfAdd: "hawaii.edu:custom:test:grouping-path:include",
                    pathOfRemoved: "hawaii.edu:custom:test:grouping-path:exclude",
                    result: "FAILURE",
                    uhUuid: "invalidName02",
                    uid: "invalidName02",
                    userIdentifier: "invalidName02",
                    userWasAdded: false,
                    userWasRemoved: false
                },
                {
                    name: "invalidName03",
                    pathOfAdd: "hawaii.edu:custom:test:grouping-path:include",
                    pathOfRemoved: "hawaii.edu:custom:test:grouping-path:exclude",
                    result: "FAILURE",
                    uhUuid: "invalidName03",
                    uid: "invalidName03",
                    userIdentifier: "invalidName03",
                    userWasAdded: false,
                    userWasRemoved: false
                },
                {
                    name: "invalidName04",
                    pathOfAdd: "hawaii.edu:custom:test:grouping-path:include",
                    pathOfRemoved: "hawaii.edu:custom:test:grouping-path:exclude",
                    result: "FAILURE",
                    uhUuid: "invalidName04",
                    uid: "invalidName04",
                    userIdentifier: "invalidName04",
                    userWasAdded: true,
                    userWasRemoved: false
                }
            ];
            validPeople = [
                {
                    name: "iamtst01",
                    uhUuid: "iamtst01",
                    uid: "iamtst01"
                },
                {
                    name: "iamtst02",
                    uhUuid: "iamtst02",
                    uid: "iamtst02"
                },
                {
                    name: "iamtst03",
                    uhUuid: "iamtst03",
                    uid: "iamtst03"
                },
                {
                    name: "iamtst04",
                    uhUuid: "iamtst04",
                    uid: "iamtst04"
                }
            ];
            duplicatePeople = [
                {
                    name: "iamtst01",
                    uhUuid: "iamtst01",
                    uid: "iamtst01"
                },
                {
                    name: "iamtst03",
                    uhUuid: "iamtst03",
                    uid: "iamtst03"
                }
            ];
        });

        it("should return a list of the members to be imported to the include list", () => {
            scope.multiAddResults = [];
            scope.multiAddResultsGeneric = [];
            scope.successfulAddHandler(res, membersToAdd, "Include");
            expect(scope.multiAddResults.length).toEqual(4);
            expect(scope.multiAddResultsGeneric.length).toEqual(4);
            expect(scope.multiAddResults).toEqual(validPeople);
            expect(scope.multiAddResultsGeneric).toEqual(validPeople);
        });

        it("should return a list of invalid users and 0 members to be imported", () => {
            scope.membersNotInList = [];
            scope.successfulAddHandler(invalidRes, invalidMembersToAdd, "Include");
            expect(scope.membersNotInList).toEqual("invalidName01, invalidName02, invalidName03, invalidName04");
            expect(scope.multiAddResults).toEqual([]);
        });

        it("should not add a duplicate user to the invalid list", () => {
            scope.membersNotInList = [];
            scope.successfulAddHandler(duplicateRes, duplicateMembersToAdd, "Include");
            expect(scope.membersNotInList).toEqual("iamtst01");
            expect(scope.multiAddResults).toEqual(duplicatePeople);
            expect(scope.multiAddResultsGeneric).toEqual(duplicatePeople);
        });

        it("should call the launchMultiAddResultModal if the multiAddResults length is > 0", () => {
            spyOn(scope, "launchMultiAddResultModal");
            // Add people to the multiAddResults array
            scope.successfulAddHandler(res, membersToAdd, "Include");
            expect(scope.multiAddResults.length).toBe(4);
            expect(scope.launchMultiAddResultModal).toHaveBeenCalled();

        });

        it("should call the launchDynamicModal if the multiAddResults length is < 0", () => {
            let noResToAdd = [];
            let noMembersToAdd = "";
            scope.multiAddResults = [];
            spyOn(scope, "launchDynamicModal");
            scope.successfulAddHandler(noResToAdd, noMembersToAdd, "Include");
            expect(scope.launchDynamicModal).toHaveBeenCalled();
        });
    });

    describe("addMultipleMembers", () => {
        let membersToAdd;
        beforeEach(() => {
            membersToAdd = "iamtst01,iamtst02,iamtst03,iamtst04";
        });
        it("should return a call to addMembersToIncludeAsync", () => {
            spyOn(gs, "addMembersToIncludeAsync");
            scope.addMultipleMembers(membersToAdd, "Include");
            expect(gs.addMembersToIncludeAsync).toHaveBeenCalled();
        });

        it("should turn the small spinner off", () => {
            scope.addMultipleMembers(membersToAdd, "admin");
            expect(scope.waitingForImportResponse).toBeTrue();
        });

        describe("add user to Include and Exclude list", () => {
            it("should return a call to addMembersToExcludeAsync", () => {
                spyOn(gs, "addMembersToExcludeAsync");
                scope.addMultipleMembers(membersToAdd, "Exclude");
                expect(gs.addMembersToExcludeAsync).toHaveBeenCalled();
            });

            it("should return a call to addMembersToIncludeAsync", () => {
                spyOn(gs, "addMembersToIncludeAsync");
                scope.addMultipleMembers(membersToAdd, "Include");
                expect(gs.addMembersToIncludeAsync).toHaveBeenCalled();
            });
        });
    });

    describe("launchMultiAddResultModal", () => {
        it("should check that the multiAddResultModalInstance is opened", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.launchMultiAddResultModal(scope.listName);
            expect(uibModal.open).toHaveBeenCalled();
        });

        it("should set scope.loading to false", () => {
            scope.loading = true;
            scope.launchMultiAddResultModal(scope.listName);
            expect(scope.loading).toBeFalse();
        });
    });

    describe("launchImportErrorModal", () => {
        beforeEach(() => {
            scope.confirmImportInstance = {
                dismiss: () => {
                    // Mock $uib modal dismiss
                }
            };
        });

        it("should check that confirmImportErrorInstance is launched", () => {
            scope.launchMultiAddResultModal("testListName");
            spyOn(scope.confirmImportInstance, "dismiss").and.callThrough();
            spyOn(uibModal, "open");

            scope.launchImportErrorModal();

            expect(scope.confirmImportInstance.dismiss).toHaveBeenCalled();
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("closeMultiAddResultInstance", () => {
        beforeEach(() => {
            scope.launchMultiAddResultModal("testListName");
        });

        it("should dismiss closeMultiAddResultInstance", () => {
            spyOn(scope.multiAddResultModalInstance, "dismiss").and.callThrough();
            scope.closeMultiAddResultInstance();
            expect(scope.multiAddResultModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe("cancelImportModalInstance", () => {
        beforeEach(() => {
            scope.launchImportModal(scope.listName);
        });

        it("should dismiss confirmImportInstance", () => {
            spyOn(scope.confirmImportInstance, "dismiss").and.callThrough();
            scope.cancelImportModalInstance();
            expect(scope.confirmImportInstance.dismiss).toHaveBeenCalled();
        });
    });
    
    describe("proceedAddMembers", () => {
        beforeEach(() => {
            scope.launchImportModal();
        });

        it("should close confirmImportInstance modal", () => {
            spyOn(scope.confirmImportInstance, "close").and.callThrough();
            scope.proceedAddMembers();
            expect(scope.confirmImportInstance.close).toHaveBeenCalled();
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

    describe("addMember", () => {
        describe("user adds 'user8', who is not in any list, to the Include list", () => {
            beforeEach(() => {
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
            beforeEach(() => {
                scope.userToAdd = "user1";
            });

            it("should create a modal asking if the user wants to remove 'user1' from the Include list", () => {
                    spyOn(scope, "createCheckModal").and.callThrough();
                    scope.addMember("Exclude");
                    expect(scope.createCheckModal).toHaveBeenCalled();
                }
            );
        });

        describe("user tries to add a blank username to a list", () => {
            beforeEach(() => {
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
            beforeEach(() => {
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
            beforeEach(() => {
                scope.userToAdd = "user7";
            });

            it("should create a modal asking if the user wants to add 'user7', who is in Basis, in the Include list", () => {
                spyOn(scope, "createBasisWarningModal").and.callThrough();
                scope.addMember("Include");
                expect(scope.createBasisWarningModal).toHaveBeenCalled();
            });
        });

        describe("user tries to add 'user8', who is currently not in the Basis list, to the Exclude list", () => {
            beforeEach(() => {
                scope.userToAdd = "user8";
            });

            it("should create a modal asking if the user wants to add 'user8', who not in Basis, in the Exclude list", () => {
                spyOn(scope, "createBasisWarningModal").and.callThrough();
                scope.addMember("Exclude");
                expect(scope.createBasisWarningModal).toHaveBeenCalled();
            });
        });
    });

    describe("updateAddMember", () => {
        it("should call the addMembersToInclude groupingsService call when list is Include", () => {
            spyOn(gs, "addMembersToInclude");
            scope.listName = "Include";
            scope.updateAddMember("iamtst01", scope.listName);
            expect(gs.addMembersToInclude).toHaveBeenCalled();
        });

        it("should call the addMembersToExclude groupingsService call when list is Exclude", () => {
            spyOn(gs, "addMembersToExclude");
            scope.listName = "Exclude";
            scope.updateAddMember("iamtst01", scope.listName);
            expect(gs.addMembersToExclude).toHaveBeenCalled();
        });

        it("should call the addOwnerships groupingsService call when list is owners", () => {
            spyOn(gs, "addOwnerships");
            scope.listName = "owners";
            scope.updateAddMember("iamtst01", scope.listName);
            expect(gs.addOwnerships).toHaveBeenCalled();
        });

        it("should call the addAdmin groupingsService call when list is admins", () => {
            spyOn(gs, "addAdmin");
            scope.listName = "admins";
            scope.updateAddMember("iamtst01", scope.listName);
            expect(gs.addAdmin).toHaveBeenCalled();
        });
    });

    describe("isInAnotherList", () => {
        describe("user tries to add 'user1', who is currently in the Include list, to the Exclude list", () => {
            beforeEach(() => {
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
            beforeEach(() => {
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
            beforeEach(() => {
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

    describe("createCheckModal", () => {
        beforeEach(() => {
            spyOn(uibModal, "open").and.callThrough();
            scope.createCheckModal("testUser", "testListName", "testSwap", "testBasis");
        });

        it("should set scope.list name to passed in list name", () => {
            expect(scope.listName).toBe("testListName");
        });

        it("should set scope.swap to passed in swap", () => {
            expect(scope.swap).toBe("testSwap");
        });

        it("should set scope.inBasis to passed in inBasis", () => {
            expect(scope.inBasis).toBe("testBasis");
        });

        it("should open uibModal", () => {
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("createConfirmAddMembersModal", () => {
        let options = { manageMembers: "testMembers", listName: "testList" };

        it("should open confirmAddModalInstance modal", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.createConfirmAddMembersModal(options);
            expect(uibModal.open).toHaveBeenCalled();
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
    
    describe("validateAndAddUser", () => {
        describe("user adds 'validUser', who is a valid user and is not in any list, to the Include list", () => {
            const validUser = {
                name: "Valid User",
                username: "validUser",
                uhUuid: "00000010",
                firstName: "Valid",
                lastName: "User"
            };
            beforeEach(() => {
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
            beforeEach(() => {
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

    describe("createConfirmAddModal", () => {
        let options = { userToAdd: "testUser", listName: "testList" };

        it("should call for groupings service getMemberAttributes", () => {
            spyOn(gs, "getMemberAttributes").and.callThrough();
            scope.createConfirmAddModal(options);
            expect(gs.getMemberAttributes).toHaveBeenCalled();
        });

        it("should set scope.listName top passed in options.listName", () => {
            scope.createConfirmAddModal(options);
            expect(scope.listName).toEqual("testList");
        });
    });

    describe("proceedCheckModal", () => {
        beforeEach(() => {
            scope.createCheckModal("testUser", "testList", "testSwap", "testInBasis");
        });
        it("should close the checkModalInstance modal", () => {
            spyOn(scope.checkModalInstance, "close").and.callThrough();
            scope.proceedCheckModal();
            expect(scope.checkModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("closeCheckModal", () => {
        beforeEach(() => {
            scope.createCheckModal("testUser", "testList", "testSwap", "testInBasis");
        });
        it("should dismiss the checkModalInstance modal", () => {
            spyOn(scope.checkModalInstance, "dismiss").and.callThrough();
            scope.closeCheckModal();
            expect(scope.checkModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe("proceedConfirmAddUser", () => {
        beforeEach(() => {
            scope.createConfirmAddMembersModal(scope.listName);
        });

        it("should close confirmAddModalInstance", () => {
            spyOn(scope.confirmAddModalInstance, "close").and.callThrough();
            scope.proceedConfirmAddUser();
            expect(scope.confirmAddModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("cancelConfirmAddUser", () => {
        beforeEach(() => {
            scope.createConfirmAddMembersModal(scope.listName);
        });

        it("should dismiss confirmAddModalInstance", () => {
            spyOn(scope.confirmAddModalInstance, "dismiss").and.callThrough();
            scope.cancelConfirmAddUser();
            expect(scope.confirmAddModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe("addOwners", () => {
        beforeEach(() => {
            scope.listName = "owners";
        });

        it("should set the emptyInput to true", () => {
            scope.manageMembers = [];
            scope.addOwners();
            expect(scope.emptyInput).toBeTrue();
        });

        it("should make scope.emptyInput true if scope.manageMembers is empty", () => {
            scope.emptyInput = false;
            scope.manageMembers = null;
            scope.addOwners(scope.listName);
            expect(scope.emptyInput).toBeTrue();
        });

        it("should call addMultipleMembers when the usersToAdd is below our maxImport", () => {
            spyOn(scope, "addMultipleMembers");
            scope.manageMembers = "iamtst01 iamtst02";
            scope.addOwners(scope.listName);
            expect(scope.addMultipleMembers).toHaveBeenCalled();
        });

        it("should call launchDynamicModal when the usersToAdd is above the multiAddThreshold", () => {
            spyOn(scope, "launchDynamicModal");
            let arr = [];
            for (let i = 0; i < 102; i++) {
                arr.push("iamtst01");
            }
            scope.manageMembers = arr.toString().split(",").join(" ");
            scope.addOwners(scope.listName);
            expect(scope.launchDynamicModal).toHaveBeenCalled();
        });

        it("should call validateAndAddUser when numMembers is less than 0", () => {
            spyOn(scope, "validateAndAddUser");
            scope.manageMembers = "iamtst01";
            scope.addOwners(scope.listName);
            expect(scope.userToAdd).toBe("iamtst01");
            expect(scope.validateAndAddUser).toHaveBeenCalled();
        });
    });

    describe("createSuccessfulAddModal", () => {
        let options = { userToAdd: "testUser", response: "testResponse", listName: "testList" };

        it("should set scope.listName to passed in option object's listName", () => {
            scope.createSuccessfulAddModal(options);
            expect(scope.listName).toBe("testList");
        });

        it("should open addModalInstance modal", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.createSuccessfulAddModal(options);
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("closeSuccessfulAddModal", () => {
        beforeEach(() => {
            scope.createSuccessfulAddModal("testString");
        });

        it("should close addModalInstance", () => {
            spyOn(scope.addModalInstance, "close").and.callThrough();
            scope.closeSuccessfulAddModal();
            expect(scope.addModalInstance.close).toHaveBeenCalled();
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
        it("should call the createRemoveModal", () => {
            let listName = "Include";
            spyOn(scope, "createRemoveModal");
            scope.removeMemberWithTrashcan(listName, 0, 0);
            expect(scope.createRemoveModal).toHaveBeenCalled();
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
            expect(scope.personProps).toEqual(["uid", "uhUuid", "name"]);
        });

        it("should return true when all members are in the list", () => {
            members = ["iamtst01", "iamtst02"];
            expect(scope.fetchMemberProperties(members, "Include")).toBeTrue();
            expect(scope.membersNotInList).toEqual("");
            expect(scope.personProps).toEqual(["uid", "uhUuid", "name"]);
        });

        it("should return true although some members are not in the list", () => {
            members = ["iamtst01", "iamtst03", "iamtst04"];
            expect(scope.fetchMemberProperties(members, "Include")).toBeTrue();
            expect(scope.membersNotInList).toEqual("iamtst03, iamtst04");
            expect(scope.personProps).toEqual(["uid", "uhUuid", "name"]);
        });

        it("should return false when members are not in the list", () => {
            members = ["iamtst03", "iamtst04"];
            expect(scope.fetchMemberProperties(members, "Include")).toBeFalse();
            expect(scope.membersNotInList).toEqual("iamtst03, iamtst04");
            expect(scope.personProps).toEqual([]);
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

        it("should call createRemoveModal", () => {
            scope.groupingInclude = [{
                name: "iamtst01",
                username: "iamtst01",
                uhUuid: "iamtst01"
            }];
            scope.manageMembers = "iamtst01";
            spyOn(scope, "createRemoveModal");
            spyOn(scope, "launchDynamicModal");
            scope.removeMembers("Include");
            expect(scope.membersToModify).toEqual("iamtst01");
            expect(scope.createRemoveModal).toHaveBeenCalled();
        });

        it("should call launchDynamicModal when member does not exist in list", () => {
            scope.groupingInclude = [{
                name: "iamtst01",
                username: "iamtst01",
                uhUuid: "iamtst01"
            }];
            scope.manageMembers = "iamtst02";
            spyOn(scope, "createRemoveModal");
            spyOn(scope, "launchDynamicModal");
            scope.removeMembers("Include");
            expect(scope.membersToModify).toEqual("iamtst02");
            expect(scope.createRemoveModal).not.toHaveBeenCalled();
            expect(scope.launchDynamicModal).toHaveBeenCalled();
        });

        it("should call the createRemoveErrorModal when the listName is owners", () => {
            scope.groupingOwners = [{
                name: "iamtst03",
                username: "iamtst03",
                uhUuid: "iamtst03"
            }];
            scope.manageMembers = "iamtst03";
            spyOn(scope, "createRemoveErrorModal");
            scope.removeMembers("owners");
            expect(scope.createRemoveErrorModal).toHaveBeenCalled();
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
            spyOn(scope, "createRemoveModal");
            scope.removeOwnerWithTrashcan(0, 0);
            expect(scope.createRemoveModal).toHaveBeenCalled();
        });

        it("should create the remove error modal if groupingOwners < 1", () => {
            scope.groupingOwners = [{
                name: "iamtst01",
                username: "iamtst01",
                uhUuid: "iamtst01"
            }];
            spyOn(scope, "createRemoveErrorModal");
            scope.removeOwnerWithTrashcan(0, 0);
            expect(scope.createRemoveErrorModal).toHaveBeenCalledWith("owner");
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

    describe("createRemoveModal", () => {
        let options;
        let mockModal = {
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
            scope.createRemoveModal(options);
            expect(scope.removeInputError).toBeTrue();
        });

        it("should open uibModal with removeModal template when removing 1 user", () => {
            spyOn(scope, "initMemberDisplayName");
            spyOn(uibModal, "open").and.returnValue(mockModal);

            options = { members: { name: "iamtst01", username: "iamtst01", uhUuid: "iamtst01" }, listName: "Include" };
            scope.createRemoveModal(options);
            expect(scope.initMemberDisplayName).toHaveBeenCalled();
            expect(scope.memberToRemove).toEqual("iamtst01");
            expect(scope.isMultiRemove).toBeFalse();
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/removeModal",
                windowClass: "",
                backdrop: "static",
                scope
            });
            spyOn(gs, "removeMembersFromInclude").and.callThrough();
            scope.removeModalProceed();
            expect(gs.removeMembersFromInclude).toHaveBeenCalled();

            options = { members: ["iamtst03"], listName: "Exclude" };
            scope.createRemoveModal(options);
            expect(scope.initMemberDisplayName).toHaveBeenCalled();
            expect(scope.memberToRemove).toEqual("iamtst03");
            expect(scope.isMultiRemove).toBeFalse();
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/removeModal",
                windowClass: "",
                backdrop: "static",
                scope
            });
            spyOn(gs, "removeMembersFromExclude").and.callThrough();
            scope.removeModalProceed();
            expect(gs.removeMembersFromExclude).toHaveBeenCalled();
        });

        it("should open uibModal with multiRemoveModal template when removing more than 1 user", () => {
            spyOn(uibModal, "open").and.returnValue(mockModal);
            spyOn(gs, "removeMembersFromInclude").and.callThrough();
            spyOn(gs, "removeMembersFromExclude").and.callThrough();

            options = { members: ["iamtst01", "iamtst02"], listName: "Include" };
            scope.createRemoveModal(options);
            expect(scope.membersToRemove).toEqual(["iamtst01", "iamtst02"]);
            expect(scope.isMultiRemove).toBeTrue();
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/multiRemoveModal",
                windowClass: "",
                backdrop: "static",
                scope
            });
            scope.removeModalProceed();
            expect(gs.removeMembersFromInclude).toHaveBeenCalled();

            options = { members: ["iamtst03", "iamtst04"], listName: "Exclude" };
            scope.createRemoveModal(options);
            expect(scope.membersToRemove).toEqual(["iamtst03", "iamtst04"]);
            expect(scope.isMultiRemove).toBeTrue();
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/multiRemoveModal",
                windowClass: "",
                backdrop: "static",
                scope
            });
            scope.removeModalProceed();
            expect(gs.removeMembersFromExclude).toHaveBeenCalled();
        });


        it("should show warning in uibModal when removing the currentUser", () => {
            spyOn(uibModal, "open").and.returnValue(mockModal);
            spyOn(gs, "removeMembersFromInclude").and.callThrough();

            options = { members: { name: "iamtst01", username: "iamtst01", uhUuid: "iamtst01" }, listName: "Include" };
            scope.currentUser = "iamtst01";
            scope.createRemoveModal(options);
            expect(scope.memberToRemove).toEqual("iamtst01");
            expect(scope.isMultiRemove).toBeFalse();
            expect(scope.showWarningRemovingSelfFromList()).toBeTrue();
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/removeModal",
                windowClass: "",
                backdrop: "static",
                scope
            });
            scope.removeModalProceed();
            expect(gs.removeMembersFromInclude).toHaveBeenCalled();
        });

        it("should have uibModal open with windowClass modal-danger when removing the owner from owners list", () => {
            spyOn(uibModal, "open").and.returnValue(mockModal);
            spyOn(gs, "removeOwnerships").and.callThrough();

            options = { members: { name: "iamtst05", username: "iamtst05", uhUuid: "iamtst05" }, listName: "owners" };
            scope.currentUser = "iamtst05";
            scope.createRemoveModal(options);
            expect(scope.memberToRemove).toEqual("iamtst05");
            expect(scope.isMultiRemove).toBeFalse();
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/removeModal",
                windowClass: "modal-danger",
                backdrop: "static",
                scope
            });
            scope.removeModalProceed();
            expect(gs.removeOwnerships).toHaveBeenCalled();

            options = { members: ["iamtst05", "iamtst06"], listName: "owners" };
            scope.createRemoveModal(options);
            expect(scope.membersToRemove).toEqual(["iamtst05", "iamtst06"]);
            expect(scope.isMultiRemove).toBeTrue();
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/multiRemoveModal",
                windowClass: "modal-danger",
                backdrop: "static",
                scope
            });
            scope.removeModalProceed();
            expect(gs.removeOwnerships).toHaveBeenCalled();
        });
    });

    describe("removeModalProceed", () => {
        beforeEach(() => {
            scope.removeModalInstance = {
                close: () => {
                    // Mock $uib modal close
                }
            };
        });

        it("should close removeModalInstance", () => {
            spyOn(scope.removeModalInstance, "close").and.callThrough();
            scope.removeModalProceed();
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

    describe("removeModalCancel", () => {
        beforeEach(() => {
            scope.removeModalInstance = {
                dismiss: () => {
                    // Mock $uib modal dismiss
                }
            };
        });

        it("should dismiss removeModalInstance", () => {
            spyOn(scope.removeModalInstance, "dismiss").and.callThrough();
            scope.removeModalCancel();
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

    describe("createRemoveErrorModal", () => {
        it("should set userType to userType that is passed in", () => {
            expect(scope.userType).toBeUndefined();
            scope.createRemoveErrorModal("admin");
            expect(scope.userType).toBe("admin");
        });

        it("should check that the removeErrorModalInstance is launched", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.createRemoveErrorModal("test");
            expect(uibModal.open).toHaveBeenCalled();
        });
    });
    
    describe("closeRemoveErrorModal", () => {
        beforeEach(() => {
            scope.createRemoveErrorModal("testString");
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

        it("should set removeInputError to false", () => {
            scope.removeInputError = true;
            scope.resetErrors();
            expect(scope.removeInputError).toBeFalse();
        });
    });

    describe("addOnClick", () => {
        it("should call addOwners", () => {
            spyOn(scope, "addOwners");
            scope.addOnClick("owners");
            expect(scope.addOwners).toHaveBeenCalled();
        });

        it("should call addMembers with Include", () => {
            spyOn(scope, "addMembers");
            scope.addOnClick("Include");
            expect(scope.addMembers).toHaveBeenCalledWith("Include", scope.manageMembers);
        });

        it("should call addMembers with Exclude", () => {
            spyOn(scope, "addMembers");
            scope.addOnClick("Exclude");
            expect(scope.addMembers).toHaveBeenCalledWith("Exclude", scope.manageMembers);
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

    describe("createBasisWarningModal", () => {
        it("should set scope.listName to passed in listName", () => {
            scope.createBasisWarningModal("testUser", "testList", "testBasis");
            expect(scope.listName).toEqual("testList");
        });

        it("should set scope.inBasis to passed in basis", () => {
            scope.createBasisWarningModal("testUser", "testList", "testBasis");
            expect(scope.inBasis).toEqual("testBasis");
        });

        it("should open basisWarningModalInstance modal", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.createBasisWarningModal("testUser", "testList", "testBasis");
            expect(uibModal.open).toHaveBeenCalled();
        });
    });
    
    describe("proceedBasisWarningModal", () => {
        beforeEach(() => {
            scope.createBasisWarningModal("testUser", "testListName", "testBasis");
        });

        it("should close basisWarningModalInstance", () => {
            spyOn(scope.basisWarningModalInstance, "close").and.callThrough();
            scope.proceedBasisWarningModal();
            expect(scope.basisWarningModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("closeBasisWarningModal", () => {
        beforeEach(() => {
            scope.createBasisWarningModal("testUser", "testListName", "testBasis");
        });

        it("should dismiss basisWarningModalInstance", () => {
            spyOn(scope.basisWarningModalInstance, "dismiss").and.callThrough();
            scope.closeBasisWarningModal();
            expect(scope.basisWarningModalInstance.dismiss).toHaveBeenCalled();
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
                scope.currentUser = "jdoe";
                scope.memberToRemove = "jdoe";
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

    describe("showWarningRemovingSelfFromList", () => { 
        beforeEach(() => { 
            scope.memberToRemove = "iamtst01";
        });

        it("should return true if currentUser is the same as the memberToRemove username", () => {
            scope.currentUser = "iamtst01";
            expect(scope.showWarningRemovingSelfFromList()).toBeTrue();
        });

        it("should return false if currentUser is not the same as the memberToRemove username", () => {
            scope.currentUser = "iamtst02";
            expect(scope.showWarningRemovingSelfFromList()).toBeFalse();
        });
    });
    
    describe("showWarningRemovingSelfResetModal", () => {
        let result;
        it("should return true if membersToRemove includes currentUser && listName is \"owners\" ", () => {
            scope.currentUser = "testUser";
            scope.membersToRemove = ["testUser"];
            scope.listName = "owners";
            result = scope.showWarningRemovingSelfResetModal();
            expect(result).toBeTrue();
        });

        it("should return true if membersToRemove includes currentUser && listName is \"admins\" ", () => {
            scope.currentUser = "testUser";
            scope.membersToRemove = ["testUser"];
            scope.listName = "admins";
            result = scope.showWarningRemovingSelfResetModal();
            expect(result).toBeTrue();
        });

        it("should return false if membersToRemove includes currentUser && listName is not \"owners\" or \"admins\" ", () => {
            scope.currentUser = "testUser";
            scope.membersToRemove = ["testUser"];
            scope.listName = "badListName";
            result = scope.showWarningRemovingSelfResetModal();
            expect(result).toBeFalse();
        });

        it("should return false if membersToRemove does not include currentUser && listName is \"owners\" ", () => {
            scope.currentUser = "badTestUser";
            scope.membersToRemove = ["testUser"];
            scope.listName = "owners";
            result = scope.showWarningRemovingSelfResetModal();
            expect(result).toBeFalse();
        });

        it("should return false if membersToRemove does not include currentUser && listName is \"admins\" ", () => {
            scope.currentUser = "badTestUser";
            scope.membersToRemove = ["testUser"];
            scope.listName = "admins";
            result = scope.showWarningRemovingSelfResetModal();
            expect(result).toBeFalse();
        });

        it("should return false if membersToRemove does not include currentUser", () => {
            scope.currentUser = "badTestUser";
            scope.membersToRemove = ["testUser"];
            result = scope.showWarningRemovingSelfResetModal();
            expect(result).toBeFalse();
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
