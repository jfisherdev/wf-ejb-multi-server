package com.jfisherdev.wfejbmultiserver.commons.ejbclient;

import com.jfisherdev.wfejbmultiserver.commons.EjbStringUtils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Josh Fisher
 */
public class EjbClient implements AutoCloseable {


    /**
     * This is an optional vendor-specific name component that follows the module name and precedes the bean-name.
     * It is NOT currently supported or used by this project; however, an empty "//" segment between the namespace and the bean name
     * is required in some client versions. This is here mainly for readability.
     */
    private static final String DISTINCT_NAME = "";

    static final String WILDFLY_NAMING_CLIENT_INITIAL_CONTEXT_FACTORY = "org.wildfly.naming.client.WildFlyInitialContextFactory";

    //Global properties for all connections
    static final String SCOPED_CONTEXT_PROPERTY = "org.jboss.ejb.client.scoped.context";
    static final String SSL_ENABLED_PROPERTY = "remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED";

    static final String REMOTE_CONNECTIONS_PROPERTY = "remote.connections";

    //Everything else is for properties at the connection level
    static final String CONNECTION_PREFIX = "remote.connection.";
    static final String DEFAULT_CONNECTION_NAME = "default";

    static final String CONNECTION_HOST = "host";
    static final String CONNECTION_PORT = "port";
    static final String CONNECTION_SASL_POLICY_NOANONYMOUS = "connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS";
    static final String CONNECTION_SASL_POLICY_NOPLAINTEXT = "connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT";

    static final String HEARTBEAT_INTERVAL_PROPERTY = "ejb.client.heartbeat.interval.ms";
    static final String DEFAULT_HEARTBEAT_INTERVAL_MS = "60000";
    static final String WILDFLY_HEARTBEAT_INTERVAL_PROPERTY = "connect.options.org.jboss.remoting3.RemotingOptions.HEARTBEAT_INTERVAL";

    static final String MAX_INBOUND_MESSAGES_PROPERTY = "ejb.client.max.inbound.messages";
    static final String DEFAULT_MAX_INBOUND_MESSAGES = "80";
    static final String WILDFLY_MAX_INBOUND_MESSAGES_PROPERTY = "channel.options.org.jboss.remoting3.RemotingOptions.MAX_INBOUND_MESSAGES";

    static final String MAX_OUTBOUND_MESSAGES_PROPERTY = "ejb.client.max.outbound.messages";
    static final String DEFAULT_MAX_OUTBOUND_MESSAGES = "80";
    static final String WILDFLY_MAX_OUTBOUND_MESSAGES_PROPERTY = "channel.options.org.jboss.remoting3.RemotingOptions.MAX_OUTBOUND_MESSAGES";

    static final String CONNECT_TIMEOUT_PROPERTY = "client.connect.timeout";
    static final String DEFAULT_CONNECT_TIMEOUT = "30000";
    static final String WILDFLY_CONNECT_TIMEOUT_PROPERTY = "connect.timeout";


    /**
     * Convenience method to create a connection-specific property name.
     *
     * @param connection connection name
     * @param property   property name
     * @return a connection-specific property name
     */
    static String connectionProperty(String connection, String property) {
        return CONNECTION_PREFIX + connection + "." + property;
    }

    private final String providerUrl;
    private final Properties customProperties;
    private Context ejbContext;

    public EjbClient() {
        this("");
    }

    public EjbClient(String providerUrl) {
        this(providerUrl, new Properties());
    }

    public EjbClient(String providerUrl, Properties customProperties) {
        this.providerUrl = providerUrl;
        this.customProperties = Objects.requireNonNull(customProperties, "Custom properties may not be null");
    }

    public String getLookupName(String app,
                                String module,
                                String beanName,
                                Class<?> beanInterface) {
        EjbStringUtils.required(module, "Module is required");
        EjbStringUtils.required(beanName, "Bean name is required");
        Objects.requireNonNull(beanInterface, "Bean interface is required");
        final StringBuilder nameBuilder = new StringBuilder();
        if (EjbStringUtils.isPopulated(app)) {
            nameBuilder.append(app).append("/");
        }

        return nameBuilder.append(module).
                append("/").
                append(DISTINCT_NAME).
                append("/").
                append(fixBeanName(beanName)).
                append("!").
                append(beanInterface.getCanonicalName()).toString();
    }

    public <T> T lookup(String app,
                        String module,
                        String beanName,
                        Class<T> beanInterface) throws NamingException {
        return lookup(getLookupName(app, module, beanName, beanInterface));
    }

    public <T> T lookup(String lookupName) throws NamingException {
        return (T) getEjbContext().lookup(lookupName);
    }

    private Context getEjbContext() throws NamingException {
        if (ejbContext == null) {
            ejbContext = getRootContext();
        }
        return ejbContext;
    }

