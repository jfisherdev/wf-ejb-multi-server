package com.jfisherdev.wfejbmultiserver.commons.ejbclient;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author Josh Fisher
 */
public class CachedEjbClientFactory implements EjbClientFactory {

    private static class Key {
        private final String providerUrl;
        private final Properties properties;

        private Key(String providerUrl, Properties properties) {
            this.providerUrl = providerUrl;
            this.properties = properties;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return providerUrl.equals(key.providerUrl) && properties.equals(key.properties);
        }

        @Override
        public int hashCode() {
            return Objects.hash(providerUrl, properties);
        }

        @Override
        public String toString() {
            return "Key{" +
                    "providerUrl='" + providerUrl + '\'' +
                    ", properties=" + properties +
                    '}';
        }
    }

    private static Key toKey(String providerUrl, Properties properties) {
        return new Key(providerUrl, properties);
    }

    private static class Holder {
        static final CachedEjbClientFactory INSTANCE = new CachedEjbClientFactory();
    }

    public static CachedEjbClientFactory getInstance() {
        return Holder.INSTANCE;
    }

    private final Map<Key, EjbClient> cache = new ConcurrentHashMap<>();

    @Override
    public EjbClient getEjbClient() {
        return getEjbClient("");
    }

    @Override
    public EjbClient getEjbClient(String providerUrl) {
        return getEjbClient(providerUrl, new Properties());
    }

    @Override
    public EjbClient getEjbClient(String providerUrl, Properties properties) {
        final Key key = toKey(providerUrl, properties);
        return cache.computeIfAbsent(key, key1 -> new EjbClient(providerUrl, properties));
    }

}
