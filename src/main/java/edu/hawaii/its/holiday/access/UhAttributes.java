package edu.hawaii.its.holiday.access;

import java.util.List;
import java.util.Map;

public interface UhAttributes {

    // Methods specific to UH results.

    public String getUid();

    public String getUhUuid();

    public String getName();

    public List<String> getMail();

    public List<String> getAffiliation();

    // Generic methods.

    public String getValue(String name);

    public List<String> getValues(String name);

    public Map<?, ?> getMap();
}
