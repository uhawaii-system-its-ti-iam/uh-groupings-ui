package edu.hawaii.its.api.type;

import javax.persistence.Entity;

@Entity
public final class EmptyGroup extends Group {

    @Override
    public void addMember(Person person) {
        throw new UnsupportedOperationException();
    }
}
