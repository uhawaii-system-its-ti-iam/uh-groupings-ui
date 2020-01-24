package edu.hawaii.its.groupings.access;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class UhCasAttributesTest {

    @Test
    public void loadNullMap() {
        UhCasAttributes attributes = new UhCasAttributes(null);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is(""));
        assertThat(attributes.getUid(), is(""));

        assertThat(attributes.getValue("not-a-key"), is(""));
    }

    @Test
    public void loadMapValid() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "666666");
        map.put("uid", "duckart");
        UhCasAttributes attributes = new UhCasAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is("666666"));
        assertThat(attributes.getUid(), is("duckart"));
        assertThat(attributes.getValue("not-a-key"), is(""));
        assertThat(attributes.getValue(null), is(""));
    }

    @Test
    public void loadMapInvalidValueType() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "666666");
        map.put("uid", new Integer(666));
        UhCasAttributes attributes = new UhCasAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is("666666"));
        assertThat(attributes.getUid(), is("")); // Internal error.
        assertThat(attributes.getValue("not-a-key"), is(""));
    }

    @Test
    public void loadMapInvalidKeyType() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "666666");
        map.put(new Integer(666), new Integer(666));
        UhCasAttributes attributes = new UhCasAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is("666666"));
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getValue("not-a-key"), is(""));
    }

    @Test
    public void loadMapInvalidTypes() {
        Map<Object, Object> map = new HashMap<>();
        map.put(new Integer(666), new Integer(666));
        UhCasAttributes attributes = new UhCasAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is(""));
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getValue("not-a-key"), is(""));
    }

    @Test
    public void loadMapWithArrayList() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "10967714");
        List<Object> uids = new ArrayList<>();
        uids.add("cahana");
        uids.add("mjrules");
        map.put("uid", uids);
        UhCasAttributes attributes = new UhCasAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is("10967714"));
        assertThat(attributes.getUhUuid(), is("10967714"));
        assertThat(attributes.getUid(), is("cahana"));
    }

    @Test
    public void loadMapWithArrayListWithNullEntries() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "10967714");
        List<Object> uids = new ArrayList<>();
        uids.add(null);
        uids.add(null);
        map.put("uid", uids);
        UhCasAttributes attributes = new UhCasAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getUhUuid(), is("10967714"));
    }

    @Test
    public void loadMapWithArrayListWithEmptyEntries() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "10967714");
        List<Object> uids = new ArrayList<>();
        uids.add("");
        uids.add("");
        map.put("uid", uids);
        UhCasAttributes attributes = new UhCasAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getUhUuid(), is("10967714"));
    }

    @Test
    public void loadMapWithArrayListWithManyEntries() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "10967714");
        List<Object> uids = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            uids.add("");
        }
        uids.add("cahana");
        map.put("uid", uids);
        UhCasAttributes attributes = new UhCasAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUid(), is("")); // Note this result.
        assertThat(attributes.getUhUuid(), is("10967714"));
    }

    @Test
    public void loadMapWithNullMap() {
        Map<Object, Object> map = null;
        UhCasAttributes attributes = new UhCasAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is(""));
        assertThat(attributes.getUid(), is(""));
    }

    @Test
    public void loadMapWithNullMapEntry() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uid", null);
        map.put("uhUuid", null);
        UhCasAttributes attributes = new UhCasAttributes(map);
        assertThat( attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is(""));
        assertThat(attributes.getUid(), is(""));
    }

    @Test
    public void loadMapWithEmptyMapEntry() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uid", new ArrayList<>());
        map.put("uhUuid", new ArrayList<>(0));
        UhCasAttributes attributes = new UhCasAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is(""));
        assertThat(attributes.getUid(), is(""));
    }

    @Test
    public void loadMapWithNullKey() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "10967714");
        map.put(null, "cahana");
        UhCasAttributes attributes = new UhCasAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is("10967714"));
        assertThat(attributes.getUid(), is("")); // Note this result.
    }

    @Test
    public void loadMapWithUnexpectedType() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "666666");

        Map<Long, java.util.Date> uidMap = new HashMap<>();
        uidMap.put(new Long(666), new java.util.Date());
        map.put("uid", uidMap);

        UhCasAttributes attributes = new UhCasAttributes(map);

        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is("666666"));
        assertThat(attributes.getUid(), is("")); // Note result.
    }

    @Test
    public void loadMapWithNullUsername() {
        String username = null;
        Map<Object, Object> map = new HashMap<>();
        map.put("uid", "duckart");
        map.put("uhUuid", "6666666");
        UhCasAttributes attributes = new UhCasAttributes(username, map);
        assertThat(attributes.getUid(), is("duckart"));
        assertThat(attributes.getUhUuid(), is("6666666"));
        assertThat(attributes.getUsername(), is(""));
    }

    @Test
    public void misc() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uid", "duckart");
        map.put("uhUuid", "666666");
        map.put("cn", "Frank");
        map.put("mail", "frank@example.com");
        map.put("eduPersonAffiliation", "aff");
        UhCasAttributes attributes = new UhCasAttributes(map);

        assertThat(attributes.getMap().size(), equalTo(5));
        assertThat(attributes.getUid(), equalTo("duckart"));
        assertThat(attributes.getUhUuid(), equalTo("666666"));
        assertThat(attributes.getName(), equalTo("Frank"));
        assertThat(attributes.getMail().get(0), equalTo("frank@example.com"));
        assertThat(attributes.getAffiliation().get(0), equalTo("aff"));

        assertThat(attributes.toString(), containsString("uid=duckart"));
    }
}
