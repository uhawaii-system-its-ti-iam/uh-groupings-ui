package edu.hawaii.its.holiday.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JsonData<E> {

    private String key;
    private E data;

    public JsonData(String key, E data) {
        this.key = key;
        this.data = data;
    }

    public JsonData(E data) {
        this("data", data);
    }

    @JsonIgnore
    public String getKey() {
        return key;
    }

    public E getData() {
        return data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        JsonData<?> other = (JsonData<?>) obj;
        if (data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!data.equals(other.data)) {
            return false;
        }
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "JsonData [key=" + key + ", data=" + data + "]";
    }

}
