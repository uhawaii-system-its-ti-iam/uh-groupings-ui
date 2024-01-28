/* global _, angular, UHGroupingsApp */

(() => {

    /**
     * This controller contains shared functions between the all pages.
     * @param $scope - binding between controller and HTML page
     * @param $controller - service for instantiating controllers
     * @param $window - the browser window object
     * @param $uibModal - the UI Bootstrap service for creating modals
     * @param dataProvider - service that handles redirection to the feedback page upon error
     * @param groupingsService - service for creating requests to the groupings API
     * @param Message - display messages
     */

    function GeneralJsController($scope, $controller, $window, $uibModal, groupingsService, dataProvider, Message) {
        // This is a regex Pattern that contains all valid UH Identifiers which consists of uid (Username) and uhUuid (UH Numbers) chars.
        $scope.uhIdentifierPattern = new RegExp("^[_?a-z-?@?0-9]{2,64}$");
        $scope.currentUser = {};

        $scope.group = "";
        $scope.resStatus = 0;
        $scope.showAdminTab = true;

        //Retrieve table column settings from local storage or default value if local storage is empty
        $scope.columnDisplaySetting = localStorage.getItem("columnDisplaySetting") ?? "description";
        $scope.showDescriptionColumn = JSON.parse(localStorage.getItem("showDescriptionColumn") ?? true);
        $scope.showGroupingPathColumn = JSON.parse(localStorage.getItem("showPathColumn") ?? false);

        angular.extend(this, $controller("TableJsController", { $scope }));

        /**
         * Get the uid of the current user
         */
        groupingsService.getCurrentUser((res) => {
            $scope.currentUser = {
                uid: res.data.uid,
                uhUuid: res.data.uhUuid
            };
            $scope.feedbackEmail = $scope.currentUser.uid + Message.Csv.EMAIL_SUFFIX;
        });

        /**
         * Shows/hides admin tabs: Manage Groupings, Manage Admins, Manage Person
         */
        $scope.toggleShowAdminTab = () => {
            $scope.showAdminTab = $scope.showAdminTab === false;
        };

        /**
         * Display a modal for errors in loading data from the API.
         */
        $scope.displayApiErrorModal = () => {
            $scope.loading = false;
            $scope.apiErrorModalInstance = $uibModal.open({
                templateUrl: "modal/apiError",
                scope: $scope,
                backdrop: "static",
                keyboard: false,
                ariaLabelledBy: "api-error-modal"
            });
        };

        /**
         * Close the API error modal.
         */
        $scope.closeApiError = () => {
            $scope.apiErrorModalInstance.close();
        };

        /**
         * Checks that a users name matches the pattern of either a valid uid or a uhUuid.
         * @param {Object[]|string} names - An array of users or string of a single user to validate.
         * @returns {Object[]|string} - An array of valid users or valid string of a user .
         */
        $scope.sanitizer = (names) => {
            const validNames = [];
            for (const name of [].concat(names)) {
                if (name) {
                    const trimmedLowercaseName = name.toLowerCase().trim();
                    if (trimmedLowercaseName != null && $scope.uhIdentifierPattern.test(trimmedLowercaseName)) {
                        const validInput = trimmedLowercaseName.match($scope.uhIdentifierPattern);
                        validNames.push(validInput.toString());
                    }
                }
            }
            return _.isString(names) ? validNames.toString() : validNames;
        };

        /**
         * Set a style attribute of a html component associated with id.
         * @param {string} id - id of html component
         * @param {string} attribute - the css attribute
         * @param {string} setAs - set attribute to
         */
        $scope.changeStyleAttribute = (id, attribute, setAs) => {
            document.getElementById(id).style[`${attribute}`] = setAs;
        };

        /**
         * Display a modal with a title, and body message. The modal will close in the case of pressing the ok button
         * and will cancel if the timeTillClose is set and time runs out. The modal will not timeout unless the
         * timeTillClose is set.
         */
        $scope.displayDynamicModal = (title, body, timeTillClose) => {
            $scope.currentModalTitle = title;
            $scope.currentModalTitleKebabCase = title.replace(/ /g, "-").toLowerCase();
            $scope.currentModalBody = body;

            $scope.dynamicModal = $uibModal.open({
                templateUrl: "modal/dynamicModal",
                scope: $scope,
                backdrop: "static",
                ariaLabelledBy: $scope.currentModalTitleKebabCase + "-modal"
            });

            if (!_.isUndefined(timeTillClose)) {
                let closeOnTimeout = () => {
                    $scope.dynamicModal.dismiss();
                };
                setTimeout(closeOnTimeout, timeTillClose);
            }
            $scope.closeDynamicModal = () => {
                $scope.dynamicModal.close();
            };
        };

        /**
         * Display a modal telling the user that they do not have access to perform this action and that they
         * will be logged out and redirected to the homepage.
         */
        $scope.displayRoleErrorModal = () => {
            $scope.loading = false;
            $scope.RoleErrorModalInstance = $uibModal.open({
                templateUrl: "modal/roleErrorModal",
                scope: $scope,
                backdrop: "static",
                keyboard: false,
                ariaLabelledBy: "role-error-modal"
            });
        };

        $scope.resetErrors = () => {
            $scope.resStatus = 0;
            $scope.emptyInput = false;
            $scope.emptySelect = false;
            $scope.containsInput = false;
            $scope.invalidInput = false;
            $scope.addInputError = false;
            $scope.removeInputError = false;
        };

        /**
         * Removes error message and resets errors
         */
        $scope.dismissErrors = () => {
            $scope.errorDismissed = true;
            $scope.resetErrors();
        };

        /**
         * Gets cookie information
         * @param cname = name of cookie you want to look for.
         * @returns {*}
         */
        $scope.getCookie = (cname) => {
            let name = cname + "=";
            let decodedCookie = decodeURIComponent(document.cookie);
            let ca = decodedCookie.split(";");
            for (let c of ca) {
                while (c.charAt(0) === " ") {
                    c = c.substring(1);
                }
                if (c.indexOf(name) === 0) {
                    return c.substring(name.length, c.length);
                }
            }
            return "";
        };

        /**
         * Log out the user and redirect them to the home page
         */
        $scope.proceedLogoutUser = () => {
            const basePath = $scope.getBasePath();
            $window.location.href = basePath;
            let r = new XMLHttpRequest();
            r.open("POST", `${basePath}/logout`, true);
            r.setRequestHeader("X-XSRF-TOKEN", $scope.getCookie("XSRF-TOKEN"));
            r.send();
        };

        /**
         * Redirect the user to the feedback page.
         */
        $scope.proceedRedirectFeedback = () => {
            $window.location.href = `${$scope.getBasePath()}/feedback`;
        };

        /**
         * Redirect the user to the home page.
         */
        $scope.proceedRedirect = () => {
            $window.location.href = $scope.getBasePath();
        };

        /**
         * Shows column when radio button is selected in table column settings dropdown then
         * saves it to local storage.
         * @param column {string} - the column to show
         */
        $scope.showColumn = (column) => {
            $scope.columnDisplaySetting = column;
            $scope.showDescriptionColumn = (column === "description") || (column === "all");
            $scope.showGroupingPathColumn = (column === "groupingPath") || (column === "all");

            localStorage.setItem("columnDisplaySetting", $scope.columnDisplaySetting);
            localStorage.setItem("showDescriptionColumn", JSON.stringify($scope.showDescriptionColumn));
            localStorage.setItem("showPathColumn", JSON.stringify($scope.showGroupingPathColumn));
        };

        /**
         * Returns the correct basePath (/uhgroupings or /its/uhgroupings)
         */
        $scope.getBasePath = () => {
            const pathSegments = $window.location.pathname.split("/");

            if (pathSegments.includes("its")) {
                return "/its/uhgroupings";
            }
            return "/uhgroupings";
        };

        /**
         * Used with ng-keydown directive in HTML to click using the enter key.
         * @param event - pass in $event in HTML
         */
        $scope.clickWithEnter = (event) => {
            if (event.keyCode === 13) {
                event.target.click();
                event.preventDefault();
            }
        };

        /**
         * Copy the path of a grouping through the clipboard button
         * @param {Object} grouping - the current selected grouping
         */
        $scope.copyPath = (grouping) => {
            $("[data-content='copy']").popover("hide");

            $("[data-content='copied!']").popover();
            setTimeout(() => {
                $("[data-content='copied!']").popover("hide");
            }, 1000);

            let copyText = document.getElementById(grouping.path);
            copyText.select();
            document.execCommand("copy");
        };

        /**
         * Toggle 'copy' popover when clipboard is being hovered.
         */
        $scope.hoverCopy = () => {
            $("[data-content='copy']").popover();
        };
    }

    UHGroupingsApp.controller("GeneralJsController", GeneralJsController);
})();
