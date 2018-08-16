describe("OwnerController", function () {

    // Set up mock element for setting the current user
    var mockElement = document.createElement("div");
    mockElement.innerHTML = "jdoe";
    document.getElementById = jasmine.createSpy("name").and.returnValue(mockElement);

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    var scope;
    var controller;
    var httpBackend;
    var BASE_URL;

    beforeEach(inject(function ($rootScope, $controller, _BASE_URL_, _$httpBackend_) {
        scope = $rootScope.$new();
        controller = $controller("OwnerJsController", {
            $scope: scope
        });
        httpBackend = _$httpBackend_;
        BASE_URL = _BASE_URL_;
    }));

    it("should define the owner controller", function () {
        expect(controller).toBeDefined();
    });

    it("should define the variables and methods in the table controller", function () {
        expect(scope.columnSort).toBeDefined();
        expect(scope.groupToPages).toBeDefined();
        expect(scope.filter).toBeDefined();
        expect(scope.pageRange).toBeDefined();
        expect(scope.setPage).toBeDefined();
        expect(scope.disableFirstAndPrev).toBeDefined();
        expect(scope.disableNextAndLast).toBeDefined();
        expect(scope.sortBy).toBeDefined();
    });

    it("should define the variables in the general controller", function () {
        expect(scope.groupingsList).toBeDefined();
        expect(scope.pagedItemsGroupings).toBeDefined();
        expect(scope.currentPageGroupings).toBeDefined();

        expect(scope.groupingBasis).toBeDefined();
        expect(scope.pagedItemsBasis).toBeDefined();
        expect(scope.currentPageBasis).toBeDefined();

        expect(scope.groupingMembers).toBeDefined();
        expect(scope.pagedItemsMembers).toBeDefined();
        expect(scope.currentPageMembers).toBeDefined();

        expect(scope.groupingInclude).toBeDefined();
        expect(scope.pagedItemsInclude).toBeDefined();
        expect(scope.currentPageInclude).toBeDefined();

        expect(scope.groupingExclude).toBeDefined();
        expect(scope.pagedItemsExclude).toBeDefined();
        expect(scope.currentPageExclude).toBeDefined();

        expect(scope.groupingOwners).toBeDefined();
        expect(scope.pagedItemsOwners).toBeDefined();
        expect(scope.currentPageOwners).toBeDefined();

        expect(scope.allowOptIn).toBeDefined();
        expect(scope.allowOptOut).toBeDefined();
        expect(scope.listserv).toBeDefined();
        expect(scope.ldap).toBeDefined();

        expect(scope.showGrouping).toBeDefined();

        expect(scope.loading).toBeDefined();
    });

    it("should define the methods in the general controller", function () {
        expect(scope.displayGrouping).toBeDefined();
        expect(scope.getGroupingInformation).toBeDefined();
        expect(scope.createApiErrorModal).toBeDefined();
        expect(scope.closeApiError).toBeDefined();
        expect(scope.addInBasis).toBeDefined();
        expect(scope.addWhereListed).toBeDefined();
        expect(scope.addMember).toBeDefined();
        expect(scope.updateAddMember).toBeDefined();
        expect(scope.isInAnotherList).toBeDefined();
        expect(scope.createCheckModal).toBeDefined();
        expect(scope.proceedCheckModal).toBeDefined();
        expect(scope.closeCheckModal).toBeDefined();
        expect(scope.addOwner).toBeDefined();
        expect(scope.createAddModal).toBeDefined();
        expect(scope.closeAddModal).toBeDefined();
        expect(scope.removeMember).toBeDefined();
        expect(scope.removeOwner).toBeDefined();
        expect(scope.proceedRemoveUser).toBeDefined();
        expect(scope.cancelRemoveUser).toBeDefined();
        expect(scope.returnToGroupingsList).toBeDefined();
        expect(scope.resetGroupingInformation).toBeDefined();
        expect(scope.createPreferenceInfoModal).toBeDefined();
        expect(scope.closePreferenceInfo).toBeDefined();
        expect(scope.updateAllowOptOut).toBeDefined();
        expect(scope.updateAllowOptIn).toBeDefined();
        expect(scope.updateListserv).toBeDefined();
        expect(scope.updateLdap).toBeDefined();
        expect(scope.createPreferenceErrorModal).toBeDefined();
        expect(scope.closePreferenceError).toBeDefined();
        expect(scope.resetFields).toBeDefined();
        expect(scope.exportGroupToCsv).toBeDefined();
        expect(scope.convertListToCsv).toBeDefined();
        expect(scope.showWarningRemovingSelf).toBeDefined();
    });

    it("should correctly set the currentUser", function () {
        expect(scope.currentUser).toEqual("jdoe");
    });

});
