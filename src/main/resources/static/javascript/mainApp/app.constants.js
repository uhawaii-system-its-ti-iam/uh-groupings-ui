/* global UHGroupingsApp */

const threshold = {
    "MULTI_ADD": 100,
    "MULTI_REMOVE": 100,
    "MAX_IMPORT": 100000,
    "MAX_LIST_SIZE": 10,
    "RESET_GROUP": 750
};
UHGroupingsApp.constant("Threshold", threshold);
UHGroupingsApp.constant("Utility", {
    compareObjects: (obj1, obj2) => {
        return (JSON.stringify(obj1) === JSON.stringify(obj2));
    }
});
UHGroupingsApp.constant("Message", {
    Title: {
        IMPORT_OUT_OF_BOUNDS: "Out of Bounds Import Warning",
        SLOW_IMPORT: "Slow Import Warning",
        BAD_INPUT_ERROR: "Error in Input",
        NO_MEMBERS_ADDED: "No Members Were Added",
        INVALID_FILE: "Invalid File",
        ADD_MEMBER: "Add Member",
        ADD_MEMBERS: "Add Members",
        ADD_GROUP_PATH: "Add Owner-Grouping",
        REMOVE_MEMBER: "Remove Member",
        REMOVE_MEMBERS: "Remove Members",
        REMOVE_GROUP_PATH: "Remove Owner-Grouping",
        OWNER_NOT_ADDED: "Owner Was Not Added",
        DISPLAY_OWNERS_ERROR: "Error Displaying Owners",
        INVALID_EMAIL_ERROR: "Invalid Email",
        INVALID_MULTI_ADD: "Invalid Multi-Add",
        INVALID_MULTI_REMOVE: "Invalid Multi-Remove"
    },
    Body: {
        IMPORT_OUT_OF_BOUNDS: `Importing more than ${threshold.MAX_IMPORT} users is not allowed.`,
        SLOW_IMPORT: "Feel free to exit your browser.",
        REMOVE_INPUT_ERROR: "The members you've attempted to remove do not exist.",
        ADD_INPUT_ERROR: "The members you've attempted to add do not exist.",
        NO_MEMBERS_ADDED: { with: (listName) => `All the members you attempted to add already exist in the ${listName} list.` },
        ADD_MEMBER: { with: (member, listName) => `${member} has been successfully added to the ${listName} list.` },
        ADD_MEMBERS: { with: (listName) => `All members have been added successfully to the ${listName} list.` },
        ADD_GROUP_PATH: { with: (groupingName, listName) => `${groupingName} has been successfully added to the ${listName} list.` },
        ADD_CURRENT_PATH_ERROR: "Adding a grouping to its own owners list is not allowed.",
        REMOVE_MEMBER: { with: (member, listName) => `${member} has been successfully removed from the ${listName} list.` },
        REMOVE_MEMBERS: { with: (listName) => `All selected members have been successfully removed from the ${listName} list.` },
        REMOVE_GROUP_PATH: { with: (groupingName, listName) => `${groupingName} has been successfully removed from the ${listName} list.`},
        OWNER_NOT_ADDED: "Department accounts may not be assigned ownership due to accountability and lifecycle issues.  Please contact the IAM team, <its-iam-help@lists.hawaii.edu>, if there are any questions.",
        DISPLAY_OWNERS_ERROR: "There was an error displaying the owners.",
        INVALID_EMAIL_ERROR: "Please enter a valid email address.",
        INVALID_MULTI_ADD: "Only one owner-grouping can be added at a time.",
        INVALID_MULTI_REMOVE: "Only one owner-grouping can be removed at a time."
    },
    Csv: {
        GROUP_NOT_LOADED: "Export will be available once all members are loaded.",
        GROUP_EMPTY: "No members to export.",
        EMAIL_SUFFIX: "@hawaii.edu",
        UUID_COLUMN_HEADER: "UH Number",
    },
    ResetGroupSuccess: {
        INCLUDE_AND_EXCLUDE: "Include and Exclude lists have",
        INCLUDE: "Include list has",
        EXCLUDE: "Exclude list has"
    },
    ResetGroupError: {
        ResetGroupErrorMessages: {
            TITLE: "Reset Grouping Error",
            Body: [
                "Neither list has been reset due to an error.",
                "The Exclude list has been reset. There was an error preventing the Include list reset.",
                "There was an error preventing the Include list reset.",
                "The Include list has been reset. There was an error preventing the Exclude list reset.",
                "There was an error preventing the Exclude list reset."
            ]
        },
        // This is used to match an API result with the proper error messages in RestGroupErrorMessages.Body.
        ResetGroupErrorMessageMap: [
            { includeFailure: true, excludeFailure: true, includeSuccess: false, excludeSuccess: false },
            { includeFailure: true, excludeFailure: false, includeSuccess: false, excludeSuccess: true },
            { includeFailure: true, excludeFailure: false, includeSuccess: false, excludeSuccess: false },
            { includeFailure: false, excludeFailure: true, includeSuccess: true, excludeSuccess: false },
            { includeFailure: false, excludeFailure: true, includeSuccess: false, excludeSuccess: false }
        ]
    },
    SyncDestModal: {
        confirmationMessage: (isSingular) => `Are you sure you want to toggle the synchronization ${isSingular ? 'destination' : 'destinations'}?:`
    },
    OptPreferencesModal: {
        confirmationMessage: (isSingular) => `Are you sure you want to change opt ${isSingular ? "preference" :
            "preferences"}?`
    }
});
UHGroupingsApp.constant("BASE_URL", "api/groupings/");
UHGroupingsApp.constant("PAGE_SIZE", 700);

UHGroupingsApp.run(($rootScope, Threshold) => {
    $rootScope.Threshold = Threshold;
});

