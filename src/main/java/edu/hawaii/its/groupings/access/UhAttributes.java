package edu.hawaii.its.groupings.access;

import java.util.List;
import java.util.Map;

public interface UhAttributes {

    // Methods specific to UH results.

    String getUid();

    String getUhUuid();

    String getName();

    List<String> getMail();

    List<String> getAffiliation();

    // Generic methods.

    String getValue(String name);

    List<String> getValues(String name);

    Map<?, ?> getMap();
}
