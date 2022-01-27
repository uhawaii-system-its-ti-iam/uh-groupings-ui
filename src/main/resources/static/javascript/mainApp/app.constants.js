UHGroupingsApp.constant("BASE_URL", "api/groupings/");
UHGroupingsApp.constant("PAGE_SIZE", 700);
UHGroupingsApp.constant("Message", {
    Title: {
        IMPORT_OUT_OF_BOUNDS: "Out of Bounds Import Warning",
        LARGE_IMPORT: "Large Import Warning",
        SLOW_IMPORT: "Slow Import Warning",
        NO_MEMBERS_ADDED: "No Members Were Added",
        NO_MEMBERS_REMOVED: "No Members Were Removed",
        LAST_OWNER_NOT_REMOVED: "Owner Not Removed",
        LAST_ADMIN_NOT_REMOVED: "Admin Not Removed"
    },
    Body: {
        SLOW_IMPORT: "Feel free to exit your browser, an email will be sent of the add results.",
        NO_MEMBERS_ADDED: "All the members you attempted to add are ether invalid or already exist in the group.",
        NO_MEMBERS_REMOVED: "All the members you attempted to remove are ether invalid or do not exist in the group.",
        LAST_OWNER_NOT_REMOVED: "You are unable to remove this owner. There must be at least one owner remaining.",
        LAST_ADMIN_NOT_REMOVED: "You are unable to remove this admin. There must be at least one admin remaining."
    },
    Csv: {
        GROUP_NOT_LOADED: "Export will be available once all members are loaded.",
        GROUP_LOADED: "Export entire member list to CSV.",
        EMAIL_SUFFIX: "@hawaii.edu"
    }
});
