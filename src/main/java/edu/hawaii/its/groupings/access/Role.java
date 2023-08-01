package edu.hawaii.its.groupings.access;

public enum Role {
    ANONYMOUS,
    UH,
    EMPLOYEE,
    OWNER,
    ADMIN,
    DEPARTMENT;

    public String longName() {
        return "ROLE_" + name();
    }

    @Override
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
