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
    let window;

    beforeEach(inject(($rootScope, $controller, _BASE_URL_, _$httpBackend_, groupingsService, $uibModal, _$window_) => {
        scope = $rootScope.$new(true);
        window = {
            location: {
                pathname: "/uhgroupings/",
                href: _$window_
            }
        };
        controller = $controller("GeneralJsController", {
            $scope: scope,
            $window: window
        });
        httpBackend = _$httpBackend_;
        BASE_URL = _BASE_URL_;
        gs = groupingsService;
        uibModal = $uibModal;
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
        const mockUser = { uid: "iamtst01", uhUuid: "iamtst01" };
        const mockResponse = 999;
        beforeEach(() => {
            httpBackend.whenGET("currentUser")
                .respond(200, mockUser);
        });

        it("should make an API call to getCurrentUser", () => {
            httpBackend.expectGET("currentUser").respond(200, mockUser);
            expect(httpBackend.flush).not.toThrow();
        });

        it("should initialize currentUser", () => {
            httpBackend.expectGET("currentUser").respond(200, mockUser);
            httpBackend.flush();

            expect(scope.currentUser).toEqual({ uid: "iamtst01", uhUuid: "iamtst01" });
        });
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

    describe("displayApiErrorModal", () => {
        it("should check that the displayApiErrorModal is displayed", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.displayApiErrorModal();
            expect(uibModal.open).toHaveBeenCalled();
        });
    });

    describe("closeApiError", () => {
        beforeEach(() => {
            scope.displayApiErrorModal();

            it("should close modal", () => {
                spyOn(scope.apiErrorModalInstance, "close").and.callThrough();
                scope.displayApiErrorModal();
            });

            it("should close modal", () => {
                spyOn(scope.apiErrorModalInstance, "close").and.callThrough();
                scope.closeApiError();
                expect(scope.apiErrorModalInstance.close).toHaveBeenCalled();
            });
        });
    });

    describe("sanitizer", () => {
        let goodFile, badFile, parseFile;
        beforeEach(() => {
            let bad1, bad2, bad3, bad4, bad5, bad6, bad7, bad8, bad9, bad10, bad11;
            bad1 = "<img src onerror=alert(\"testiwta is testing\")/>\n";
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

            goodFile = "iamtst01\niamtst02\niamtst03\niamtst04\niamtst05\niamtst06\n22222222\n12345678\nbogusname\nfakename\n_1234455\n_iamtst01\n_test_123-abc\nab";
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

        it("should return an array of uids that match the definition of a uhuuid or a uid", () => {
            const arrayOfValidNames = parseFile(goodFile);
            expect(arrayOfValidNames.length).toEqual(14);
            expect(arrayOfValidNames.toString()).toEqual("iamtst01,iamtst02,iamtst03,iamtst04,iamtst05,iamtst06,22222222,12345678,bogusname,fakename,_1234455,_iamtst01,_test_123-abc,ab");
        });
    });

    describe("displayDynamicModal", () => {
        beforeEach(() => {
            scope.dynamicModal = {
                close: () => {
                    // Mock $uib modal dismiss
                }
            };
        });

        it("should set currentModalTitle & currentModalBody to passed in title & body", () => {
            scope.displayDynamicModal("testTitle", "testBody", 5000);
            expect(scope.currentModalTitle).toBe("testTitle");
            expect(scope.currentModalBody).toBe("testBody");
        });

        it("should open displayDynamicModal modal", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.displayDynamicModal("testTitle", "testBody", 5000);
            expect(uibModal.open).toHaveBeenCalled();
        });

        it("should call closeDynamicModal to dismiss modal", () => {
            spyOn(scope.dynamicModal, "close").and.callThrough();
            scope.displayDynamicModal("testTitle", "testBody", 5000);
            expect(scope.dynamicModal.close()).toBeTrue();
        });
    });

    describe("displayRoleErrorModal", () => {
        it("should set scope.loading to false", () => {
            scope.loading = true;
            scope.displayRoleErrorModal();
            expect(scope.loading).toBeFalse();
        });

        it("should check that displayRoleErrorModal is displayed", () => {
            spyOn(uibModal, "open").and.callThrough();
            scope.displayRoleErrorModal();
            expect(uibModal.open).toHaveBeenCalled();
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

        it("should set removeInputError to false", () => {
            scope.removeInputError = true;
            scope.resetErrors();
            expect(scope.removeInputError).toBeFalse();
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

    describe("getCookie", () => {
        let result;

        it("should return empty string when cookie not found", () => {
            result = scope.getCookie("badCookie");
            expect(result).toEqual("");
        });
    });

    describe("getBasePath", () => {
        it("should return /uhgroupings", () => {
            expect(scope.getBasePath()).toBe("/uhgroupings");
            window.location.pathname = "/uhgroupings/groupings";
            expect(scope.getBasePath()).toBe("/uhgroupings");
        });

        it("should return /its/uhgroupings", () => {
            window.location.pathname = "/its/uhgroupings";
            expect(scope.getBasePath()).toBe("/its/uhgroupings");
            window.location.pathname = "/its/uhgroupings/groupings";
            expect(scope.getBasePath()).toBe("/its/uhgroupings");
        });
    });

    describe("proceedLogoutUser", () => {
        it("should call $scope.getBasePath() and set $window.location.href to the home path", () => {
            spyOn(scope, "getBasePath").and.callThrough();
            scope.proceedLogoutUser();
            expect(scope.getBasePath).toHaveBeenCalled();
            expect(window.location.href).toBe("/uhgroupings");
        });

        it("should make a POST request to logout the user", () => {
            spyOn(XMLHttpRequest.prototype, "open").and.callThrough();
            spyOn(XMLHttpRequest.prototype, "send");
            scope.proceedLogoutUser();

            expect(XMLHttpRequest.prototype.open).toHaveBeenCalledWith("POST", "/uhgroupings/logout", true);
        });
    });

    describe("proceedRedirectFeedback", () => {
        it("should call $scope.getBasePath() and set $window.location.href to the feedback path", () => {
            spyOn(scope, "getBasePath").and.callThrough();
            scope.proceedRedirectFeedback();
            expect(scope.getBasePath).toHaveBeenCalled();
            expect(window.location.href).toBe("/uhgroupings/feedback");
        });
    });

    describe("proceedRedirect", () => {
        it("should call $scope.getBasePath() and set $window.location.href to the home path", () => {
            spyOn(scope, "getBasePath").and.callThrough();
            scope.proceedRedirect();
            expect(scope.getBasePath).toHaveBeenCalled();
            expect(window.location.href).toBe("/uhgroupings");
        });
    });

    describe("showColumn", () => {
        let column;

        afterEach(() => {
            localStorage.clear();
        });

        it("should use the default values", () => {
            localStorage.clear();
            expect(localStorage.getItem("columnDisplaySetting")).toBe(null);
            expect(localStorage.getItem("showDescriptionColumn")).toBe(null);
            expect(localStorage.getItem("showGroupingPathColumn")).toBe(null);

            expect(scope.columnDisplaySetting).toBe("description");
            expect(scope.showDescriptionColumn).toBe(true);
            expect(scope.showGroupingPathColumn).toBe(false);
        });

        it("should show the description column", () => {
            column = "description";
            scope.showColumn(column);
            expect(scope.columnDisplaySetting).toBe("description");
            expect(scope.showDescriptionColumn).toBe(true);
            expect(scope.showGroupingPathColumn).toBe(false);

            expect(localStorage.getItem("columnDisplaySetting")).toBe("description");
            expect(localStorage.getItem("showDescriptionColumn")).toBe("true");
            expect(localStorage.getItem("showPathColumn")).toBe("false");
        });

        it("should show the grouping path column", () => {
            column = "groupingPath";
            scope.showColumn(column);
            expect(scope.columnDisplaySetting).toBe("groupingPath");
            expect(scope.showDescriptionColumn).toBe(false);
            expect(scope.showGroupingPathColumn).toBe(true);

            expect(localStorage.getItem("columnDisplaySetting")).toBe("groupingPath");
            expect(localStorage.getItem("showDescriptionColumn")).toBe("false");
            expect(localStorage.getItem("showPathColumn")).toBe("true");
        });

        it("should show both grouping path and description columns", () => {
            column = "all";
            scope.showColumn(column);
            expect(scope.columnDisplaySetting).toBe("all");
            expect(scope.showDescriptionColumn).toBe(true);
            expect(scope.showGroupingPathColumn).toBe(true);

            expect(localStorage.getItem("columnDisplaySetting")).toBe("all");
            expect(localStorage.getItem("showDescriptionColumn")).toBe("true");
            expect(localStorage.getItem("showPathColumn")).toBe("true");
        });
    });

    describe("hoverCopy", () => {
        it("should copy popover when clipboard is being hovered", () => {
            spyOn($.fn, "popover").and.callThrough();

            scope.hoverCopy();
            expect($.fn.popover).toHaveBeenCalled();
        });
    });
});
