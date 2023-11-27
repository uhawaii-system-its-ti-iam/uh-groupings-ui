package edu.hawaii.its.groupings.access;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class UhCasAttributesTest {

    @Test
    public void loadNullMap() {
        UhAttributes attributes = new UhAttributes(null);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is(""));
        assertThat(attributes.getUid(), is(""));

        assertThat(attributes.getValue("not-a-key"), is(""));
    }

    @Test
    public void loadMapValid() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "666666");
        map.put("uid", "testiwd");
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is("666666"));
        assertThat(attributes.getUid(), is("testiwd"));
        assertThat(attributes.getValue("not-a-key"), is(""));
        assertThat(attributes.getValue(null), is(""));
    }

    @Test
    public void loadMapInvalidValueType() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "666666");
        map.put("uid", 666);
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is("666666"));
        assertThat(attributes.getUid(), is("")); // Internal error.
        assertThat(attributes.getValue("not-a-key"), is(""));
    }

    @Test
    public void loadMapInvalidKeyType() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "666666");
        map.put(666, 666);
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is("666666"));
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getValue("not-a-key"), is(""));
    }

    @Test
    public void loadMapInvalidTypes() {
        Map<Object, Object> map = new HashMap<>();
        map.put(666, 666);
        UhAttributes attributes = new UhAttributes(map);
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
        uids.add("testiwc");
        uids.add("testiwb");
        map.put("uid", uids);
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is("10967714"));
        assertThat(attributes.getUhUuid(), is("10967714"));
        assertThat(attributes.getUid(), is("testiwc"));
    }

    @Test
    public void loadMapWithArrayListWithNullEntries() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "10967714");
        List<Object> uids = new ArrayList<>();
        uids.add(null);
        uids.add(null);
        map.put("uid", uids);
        UhAttributes attributes = new UhAttributes(map);
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
        UhAttributes attributes = new UhAttributes(map);
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
        uids.add("testiwc");
        map.put("uid", uids);
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUid(), is("")); // Note this result.
        assertThat(attributes.getUhUuid(), is("10967714"));
    }

    @Test
    public void loadMapWithNullMap() {
        Map<Object, Object> map = null;
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is(""));
        assertThat(attributes.getUid(), is(""));
    }

    @Test
    public void loadMapWithNullMapEntry() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uid", null);
        map.put("uhUuid", null);
        UhAttributes attributes = new UhAttributes(map);
        assertThat( attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is(""));
        assertThat(attributes.getUid(), is(""));
    }

    @Test
    public void loadMapWithEmptyMapEntry() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uid", new ArrayList<>());
        map.put("uhUuid", new ArrayList<>(0));
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is(""));
        assertThat(attributes.getUid(), is(""));
    }

    @Test
    public void loadMapWithNullKey() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "10967714");
        map.put(null, "testiwc");
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is("10967714"));
        assertThat(attributes.getUid(), is("")); // Note this result.
    }

    @Test
    public void loadMapWithUnexpectedType() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "666666");

        Map<Long, java.util.Date> uidMap = new HashMap<>();
        uidMap.put(666L, new java.util.Date());
        map.put("uid", uidMap);

        UhAttributes attributes = new UhAttributes(map);

        assertThat(attributes.getUsername(), is(""));
        assertThat(attributes.getUhUuid(), is("666666"));
        assertThat(attributes.getUid(), is("")); // Note result.
    }

    @Test
    public void loadMapWithNullUsername() {
        String username = null;
        Map<Object, Object> map = new HashMap<>();
        map.put("uid", "testiwd");
        map.put("uhUuid", "6666666");
        UhAttributes attributes = new UhAttributes(username, map);
        assertThat(attributes.getUid(), is("testiwd"));
        assertThat(attributes.getUhUuid(), is("6666666"));
        assertThat(attributes.getUsername(), is(""));
    }

    @Test
    public void misc() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uid", "testiwd");
        map.put("uhUuid", "666666");
        map.put("cn", "IamtstC");
        map.put("mail", "iamtstc@example.com");
        map.put("eduPersonAffiliation", "aff");
        UhAttributes attributes = new UhAttributes(map);

        assertThat(attributes.getMap().size(), is(5));
        assertThat(attributes.getUid(), is("testiwd"));
        assertThat(attributes.getUhUuid(), is("666666"));
        assertThat(attributes.getName(), is("IamtstC"));
        assertThat(attributes.getMail().get(0), is("iamtstc@example.com"));
        assertThat(attributes.getAffiliation().get(0), is("aff"));

        assertThat(attributes.toString(), containsString("uid=testiwd"));
    }
}
