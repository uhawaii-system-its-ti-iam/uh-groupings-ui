/* global inject */

describe("AdminController", function () {

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    let scope;
    let controller;
    let BASE_URL;
    let gs;
    let httpBackend;
    let uibModal;
    let window;

    let fakeModal = {
        result: {
            then: (confirmCallback, cancelCallBack) => {
                this.confirmCallback = confirmCallback;
                this.cancelCallBack = cancelCallBack;
            }
        },
        close: (item) => {
            //the user clicks OK on the modal dialog, call the stored callback w/ the selected item
            this.result.confirmCallback(item);
        },
        dismiss: (type) => {
            // The user clicked on cancel, call the stored cancel callback
            this.result.cancelCallBack(type);
        }
    };

    beforeEach(inject(($rootScope, $controller, $uibModal, _BASE_URL_, _$httpBackend_, groupingsService, _$window_) => {
        scope = $rootScope.$new(true);
        window = {
            location: {
                pathname: "/uhgroupings/",
                href: _$window_
            },
            open(href) {}
        };
        controller = $controller("AdminJsController", {
            $scope: scope,
            $window: window
        });
        BASE_URL = _BASE_URL_;
        gs = groupingsService;
        httpBackend = _$httpBackend_;
        uibModal = $uibModal;
        spyOn($uibModal, "open").and.returnValue(fakeModal);
    }));

    it("should define the admin controller", () => {
        expect(controller).toBeDefined();
    });

    describe("getGroupingAdminsCallbackOnSuccess", () => {
        let res = {};
        beforeEach(() => {
            res = {
                "resultCode": "resultCode",
                "groupPath": "groupPath",
                "members": [
                    {
                        "uid": "uid",
                        "uhUuid": "uhUuid",
                        "name": "name",
                        "firstName": "firstName",
                        "lastName": "lastName",
                        "resultCode": "resultCode"
                    },
                    {
                        "uid": "uid",
                        "uhUuid": "uhUuid",
                        "name": "name",
                        "firstName": "firstName",
                        "lastName": "lastName",
                        "resultCode": "resultCode"
                    }
                ]
            }
        });
        it("should call objToPageArray", () => {
            spyOn(scope, "objToPageArray").and.callThrough();
            scope.getGroupingAdminsCallbackOnSuccess(res);
            expect(scope.objToPageArray).toHaveBeenCalled();
        });

        it("should instantiate scope.pagedItemsAdmins", () => {
            scope.getGroupingAdminsCallbackOnSuccess(res);
            expect(scope.pagedItemsAdmins).toBeDefined();
        });

        it("should set scope.loading to false", () => {
            scope.getGroupingAdminsCallbackOnSuccess(res);
            expect(scope.loading).toBeFalse();
        });
    });

    describe("getAllGroupingsCallbackOnSuccess", () => {
        let res = {};
        beforeEach(() => {
            res = {
                "groupingPaths": [
                    {
                        "path": "path",
                        "name": "name",
                        "description": "description"
                    },
                    {
                        "path": "path",
                        "name": "name",
                        "description": "description"
                    }
                ],
            };
        });

        it("should call objToPageArray", () => {
            spyOn(scope, "objToPageArray").and.callThrough();
            scope.getAllGroupingsCallbackOnSuccess(res);
            expect(scope.objToPageArray).toHaveBeenCalled();
        });

        it("should instantiate scope.groupingsList", () => {
            scope.getAllGroupingsCallbackOnSuccess(res);
            expect(scope.groupingsList).toBeDefined();
        });

        it("should set scope.loading to false", () => {
            scope.getAllGroupingsCallbackOnSuccess(res);
            expect(scope.loading).toBeFalse();
        });
    });

    describe("init", () => {
        beforeEach(() => {
            sessionStorage.clear();
        });

        it("should call groupingsService.getGroupingAdmins", () => {
            spyOn(gs, "getGroupingAdmins").and.callThrough();
            scope.init();
            expect(gs.getGroupingAdmins).toHaveBeenCalled();
        });

        it("should call groupingsService.getAllGroupings", () => {
            spyOn(gs, "getAllGroupings").and.callThrough();
            scope.init();
            expect(gs.getAllGroupings).toHaveBeenCalled();
        });

        it("should call scope.initManagePersonGrouping", () => {
            const managePersonGrouping = {name: "testName", path: "testPath"};
            spyOn(scope, "initManagePersonGrouping");
            sessionStorage.setItem("managePersonGrouping", JSON.stringify(managePersonGrouping));
            scope.init();
            expect(scope.initManagePersonGrouping).toHaveBeenCalledWith(managePersonGrouping);
        });
    });

    describe("searchForUserGroupingInformationOnSuccessCallback", () => {
        let res;
        beforeEach(() => {
            res = [
                {
                    "identifier": null,
                    "person": null,
                    "group": null,
                    "path": "path-to-grouping-name",
                    "name": "grouping-name",
                    "inBasis": false,
                    "inInclude": false,
                    "inExclude": false,
                    "inOwner": true,
                    "inBasisAndInclude": false,
                    "optOutEnabled": true,
                    "optInEnabled": false,
                    "selfOpted": false
                }];

        });

        it("should call scope.filter", () => {
            spyOn(scope, "filter").and.callThrough();
            scope.searchForUserGroupingInformationOnSuccessCallback(res);
            expect(scope.filter).toHaveBeenCalled();
        });

        it("should set scope.personList equal to api response", () => {
            scope.searchForUserGroupingInformationOnSuccessCallback(res);
            expect(scope.personList).toEqual(res);
        });

        it("should set scope.user equal to scope.personToLookup", () => {
            scope.searchForUserGroupingInformationOnSuccessCallback(res);
            expect(expect(scope.user).toEqual(scope.personToLookup));
        });

        it("should set scope.loading to be false", () => {
            scope.searchForUserGroupingInformationOnSuccessCallback(res);
            expect(scope.loading).toBeFalse();
        });
    });

    describe("searchForUserGroupingInformationOnErrorCallback", () => {
        beforeEach(() => {
            scope.searchForUserGroupingInformationOnErrorCallback([]);
        });
        it("should set scope.loading to be false", () => {
            expect(scope.loading).toBeFalse();
        });
        it("should set scope.user equal to scope.personToLookup", () => {
            expect(expect(scope.user).toEqual(scope.personToLookup));
        });
    });

    describe("searchForUserGroupingInformation", () => {
        it("should set emptyInput to true", () => {
            scope.searchForUserGroupingInformation();
            expect(scope.emptyInput).toBeTrue();
            expect(scope.personList).toEqual([]);
            expect(scope.currentManagePerson).toEqual("");
        });
        it("should set emptyInput to be true and personList to empty array", () => {
            scope.personToLookup = "";
            scope.searchForUserGroupingInformation();
            expect(scope.emptyInput).toBeTrue();
            expect(scope.personList).toEqual([]);
        });
        it("should set invalidInput to be true", () => {
            scope.invalidInput = false;
            scope.personToLookup = "123$";
            scope.searchForUserGroupingInformation();
            expect(scope.invalidInput).toBeTrue();
        });
        it("should call groupingsService.managePersonResults", () => {
            scope.personToLookup = "iamtst01";
            spyOn(gs, "managePersonResults").and.callThrough();
            scope.searchForUserGroupingInformation();
            expect(gs.managePersonResults).toHaveBeenCalled();
        });
        it("should call groupingsService.getMemberAttributeResults", () => {
            scope.personToLookup = "iamtst01";
            spyOn(gs, "getMemberAttributeResults").and.callThrough();
            scope.searchForUserGroupingInformation();
            expect(gs.getMemberAttributeResults).toHaveBeenCalled();
        });
        it("should clear the table", () => {
            scope.personToLookup = "j";
            scope.searchForUserGroupingInformation();
            expect(scope.personList).toEqual([]);

            scope.personToLookup = "*";
            scope.searchForUserGroupingInformation();
            expect(scope.personList).toEqual([]);
        });
    });

    describe("setCurrentManagePerson", () => {
        it("should not set currentManagePerson and set invalidInput to true", () => {
            scope.uhUuid = null;
            scope.invalidInput = false;
            scope.setCurrentManagePerson("");
            expect(scope.invalidInput).toBeTrue();
            expect(scope.currentManagePerson).toEqual("");
        });
        it("should set currentManagePerson and not set invalidInput", () => {
            scope.uhUuid = "notnull";
            scope.invalidInput = false;
            scope.setCurrentManagePerson("iamtst");
            expect(scope.invalidInput).toBeFalse();
            expect(scope.currentManagePerson).not.toEqual("");
        });
    });

    describe("checkSoleOwner", () => {
        let res = {username: "testUsername", name: "testName", uhUuid: "testId"};
        it("should empty soleOwnerGroupingNames", () => {
            scope.soleOwnerGroupingNames = ["test1", "test2"];
            scope.selectedOwnedGroupings = ["test"];
            scope.checkSoleOwner(res);
            expect(scope.soleOwnerGroupingNames).toEqual([]);
        });

        it("should call the removeFromGroupsCallbackOnSuccess when selectedOwnedGroupings.length is 0", () => {
            scope.selectedOwnedGroupings = [];
            spyOn(scope, "removeFromGroupsCallbackOnSuccess");
            scope.checkSoleOwner(res);
            expect(scope.removeFromGroupsCallbackOnSuccess).toHaveBeenCalled();
        });

        it("should not call removeFromGroupsCallbackOnSuccess if selectedOwnedGroupings.length === 0", () => {
            scope.selectedOwnedGroupings = ["test"];
            spyOn(scope, "removeFromGroupsCallbackOnSuccess").and.callThrough();

            expect(scope.removeFromGroupsCallbackOnSuccess).not.toHaveBeenCalled();
        });
    });

    describe("removeFromGroupsCallbackOnSuccess", () => {
        let res;
        beforeEach(() => {
            scope.personToLookup = "iamtst01";
            res = {
                "uid": "iamtst01",
                "uhUuid": "iamtst01",
                "givenName": "tst01name",
                "cn": "tst01name",
                "sn": "tst01name"
            };
        });

        it("should set scope.emptySelect to true when scope.selectedGroupingsPaths is empty", () => {
            scope.removeFromGroupsCallbackOnSuccess(res);
            expect(scope.emptySelect).toBeTrue();
        });
    });

    describe("removeFromGroups", () => {
        beforeEach(() => {
            scope.personToLookup = "";
        });

        it("should call groupingsService.getMemberAttributeResults", () => {
            spyOn(gs, "getMemberAttributeResults").and.callThrough();
            scope.removeFromGroups();
            expect(gs.getMemberAttributeResults).toHaveBeenCalled();
        });
    });

    describe("createGroupPathsAndNames", () => {
        let selectedGroupingsNames, selectedGroupingsPaths, selectedOwnedGroupings, selectedOwnedGroupingsNames,
            currentPage;
        beforeEach(() => {
            selectedGroupingsNames = [];
            selectedGroupingsPaths = [];
            selectedOwnedGroupings = [];
            selectedOwnedGroupingsNames = [];
            currentPage = [
                {
                    inBasis: false,
                    inInclude: false,
                    inExclude: false,
                    inOwner: true,
                    isSelected: true,
                    path: "grouping:grouping-name-group0",
                    name: "grouping-name0"
                },
                {
                    inBasis: false,
                    inInclude: true,
                    inExclude: false,
                    inOwner: false,
                    isSelected: true,
                    path: "grouping:grouping-name-group1",
                    name: "grouping-name1"
                },
                {
                    inBasis: false,
                    inInclude: false,
                    inExclude: false,
                    inOwner: true,
                    isSelected: false,
                    path: "grouping:grouping-name-group2",
                    name: "grouping-name2"
                }
            ];
        });

        it("should concatenate the subgroup onto the grouping paths", () => {
            scope.createGroupPathsAndNames(currentPage, selectedGroupingsNames, selectedGroupingsPaths, selectedOwnedGroupings, selectedOwnedGroupingsNames);
            expect(selectedGroupingsPaths[0]).toEqual("grouping:grouping-name-group0:owners");
            expect(selectedGroupingsPaths[1]).toEqual("grouping:grouping-name-group1:include");
        });

        it("should create the same amount of paths as names", () => {
            scope.createGroupPathsAndNames(currentPage, selectedGroupingsNames, selectedGroupingsPaths, selectedOwnedGroupings, selectedOwnedGroupingsNames);
            expect(selectedGroupingsPaths.length).toEqual(selectedGroupingsNames.length);
        });

        it("should fetch the grouping names", () => {
            scope.createGroupPathsAndNames(currentPage, selectedGroupingsNames, selectedGroupingsPaths, selectedOwnedGroupings, selectedOwnedGroupingsNames);
            expect(selectedGroupingsNames[0]).toEqual("grouping-name0");
            expect(selectedGroupingsNames[1]).toEqual("grouping-name1");
        });

        it("should not fetch data that is not selected", () => {
            scope.createGroupPathsAndNames(currentPage, selectedGroupingsNames, selectedGroupingsPaths, selectedOwnedGroupings, selectedOwnedGroupingsNames);
            expect(selectedGroupingsNames[2]).toBeUndefined();
            expect(selectedGroupingsPaths[2]).toBeUndefined();
            expect(selectedGroupingsNames.length).toEqual(2);
            expect(selectedGroupingsPaths.length).toEqual(2);
        });
    });

    describe("updateCheckBoxes", () => {
        beforeEach(() => {
            scope.pagedItemsPerson[scope.currentPagePerson] = {
                inBasis: false,
                inExclude: false,
                inInclude: true,
                isSelected: false
            };
        });

        it("should negate scope.checkAll", () => {
            let checkAll = scope.checkAll;
            scope.updateCheckBoxes();
            expect(scope.checkAll).toEqual(!checkAll);
        });
    });

    describe("addAdmin", () => {
        const uhIdentifiers = ["testiwta"];
        const results = { resultCode: "SUCCESS", invalid: [], results: uhIdentifiers };
        const invalid = { resultCode: "FAILURE", invalid: uhIdentifiers, results: [] };

        beforeEach(() => {
            httpBackend.whenGET("currentUser").passThrough();
        });

        it("should check if the admin to add is empty", () => {
            scope.emptyInput = false;
            scope.addAdmin();
            expect(scope.emptyInput).toBeTrue();
        });

        it("should check that the admin to add is in the admin list", () => {
            scope.containsInput = false;
            scope.adminToAdd = "testiwta";
            scope.adminsList = [{uid: "testiwta", uhUuid: "99997027" }];
            scope.addAdmin();

            expect(scope.user).toBe(scope.adminToAdd);
            expect(scope.listName).toBe("admins");
            expect(scope.containsInput).toBeTrue();
        });

        it("should display the add modal", () => {
            spyOn(scope, "displayAddModal");
            scope.adminToAdd = uhIdentifiers;
            scope.addAdmin();

            httpBackend.expectPOST(BASE_URL + "members", [uhIdentifiers]).respond(200, results);
            httpBackend.flush();

            expect(scope.user).toEqual(scope.adminToAdd);
            expect(scope.displayAddModal).toHaveBeenCalled();
        });
    });

    describe("removeAdmin", () => {
        beforeEach(() => {
            scope.pagedItemsAdmins[0] = {name: "zzz", username: "zzz", uhUuid: "zzz"};
        });
        it("should call scope.displayRemoveModal", () => {
            scope.adminsList = [
                {
                    name: "iamtst01",
                    username: "iamtst01",
                    uhUuid: "iamtst01"
                },
                {
                    name: "iamtst02",
                    username: "iamtst02",
                    uhUuid: "iamtst02"
                },
                {
                    name: "iamtst03",
                    username: "iamtst03",
                    uhUuid: "iamtst03"
                }
            ];
            spyOn(scope, "displayRemoveModal").and.callThrough();
            scope.removeAdmin(0, 0);
            expect(scope.displayRemoveModal).toHaveBeenCalled();
        });
        it("should call scope.displayRemoveErrorModal", () => {
            spyOn(scope, "displayRemoveErrorModal").and.callThrough();
            scope.removeAdmin(0, 0);
            expect(scope.displayRemoveErrorModal).toHaveBeenCalled();
        });
    });

    describe("displayRemoveFromGroupsModal", () => {
        let options = { member: { uhUuid: "testId" }, groupPaths: "testPath", listNames: ["testList"] };

        it("should set scope variables to passed in option's object", () => {
            scope.memberToRemove = {};
            scope.groupPaths = "badPath";
            scope.listNames = ["badList"];
            scope.displayRemoveFromGroupsModal(options);

            expect(scope.memberToRemove).toEqual({uhUuid: "testId"});
            expect(scope.groupPaths).toBe("testPath");
            expect(scope.listNames).toBe("testList");
        });

        it("should call showWarningRemovingSelf function", () => {
            spyOn(scope, "showWarningRemovingSelf").and.callThrough();
            scope.displayRemoveFromGroupsModal(options);

            expect(scope.showWarningRemovingSelf).toHaveBeenCalled();
        });

        it("should call groupingsService", () => {
            spyOn(gs, "getMemberAttributeResults").and.callThrough();
            scope.displayRemoveFromGroupsModal(options);

            expect(gs.getMemberAttributeResults).toHaveBeenCalled();
        });
    });

    describe("displayGroupingInNewTab", () => {
        afterEach(() => {
            sessionStorage.clear();
        });

        it("should set the items managePersonGrouping and personToLookup", () => {
            spyOn(sessionStorage, "removeItem");
            scope.personToLookup = "testId";
            scope.displayGroupingInNewTab("testName", "testPath");
            expect(JSON.parse(sessionStorage.getItem("managePersonGrouping")))
                .toEqual({name: "testName", path: "testPath"});
            expect(sessionStorage.getItem("personToLookup")).toBe(scope.personToLookup);
        });

        it("should remove the managePersonGrouping item from sessionStorage at the end", () => {
            scope.personToLookup = "testId";
            scope.displayGroupingInNewTab("testName", "testPath");
            expect(sessionStorage.getItem("managePersonGrouping")).toBeNull();
            expect(sessionStorage.getItem("personToLookup")).toBe(scope.personToLookup);
        });

        it("should open a new /admin tab", () => {
            spyOn(window, "open");
            scope.personToLookup = "testId";
            scope.displayGroupingInNewTab("testGroupingName", "testGroupingPath");
            expect(window.open).toHaveBeenCalledWith("admin");
        });
    });

    describe("initManagePersonGrouping", () => {
        const managePersonGrouping = {name: "testName", path: "testPath"};

        beforeEach(() => {
            spyOn(scope, "getGroupingInformation");
            spyOn(scope, "toggleShowAdminTab");
            sessionStorage.setItem("managePersonGrouping", JSON.stringify(managePersonGrouping));
        });

        it("should set fromManagePerson and showGrouping to true", () => {
            scope.fromManagePerson = false;
            scope.showGrouping = false;
            scope.initManagePersonGrouping(managePersonGrouping);
            expect(scope.fromManagePerson).toBeTrue();
            expect(scope.showGrouping).toBeTrue();
        });

        it("should set scope.selectedGrouping to the managePersonGrouping passed in", () => {
            scope.initManagePersonGrouping(managePersonGrouping);
            expect(scope.selectedGrouping).toBe(managePersonGrouping);
        });

        it("should call scope.getGroupingInformation and scope.toggleShowAdminTab", () => {
            scope.initManagePersonGrouping(managePersonGrouping);
            expect(scope.getGroupingInformation).toHaveBeenCalled();
            expect(scope.toggleShowAdminTab).toHaveBeenCalled();
        });

        it("should remove the managePersonGrouping item from sessionStorage", () => {
            scope.initManagePersonGrouping(managePersonGrouping);
            expect(sessionStorage.getItem("managePersonGrouping")).toBeNull();
        });
    });

    describe("returnToManagePerson", () => {
        const personToLookup = "testId";

        beforeEach(() => {
            spyOn(scope, "searchForUserGroupingInformation");
            spyOn(gs, "getGroupingAdmins");
            spyOn(gs, "getAllGroupings");
            sessionStorage.setItem("personToLookup", personToLookup);
        });

        it("should set fromManagePerson and showGrouping to false", () => {
            scope.fromManagePerson = true;
            scope.showGrouping = true;
            scope.returnToManagePerson();
            expect(scope.fromManagePerson).toBeFalse();
            expect(scope.showGrouping).toBeFalse();
        });

        it("should open the manage-person tab", () => {
            spyOn($.fn, "tab");
            spyOn(document, "getElementById");
            scope.returnToManagePerson();
            expect($.fn.tab).toHaveBeenCalledWith("show");
            expect(document.getElementById).toHaveBeenCalledWith("manage-person-tab");
        });

        it("should set scope.personToLookup to personToLookup from sessionStorage", () => {
            scope.returnToManagePerson();
            expect(sessionStorage.getItem("personToLookup")).toBe(personToLookup);
        });

        it("should call scope.searchForUserGroupingInformation and gs.getGroupingAdmins", () => {
            scope.returnToManagePerson();
            expect(scope.searchForUserGroupingInformation).toHaveBeenCalled();
            expect(gs.getGroupingAdmins).toHaveBeenCalled();
        });
    });

    describe("clearManagePersonCheckboxes", () => {
        beforeEach(() => {
            scope.pagedItemsPerson[scope.currentPagePerson] = {
                inBasis: false,
                inExclude: false,
                inInclude: true,
                isSelected: true
            };
        });

        it("should negate scope.checkAll", () => {
            scope.checkAll = true;
            scope.clearManagePersonCheckboxes();
            expect(scope.checkAll).toEqual(false);
        });
    });

    describe("proceedRemoveFromGroupsModal", () => {
        beforeEach(() => {
            scope.removeFromGroupsModalInstance = {
                close: () => {}
            };
        });

        it("should close removeFromGroupsModalInstance", () => {
            spyOn(scope.removeFromGroupsModalInstance, "close").and.callThrough();
            scope.proceedRemoveFromGroupsModal();
            expect(scope.removeFromGroupsModalInstance.close).toHaveBeenCalled();
        });
    });

    describe("cancelRemoveFromGroupsModal", () => {
        beforeEach(() => {
            scope.removeFromGroupsModalInstance = {
                dismiss: () => {}
            };
        });

        it("should dismiss the removeFromGroupsModal", () => {
            spyOn(scope.removeFromGroupsModalInstance, "dismiss").and.callThrough();
            scope.cancelRemoveFromGroupsModal();
            expect(scope.removeFromGroupsModalInstance.dismiss).toHaveBeenCalled();
        });

        it("should clear all checkboxes in Manage Person", () => {
            spyOn(scope, "clearManagePersonCheckboxes");
            scope.listName = "admins";
            scope.cancelRemoveFromGroupsModal();
            expect(scope.clearManagePersonCheckboxes).toHaveBeenCalled();
        });
    });

    describe("closeRemoveErrorModal", () => {
        beforeEach(() => {
            scope.removeErrorModalInstance = {
                close: () => {
                }
            };
        });

        it("should close removeErrorModalInstance", () => {
            spyOn(scope.removeErrorModalInstance, "close").and.callThrough();
            scope.closeRemoveErrorModal();
            expect(scope.removeErrorModalInstance.close).toHaveBeenCalled();
        });

        it("should call clearManagePersonCheckboxes", () => {
            spyOn(scope, "clearManagePersonCheckboxes").and.callThrough();
            scope.listName = "admins";
            scope.closeRemoveErrorModal();
            expect(scope.clearManagePersonCheckboxes).toHaveBeenCalled();
        });
    });

    describe ("handleGroupingOwnersOnSuccess", () => {
        const res = {
            members: [
                {
                    uid: "testiwta",
                    uhUuid: "99997010",
                    name: "testiwta"
                },
                {
                    uid: "testiwtb",
                    uhUuid: "99997027",
                    name: "testiwtb"
                },
                {
                    uid: "testiwtc",
                    uhUuid: "99997033",
                    name: "testiwtc"
                }]
            };

        it("should set scope.owners equal to API response", () => {
            scope.handleGroupingOwnersOnSuccess(res);
            expect(scope.owners).toEqual(res.members);
        });

        it("should call displayGroupingOwnersModal", () => {
            spyOn(scope, "displayGroupingOwnersModal");
            scope.handleGroupingOwnersOnSuccess(res);
            expect(scope.displayGroupingOwnersModal).toHaveBeenCalled();
        });
    });

    describe ("handleGroupingOwnersOnError", () => {
        it("should set scope.loading to be false", () => {
            scope.handleGroupingOwnersOnError();
            expect(scope.loading).toBeFalse();
        });
    });

    describe("displayGroupingOwnersModal", () => {
        it("should open up groupingOwnersModal", () => {
            scope.displayGroupingOwnersModal();
            expect(uibModal.open).toHaveBeenCalled();
        });

        it("should define scope.groupingOwnersModal", () => {
            scope.displayGroupingOwnersModal();
            expect(scope.groupingOwnersModal).toBeDefined();
        });

        it("should define correct modal path", () => {
            scope.displayGroupingOwnersModal();
            expect(uibModal.open).toHaveBeenCalledWith({
                templateUrl: "modal/groupingOwnersModal",
                scope,
                backdrop: "static",
                ariaLabelledBy: "grouping-owners-modal"
            });
        });

        it("should define scope.displayGroupingOwnersModalOnClose", () => {
            scope.displayGroupingOwnersModal();
            expect(scope.displayGroupingOwnersModalOnClose).toBeDefined();
        });
    });

    describe("getGroupingOwnersOnClick", () => {
        const groupingPath = "grouping-path";
        it("should assign ownersModalGroupingPath to be grouping-path", () => {
            scope.getGroupingOwnersOnClick(groupingPath);
            expect(scope.ownersModalGroupingPath).toBe(groupingPath);
        });

        it("should send correct onSuccess and onError functions to groupingOwners", () => {
            spyOn(gs, "groupingOwners");
            scope.getGroupingOwnersOnClick(groupingPath);
            expect(gs.groupingOwners).toHaveBeenCalledWith(
                groupingPath, scope.handleGroupingOwnersOnSuccess, scope.handleGroupingOwnersOnError);
        });
    });

    describe("throwException", () => {
        it("should throw an exception", () => {
            spyOn(gs, "throwException");
            scope.throwException();
            expect(gs.throwException).toHaveBeenCalled();
        });
    });
});
