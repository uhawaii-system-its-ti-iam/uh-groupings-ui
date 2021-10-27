UHGroupingsApp.constant("BASE_URL", "api/groupings/");
UHGroupingsApp.constant("PAGE_SIZE", 700);
UHGroupingsApp.constant("Message", {
    Title: {
        IMPORT_OUT_OF_BOUNDS: "Out of Bounds Import Warning",
        LARGE_IMPORT: "Large Import Warning",
        SLOW_IMPORT: "Slow Import Warning",
        REMOVE_INPUT_ERROR  : "Error in input"
    },
    Body: {
        SLOW_IMPORT: "Feel free to exit your browser, an email will be sent of the add results.",
        REMOVE_INPUT_ERROR: "The members you've attempted to remove are invalid."
    },
    Csv: {
        GROUP_NOT_LOADED: "Export will be available once all members are loaded.",
        GROUP_LOADED: "Export entire member list to CSV.",
        EMAIL_SUFFIX: "@hawaii.edu"
    }
});
