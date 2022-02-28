package edu.hawaii.its.groupings.access;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UhCasAttributes implements UhAttributes {

    private Map<String, List<String>> uhAttributeMap = new HashMap<>();
    private final String uid; // CAS login username.
    private final Map<?, ?> map; // Original CAS results.

    // Constructor.
    public UhCasAttributes() {
        this(new HashMap<>());
    }

    // Constructor.
    public UhCasAttributes(Map<?, ?> map) {
        this("", map);
    }

    // Constructor.
    public UhCasAttributes(String uid, Map<?, ?> map) {
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

    @Override
    public String getName() {
        return getValue("cn");
    }

    public String getUsername() {
        //return username;
        return "";
    }

    @Override
    public String getUid() {
        List<String> values = uhAttributeMap.get("uid");
        if (values != null) {
            // Check expected case first.
            if (values.size() == 1) {
                return values.get(0); // We are done.
            }

            if (values.size() > 1) {
                // More than one uid in the results.
                // Try to match up with the username.
                for (String s : values) {
                    if (s.equals(getUsername())) {
                        return s;
                    }
                }

                // Couldn't match up username with uid,
                // so just return first value.
                return values.get(0); // We are done.
            }
        }

        return ""; // Didn't find anything.
    }

    @Override
    public String getUhUuid() {
        return getValue("uhUuid");
    }

    @Override
    public List<String> getMail() {
        return getValues("mail");
    }

    @Override
    public List<String> getAffiliation() {
        return getValues("eduPersonAffiliation");
    }

    @Override
    public List<String> getValues(String name) {
        List<String> results = uhAttributeMap.get(toLowerCase(name));
        if (results != null) {
            return Collections.unmodifiableList(results);
        }
        return Collections.emptyList();
    }

    @Override
    public String getValue(String name) {
        List<String> results = getValues(name);
        return results.isEmpty() ? "" : results.get(0);
    }

    @Override
    public Map<?, ?> getMap() {
        return Collections.unmodifiableMap(map);
    }

    private String toLowerCase(String s) {
        return (s != null) ? s.toLowerCase() : s;
    }

    @Override
    public String toString() {
        return "UhCasAttributes [username=" + uid
                + ", uhAttributeMap=" + uhAttributeMap
                + ", map=" + map + "]";
    }

}
