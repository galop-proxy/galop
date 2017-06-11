package io.github.galop_proxy.galop.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.*;

final class ConfigurationFileLoaderImpl implements ConfigurationFileLoader {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationFileLoader.class);

    private final ConfigurationFactory configurationFactory;

    @Inject
    ConfigurationFileLoaderImpl(final ConfigurationFactory configurationFactory) {
        this.configurationFactory = checkNotNull(configurationFactory, "configurationFactory");
    }

    @Override
    public Configuration load(final Path path) throws IOException, InvalidConfigurationException {

        checkNotNull(path, "path");

        LOGGER.info("Loading configuration file: " + path.toAbsolutePath());

        try (final FileInputStream fileInputStream = new FileInputStream(path.toFile())) {

            final Properties properties = new Properties();
            properties.load(fileInputStream);

            final Configuration configuration = configurationFactory.parse(convertToMap(properties));
            logConfiguration(configuration);
            return configuration;

        } catch (final FileNotFoundException ex) {
            LOGGER.error("Could not find configuration file: " + path.toAbsolutePath());
            throw ex;
        } catch (final IOException | InvalidConfigurationException | RuntimeException ex) {
            LOGGER.error("Could not parse configuration file: " + ex.getMessage());
            throw ex;
        }

    }

    private Map<String, String> convertToMap(final Properties properties) {
        final Map<String, String> map = new HashMap<>();
        properties.stringPropertyNames().forEach(name -> map.put(name, properties.getProperty(name)));
        return map;
    }

    private void logConfiguration(final Configuration configuration) {
        LOGGER.info("Loaded configuration:");
        logProxyConfiguration(configuration.getProxy());
        logTargetConfiguration(configuration.getTarget());
        logHttpConfiguration(configuration.getHttp());
    }

    private void logProxyConfiguration(final ProxyConfiguration configuration) {
        log(PROXY_PORT, configuration.getPort());
        log(PROXY_BACKLOG_SIZE, configuration.getBacklogSize());
        log(PROXY_BIND_ADDRESS, configuration.getBindAddress());
    }

    private void logTargetConfiguration(final TargetConfiguration configuration) {
        log(TARGET_ADDRESS, configuration.getAddress());
        log(TARGET_PORT, configuration.getPort());
        log(TARGET_CONNECTION_TIMEOUT, configuration.getConnectionTimeout());
    }

    private void logHttpConfiguration(final HttpConfiguration configuration) {
        logHttpConnectionConfiguration(configuration.getConnection());
        logHttpHeaderConfiguration(configuration.getHeader());
    }

    private void logHttpConnectionConfiguration(final HttpConnectionConfiguration configuration) {
        log(HTTP_CONNECTION_LOG_INTERVAL, configuration.getLogInterval());
        log(HTTP_CONNECTION_TERMINATION_TIMEOUT, configuration.getTerminationTimeout());
    }

    private void logHttpHeaderConfiguration(final HttpHeaderConfiguration configuration) {
        logHttpHeaderRequestConfiguration(configuration.getRequest());
        logHttpHeaderResponseConfiguration(configuration.getResponse());
    }

    private void logHttpHeaderRequestConfiguration(final HttpHeaderRequestConfiguration configuration) {
        log(HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT, configuration.getReceiveTimeout());
        log(HTTP_HEADER_REQUEST_MAX_SIZE, configuration.getMaxSize());
    }

    private void logHttpHeaderResponseConfiguration(final HttpHeaderResponseConfiguration configuration) {
        log(HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT, configuration.getReceiveTimeout());
        log(HTTP_HEADER_RESPONSE_MAX_SIZE, configuration.getMaxSize());
    }

    private void log(final String key, final Object value) {
        LOGGER.info(key + " = " + value);
    }

}
