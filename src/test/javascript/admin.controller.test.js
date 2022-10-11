/* global inject */

describe("AdminController", function () {

  beforeEach(module("UHGroupingsApp"));
  beforeEach(module("ngMockE2E"));

  let scope;
  let controller;
  let gs;
  let uibModal;

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

  beforeEach(inject(($rootScope, $controller, $uibModal, groupingsService) => {
    scope = $rootScope.$new();
    controller = $controller("AdminJsController", {
      $scope: scope
    });
    gs = groupingsService;
    uibModal = $uibModal;
    spyOn($uibModal, "open").and.returnValue(fakeModal);
  }));

  it("should define the admin controller", () => {
    expect(controller).toBeDefined();
  });

    describe("getAdminListsCallbackOnSuccess", () => {
        let res = {};
        beforeEach(() => {
            res = {
                "allGroupingPaths": [
                    {
                        "path": "path",
                        "name": "name"
                    },
                    {
                        "path": "path",
                        "name": "name"
                    }
                ],
                "adminGroup": {
                    "members": [
                        {
                            "username": "username",
                            "uhUuid": "uhUuid",
                            "firstName": "firstName",
                            "lastName": "lastName",
                            "name": "name"
                        },
                        {
                            "username": "username",
                            "uhUuid": "uhUuid",
                            "firstName": "firstName",
                            "lastName": "lastName",
                            "name": "name"
                        }
                    ]
                }
            };
        });

        it("should call objToPageArray", () => {
            spyOn(scope, "objToPageArray").and.callThrough();
            scope.getAdminListsCallbackOnSuccess(res);
            expect(scope.objToPageArray).toHaveBeenCalled();
        });

        it("should instantiate scope.pagedItemsAdmins", () => {
            scope.getAdminListsCallbackOnSuccess(res);
            expect(scope.pagedItemsAdmins).toBeDefined();
        });

        it("should instantiate scope.groupingsList", () => {
            scope.getAdminListsCallbackOnSuccess(res);
            expect(scope.groupingsList).toBeDefined();
        });

        it("should set scope.loading to false", () => {
            scope.getAdminListsCallbackOnSuccess(res);
            expect(scope.loading).toBeFalse();
        });
    });

    describe("init", () => {
        it("should call groupingsService.getAdminLists", () => {
            spyOn(gs, "getAdminLists").and.callThrough();
            scope.init();
            expect(gs.getAdminLists).toHaveBeenCalled();
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
            expect(scope.currentManagePerson).toEqual("");
        });
        it("should call groupingsService.getMembershipAssignmentForUser", () => {
            scope.personToLookup = "iamtst01";
            spyOn(gs, "getMembershipAssignmentForUser").and.callThrough();
            scope.searchForUserGroupingInformation();
            expect(gs.getMembershipAssignmentForUser).toHaveBeenCalled();
        });
        it("should call groupingsService.getMemberAttributes", () => {
            scope.personToLookup = "iamtst01";
            spyOn(gs, "getMemberAttributes").and.callThrough();
            scope.searchForUserGroupingInformation();
            expect(gs.getMemberAttributes).toHaveBeenCalled();
        });
        it("should set the resStatus to 500 and clear table", () => {
            scope.personToLookup = "j";
            scope.searchForUserGroupingInformation();
            expect(scope.resStatus).toEqual(500);
            expect(scope.personList).toEqual([]);
            expect(scope.currentManagePerson).toEqual("");

            scope.personToLookup = "*";
            scope.searchForUserGroupingInformation();
            expect(scope.resStatus).toEqual(500);
            expect(scope.personList).toEqual([]);
            expect(scope.currentManagePerson).toEqual("");
        });
    });

    describe("checkSoleOwner", () => {
        let res = { username: "testUsername", name: "testName", uhUuid: "testId" };
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
      
    it("should call groupingsService.getMemberAttributes", () => {
      spyOn(gs, "getMemberAttributes").and.callThrough();
      scope.removeFromGroups();
      expect(gs.getMemberAttributes).toHaveBeenCalled();
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
        it("should check that the admin to add is in the admin list", () => {
            scope.adminToAdd = "iamtst01";
            scope.addAdmin();
            expect(scope.listName).toBe("admins");
        });

        it("should check if the admin to add is empty", () => {
            scope.emptyInput = false;
            scope.addAdmin();
            expect(scope.emptyInput).toBeTrue();
        });

        it("should set waitingForImportResponse to false", () => {
            scope.waitingForImportResponse = true;
            scope.addAdmin();
            expect(scope.waitingForImportResponse).toBeFalse();

        });
    });

    describe("removeAdmin", () => {
        beforeEach(() => {
            scope.pagedItemsAdmins[0] = {name: "zzz", username: "zzz", uhUuid: "zzz"};
        });
        it("should call scope.createRemoveModal", () => {
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
            spyOn(scope, "createRemoveModal").and.callThrough();
            scope.removeAdmin(0, 0);
            expect(scope.createRemoveModal).toHaveBeenCalled();
        });
        it("should call scope.createRemoveErrorModal", () => {
            spyOn(scope, "createRemoveErrorModal").and.callThrough();
            scope.removeAdmin(0, 0);
            expect(scope.createRemoveErrorModal).toHaveBeenCalled();
        });
    });

    describe("createRemoveFromGroupsModal", () => {
        let options = { member: { uhUuid: "testId" }, groupPaths: "testPath", listName: "testList" };

        it("should set scope variables to passed in option's object", () => {
            scope.memberToRemove = {};
            scope.groupPaths = "badPath";
            scope.listName = "badList";
            scope.createRemoveFromGroupsModal(options);

            expect(scope.memberToRemove).toEqual({ uhUuid: "testId" });
            expect(scope.groupPaths).toBe("testPath");
            expect(scope.listName).toBe("testList");
            expect(scope.ownerOfListName).toBe("");
        });

        it("should call showWarningRemovingSelfFromList function", () => {
            spyOn(scope, "showWarningRemovingSelfFromList").and.callThrough();
            scope.createRemoveFromGroupsModal(options);

            expect(scope.showWarningRemovingSelfFromList).toHaveBeenCalled();
        });

        it("should call groupingsService", () => {
            spyOn(gs, "getMemberAttributes").and.callThrough();
            scope.createRemoveFromGroupsModal(options);

            expect(gs.getMemberAttributes).toHaveBeenCalled();
        });
    });
});
