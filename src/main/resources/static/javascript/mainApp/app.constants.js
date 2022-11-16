/* global UHGroupingsApp */
const threshold = {
    "MULTI_ADD": 100,
    "MULTI_REMOVE": 100,
    "MAX_IMPORT": 100000,
    "MAX_INVALID_UH_IDS": 10
};

UHGroupingsApp.constant("Threshold", threshold);
UHGroupingsApp.constant("PAGE_SIZE", 700);
UHGroupingsApp.constant("BASE_URL", "api/groupings/");
UHGroupingsApp.constant("Message", {
    Title: {
        IMPORT_OUT_OF_BOUNDS: "Out of Bounds Import Warning",
        SLOW_IMPORT: "Slow Import Warning",
        REMOVE_INPUT_ERROR  : "Error in Input",
        NO_MEMBERS_ADDED: "No Members Were Added",
        INVALID_FILE: "Invalid File"
    },
    Body: {
        IMPORT_OUT_OF_BOUNDS: `Importing more than ${threshold.MAX_IMPORT} users is not allowed.`,
        SLOW_IMPORT: "Feel free to exit your browser, an email will be sent of the add results.",
        REMOVE_INPUT_ERROR: "The members you've attempted to remove do not exist.",
        NO_MEMBERS_ADDED: "At least one of the members you attempted to add were invalid (possibly \"banana\")."
    },
    Csv: {
        GROUP_NOT_LOADED: "Export will be available once all members are loaded.",
        GROUP_LOADED: "Export entire member list to CSV.",
        EMAIL_SUFFIX: "@hawaii.edu"
    }
});

UHGroupingsApp.run(function ($rootScope, Threshold) {
    $rootScope.Threshold = Threshold;
});

