/* global _, UHGroupingsApp */

(() => {

    /**
     * Combines the pages of the members of a grouping's Basis, Include, and Exclude lists as they are fetched. It
     * works on the $scope of GroupingDetailsJsController, which instantiates it.
     * @param $scope - binding between controller and HTML page
     * @param MEMBER_PAGES_IN_FLIGHT - the most member pages requested at once, from app.constants.js
     * @returns {{addMemberPage: function, fetchMemberPages: function}} the functions that GroupingDetailsJsController
     * uses to load the members of a grouping
     */
    function GroupingMemberPagesJsController($scope, MEMBER_PAGES_IN_FLIGHT) {

        /**
         * Sort members by display name, then uhUuid (orphans with empty names sort predictably).
         * @param {object[]} members - the members of the group
         * @returns {object[]} sorted distinct members by uhUuid
         */
        const sortGroupMembers = (members) => {
            members = _.filter(members, (member) => $scope.isOrphanMember(member) || !!member.name);
            members = _.uniqBy(members, "uhUuid");
            return _.sortBy(members, [(member) => (member.name || "").toLowerCase(), "uhUuid"]);
        };

        /**
         * Concatenate initialMembers and membersToAdd into one sorted list of distinct members. A member in both is
         * taken from initialMembers.
         * @param {object[]} initialMembers - initial members in group
         * @param {object[]} membersToAdd - members to add to group
         * @returns {object[]} the members of both groups in one array, sorted by name
         */
        const combineGroupMembers = (initialMembers, membersToAdd) =>
            sortGroupMembers(_.concat(initialMembers, membersToAdd));

        /**
         * Helper - fetchGrouping
         * The members of the grouping (Basis and Include, minus Exclude), each with where it is listed. They are
         * worked out from the whole lists loaded so far, because the API's members of a page (allMembers) only
         * account for the Basis, Include, and Exclude members on that same page.
         * @param {object[]} basis - the members of the Basis list
         * @param {object[]} include - the members of the Include list
         * @param {object[]} exclude - the members of the Exclude list
         * @returns {object[]} the members of the grouping, sorted by name
         */
        const groupingMembersOf = (basis, include, exclude) => {
            const excluded = new Set(exclude.map((member) => member.uhUuid));
            const included = new Set(include.map((member) => member.uhUuid));
            const inBasis = new Set(basis.map((member) => member.uhUuid));
            const asMember = (member, whereListed) => ({
                name: member.name,
                firstName: member.firstName,
                lastName: member.lastName,
                uid: member.uid,
                uhUuid: member.uhUuid,
                orphan: member.orphan,
                whereListed
            });
            const members = [
                ...basis.filter((member) => !excluded.has(member.uhUuid))
                    .map((member) => asMember(member, included.has(member.uhUuid) ? "Basis & Include" : "Basis")),
                ...include.filter((member) => !excluded.has(member.uhUuid) && !inBasis.has(member.uhUuid))
                    .map((member) => asMember(member, "Include"))
            ];
            return sortGroupMembers(members);
        };

        /**
         * Helper - fetchGrouping
         * Add a page of members to the grouping's Basis, Include, and Exclude lists, and work out its members again.
         * @param {object} res - the page of members (GroupingGroupsMembers)
         */
        const addMemberPage = (res) => {
            $scope.groupingBasis = combineGroupMembers(res.groupingBasis.members, $scope.groupingBasis);
            $scope.filter($scope.groupingBasis, "pagedItemsBasis", "currentPageBasis", $scope.basisQuery, false);

            $scope.groupingInclude = combineGroupMembers(res.groupingInclude.members, $scope.groupingInclude);
            $scope.groupingExclude = combineGroupMembers(res.groupingExclude.members, $scope.groupingExclude);
            // Any page can add Basis members, so whether each Include and Exclude member is in Basis is checked again.
            $scope.addInBasis($scope.groupingInclude);
            $scope.addInBasis($scope.groupingExclude);
            $scope.filter($scope.groupingInclude, "pagedItemsInclude", "currentPageInclude", $scope.includeQuery, false);
            $scope.filter($scope.groupingExclude, "pagedItemsExclude", "currentPageExclude", $scope.excludeQuery, false);
            // The Reset Include and Reset Exclude checkboxes are disabled while their list is empty.
            $scope.disableResetCheckboxes();

            $scope.groupingMembers =
                groupingMembersOf($scope.groupingBasis, $scope.groupingInclude, $scope.groupingExclude);
            $scope.filter($scope.groupingMembers, "pagedItemsMembers", "currentPageMembers", $scope.membersQuery, false);
        };

        /**
         * Helper - getGroupingInformation
         * Fetch the pages of the members of groupPaths, MEMBER_PAGES_IN_FLIGHT at a time, until a page is past the
         * last members, a page fails, or isCurrent() turns false (another load has started, or the grouping was left).
         * @param {string[]} groupPaths - the paths of the Basis, Include, and Exclude lists
         * @param {function} isCurrent - whether this load should keep fetching pages
         * @returns {Promise<boolean>} whether every page of members was fetched
         */
        const fetchMemberPages = (groupPaths, isCurrent) => new Promise((resolve) => {
            let nextPage = 1;
            let inFlight = 0;
            let stopped = false;
            let complete = false;
            let failed = false;
            const fetchNextPage = () => {
                if (stopped || !isCurrent()) {
                    stopped = true;
                    if (inFlight === 0) {
                        resolve(complete && !failed);
                    }
                    return;
                }
                const page = nextPage++;
                inFlight++;
                $scope.fetchGrouping(page, groupPaths, isCurrent).then((outcome) => {
                    inFlight--;
                    if (outcome !== "more") {
                        stopped = true;
                        complete = complete || outcome === "complete";
                        failed = failed || outcome === "failed";
                    }
                    fetchNextPage();
                });
            };
            for (let i = 0; i < MEMBER_PAGES_IN_FLIGHT; i++) {
                fetchNextPage();
            }
        });

        return { addMemberPage, fetchMemberPages };
    }

    UHGroupingsApp.controller("GroupingMemberPagesJsController", GroupingMemberPagesJsController);
})();
