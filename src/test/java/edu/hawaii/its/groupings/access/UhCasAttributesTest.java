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
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getUhUuid(), is(""));
        assertThat(attributes.getUid(), is(""));

        assertThat(attributes.getValue("not-a-key"), is(""));
    }

    @Test
    public void loadMapValid() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "99997010");
        map.put("uid", "testiwta");
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUhUuid(), is("99997010"));
        assertThat(attributes.getUid(), is("testiwta"));
        assertThat(attributes.getValue("not-a-key"), is(""));
        assertThat(attributes.getValue(null), is(""));
    }

    @Test
    public void loadMapInvalidValueType() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "99997010");
        map.put("uid", 666);
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getUhUuid(), is("99997010"));
        assertThat(attributes.getUid(), is("")); // Internal error.
        assertThat(attributes.getValue("not-a-key"), is(""));
    }

    @Test
    public void loadMapInvalidKeyType() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "99997010");
        map.put(666, 666);
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getUhUuid(), is("99997010"));
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getValue("not-a-key"), is(""));
    }

    @Test
    public void loadMapInvalidTypes() {
        Map<Object, Object> map = new HashMap<>();
        map.put(666, 666);
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getUhUuid(), is(""));
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getValue("not-a-key"), is(""));
    }

    @Test
    public void loadMapWithArrayList() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "99997033");
        List<Object> uids = new ArrayList<>();
        uids.add("testiwtc");
        uids.add("testiwtb");
        map.put("uid", uids);
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUhUuid(), is("99997033"));
        assertThat(attributes.getUhUuid(), is("99997033"));
        assertThat(attributes.getUid(), is("testiwtc"));
    }

    @Test
    public void loadMapWithArrayListWithNullEntries() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "99997033");
        List<Object> uids = new ArrayList<>();
        uids.add(null);
        uids.add(null);
        map.put("uid", uids);
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getUhUuid(), is("99997033"));
    }

    @Test
    public void loadMapWithArrayListWithEmptyEntries() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "99997033");
        List<Object> uids = new ArrayList<>();
        uids.add("");
        uids.add("");
        map.put("uid", uids);
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getUhUuid(), is("99997033"));
    }

    @Test
    public void loadMapWithArrayListWithManyEntries() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "99997033");
        List<Object> uids = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            uids.add("");
        }
        uids.add("testiwtc");
        map.put("uid", uids);
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getUid(), is("")); // Note this result.
        assertThat(attributes.getUhUuid(), is("99997033"));
    }

    @Test
    public void loadMapWithNullMap() {
        Map<Object, Object> map = null;
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getUhUuid(), is(""));
        assertThat(attributes.getUid(), is(""));
    }

    @Test
    public void loadMapWithNullMapEntry() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uid", null);
        map.put("uhUuid", null);
        UhAttributes attributes = new UhAttributes(map);
        assertThat( attributes.getUid(), is(""));
        assertThat(attributes.getUhUuid(), is(""));
        assertThat(attributes.getUid(), is(""));
    }

    @Test
    public void loadMapWithEmptyMapEntry() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uid", new ArrayList<>());
        map.put("uhUuid", new ArrayList<>(0));
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getUhUuid(), is(""));
        assertThat(attributes.getUid(), is(""));
    }

    @Test
    public void loadMapWithNullKey() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "99997033");
        map.put(null, "testiwtc");
        UhAttributes attributes = new UhAttributes(map);
        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getUhUuid(), is("99997033"));
        assertThat(attributes.getUid(), is("")); // Note this result.
    }

    @Test
    public void loadMapWithUnexpectedType() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uhUuid", "99997010");

        Map<Long, java.util.Date> uidMap = new HashMap<>();
        uidMap.put(666L, new java.util.Date());
        map.put("uid", uidMap);

        UhAttributes attributes = new UhAttributes(map);

        assertThat(attributes.getUid(), is(""));
        assertThat(attributes.getUhUuid(), is("99997010"));
        assertThat(attributes.getUid(), is("")); // Note result.
    }

    @Test
    public void loadMapWithNullUid() {
        String uid = null;
        Map<Object, Object> map = new HashMap<>();
        map.put("uid", "testiwtd");
        map.put("uhUuid", "99997043");
        UhAttributes attributes = new UhAttributes(uid, map);
        assertThat(attributes.getUid(), is("testiwtd"));
        assertThat(attributes.getUhUuid(), is("99997043"));
    }

    @Test
    public void misc() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uid", "testiwtd");
        map.put("uhUuid", "99997043");
        map.put("cn", "Testf-iwt-d TestIAM-faculty");
        map.put("mail", "iamtstd@example.com");
        map.put("eduPersonAffiliation", "aff");
        UhAttributes attributes = new UhAttributes(map);

        assertThat(attributes.getMap().size(), is(5));
        assertThat(attributes.getUid(), is("testiwtd"));
        assertThat(attributes.getUhUuid(), is("99997043"));
        assertThat(attributes.getName(), is("Testf-iwt-d TestIAM-faculty"));
        assertThat(attributes.getMail().get(0), is("iamtstd@example.com"));
        assertThat(attributes.getAffiliation().get(0), is("aff"));

        assertThat(attributes.toString(), containsString("uid=testiwtd"));
    }
}
