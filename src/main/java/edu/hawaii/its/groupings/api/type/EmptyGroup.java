package edu.hawaii.its.groupings.api.type;

public final class EmptyGroup extends Group {

    @Override
    public void addMember(Person person) {
        throw new UnsupportedOperationException();
    }
}
