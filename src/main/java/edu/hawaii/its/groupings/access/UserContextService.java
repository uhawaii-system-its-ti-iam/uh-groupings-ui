package edu.hawaii.its.groupings.access;

public interface UserContextService {
    User getCurrentUser();

    String getCurrentUsername();

    String getCurrentUhuuid();

    void setCurrentUhuuid(String uhuuid);
}