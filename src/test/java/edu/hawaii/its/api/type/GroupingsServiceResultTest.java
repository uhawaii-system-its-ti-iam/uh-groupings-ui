package edu.hawaii.its.api.type;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.Before;

public class GroupingsServiceResultTest {

    private GroupingsServiceResult groupingsServiceResult;

    @Before
    public void setup() {
        groupingsServiceResult = new GroupingsServiceResult();
    }

    @Test
    public void GroupingsServiceResultTest(){
        assertNotNull(groupingsServiceResult);
        groupingsServiceResult.setResultCode("resultCode");
        groupingsServiceResult.setAction("Action");
        assertThat(groupingsServiceResult.getResultCode(), equalTo("resultCode"));
        assertThat(groupingsServiceResult.getAction(), equalTo("Action"));
    }
}