/* global _, inject */
describe("GroupingController", () => {

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    let scope;
    let controller;
    let BASE_URL;
    let httpBackend;
    let gs;
    let uibModal;
    let threshold;
    let message;

    beforeEach(inject(($rootScope, $controller, _BASE_URL_, _$httpBackend_, groupingsService, $uibModal, Threshold, Message) => {
        scope = $rootScope.$new(true);
        controller = $controller("GroupingJsController", {
            $scope: scope
        });
        httpBackend = _$httpBackend_;
        BASE_URL = _BASE_URL_;
        gs = groupingsService;
        uibModal = $uibModal;
        threshold = Threshold;
        message = Message;
    }));

    it("should define the owner controller", () => {
        expect(controller).toBeDefined();
    });

    // Set up mock data
    beforeEach(() => {
        scope.selectedGrouping = { name: "grouping1", path: "path:path2:grouping1" };
        scope.itemsPerPage = 20;

        scope.groupingBasis = [
            {
                name: "User One",
                uid: "user1",
                uhUuid: "00000001",
                firstName: "User",
                lastName: "One"
            },
            {
                name: "User Four",
                uid: "user4",
                uhUuid: "00000004",
                firstName: "User",
                lastName: "Four"
            },
            {
                name: "User Seven",
                uid: "user7",
                uhUuid: "00000007",
                firstName: "User",
                lastName: "Seven"
            }
        ];
        scope.pagedItemsBasis = scope.groupToPages(scope.groupingBasis);

        scope.groupingInclude = [
            {
                name: "User One",
                uid: "user1",
                uhUuid: "00000001",
                firstName: "User",
                lastName: "One"
            },
            {
                name: "User Two",
                uid: "user2",
                uhUuid: "00000002",
                firstName: "User",
                lastName: "Two"
            },
            {
                name: "User Three",
                uid: "user3",
                uhUuid: "00000003",
                firstName: "User",
                lastName: "Three"
            }
        ];
        scope.pagedItemsInclude = scope.groupToPages(scope.groupingInclude);

        scope.groupingExclude = [
            {
                name: "User Four",
                uid: "user4",
                uhUuid: "00000004",
                firstName: "User",
                lastName: "Four"
            },
            {
                name: "User Five",
                uid: "user5",
                uhUuid: "00000005",
                firstName: "User",
                lastName: "Five"
            },
            {
                name: "User Nine",
                uid: "",
                uhUuid: "00000009",
                firstName: "User",
                lastName: "Nine"
            }
        ];
        scope.pagedItemsExclude = scope.groupToPages(scope.groupingExclude);

        scope.groupingMembers = _.cloneDeep(scope.groupingInclude);
        scope.groupingMembers.push({
            name: "User Seven",
            uid: "user7",
            uhUuid: "00000007",
            firstName: "User",
            lastName: "Seven"
        });
        scope.pagedItemsMembers = scope.groupToPages(scope.groupingMembers);

        scope.groupingOwners = [
            {
                name: "User Six",
                uid: "user6",
                uhUuid: "00000006",
                firstName: "User",
                lastName: "Six"
            }
        ];
        scope.pagedItemsOwners = scope.groupToPages(scope.groupingOwners);

        scope.listserv = true;
        scope.allowOptIn = true;
        scope.allowOptOut = true;

        scope.pagedItemsPerson = [];
        scope.currentPagePerson = 0;
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

    describe("reloadGrouping", () => {
        it("should call getGroupingInformation when paginating is complete", () => {
            scope.paginatingComplete = true;
            spyOn(scope, "getGroupingInformation").and.callThrough();
            scope.reloadGrouping();
            expect(scope.getGroupingInformation).toHaveBeenCalled();
        });

        it("should not call getGroupingInformation when paginating is incomplete", () => {
            scope.paginatingComplete = false;
            spyOn(scope, "getGroupingInformation").and.callThrough();
            scope.reloadGrouping();
            expect(scope.getGroupingInformation).toHaveBeenCalledTimes(0);
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
            scope.fetchGrouping();
            expect(gs.getGrouping).toHaveBeenCalled();
        });

        it("should set descriptionLoaded & paginatingProgress to true & paginatingComplete to false after calling gs.getGrouping", () => {
            spyOn(gs, "getGrouping").and.callThrough();
            scope.fetchGrouping();
            expect(gs.getGrouping).toHaveBeenCalled();
        });

        it("should call getGroupingDescription", () => {
            spyOn(gs, "getGroupingDescription").and.callThrough();
            scope.getGroupingInformation().then((res) => expect(gs.getGroupingDescription).toHaveBeenCalled());
        });

        it("should call getGroupingSyncDest", () => {
            spyOn(gs, "getGroupingSyncDest").and.callThrough();
            scope.getGroupingInformation().then((res) => expect(gs.getGroupingSyncDest).toHaveBeenCalled());
        });

        it("should call getGroupingOptAttributes", () => {
            spyOn(gs, "getGroupingOptAttributes").and.callThrough();
            scope.getGroupingInformation().then((res) => expect(gs.getGroupingOptAttributes).toHaveBeenCalled());
        });

        it("should call disableResetCheckboxes", () => {
             spyOn(scope, "disableResetCheckboxes").and.callThrough();
            scope.getGroupingInformation().then((res) => expect(scope.disableResetCheckboxes).toHaveBeenCalled());
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
        it("should set the modelDescription to the groupingDescription", () => {
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
            scope.groupingDescription = "description";
        });


        it("should update the description with default value when new description is an empty string for a grouping", () => {
            scope.modelDescription = "";
            spyOn(gs, "updateDescription").and.callThrough();
            scope.saveDescription();
            expect(scope.groupingDescription).toBe("No description given for this Grouping.");
            expect(gs.updateDescription).toHaveBeenCalled();
        });
        
        it("should update the description for a grouping", () => {
            scope.modelDescription = "descriptionOfAModal";
            spyOn(gs, "updateDescription").and.callThrough();
            scope.saveDescription();
            expect(scope.groupingDescription).toBe("descriptionOfAModal");
            expect(gs.updateDescription).toHaveBeenCalled();
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

    describe("getCSVToolTipMessage", () => {
        it("should return an empty string when paginatingComplete has loaded", () => {
            scope.paginatingComplete = true;
            expect(scope.getCSVToolTipMessage()).toBe("");
        });

        it("should return a not loaded message when paginatingComplete has not loaded", () => {
            scope.paginatingComplete = false;
            expect(scope.getCSVToolTipMessage()).toBe(message.Csv.GROUP_NOT_LOADED);
        });

        it("should return a grouping empty message when the entire grouping is empty", () => {
            scope.groupingMembers = [];
            scope.groupingExclude = [];
            scope.paginatingComplete = true;
            expect(scope.getCSVToolTipMessage()).toBe(message.Csv.GROUP_EMPTY);
        });
    });

    describe("displayImportModal", () => {
        it("should set listName to listName passed in", () => {
            scope.listName = "";
            scope.displayImportModal("admin");
            expect(scope.listName).toBe("admin");
        });

        it("should check that the import modal is displayed", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.displayImportModal(scope.listName);
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("cancelImportModal", () => {
        beforeEach(() => {
            scope.displayImportModal(scope.listName);
        });

        it("should dismiss confirmImportInstance", () => {
            spyOn(scope.importModalInstance, "dismiss").and.callThrough();
            scope.cancelImportModal();
            expect(scope.importModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe("displayImportErrorModal", () => {
        beforeEach(() => {
            scope.importErrorModalInstance = {
                dismiss: () => {}
            };
        });

        it("should open importErrorModalInstance", () => {
            spyOn(uibModal, "open");
            scope.displayImportErrorModal();
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("closeImportErrorModal", () => {
        beforeEach(() => {
            scope.displayImportErrorModal();
        });

        it("should dismiss confirmImportInstance", () => {
            spyOn(scope.importErrorModalInstance, "dismiss").and.callThrough();
            scope.closeImportErrorModal();
            expect(scope.importErrorModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe("readTextFile", () => {
        it("should check for the MIME type and match it with text/plain", () => {
            let fakeNames = ["iamtst01", "iamtst02", "iamtst03", "iamtst04", "iamtst05", "iamtst06", "22222222", "12345678", "bogusname", "fakename", "_1234455", "_iamtst01", "_test_123-abc"];
            const fakeFile = new File(fakeNames, "file.txt", { type: "text/plain" });
            expect(scope.verifyImportFileType).toBeDefined();

            expect(scope.verifyImportFileType(fakeFile)).toBeTrue();
            expect(scope.verifyImportFileType({ type: "text/plain" })).toBeTrue();
            expect(scope.verifyImportFileType({ type: "TEXT/PLAIN" })).toBeTrue();

            expect(scope.verifyImportFileType({ type: null })).toBeFalse();
            expect(scope.verifyImportFileType({ type: "text/csv" })).toBeTrue();
            expect(scope.verifyImportFileType(null)).toBeFalse();
        });

        it("should check for the file size to be within 5MB", () => {
            let fakeNames = ["iamtst01", "iamtst02", "iamtst03", "iamtst04", "iamtst05", "iamtst06", "22222222", "12345678", "bogusname", "fakename", "_1234455", "_iamtst01", "_test_123-abc"];
            const fakeFile = new File(fakeNames, "file.txt", { type: "text/plain" });
            expect(scope.verifyImportFileSize).toBeDefined();

            expect(scope.verifyImportFileSize(fakeFile)).toBeTrue();
            expect(scope.verifyImportFileSize({ size: 1000000 })).toBeTrue();
            expect(scope.verifyImportFileSize({ size: 5242880 })).toBeTrue();

            expect(scope.verifyImportFileSize({ size: 5242881 })).toBeFalse();
        });

        it("should check for file name size to be under 50 characters.", () => {
            let fakeNames = ["iamtst01", "iamtst02", "iamtst03", "iamtst04", "iamtst05", "iamtst06", "22222222", "12345678", "bogusname", "fakename", "_1234455", "_iamtst01", "_test_123-abc"];
            const fakeFile = new File(fakeNames, "file.txt", { type: "text/plain" });
            expect(scope.verifyImportFileSize).toBeDefined();

            expect(scope.verifyImportFileNameSize(fakeFile)).toBeTrue();
            expect(scope.verifyImportFileNameSize({ name: "ThisIsALongNameOverFiftyCharactersaaaaaaaaaaaaaaaa.txt" }));

            expect(scope.verifyImportFileNameSize({ name: "ThisIsALongNameOverFiftyCharactersaaaaaaaaaaaaaaaa.txt" })).toBeFalse();
        });

        it("should check for the file name for any illegal characters.", () => {
            let fakeNames = ["iamtst01", "iamtst02", "iamtst03", "iamtst04", "iamtst05", "iamtst06", "22222222", "12345678", "bogusname", "fakename", "_1234455", "_iamtst01", "_test_123-abc"];
            const fakeFile = new File(fakeNames, "file.txt", { type: "text/plain" });
            expect(scope.verifyImportFileName).toBeDefined();

            expect(scope.verifyImportFileName(fakeFile)).toBeTrue();
            expect(scope.verifyImportFileName({ name: "THISISATEXTFILE.txt" })).toBeTrue();
            expect(scope.verifyImportFileName({ name: "thisisatextfile.txt" })).toBeTrue();
            expect(scope.verifyImportFileName({ name: "this_is_a_text_file.txt" })).toBeTrue();
            expect(scope.verifyImportFileName({ name: "this is a text file.txt" })).toBeTrue();

            expect(scope.verifyImportFileName({ name: "badtextfile%.txt" })).toBeFalse();
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

    describe("initMemberDisplayName", () => {
        it("should set $scope.fullName, $scope.givenName, $scope.uhUuid and $scope.uid to the appropriate values", () => {
            let attributes = {
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
                uid: "testUsername",
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
                uid: "testUsername",
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
                uid: "testUsername"
            };

            scope.initMemberDisplayName(attributes);
            expect(scope.member).toBe(scope.fullName);
        });

        it("should set $scope.member to uid if fullName.Length == 0 and uid > 0", () => {
            const attributes = {
                name: "",
                firstName: "testFirstName",
                uhUuid: "testuhUuid",
                uid: "testUsername"
            };

            scope.initMemberDisplayName(attributes);
            expect(scope.member).toBe(scope.uid);
        });

        it("should set $scope.member to uhUuid if fullName & uid both == 0", () => {
            const attributes = {
                name: "",
                firstName: "testFirstName",
                uhUuid: "testuhUuid",
                uid: ""
            };

            scope.initMemberDisplayName(attributes);
            expect(scope.member).toBe(scope.uhUuid);
        });
    });

    describe("existsInList", () => {
        beforeEach(() => {
            scope.groupingInclude = [
                {
                    name: "iamtst01",
                    uhUuid: "iamtst01",
                    uid: "iamtst01"
                },
                {
                    name: "iamtst02",
                    uhUuid: "iamtst02",
                    uid: "iamtst02"
                }
            ];

            scope.groupingExclude = [
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

            scope.groupingOwners = [{
                name: "iamtst05",
                uhUuid: "iamtst05",
                uid: "iamtst05"
            }];

            scope.adminsList = [{
                name: "iamtst06",
                uhUuid: "iamtst06",
                uid: "iamtst06"
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

        it("should call $scope.displayDynamicModal() when the members we are adding are above the maxImport", () => {
            spyOn(scope, "displayDynamicModal").and.callThrough();
            const arr = [];
            for (let i = 0; i < 100002; i++) {
                arr.push("iamtst01");
            }

            scope.addMembers("Include", arr);
            expect(scope.displayDynamicModal).toHaveBeenCalled();
        });

        it("should call $scope.existsInList", () => {
            spyOn(scope, "existsInList");
            scope.addMembers("Include", "iamtst01");

            expect(scope.existsInList).toHaveBeenCalled();
        });

        it("should call $scope.displayDynamicModal() when ALL the members to add already exist in the list", () => {
            for (let i = 0; i < threshold.MAX_LIST_SIZE + 1; i++) {
                scope.groupingInclude.push({
                    name: `iamtst${i}`,
                    uhUuid: `iamtst${i}`,
                    uid: `iamtst${i}`
                });
            }
            const arr = scope.groupingInclude.map((member) => member.uhUuid);

            spyOn(scope, "displayDynamicModal").and.callThrough();
            scope.addMembers("Include", arr);
            expect(scope.displayDynamicModal).toHaveBeenCalled();
        });

        it("should set $scope.errorDismissed to false when ALL the members to add already exist in the list", () => {
            scope.groupingInclude = [{
                name: "iamtst01",
                uhUuid: "iamtst01",
                uid: "iamtst01"
            }];
            scope.addMembers("Include", ["iamtst01"]);
            expect(scope.errorDismissed).toBeFalse();
        });

        it("should call gs.invalidUhIdentifiers and set $scope.waitingForImportResponse to true", () => {
            scope.waitingForImportResponse = false;
            spyOn(gs, "invalidUhIdentifiers").and.callThrough();
            scope.addMembers("Include", ["iamtst01"]);

            expect(scope.waitingForImportResponse).toBeTrue();
            expect(gs.invalidUhIdentifiers).toHaveBeenCalled();
        });

        describe("gs.invalidUhIdentifiers callbacks", () => {
            const uhIdentifier = ["iamtst01"];
            const uhIdentifiers = ["iamtst01", "iamtst02"];

            beforeEach(() => {
                httpBackend.whenGET("currentUser").passThrough();
                httpBackend.whenGET("modal/addModal").passThrough();
                httpBackend.whenGET("modal/importConfirmationModal").passThrough();
                httpBackend.whenGET("modal/importErrorModal").passThrough();
                httpBackend.whenGET("modal/dynamicModal").passThrough();
            });

            describe("onSuccess", () => {
                it("should set $scope.waitingForImportResponse to false", () => {
                    scope.waitingForImportResponse = true;
                    scope.addMembers("Include", uhIdentifier);

                    httpBackend.expectPOST(BASE_URL + "members/invalid", uhIdentifier).respond(200, uhIdentifier);
                    httpBackend.flush();

                    expect(scope.waitingForImportResponse).toBeFalse();
                });

                it("should set $scope.isBatchImport", () => {
                    scope.isBatchImport = true;
                    scope.addMembers("Include", uhIdentifier);
                    httpBackend.expectPOST(BASE_URL + "members/invalid", uhIdentifier).respond(200, uhIdentifier);
                    httpBackend.flush();
                    expect(scope.isBatchImport).toBeFalse();

                    scope.isBatchImport = false;
                    const arr = [];
                    for (let i = 0; i < 101; i++) {
                        arr.push(`iamtst${i}`);
                    }
                    scope.addMembers("Include", arr);
                    httpBackend.expectPOST(BASE_URL + "members/invalidAsync", arr).respond(200, []);
                    httpBackend.flush();
                    expect(scope.isBatchImport).toBeTrue();
                });

                it("should call $scope.displayDynamicModal when all members pre-exist and is a batch import", () => {
                    for (let i = 0; i < 100; i++) {
                        scope.groupingInclude.push({
                            name: `iamtst${i}`,
                            uhUuid: `iamtst${i}`,
                            uid: `iamtst${i}`
                        });
                    }
                    const arr = scope.groupingInclude.map((member) => member.uhUuid);

                    spyOn(scope, "displayDynamicModal").and.callThrough();
                    scope.addMembers("Include", arr);
                    expect(scope.displayDynamicModal).toHaveBeenCalled();
                });

                it("should filter out the members to add that already exist in the list", () => {
                    scope.groupingInclude = [
                        {
                            name: "iamtst02",
                            uhUuid: "iamtst02",
                            uid: "iamtst02"
                        }
                    ];

                    spyOn(scope, "displayAddModal");
                    scope.addMembers("Include", uhIdentifiers);

                    httpBackend.expectPOST(BASE_URL + "members/invalid", uhIdentifiers).respond(200, []);
                    httpBackend.flush();

                    expect(scope.displayAddModal).toHaveBeenCalledWith({
                        membersToAdd: uhIdentifier,
                        listName: "Include"
                    });
                });

                it("should call $scope.displayImportConfirmationModal when adding more than multi-add threshold", () => {
                    spyOn(gs, "invalidUhIdentifiersAsync").and.callFake(gs.invalidUhIdentifiers);
                    spyOn(scope, "displayImportConfirmationModal").and.callThrough();
                    let arr = [];
                    for (let i = 0; i < 102; i++) {
                        arr.push("iamtst01");
                    }
                    scope.addMembers("Include", arr);

                    httpBackend.expectPOST(BASE_URL + "members/invalid", arr).respond(200, []);
                    httpBackend.flush();

                    expect(scope.displayImportConfirmationModal).toHaveBeenCalled();
                });

                it("should set $scope.invalidMembers and $scope.addInputError when res has invalid uhIdentifiers", () => {
                    scope.addInputError = false;
                    scope.addMembers("Include", uhIdentifier);

                    httpBackend.expectPOST(BASE_URL + "members/invalid", uhIdentifier).respond(200, uhIdentifier);
                    httpBackend.flush();

                    expect(scope.invalidMembers).toEqual(uhIdentifier);
                    expect(scope.addInputError).toBeTrue();
                });

                it("should set $scope.invalidMembers and call $scope.displayImportErrorModal when res has uhIdentifiers", () => {
                    spyOn(gs, "invalidUhIdentifiersAsync").and.callFake(gs.invalidUhIdentifiers);
                    spyOn(scope, "displayImportErrorModal").and.callThrough();
                    let arr = [];
                    for (let i = 0; i < 102; i++) {
                        arr.push("iamtst01");
                    }
                    scope.addMembers("Include", arr);

                    httpBackend.expectPOST(BASE_URL + "members/invalid", arr).respond(200, uhIdentifier);
                    httpBackend.flush();

                    expect(scope.invalidMembers).toEqual(uhIdentifier);
                    expect(scope.displayImportErrorModal).toHaveBeenCalled();
                });
            });

            describe("onError", () => {
                it("should set $scope.waitingForImportResponse, $scope.resStatus and call $scope.displayApiErrorModal", () => {
                    const resStatus = 404;
                    spyOn(scope, "displayApiErrorModal").and.callThrough();
                    scope.addMembers("Include", uhIdentifier);

                    httpBackend.expectPOST(BASE_URL + "members/invalid", uhIdentifier).respond(resStatus);
                    httpBackend.expectGET("modal/apiError").respond(200);
                    httpBackend.flush();

                    expect(scope.waitingForImportResponse).toBeFalse();
                    expect(scope.resStatus).toBe(resStatus);
                    expect(scope.displayApiErrorModal).toHaveBeenCalled();
                });
            });
        });
    });

    describe("displayAddModal", () => {
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
            scope.displayAddModal({
                membersToAdd: member,
                listName: "Include"
            });

            expect(scope.listName).toBe("Include");
        });

        it("should set $scope.containsInput to true and return when membersToAdd is empty", () => {
            spyOn(gs, "getMembersAttributes");
            scope.displayAddModal({
                membersToAdd: [],
                listName: "Include"
            });

            expect(scope.containsInput).toBeTrue();
            expect(gs.getMembersAttributes).not.toHaveBeenCalled();
        });

        it("should call gs.getMembersAttributes and set $scope.waitingForImportResponse to true", () => {
            scope.waitingForImportResponse = false;
            spyOn(gs, "getMembersAttributes").and.callThrough();
            scope.displayAddModal({
                membersToAdd: member,
                listName: "Include"
            });

            expect(scope.waitingForImportResponse).toBeTrue();
            expect(gs.getMembersAttributes).toHaveBeenCalled();
        });

        describe("gs.getMembersAttributes callbacks", () => {
            beforeEach(() => {
                spyOn(gs, "getMembersAttributes").and.callThrough();

                httpBackend.whenGET("currentUser").passThrough();
                httpBackend.whenGET("modal/addModal").passThrough();
            });

            describe("onSuccess", () => {
                it("should set $scope.multiAddResults and set $scope.waitingForImportResponse to false", () => {
                    spyOn(scope, "addInGroups");
                    spyOn(scope, "initMemberDisplayName");
                    scope.waitingForImportResponse = true;
                    scope.displayAddModal({
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
                    scope.displayAddModal({
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

                    scope.displayAddModal({
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
                    scope.displayAddModal({
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
                        spyOn(gs, "addIncludeMembers").and.callThrough();
                        spyOn(gs, "addExcludeMembers").and.callThrough();
                        spyOn(gs, "addOwnerships").and.callThrough();
                        spyOn(gs, "addAdmin").and.callThrough();
                    });

                    it("should not make any groupingsService call when the user presses 'cancel' on addModal.html", () => {
                        scope.displayAddModal({
                            membersToAdd: member,
                            listName: "Include"
                        });

                        httpBackend.expectPOST(BASE_URL + "members", member).respond(200, mockResponseSingle);
                        httpBackend.expectGET("modal/addModal").respond(200);
                        httpBackend.flush();

                        scope.cancelAddModal();
                        expect(scope.waitingForImportResponse).toBeFalse();
                        expect(gs.addIncludeMembers).not.toHaveBeenCalled();
                        expect(gs.addExcludeMembers).not.toHaveBeenCalled();
                        expect(gs.addOwnerships).not.toHaveBeenCalled();
                        expect(gs.addAdmin).not.toHaveBeenCalled();
                    });

                    it("should not make any groupingsService call when the user presses 'cancel' on multiAddModal.html", () => {
                        scope.displayAddModal({
                            membersToAdd: members,
                            listName: "Include"
                        });

                        httpBackend.expectPOST(BASE_URL + "members", members).respond(200, mockResponseMulti);
                        httpBackend.expectGET("modal/multiAddModal").respond(200);
                        httpBackend.flush();

                        scope.cancelAddModal();
                        expect(scope.waitingForImportResponse).toBeFalse();
                        expect(gs.addIncludeMembers).not.toHaveBeenCalled();
                        expect(gs.addExcludeMembers).not.toHaveBeenCalled();
                        expect(gs.addOwnerships).not.toHaveBeenCalled();
                        expect(gs.addAdmin).not.toHaveBeenCalled();
                    });

                    it("should call gs.addIncludeMembers when the user presses 'add' in addModal.html", () => {
                        spyOn(uibModal, "open").and.returnValue(mockModal);
                        scope.displayAddModal({
                            membersToAdd: member,
                            listName: "Include"
                        });

                        httpBackend.expectPOST(BASE_URL + "members", member).respond(200, mockResponseSingle);
                        httpBackend.flush();

                        scope.proceedAddModal();
                        expect(scope.waitingForImportResponse).toBeTrue();
                        expect(gs.addIncludeMembers).toHaveBeenCalled();
                    });

                    it("should call gs.addIncludeMembers when the user presses 'add' in multiAddModal.html", () => {
                        spyOn(uibModal, "open").and.returnValue(mockModal);
                        scope.displayAddModal({
                            membersToAdd: members,
                            listName: "Include"
                        });

                        httpBackend.expectPOST(BASE_URL + "members", members).respond(200, mockResponseMulti);
                        httpBackend.flush();

                        scope.proceedAddModal();
                        expect(scope.waitingForImportResponse).toBeTrue();
                        expect(gs.addIncludeMembers).toHaveBeenCalled();
                    });

                    it("should call gs.addExcludeMembers when the user presses 'add' in addModal.html", () => {
                        spyOn(uibModal, "open").and.returnValue(mockModal);
                        scope.displayAddModal({
                            membersToAdd: member,
                            listName: "Exclude"
                        });

                        httpBackend.expectPOST(BASE_URL + "members", member).respond(200, mockResponseSingle);
                        httpBackend.flush();

                        scope.proceedAddModal();
                        expect(scope.waitingForImportResponse).toBeTrue();
                        expect(gs.addExcludeMembers).toHaveBeenCalled();
                    });

                    it("should call gs.addExcludeMembers when the user presses 'add' in multiAddModal.html", () => {
                        spyOn(uibModal, "open").and.returnValue(mockModal);
                        scope.displayAddModal({
                            membersToAdd: members,
                            listName: "Exclude"
                        });

                        httpBackend.expectPOST(BASE_URL + "members", members).respond(200, mockResponseMulti);
                        httpBackend.flush();

                        scope.proceedAddModal();
                        expect(scope.waitingForImportResponse).toBeTrue();
                        expect(gs.addExcludeMembers).toHaveBeenCalled();
                    });

                    it("should call gs.addOwnerships when the user presses 'add' in addModal.html", () => {
                        spyOn(uibModal, "open").and.returnValue(mockModal);
                        scope.displayAddModal({
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
                        scope.displayAddModal({
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
                        scope.displayAddModal({
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
                it("should set $scope.waitingForImportResponse, $scope.resStatus, and call $scope.displayApiErrorModal", () => {
                    const resStatus = 404;
                    spyOn(scope, "displayApiErrorModal").and.callThrough();
                    scope.displayAddModal({
                        membersToAdd: member,
                        listName: "Include"
                    });

                    httpBackend.expectPOST(BASE_URL + "members", member).respond(resStatus);
                    httpBackend.expectGET("modal/apiError").respond(200);
                    httpBackend.flush();

                    expect(scope.resStatus).toBe(resStatus);
                    expect(scope.displayApiErrorModal).toHaveBeenCalled();
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

    describe("displayImportConfirmationModal", () => {
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
            scope.displayImportConfirmationModal("Include", ["iamtst01", "iamtst02"]);
            expect(scope.listName).toBe("Include");
            expect(scope.importSize).toBe(2);
        });

        it("should open $uibModal with modal/importConfirmationModal", () => {
            spyOn(uibModal, "open").and.returnValue(mockModal);
            scope.displayImportConfirmationModal("Include", ["iamtst01", "iamtst02"]);
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/importConfirmationModal",
                scope,
                ariaLabelledBy: "import-confirmation-modal",
            });
        });

        it("should make the correct groupingsService and member to Include/Exclude", () => {
            spyOn(uibModal, "open").and.returnValue(mockModal);
            spyOn(gs, "addIncludeMembersAsync").and.callThrough();
            spyOn(gs, "addExcludeMembersAsync").and.callThrough();

            scope.displayImportConfirmationModal("Include", ["iamtst01", "iamtst02"]);
            scope.proceedImportConfirmationModal();
            expect(gs.addIncludeMembersAsync).toHaveBeenCalled();

            scope.displayImportConfirmationModal("Exclude", ["iamtst01", "iamtst02"]);
            scope.proceedImportConfirmationModal();
            expect(gs.addExcludeMembersAsync).toHaveBeenCalled();
        });
    });

    describe("proceedImportConfirmationModal", () => {
        beforeEach(() => {
            scope.importConfirmationModalInstance = {
                close: () => {}
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
                dismiss: () => {}
            };
        });

        it("should dismiss importConfirmationModalInstance", () => {
            spyOn(scope.importConfirmationModalInstance, "dismiss").and.callThrough();
            scope.cancelImportConfirmationModal();
            expect(scope.importConfirmationModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe("displayImportSuccessModal", () => {
        const mockModal = {
            result: {
                finally(confirmCallback) {
                    this.confirmCallBack = confirmCallback;
                }
            },
            close() {
                this.result.confirmCallBack();
            }
        };

        it("should open $uibModal with modal/importSuccessModal", () => {
            spyOn(uibModal, "open").and.returnValue(mockModal);
            scope.displayImportSuccessModal("Include", ["iamtst01", "iamtst02"]);
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/importSuccessModal",
                scope,
                ariaLabelledBy: "import-success-modal",
            });
        });

        it("should call $scope.getGroupingInformation() when the modal is closed", () => {
            spyOn(uibModal, "open").and.returnValue(mockModal);
            spyOn(scope, "getGroupingInformation");
            scope.displayImportSuccessModal("Include", ["iamtst01", "iamtst02"]);
            scope.closeImportSuccessModal();
            expect(scope.getGroupingInformation).toHaveBeenCalled();
        });
    });

    describe("closeImportSuccessModal", () => {
        beforeEach(() => {
            scope.importSuccessModalInstance = {
                close: () => {
                    // Mock $uib modal close
                }
            };
        });

        it("should close importSuccessModalInstance", () => {
            spyOn(scope.importSuccessModalInstance, "close").and.callThrough();
            scope.closeImportSuccessModal();
            expect(scope.importSuccessModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("returnMemberObject", () => {
        beforeEach(() => {
            scope.groupingInclude = [{
                name: "iamtst01",
                uhUuid: "iamtst01",
                uid: "iamtst01"
            }];

            scope.groupingExclude = [{
                name: "iamtst02",
                uhUuid: "iamtst02",
                uid: "iamtst02"
            }];

            scope.groupingOwners = [{
                name: "iamtst03",
                uhUuid: "iamtst03",
                uid: "iamtst03"
            }];

            scope.adminsList = [{
                name: "iamtst04",
                uhUuid: "iamtst04",
                uid: "iamtst04"
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

    describe("fetchMemberProperties", () => {
        let members = [];
        beforeEach(() => {
            scope.groupingInclude = [
                {
                    name: "iamtst01",
                    uhUuid: "iamtst01",
                    uid: "iamtst01"
                },
                {
                    name: "iamtst02",
                    uhUuid: "iamtst02",
                    uid: "iamtst02"
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

        it("should call displayRemoveModal", () => {
            scope.groupingInclude = [{
                name: "iamtst01",
                uid: "iamtst01",
                uhUuid: "iamtst01"
            }];
            scope.manageMembers = "iamtst01";
            spyOn(scope, "displayRemoveModal");
            spyOn(scope, "displayDynamicModal");
            scope.removeMembers("Include");
            expect(scope.membersToModify).toEqual("iamtst01");
            expect(scope.displayRemoveModal).toHaveBeenCalled();
        });

        it("should call displayDynamicModal when member does not exist in list", () => {
            scope.groupingInclude = [{
                name: "iamtst01",
                uid: "iamtst01",
                uhUuid: "iamtst01"
            }];
            scope.manageMembers = "iamtst02";
            spyOn(scope, "displayRemoveModal");
            spyOn(scope, "displayDynamicModal");
            scope.removeMembers("Include");
            expect(scope.membersToModify).toEqual("iamtst02");
            expect(scope.displayRemoveModal).not.toHaveBeenCalled();
            expect(scope.displayDynamicModal).toHaveBeenCalled();
        });

        it("should call displayRemoveErrorModal when the listName is owners", () => {
            scope.groupingOwners = [{
                name: "iamtst03",
                uid: "iamtst03",
                uhUuid: "iamtst03"
            }];
            scope.manageMembers = "iamtst03";
            spyOn(scope, "displayRemoveErrorModal");
            scope.removeMembers("owners");
            expect(scope.displayRemoveErrorModal).toHaveBeenCalled();
        });
    });

    describe("displayRemoveModal", () => {
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
                    uid: "iamtst01",
                    uhUuid: "iamtst01"
                },
                {
                    name: "iamtst02",
                    uid: "iamtst02",
                    uhUuid: "iamtst02"
                }
            ];

            scope.groupingExclude = [
                {
                    name: "iamtst03",
                    uid: "iamtst03",
                    uhUuid: "iamtst03"
                },
                {
                    name: "iamtst04",
                    uid: "iamtst04",
                    uhUuid: "iamtst04"
                }
            ];

            scope.groupingOwners = [
                {
                    name: "iamtst05",
                    uid: "iamtst05",
                    uhUuid: "iamtst05"
                },
                {
                    name: "iamtst06",
                    uid: "iamtst06",
                    uhUuid: "iamtst06"
                }
            ];
        });

        it("should set scope.removeInputError to true when no users are given", () => {
            options = { listName: "Include" };
            scope.removeInputError = false;
            scope.displayRemoveModal(options);
            expect(scope.removeInputError).toBeTrue();
        });

        it("should open uibModal with removeModal template when removing 1 user", () => {
            spyOn(scope, "initMemberDisplayName");
            spyOn(uibModal, "open").and.returnValue(mockModal);

            options = {
                membersToRemove: { name: "iamtst01", uid: "iamtst01", uhUuid: "iamtst01" },
                listName: "Include"
            };
            scope.displayRemoveModal(options);
            expect(scope.initMemberDisplayName).toHaveBeenCalled();
            expect(scope.membersToRemove).toEqual(["iamtst01"]);
            expect(scope.isMultiRemove).toBeFalse();
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/removeModal",
                backdrop: "static",
                scope,
                ariaLabelledBy: "remove-modal",
            });
            spyOn(gs, "removeIncludeMembers").and.callThrough();
            scope.proceedRemoveModal();
            expect(gs.removeIncludeMembers).toHaveBeenCalled();

            options = { membersToRemove: ["iamtst03"], listName: "Exclude" };
            scope.displayRemoveModal(options);
            expect(scope.initMemberDisplayName).toHaveBeenCalled();
            expect(scope.membersToRemove).toEqual(["iamtst03"]);
            expect(scope.isMultiRemove).toBeFalse();
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/removeModal",
                backdrop: "static",
                scope,
                ariaLabelledBy: "remove-modal",
            });
            spyOn(gs, "removeExcludeMembers").and.callThrough();
            scope.proceedRemoveModal();
            expect(gs.removeExcludeMembers).toHaveBeenCalled();
        });

        it("should open uibModal with multiRemoveModal template when removing more than 1 user", () => {
            spyOn(uibModal, "open").and.returnValue(mockModal);
            spyOn(gs, "removeIncludeMembers").and.callThrough();
            spyOn(gs, "removeExcludeMembers").and.callThrough();

            options = { membersToRemove: ["iamtst01", "iamtst02"], listName: "Include" };
            scope.displayRemoveModal(options);
            expect(scope.membersToRemove).toEqual(["iamtst01", "iamtst02"]);
            expect(scope.isMultiRemove).toBeTrue();
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/multiRemoveModal",
                backdrop: "static",
                scope,
                ariaLabelledBy: "remove-modal",
            });
            scope.proceedRemoveModal();
            expect(gs.removeIncludeMembers).toHaveBeenCalled();

            options = { membersToRemove: ["iamtst03", "iamtst04"], listName: "Exclude" };
            scope.displayRemoveModal(options);
            expect(scope.membersToRemove).toEqual(["iamtst03", "iamtst04"]);
            expect(scope.isMultiRemove).toBeTrue();
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/multiRemoveModal",
                backdrop: "static",
                scope,
                ariaLabelledBy: "remove-modal",
            });
            scope.proceedRemoveModal();
            expect(gs.removeExcludeMembers).toHaveBeenCalled();
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

        it("should clear multiRemoveResults", () => {
            scope.multiRemoveResults = [{uid: "testiwta", uhUuid: "99997010", name: "Testf-iwt-a TestIAM-staff"}];
            scope.proceedRemoveModal();
            expect(scope.multiRemoveResults).toEqual([]);
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

        it("should clear all checkboxes in a grouping", () => {
            scope.pageSelected = true;
            scope.membersInCheckboxList = {"testiwta": true, "testiwtb": true, "testiwtc": true};
            scope.cancelRemoveModal();
            expect(scope.membersInCheckboxList).toEqual({});
            expect(scope.pageSelected).toBeFalse();
        });
    });

    describe("removeMemberWithTrashcan", () => {
        it("should call displayRemoveModal", () => {
            let listName = "Include";
            spyOn(scope, "displayRemoveModal");
            scope.removeMemberWithTrashcan(listName, 0, 0);
            expect(scope.displayRemoveModal).toHaveBeenCalled();
        });
    });

    describe("removeOwnerWithTrashcan", () => {
        it("should display the remove modal if groupingOwners length > 1", () => {
            scope.groupingOwners = [
                {
                    name: "iamtst01",
                    uid: "iamtst01",
                    uhUuid: "iamtst01"
                },
                {
                    name: "iamtst02",
                    uid: "iamtst02",
                    uhUuid: "iamtst02"
                }
            ];
            spyOn(scope, "displayRemoveModal");
            scope.removeOwnerWithTrashcan(0, 0);
            expect(scope.displayRemoveModal).toHaveBeenCalled();
        });

        it("should display the remove error modal if groupingOwners < 1", () => {
            scope.groupingOwners = [{
                name: "iamtst01",
                uid: "iamtst01",
                uhUuid: "iamtst01"
            }];
            spyOn(scope, "displayRemoveErrorModal");
            scope.removeOwnerWithTrashcan(0, 0);
            expect(scope.displayRemoveErrorModal).toHaveBeenCalledWith("owner");
        });
    });

    describe("displayRemoveErrorModal", () => {
        it("should set userType to userType that is passed in", () => {
            expect(scope.userType).toBeUndefined();
            scope.displayRemoveErrorModal("admin");
            expect(scope.userType).toBe("admin");
        });

        it("should check that removeErrorModalInstance is displayed", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.displayRemoveErrorModal("test");
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("closeRemoveErrorModal", () => {
        beforeEach(() => {
            scope.displayRemoveErrorModal("testString");
        });

        it("should close removeErrorModalInstance", () => {
            spyOn(scope.removeErrorModalInstance, "close").and.callThrough();
            scope.closeRemoveErrorModal();
            expect(scope.removeErrorModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("showWarningRemovingSelf", () => {
        describe("removing self from a list", () => {
            beforeEach(() => {
                scope.currentUser = { uid: "iamtst01", uhUuid: "iamtst01" };
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

    describe("displayResetGroupModal", () => {
        let options = { user: "testUser", group: "testGroup", listNames: "testList" };

        it("should set scope.groupReset/listNames to passed in option's object group/listNames", () => {
            scope.groupReset = "";
            scope.listNames = "";
            scope.displayResetGroupModal(options);
            expect(scope.groupReset).toBe("testGroup");
            expect(scope.listNames).toBe("testList");
        });

        it("should open resetModalInstance modal", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.displayResetGroupModal(options);
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("initResetGroup", () => {
        it("should call handleGroupingReset ", () => {
            spyOn(scope, "handleGroupingReset");
            scope.initResetGroup();
            expect(scope.handleGroupingReset).toHaveBeenCalled();
        });
    });

    describe("handleGroupingReset", () => {
        const groupingPath = "grouping-path";
        const includeResultSuccess = {
            "groupPath": groupingPath + ":include",
            "resultCode": "SUCCESS"
        };
        const excludeResultSuccess = {
            "groupPath": groupingPath + ":exclude",
            "resultCode": "SUCCESS"
        };
        const resultError = {
            "status": 404
        };
        const resultErrorStatusUndef = {
            "status": "undefined"
        };

        describe("when both results are successful", () => {
            it("should call displaySuccessfulGroupResetModal", () => {
                spyOn(scope, "displaySuccessfulGroupResetModal");
                scope.handleGroupingReset(groupingPath, includeResultSuccess, excludeResultSuccess);
                expect(scope.displaySuccessfulGroupResetModal).toHaveBeenCalled();
            });
            it("should not call displayUnsuccessfulGroupResetModal", () => {
                spyOn(scope, "displayUnsuccessfulGroupResetModal");
                scope.handleGroupingReset(groupingPath, includeResultSuccess, excludeResultSuccess);
                expect(scope.displayUnsuccessfulGroupResetModal).not.toHaveBeenCalled();
            });
        });

        describe("when the Exclude group has been reset, but there was an error preventing the Include list reset.", () => {
            it("should not call displaySuccessfulGroupResetModal", () => {
                spyOn(scope, "displaySuccessfulGroupResetModal");
                scope.handleGroupingReset(groupingPath, includeResultSuccess, resultError);
                expect(scope.displaySuccessfulGroupResetModal).not.toHaveBeenCalled();
            });
            it("should call displayUnsuccessfulGroupResetModal", () => {
                spyOn(scope, "displayUnsuccessfulGroupResetModal");
                scope.handleGroupingReset(groupingPath, resultError, excludeResultSuccess);
                expect(scope.displayUnsuccessfulGroupResetModal).toHaveBeenCalled();
            });

            let message = "The Exclude list has been reset. There was an error preventing the Include list reset.";
            let title = "Reset Grouping Error";
            it(`should set the reset error modal title to \"${title}\" and message to \"${message}\" `, () => {
                spyOn(scope, "displayUnsuccessfulGroupResetModal").and.callThrough();
                scope.handleGroupingReset(groupingPath, resultError, excludeResultSuccess);
                expect(scope.currentModalBody).toBe(message);
                expect(scope.currentModalTitle).toBe(title);
            });
        });

        describe("when the Include group has been reset, but there was an error preventing the Exclude list reset.", () => {
            it("should not call displaySuccessfulGroupResetModal", () => {
                spyOn(scope, "displaySuccessfulGroupResetModal");
                scope.handleGroupingReset(groupingPath, resultError, excludeResultSuccess);
                expect(scope.displaySuccessfulGroupResetModal).not.toHaveBeenCalled();
            });
            it("should call displayUnsuccessfulGroupResetModal", () => {
                spyOn(scope, "displayUnsuccessfulGroupResetModal");
                scope.handleGroupingReset(groupingPath, includeResultSuccess, resultError);
                expect(scope.displayUnsuccessfulGroupResetModal).toHaveBeenCalled();
            });

            let message = "The Include list has been reset. There was an error preventing the Exclude list reset.";
            let title = "Reset Grouping Error";
            it(`should set the reset error modal title to \"${title}\" and message to \"${message}\" `, () => {
                spyOn(scope, "displayUnsuccessfulGroupResetModal").and.callThrough();
                scope.handleGroupingReset(groupingPath, includeResultSuccess, resultError);
                expect(scope.currentModalBody).toBe(message);
                expect(scope.currentModalTitle).toBe(title);
            });
        });

        describe("when both results return with an error code", () => {
            it("should not call displaySuccessfulGroupResetModal", () => {
                spyOn(scope, "displaySuccessfulGroupResetModal");
                scope.handleGroupingReset(groupingPath, resultError, excludeResultSuccess);
                expect(scope.displaySuccessfulGroupResetModal).not.toHaveBeenCalled();
            });
            it("should call displayUnsuccessfulGroupResetModal", () => {
                spyOn(scope, "displayUnsuccessfulGroupResetModal");
                scope.handleGroupingReset(groupingPath, resultError, resultError);
                expect(scope.displayUnsuccessfulGroupResetModal).toHaveBeenCalled();
            });

            let message = "Neither list has been reset due to an error.";
            let title = "Reset Grouping Error";
            it(`should set the reset error modal title to \"${title}\" and message to \"${message}\" `, () => {
                spyOn(scope, "displayUnsuccessfulGroupResetModal").and.callThrough();
                scope.handleGroupingReset(groupingPath, resultError, resultError);
                expect(scope.currentModalBody).toBe(message);
                expect(scope.currentModalTitle).toBe(title);
            });
        });

        describe("when both groups are reset", () => {
            let message = "Include and Exclude lists have";
            it(`should set the dynamic modal message variable (scope.group) to \"${message}\"`, () => {
                spyOn(scope, "displaySuccessfulGroupResetModal").and.callThrough();
                scope.handleGroupingReset(groupingPath, includeResultSuccess, excludeResultSuccess);
                expect(scope.group).toBe(message);
            });
        });
        describe("when only the Include group is reset", () => {
            let message = "Include list has";
            it(`should set the dynamic modal message variable (scope.group) to \"${message}\"`, () => {
                spyOn(scope, "displaySuccessfulGroupResetModal").and.callThrough();
                scope.handleGroupingReset(groupingPath, includeResultSuccess, "undefined");
                expect(scope.group).toBe(message);
            });
        });
        describe("when only the Exclude group is reset", () => {
            let message = "Exclude list has";
            it(`should set the dynamic modal message variable (scope.group) to \"${message}\"`, () => {
                spyOn(scope, "displaySuccessfulGroupResetModal").and.callThrough();
                scope.handleGroupingReset(groupingPath, "undefined", excludeResultSuccess);
                expect(scope.group).toBe(message);
            });
        });
    });

    describe("resetGroup", () => {
        it("should push uhUuid from scope.groupingInclude into scope.resetInclude", () => {
            scope.includeCheck = true;
            scope.groupingInclude = [{ uhUuid: "testId1" }, { uhUuid: "testId2" }];
            scope.resetGroup();
            expect(scope.resetResults).toEqual(scope.groupingInclude);
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

        it("should set scope.resetResults to [] if both exclude and include is false", () => {
            scope.excludeCheck = false;
            scope.includeCheck = false;
            scope.resetGroup();
            expect(scope.resetResults).toEqual([]);
        });

        it("should call displayResetGroupModal", () => {
            spyOn(scope, "displayResetGroupModal").and.callThrough();
            scope.resetGroup();
            expect(scope.displayResetGroupModal).toHaveBeenCalled();
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

    describe("displaySuccessfulGroupResetModal", () => {
        beforeEach(() => {
            scope.displaySuccessfulGroupResetModal(scope.group);
        });

        it("should close successfulGroupResetModalInstance", () => {
            spyOn(scope.successfulGroupResetModalInstance, "close").and.callThrough();
            scope.closeSuccessfulGroupResetModal();
            expect(scope.successfulGroupResetModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("displayEmptyGroupModal", () => {
        it("should check that the displayEmptyGroupModal is displayed", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.displayEmptyGroupModal();
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("displaySuccessfulGroupResetModal", () => {
        it("should set group to group that is passed in", () => {
            expect(scope.group).toBe("");
            scope.displaySuccessfulGroupResetModal("admin");
            expect(scope.group).toBe("admin");
        });

        it("should check that the resetNotifModalInstance is displayed", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.displaySuccessfulGroupResetModal();
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("proceedResetGroup", () => {
        beforeEach(() => {
            scope.displayResetGroupModal(scope.group);
        });

        it("should close resetModalInstance", () => {
            spyOn(scope.resetModalInstance, "close").and.callThrough();
            scope.proceedResetGroup();
            expect(scope.resetModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("cancelResetGroup", () => {
        beforeEach(() => {
            scope.displayResetGroupModal(scope.group);
        });

        it("should dismiss resetModalInstance", () => {
            spyOn(scope.resetModalInstance, "dismiss").and.callThrough();
            scope.cancelResetGroup();
            expect(scope.resetModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe("closeEmptyGroupModal", () => {
        beforeEach(() => {
            scope.displayEmptyGroupModal();
        });

        it("should dismiss emptyGroupModalInstance", () => {
            spyOn(scope.emptyGroupModalInstance, "dismiss").and.callThrough();
            scope.closeEmptyGroupModal();
            expect(scope.emptyGroupModalInstance.dismiss).toHaveBeenCalled();
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

        it("should reset syncDestArray and resetResults arrays", () => {
            scope.syncDestArray = ["test"];
            scope.resetResults = ["test"];

            scope.resetGroupingInformation();

            expect(scope.syncDestArray).toEqual([]);
            expect(scope.resetResults).toEqual([]);
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

    describe("displayPreferenceErrorModal", () => {
        it("should check that the preferenceErrorModalInstance is displayed", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.displayPreferenceErrorModal();
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("closePreferenceError", () => {
        beforeEach(() => {
            scope.displayPreferenceErrorModal();
        });

        it("should close preferenceErrorModalInstance", () => {
            spyOn(scope.preferenceErrorModalInstance, "close").and.callThrough();
            scope.closePreferenceError();
            expect(scope.preferenceErrorModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("updateAllowOptOut", () => {
        it("should call groupings service", () => {
            spyOn(gs, "setOptOut").and.callThrough();
            scope.updateAllowOptOut();
            expect(gs.setOptOut).toHaveBeenCalled();
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

    describe("displaySyncDestModal", () => {
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

        it("should display syncDestInstance and set selectedSyncDest", () => {
            spyOn(scope, "setSyncDestInArray").and.callThrough();
            spyOn(uibModal, "open").and.callThrough();
            scope.syncDestArray.push(testSyncDest);
            scope.setSyncDestInArray(testSyncDest.name, true);

            scope.displaySyncDestModal(testSyncDest.name);

            expect(scope.setSyncDestInArray).toHaveBeenCalled();
            expect(uibModal.open).toHaveBeenCalled();
            expect(scope.selectedSyncDest).toEqual(scope.getEntireSyncDestInArray(testSyncDest.name));
        });
    });

    describe("displayOwnerErrorModal", () => {
        it("should set loading to false", () => {
            scope.loading = true;
            scope.displayOwnerErrorModal();
            expect(scope.loading).toBeFalse();
        });

        it("should check that the OwnerErrorModalInstance is displayed", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.displayOwnerErrorModal();
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("transferMembersFromPageToCheckboxObject", () => {
        let currentPage;
        beforeEach(() => {
            currentPage = [
                {
                    firstName: "test",
                    inBasis: "No",
                    lastName: "iwa",
                    name: "test i wa",
                    uhUuid: "1111",
                    uid: "testiwa"
                },
                {
                    firstName: "test",
                    inBasis: "No",
                    lastName: "iwa",
                    name: "test i wa",
                    uhUuid: "1112",
                    uid: "testiwa"
                }
            ];
            scope.membersInCheckboxList = {
                1112: true,
                1111: true
            };
        });
        it("should make the members in the checkbox list false", () => {
            scope.transferMembersFromPageToCheckboxObject(currentPage);
            expect(scope.membersInCheckboxList).toEqual({
                1112: false,
                1111: false
            });
        });
    });

    describe("toggleSelectAllCheckbox", () => {
        beforeEach(() => {
            scope.pagedItemsInclude = [
                [
                    {
                        firstName: "iamtst01",
                        inBasis: "No",
                        lastName: "iamtst01",
                        name: "iamtst01",
                        uhUuid: "12345678",
                        uid: "testiwa"
                    }
                ]
            ];
            scope.membersInCheckboxList = scope.pagedItemsInclude;
        });

<<<<<<< HEAD
        it("should set PageSelected to true if false", () => {
=======
        it("should call assignListToGroup", () => {
            spyOn(scope, "assignListToGroup").and.callThrough();
            scope.toggleSelectAllCheckbox("Include", 9);
            expect(scope.assignListToGroup).toHaveBeenCalled();
        });

        it("should check the Select All checkbox with the Enter key", () => {
>>>>>>> 961678e4 (Rewrite the Select Page checkbox (#907))
            scope.pageSelected = false;
            scope.toggleSelectAllCheckbox("Include", 13);
            expect(scope.pageSelected).toBeTrue();
        });
<<<<<<< HEAD
        it("should set PageSelected to false if true", () => {
=======

        it("should uncheck the Select All checkbox with the Enter key", () => {
>>>>>>> 961678e4 (Rewrite the Select Page checkbox (#907))
            scope.pageSelected = true;
            scope.toggleSelectAllCheckbox("Include", 13);
            expect(scope.pageSelected).toBeFalse();
        });
<<<<<<< HEAD
        it("should set the membersInCheckboxList.uhUuid to the value of scope.pageSelected", () => {
            scope.pageSelected = false;
            scope.toggleCheckAllSelection("Include", "page");
            expect(scope.membersInCheckboxList[((scope.pagedItemsInclude[0][0]).uhUuid)]).toEqual(true);
        });
        it("should set the membersInCheckboxList.uhUuid to the value of scope.pageSelected", () => {
            scope.pageSelected = true;
            scope.toggleCheckAllSelection("Include", "page");
            expect(scope.membersInCheckboxList[((scope.pagedItemsInclude[0][0]).uhUuid)]).toEqual(false);
=======

        it("should check the Select All checkbox", () => {
            scope.pageSelected = false;
            scope.toggleSelectAllCheckbox("Include", 9);
            expect(scope.pageSelected = true);
        });

        it("should uncheck the Select All checkbox", () => {
            scope.pageSelected = false;
            scope.toggleSelectAllCheckbox("Include", 9);
            expect(scope.pageSelected = true);
        });

        it("should check all checkboxes", () => {
            scope.pageSelected = true;
            scope.toggleSelectAllCheckbox("Include", 9);
            for (let i in scope.membersInCheckboxList[0]) {
                if (scope.membersInCheckboxList[0].length !== 0) {
                    const memberUuid = scope.pagedItemsInclude[0][Number(i)].uhUuid;
                    expect(scope.membersInCheckboxList[Number(memberUuid)]).toEqual(true);
                }
            }
        });

        it("should uncheck all checkboxes", () => {
            scope.pageSelected = false;
            scope.toggleSelectAllCheckbox("Include", 9);
            for (let i in scope.membersInCheckboxList[0]) {
                if (scope.membersInCheckboxList[0].length !== 0) {
                    const memberUuid = scope.pagedItemsInclude[0][Number(i)].uhUuid;
                    expect(scope.membersInCheckboxList[Number(memberUuid)]).toEqual(false);
                }
            }
        });

        it("should call updateCheckboxEventListeners", () => {
            spyOn(scope, "updateCheckboxEventListeners").and.callThrough();
            scope.toggleSelectAllCheckbox("Include", 9);
            expect(scope.updateCheckboxEventListeners).toHaveBeenCalled();
        });

        it("should change paginationPageChange from true to false", () => {
            scope.paginationPageChange = true;
            scope.toggleSelectAllCheckbox("Include", 9);
            expect(scope.paginationPageChange).toBeFalse();
        });
    });

    describe("toggleSingleCheckbox", () => {
        let testUser;
        beforeEach(() => {
            scope.membersInCheckboxList = scope.pagedItemsInclude;
            testUser = scope.membersInCheckboxList[0][0];
        });

        it("should call assignListToGroup", () => {
            spyOn(scope, "assignListToGroup").and.callThrough();
            scope.toggleSingleCheckbox("Include", testUser, 9);
            expect(scope.assignListToGroup).toHaveBeenCalled();
        });

        it("should call updateCheckboxEventListeners", () => {
            spyOn(scope, "updateCheckboxEventListeners").and.callThrough();
            scope.toggleSingleCheckbox("Include", testUser, 9);
            expect(scope.updateCheckboxEventListeners).toHaveBeenCalled();
        });

        it("should change paginationPageChange from true to false", () => {
            scope.paginationPageChange = true;
            scope.toggleSingleCheckbox("Include", testUser, 9);
            expect(scope.paginationPageChange).toBeFalse();
        });
    });

    describe("assignListToGroup", () => {
        it("should set list with exclude values", () => {
            scope.groupingMembers = {
                allMembers: scope.groupingInclude,
                membersOnPage: scope.pagedItemsInclude,
                pageNumber: scope.currentPageInclude
            };

            scope.assignListToGroup("Exclude");
            expect(scope.groupingMembers.allMembers).toEqual(scope.groupingExclude);
            expect(scope.groupingMembers.membersOnPage).toEqual(scope.pagedItemsExclude);
            expect(scope.groupingMembers.pageNumber).toEqual(scope.currentPageExclude);
        });

        it("should set list with include values", () => {
            scope.groupingMembers = {
                allItems: scope.groupingExclude,
                pageItems: scope.pagedItemsExclude,
                pageNumber: scope.currentPageExclude
            };
            scope.assignListToGroup("Include");
            expect(scope.groupingMembers.allMembers).toEqual(scope.groupingInclude);
            expect(scope.groupingMembers.membersOnPage).toEqual(scope.pagedItemsInclude);
            expect(scope.groupingMembers.pageNumber).toEqual(scope.currentPageInclude);
        });
    });

    describe("checkMainSelectAllCheckbox", () => {
        beforeEach(() => {
            scope.testGroupingMembers = {
                allMembers: scope.groupingInclude,
                membersOnPage: scope.pagedItemsInclude,
                pageNumber: scope.currentPageInclude
            };
        });

        it("should return true", () => {
            scope.testGroupingMembers.membersOnPage = [];
            const testCheck = scope.checkMainSelectAllCheckbox(scope.testGroupingMembers);
            expect(testCheck).toBeTrue();
        });

        it("should uncheck the Select All checkbox if there are any unchecked checkboxes", () => {
            scope.membersInCheckboxList[scope.testGroupingMembers.membersOnPage[0][2].uhUuid] = false;
            const testCheck = scope.checkMainSelectAllCheckbox(scope.testGroupingMembers);
            expect(testCheck).toBeFalse();
        });

        it("should check the Select All checkbox if all checkboxes are checked", () => {
            const testCheck = scope.checkMainSelectAllCheckbox(scope.testGroupingMembers);
            expect(testCheck).toBeTrue();
>>>>>>> 961678e4 (Rewrite the Select Page checkbox (#907))
        });
    });

    describe("extractSelectedUsersFromCheckboxes", () => {
        let obj = {};
        let str = "test";
        let expectedResult = "";
        for (let i = 0; i < 10; i++) {
            let ident = str + i.toString();
            let val = Boolean(i % 2);
            obj[`${ident}`] = val;
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

    describe("resetFields", () => {
        it("should reset membersInCheckboxList array", () => {
            scope.membersInCheckboxList = [1, 2, 3];
            scope.resetFields();
            expect(scope.membersInCheckboxList).toEqual({});
        });

        it("should set PageSelected to false", () => {
            scope.pageSelected = true;
            scope.resetFields();
            expect(scope.pageSelected).toBeFalse();
        });

        it("should set waitingForImportResponse to false", () => {
            scope.waitingForImportResponse = true;
            scope.resetFields();
            expect(scope.waitingForImportResponse).toBeFalse();
        });
    });

    describe("exportGroupToCsv", () => {
        it("should call $scope.convertListToCsv with the table passed in", () => {
            spyOn(scope, "convertListToCsv");

            scope.exportGroupToCsv(scope.groupingMembers, "members");
            expect(scope.convertListToCsv).toHaveBeenCalledWith(scope.groupingMembers);

            scope.exportGroupToCsv(scope.groupingBasis, "basis");
            expect(scope.convertListToCsv).toHaveBeenCalledWith(scope.groupingBasis);

            scope.exportGroupToCsv(scope.groupingInclude, "include");
            expect(scope.convertListToCsv).toHaveBeenCalledWith(scope.groupingInclude);

            scope.exportGroupToCsv(scope.groupingExclude, "exclude");
            expect(scope.convertListToCsv).toHaveBeenCalledWith(scope.groupingExclude);
        });

        it("should call $scope.displayApiErrorModal", () => {
            spyOn(scope, "convertListToCsv").and.returnValue(null);
            spyOn(scope, "displayApiErrorModal");
            scope.exportGroupToCsv(scope.groupingMembers, "members");
            expect(scope.displayApiErrorModal).toHaveBeenCalled();
        });

        it("should download the CSV", () => {
            const mockElement = document.createElement("a");
            spyOn(document, "createElement").and.returnValue(mockElement);
            spyOn(mockElement, "click");

            scope.exportGroupToCsv(scope.groupingMembers, "members");

            expect(mockElement.href).toBe("data:text/csv;charset=utf-8,Last,First,Username,UH%20Number,Email%0D%0AOne,User,user1,00000001,user1@hawaii.edu%0D%0ATwo,User,user2,00000002,user2@hawaii.edu%0D%0AThree,User,user3,00000003,user3@hawaii.edu%0D%0ASeven,User,user7,00000007,user7@hawaii.edu%0D%0A");
            expect(mockElement.download).toBe("grouping1:members_list.csv");
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

    describe("isGroupingEmpty", () => {
        it("should return false if there are members in a grouping", () => {
            expect(scope.isGroupingEmpty()).toBeFalse();
        });

        it("should return true if there are no members in a grouping", () => {
            scope.groupingMembers = [];
            scope.groupingInclude = [];
            scope.groupingExclude = [];
            scope.groupingBasis = [];

            expect(scope.isGroupingEmpty()).toBeTrue();
        });
    });

    describe("disableResetCheckboxes", () => {
        it("should set includeCheck to false and includeDisable to true when there are no members in the include group", () => {
            scope.groupingInclude = [];
            scope.disableResetCheckboxes();
            expect(scope.includeCheck).toBeFalse();
            expect(scope.includeDisable).toBeTrue();
        });

        it("should set excludeCheck to false and excludeDisable to true when there are no members in the exclude group", () => {
            scope.groupingExclude = [];
            scope.disableResetCheckboxes();
            expect(scope.excludeCheck).toBeFalse();
            expect(scope.excludeDisable).toBeTrue();
        });

    });

});

describe("SyncDestModalController", () => {
    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    let scope;
    let controller;
    let uibModalInstance;

    beforeEach(inject(($rootScope, $controller, Message) => {
        scope = $rootScope.$new(true);
        uibModalInstance = jasmine.createSpyObj("syncDestInstance", ["dismiss", "close"]);
        controller = $controller("SyncDestModalController", {
            $scope: scope,
            $uibModalInstance: uibModalInstance,
            isSynced: true,
            syncDestDescription: "Sync dest description",
            Message: Message,
        });
    }));

    describe("SyncDestModal", () => {
        it("should close syncDestInstance", () => {
            scope.closeSyncDestModal();
            expect(uibModalInstance.dismiss).toHaveBeenCalled();
        });

        it("should proceed syncDestModal", () => {
            scope.proceedSyncDestModal();
            expect(uibModalInstance.close).toHaveBeenCalled();
        });

        it("should set the sync dest description", () => {
            expect(scope.syncDestDescription).toBe("Sync dest description");
        });

        it("should set the confirmation based on sync state", () => {
            expect(scope.syncDestConfirmationMessage).toContain("enable")
        });
    })
});
