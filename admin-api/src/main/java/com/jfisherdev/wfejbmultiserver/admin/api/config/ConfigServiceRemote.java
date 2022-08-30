package com.jfisherdev.wfejbmultiserver.admin.api.config;

/**
 * @author Josh Fisher
 */
public interface ConfigServiceRemote {

    String BEAN_NAME = "ConfigService";

    ConfigProperty getConfigProperty(String key);

}
