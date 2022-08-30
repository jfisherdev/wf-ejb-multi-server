package com.jfisherdev.wfejbmultiserver.admin.api.config;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Josh Fisher
 */
public class ConfigProperty implements Serializable {

    private String key;
    private String value;

    public ConfigProperty() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = Objects.requireNonNull(key, "Key may not be null");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean hasValue() {
        return value != null && !value.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigProperty that = (ConfigProperty) o;
        return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "ConfigProperty{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
