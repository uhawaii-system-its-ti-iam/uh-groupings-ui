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

        it("should call scope.initManageSubjectGrouping", () => {
            const manageSubjectGrouping = {name: "testName", path: "testPath"};
            spyOn(scope, "initManageSubjectGrouping");
            sessionStorage.setItem("manageSubjectGrouping", JSON.stringify(manageSubjectGrouping));
            scope.init();
            expect(scope.initManageSubjectGrouping).toHaveBeenCalledWith(manageSubjectGrouping);
        });
    });

    describe("searchForUserGroupingInformationOnSuccessCallback", () => {
        let res;
        beforeEach(() => {
            res = {
                "resultCode": "resultCode",
                "results": [
                    {
                        "path": "path-to-grouping-name",
                        "name": "grouping-name",
                        "inInclude": false,
                        "inExclude": false,
                        "inOwner": false,
                        "inBasisAndInclude": false,
                    }]
            };
        });

        it("should call scope.filter", () => {
            spyOn(scope, "filter").and.callThrough();
            scope.searchForUserGroupingInformationOnSuccessCallback(res);
            expect(scope.filter).toHaveBeenCalled();
        });

        it("should set scope.subjectList equal to api response", () => {
            scope.searchForUserGroupingInformationOnSuccessCallback(res);
            expect(scope.subjectList).toEqual(res.results);
        });

        it("should set scope.user equal to scope.subjectToLookup", () => {
            scope.searchForUserGroupingInformationOnSuccessCallback(res);
            expect(expect(scope.user).toEqual(scope.subjectToLookup));
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
        it("should set scope.user equal to scope.subjectToLookup", () => {
            expect(expect(scope.user).toEqual(scope.subjectToLookup));
        });
    });

    describe("searchForUserGroupingInformation", () => {
        it("should set emptyInput to true", () => {
            scope.searchForUserGroupingInformation();
            expect(scope.emptyInput).toBeTrue();
            expect(scope.subjectList).toEqual([]);
            expect(scope.currentManageSubject).toEqual("");
        });
        it("should set emptyInput to be true and subjectList to empty array", () => {
            scope.subjectToLookup = "";
            scope.searchForUserGroupingInformation();
            expect(scope.emptyInput).toBeTrue();
            expect(scope.subjectList).toEqual([]);
        });
        it("should set invalidInput to be true", () => {
            scope.invalidInput = false;
            scope.subjectToLookup = "123$";
            scope.searchForUserGroupingInformation();
            expect(scope.invalidInput).toBeTrue();
        });
        it("should call groupingsService.manageSubjectResults", () => {
            scope.subjectToLookup = "iamtst01";
            spyOn(gs, "manageSubjectResults").and.callThrough();
            scope.searchForUserGroupingInformation();
            expect(gs.manageSubjectResults).toHaveBeenCalled();
        });
        it("should call groupingsService.getMemberAttributeResults", () => {
            scope.subjectToLookup = "iamtst01";
            spyOn(gs, "getMemberAttributeResults").and.callThrough();
            scope.searchForUserGroupingInformation();
            expect(gs.getMemberAttributeResults).toHaveBeenCalled();
        });
        it("should clear the table", () => {
            scope.subjectToLookup = "j";
            scope.searchForUserGroupingInformation();
            expect(scope.subjectList).toEqual([]);

            scope.subjectToLookup = "*";
            scope.searchForUserGroupingInformation();
            expect(scope.subjectList).toEqual([]);
        });
    });

    describe("setCurrentManageSubject", () => {
        it("should not set currentManageSubject and set invalidInput to true", () => {
            scope.uhUuid = null;
            scope.invalidInput = false;
            scope.setCurrentManageSubject("");
            expect(scope.invalidInput).toBeTrue();
            expect(scope.currentManageSubject).toEqual("");
        });
        it("should set currentManageSubject and not set invalidInput", () => {
            scope.uhUuid = "notnull";
            scope.invalidInput = false;
            scope.setCurrentManageSubject("iamtst");
            expect(scope.invalidInput).toBeFalse();
            expect(scope.currentManageSubject).not.toEqual("");
        });
    });

    describe("checkSoleOwner", () => {
        const res = {
            resultCode: "SUCCESS",
            invalid: [],
            results: [{ uid: "testUid", name: "testName", uhUuid: "testId" }]
        };

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
            scope.subjectToLookup = "iamtst01";
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
            scope.subjectToLookup = "";
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
            scope.pagedItemsSubject[scope.currentPageSubject] = {
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

        it("should check if the admin to add is a departmental account", () => {
            scope.containsDeptAcc = false;
            scope.adminToAdd = "testiwt2";
            scope.addAdmin();

            httpBackend.expectPOST(BASE_URL + "members", ['testiwt2']).respond(200, results);
            httpBackend.flush();

            expect(scope.user).toBe(scope.adminToAdd);
            expect(scope.containsDeptAcc).toBeTrue();
        });

        it("should display the add modal", () => {
            spyOn(scope, "displayAddModal");
            scope.adminToAdd = "testiwta";
            scope.addAdmin();

            const results = { resultCode: "SUCCESS", invalid: [], results: [{ uid: "testiwta", uhUuid: "99997010" }] };
            httpBackend.expectPOST(BASE_URL + "members", ['testiwta']).respond(200, results);
            httpBackend.flush();

            expect(scope.user).toEqual(scope.adminToAdd);
            expect(scope.displayAddModal).toHaveBeenCalled();
        });
    });

    describe("removeAdmin", () => {
        beforeEach(() => {
            scope.pagedItemsAdmins[0] = {name: "zzz", uid: "zzz", uhUuid: "zzz"};
        });
        it("should call scope.displayRemoveModal", () => {
            scope.adminsList = [
                {
                    name: "iamtst01",
                    uid: "iamtst01",
                    uhUuid: "iamtst01"
                },
                {
                    name: "iamtst02",
                    uid: "iamtst02",
                    uhUuid: "iamtst02"
                },
                {
                    name: "iamtst03",
                    uid: "iamtst03",
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
    });

    describe("displayGroupingInNewTab", () => {
        afterEach(() => {
            sessionStorage.clear();
        });

        it("should set the items manageSubjectGrouping and subjectToLookup", () => {
            spyOn(sessionStorage, "removeItem");
            scope.subjectToLookup = "testId";
            scope.displayGroupingInNewTab("testName", "testPath");
            expect(JSON.parse(sessionStorage.getItem("manageSubjectGrouping")))
                .toEqual({name: "testName", path: "testPath"});
            expect(sessionStorage.getItem("subjectToLookup")).toBe(scope.subjectToLookup);
        });

        it("should remove the manageSubjectGrouping item from sessionStorage at the end", () => {
            scope.subjectToLookup = "testId";
            scope.displayGroupingInNewTab("testName", "testPath");
            expect(sessionStorage.getItem("manageSubjectGrouping")).toBeNull();
            expect(sessionStorage.getItem("subjectToLookup")).toBe(scope.subjectToLookup);
        });

        it("should open a new /admin tab", () => {
            spyOn(window, "open");
            scope.subjectToLookup = "testId";
            scope.displayGroupingInNewTab("testGroupingName", "testGroupingPath");
            expect(window.open).toHaveBeenCalledWith("admin");
        });
    });

    describe("initManageSubjectGrouping", () => {
        const manageSubjectGrouping = {name: "testName", path: "testPath"};

        beforeEach(() => {
            spyOn(scope, "getGroupingInformation");
            spyOn(scope, "toggleShowAdminTab");
            sessionStorage.setItem("manageSubjectGrouping", JSON.stringify(manageSubjectGrouping));
        });

        it("should set fromManageSubject and showGrouping to true", () => {
            scope.fromManageSubject = false;
            scope.showGrouping = false;
            scope.initManageSubjectGrouping(manageSubjectGrouping);
            expect(scope.fromManageSubject).toBeTrue();
            expect(scope.showGrouping).toBeTrue();
        });

        it("should set scope.selectedGrouping to the manageSubjectGrouping passed in", () => {
            scope.initManageSubjectGrouping(manageSubjectGrouping);
            expect(scope.selectedGrouping).toBe(manageSubjectGrouping);
        });

        it("should call scope.getGroupingInformation and scope.toggleShowAdminTab", () => {
            scope.initManageSubjectGrouping(manageSubjectGrouping);
            expect(scope.getGroupingInformation).toHaveBeenCalled();
            expect(scope.toggleShowAdminTab).toHaveBeenCalled();
        });

        it("should remove the manageSubjectGrouping item from sessionStorage", () => {
            scope.initManageSubjectGrouping(manageSubjectGrouping);
            expect(sessionStorage.getItem("manageSubjectGrouping")).toBeNull();
        });
    });

    describe("returnToManageSubject", () => {
        const subjectToLookup = "testId";

        beforeEach(() => {
            spyOn(scope, "searchForUserGroupingInformation");
            spyOn(gs, "getGroupingAdmins");
            spyOn(gs, "getAllGroupings");
            sessionStorage.setItem("subjectToLookup", subjectToLookup);
        });

        it("should set fromManageSubject and showGrouping to false", () => {
            scope.fromManageSubject = true;
            scope.showGrouping = true;
            scope.returnToManageSubject();
            expect(scope.fromManageSubject).toBeFalse();
            expect(scope.showGrouping).toBeFalse();
        });

        it("should open the manage-subject tab", () => {
            spyOn($.fn, "tab");
            spyOn(document, "getElementById");
            scope.returnToManageSubject();
            expect($.fn.tab).toHaveBeenCalledWith("show");
            expect(document.getElementById).toHaveBeenCalledWith("manage-subject-tab");
        });

        it("should set scope.subjectToLookup to subjectToLookup from sessionStorage", () => {
            scope.returnToManageSubject();
            expect(sessionStorage.getItem("subjectToLookup")).toBe(subjectToLookup);
        });

        it("should call scope.searchForUserGroupingInformation and gs.getGroupingAdmins", () => {
            scope.returnToManageSubject();
            expect(scope.searchForUserGroupingInformation).toHaveBeenCalled();
            expect(gs.getGroupingAdmins).toHaveBeenCalled();
        });
    });

    describe("clearManageSubjectCheckboxes", () => {
        beforeEach(() => {
            scope.pagedItemsSubject[scope.currentPageSubject] = {
                inBasis: false,
                inExclude: false,
                inInclude: true,
                isSelected: true
            };
        });

        it("should negate scope.checkAll", () => {
            scope.checkAll = true;
            scope.clearManageSubjectCheckboxes();
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

        it("should clear all checkboxes in Manage Subject", () => {
            spyOn(scope, "clearManageSubjectCheckboxes");
            scope.listName = "admins";
            scope.cancelRemoveFromGroupsModal();
            expect(scope.clearManageSubjectCheckboxes).toHaveBeenCalled();
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

        it("should call clearManageSubjectCheckboxes", () => {
            spyOn(scope, "clearManageSubjectCheckboxes").and.callThrough();
            scope.listName = "admins";
            scope.closeRemoveErrorModal();
            expect(scope.clearManageSubjectCheckboxes).toHaveBeenCalled();
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
