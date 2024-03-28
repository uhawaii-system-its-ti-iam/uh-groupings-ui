package edu.hawaii.its.groupings.access;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UhAttributes  {

    private Map<String, List<String>> uhAttributeMap = new HashMap<>();
    private final String uid; // CAS login uid.
    private final Map<?, ?> map; // Original CAS results.

    // Constructor.
    public UhAttributes() {
        this(new HashMap<>());
    }

    // Constructor.
    public UhAttributes(Map<?, ?> map) {
        this("", map);
    }

    // Constructor.
    public UhAttributes(String uid, Map<?, ?> map) {
        this.uid = uid != null ? uid : "";
        this.map = map;
        if (map != null) {
            for (Object key : map.keySet()) {
                if (key instanceof String) {
                    String k = ((String) key).toLowerCase();
                    Object v = map.get(key);
                    if (v != null) {
                        if (v instanceof String) {
                            uhAttributeMap.put(k, Arrays.asList((String) v));
                        } else if (v instanceof List) {
                            List<String> lst = new ArrayList<>();
                            for (Object o : (List<?>) v) {
                                if (o != null && o instanceof String) {
                                    lst.add((String) o);
                                }
                            }
                            uhAttributeMap.put(k, lst);
                        }
                    }
                }
            }
        }
    }

    public String getName() {
        return getValue("cn");
    }

    public String getUid() { return getValue("uid"); }

    public String getUhUuid() {
        return getValue("uhUuid");
    }

    public List<String> getMail() {
        return getValues("mail");
    }

    public List<String> getAffiliation() {
        return getValues("eduPersonAffiliation");
    }

    public List<String> getValues(String name) {
        List<String> results = uhAttributeMap.get(toLowerCase(name));
        if (results != null) {
            return Collections.unmodifiableList(results);
        }
        return Collections.emptyList();
    }

    public String getValue(String name) {
        List<String> results = getValues(name);
        return results.isEmpty() ? "" : results.get(0);
    }

    public Map<?, ?> getMap() {
        return Collections.unmodifiableMap(map);
    }

    private String toLowerCase(String s) {
        return (s != null) ? s.toLowerCase() : s;
    }

    public String toString() {
        return "UhCasAttributes [uid=" + uid
                + ", uhAttributeMap=" + uhAttributeMap
                + ", map=" + map + "]";
    }

}
