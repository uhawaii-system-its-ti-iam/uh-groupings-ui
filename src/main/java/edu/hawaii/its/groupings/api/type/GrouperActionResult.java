package edu.hawaii.its.groupings.api.type;

public class GrouperActionResult {
    private String action;
    private String resultCode;

    public GrouperActionResult() {
        // Empty.
    }

    public GrouperActionResult(String resultCode, String action) {
        this.resultCode = resultCode;
        this.action = action;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
