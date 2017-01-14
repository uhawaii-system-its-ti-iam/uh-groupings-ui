package edu.hawaii.its.holiday.access;

public enum Role {
    ANONYMOUS,
    UH,
    EMPLOYEE,
    ADMIN;

    public String longName() {
        return "ROLE_" + name();
    }

    public String toString() {
        return longName();
    }

    public static Role find(String name) {
        for (Role role : Role.values()) {
            if (role.name().equals(name)) {
                return role; // Found it.
            }
        }
        return null;
    }
}
