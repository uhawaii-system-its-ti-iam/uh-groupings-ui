package edu.hawaii.its.api.type;

import org.junit.Rule;
import org.junit.Test;
import org.junit.Before;
import org.junit.rules.ExpectedException;

public class EmptyGroupTest extends Group{

    private EmptyGroup emptygroup;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp(){
        emptygroup = new EmptyGroup();
    }

    @Test
    public void emptyGroupTest() {
        thrown.expect(UnsupportedOperationException.class);
        emptygroup.addMember(new Person());

    }
}
