package edu.hawaii.its.api.type;

import org.junit.Rule;
import org.junit.Test;
import org.junit.Before;
import org.junit.rules.ExpectedException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class EmptyGroupTest extends Group{

    private EmptyGroup emptygroup;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp(){
        emptygroup = new EmptyGroup();
    }

    @Test
    public void test() {
       assertThat(emptygroup.getPath().toString(),equalTo(""));
       assertThat(emptygroup.getMembers().toString(),equalTo("[]"));
       thrown.expect(UnsupportedOperationException.class);
       emptygroup.addMember(new Person());
    }
}
