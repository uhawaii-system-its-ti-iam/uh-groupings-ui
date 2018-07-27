package edu.hawaii.its.api.type;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AdminListsHolderTest {

    private AdminListsHolder adminListHolder;

    @Before
    public void setup(){
        adminListHolder = new AdminListsHolder();
    }

    @Test
    public void nullTest(){
        assertNotNull(adminListHolder);;
        assertThat(adminListHolder.getAllGroupings().toString(), equalTo("[]"));
        assertThat(adminListHolder.getAdminGroup().toString(), equalTo("Group [path=, members=[]]"));
    }
    @Test
    public void AdminListsHoldertest(){
        List<Grouping> newList = new ArrayList<Grouping>();
        adminListHolder.setAllGroupings(newList);
        assertThat(adminListHolder.getAllGroupings(), equalTo(newList));

        Group Group1 = new Group();
        adminListHolder.setAdminGroup(Group1);
        assertThat(adminListHolder.getAdminGroup(), equalTo(Group1));
        adminListHolder = new AdminListsHolder(newList,Group1);
    }
}
