package edu.hawaii.its.groupings.access;

public interface UserContextService {
    User getCurrentUser();

    String getCurrentUid();

    String getCurrentUhUuid();

    void setCurrentUhUuid(String uhUuid);
}