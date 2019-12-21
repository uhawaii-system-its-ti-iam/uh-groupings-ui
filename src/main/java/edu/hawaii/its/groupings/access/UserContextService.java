package edu.hawaii.its.groupings.access;

public interface UserContextService {
    User getCurrentUser();

    String getCurrentUsername();

    String getCurrentUhUuid();

    void setCurrentUhUuid(String uhUuid);
}