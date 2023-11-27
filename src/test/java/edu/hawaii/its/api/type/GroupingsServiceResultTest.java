package edu.hawaii.its.api.type;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GroupingsServiceResultTest {

    private GroupingsServiceResult groupingsServiceResult;

    @BeforeEach
    public void setUp() {
        groupingsServiceResult = new GroupingsServiceResult();
    }

    @Test
    public void groupingsServiceResultTest(){
        assertNotNull(groupingsServiceResult);
        groupingsServiceResult.setResultCode("resultCode");
        groupingsServiceResult.setAction("Action");
        assertThat(groupingsServiceResult.getResultCode(), is("resultCode"));
        assertThat(groupingsServiceResult.getAction(), is("Action"));
    }
}