    private Context getRootContext() throws NamingException {
        if (!EjbStringUtils.isPopulated(providerUrl)) {
            return new InitialContext();
        }
        final Context initialContext = createInitialContext();
        //Use the root ejb: context for scoped EJB client lookups
        return (Context) initialContext.lookup("ejb:");
    }

    private Context createInitialContext() throws NamingException {
        final Properties clientProperties = new Properties();
        final boolean sslEnabled = providerUrl.contains("https");
        clientProperties.setProperty(SSL_ENABLED_PROPERTY, Boolean.toString(sslEnabled));
        clientProperties.setProperty(SCOPED_CONTEXT_PROPERTY, Boolean.TRUE.toString());
        clientProperties.setProperty(Context.INITIAL_CONTEXT_FACTORY, WILDFLY_NAMING_CLIENT_INITIAL_CONTEXT_FACTORY);
        clientProperties.setProperty(Context.PROVIDER_URL, providerUrl);
        final String principal = System.getProperty(Context.SECURITY_PRINCIPAL);
        final String credentials = System.getProperty(Context.SECURITY_CREDENTIALS);
        if (EjbStringUtils.isPopulated(principal) && EjbStringUtils.isPopulated(credentials)) {
            clientProperties.setProperty(Context.SECURITY_PRINCIPAL, principal);
            clientProperties.setProperty(Context.SECURITY_CREDENTIALS, credentials);
        }
        clientProperties.putAll(getConnectionProperties());
        clientProperties.putAll(customProperties);
        return new InitialContext(clientProperties);
    }

    Properties getConnectionProperties() {
        final String connectTimeout = System.getProperty(CONNECT_TIMEOUT_PROPERTY, DEFAULT_CONNECT_TIMEOUT);
        final String heartbeatInterval = System.getProperty(HEARTBEAT_INTERVAL_PROPERTY, DEFAULT_HEARTBEAT_INTERVAL_MS);
        final String maxInboundMessages = System.getProperty(MAX_INBOUND_MESSAGES_PROPERTY, DEFAULT_MAX_INBOUND_MESSAGES);
        final String maxOutboundMessages = System.getProperty(MAX_OUTBOUND_MESSAGES_PROPERTY, DEFAULT_MAX_OUTBOUND_MESSAGES);
        final Map<String, URI> connections = getConnections();

        final Properties connectionProperties = new Properties();

        connectionProperties.setProperty(REMOTE_CONNECTIONS_PROPERTY, String.join(",", connections.keySet()));

        for (Map.Entry<String, URI> entry : connections.entrySet()) {
            final String connection = entry.getKey();
            final URI connectionURI = entry.getValue();

            connectionProperties.setProperty(connectionProperty(connection, CONNECTION_HOST), connectionURI.getHost());
            connectionProperties.setProperty(connectionProperty(connection, CONNECTION_PORT), String.valueOf(connectionURI.getPort()));
            connectionProperties.setProperty(connectionProperty(connection, CONNECTION_SASL_POLICY_NOANONYMOUS), Boolean.FALSE.toString());
            connectionProperties.setProperty(connectionProperty(connection, CONNECTION_SASL_POLICY_NOPLAINTEXT), Boolean.FALSE.toString());
            connectionProperties.setProperty(connectionProperty(connection, WILDFLY_CONNECT_TIMEOUT_PROPERTY), connectTimeout);
            connectionProperties.setProperty(connectionProperty(connection, WILDFLY_HEARTBEAT_INTERVAL_PROPERTY), heartbeatInterval);
            connectionProperties.setProperty(connectionProperty(connection, WILDFLY_MAX_INBOUND_MESSAGES_PROPERTY), maxInboundMessages);
            connectionProperties.setProperty(connectionProperty(connection, WILDFLY_MAX_OUTBOUND_MESSAGES_PROPERTY), maxOutboundMessages);
        }


        return connectionProperties;
    }

    Map<String, URI> getConnections() {
        final String[] urls = providerUrl.split(",");
        //Multiple servers present
        if (urls.length > 1) {
            final Map<String, URI> connections = new LinkedHashMap<>();
            int connectionNumber = 1;
            for (String url : urls) {
                final String connectionName = DEFAULT_CONNECTION_NAME + connectionNumber;
                connections.put(connectionName, URI.create(url));
                connectionNumber++;
            }
            return connections;
        } else {
            return Collections.singletonMap(DEFAULT_CONNECTION_NAME, URI.create(providerUrl));
        }
    }

    private String fixBeanName(String beanName) {
        /*
         * It seems that the WildFly Naming Client implementation does not accept "/" separator characters in bean names
         * and requires them to be escaped. In practice, this should not be common; however, this is present for the example with
         * names like "v2/MessageGeneratorV2" so it would be best to handle it.
         */
        if (beanName.contains("/")) {
            return "\"" + beanName + "\"";
        }
        return beanName;
    }

    @Override
    public void close() throws Exception {
        if (ejbContext != null) {
            ejbContext.close();
        }
    }
}
