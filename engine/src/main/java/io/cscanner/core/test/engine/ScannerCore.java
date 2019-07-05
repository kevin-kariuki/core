package io.cscanner.core.test.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Stream;

//todo this is too long / too many responsibilities, refactor and split
@ParametersAreNonnullByDefault
public class ScannerCore {
    private final static Logger logger = LoggerFactory.getLogger(ScannerCore.class);
    private final List<Plugin> plugins;
    private final CloudProviderConnectionFactory cloudProviderConnectionFactory = new CloudProviderConnectionFactory();
    private final RuleFactory ruleFactory = new RuleFactory();
    private final List<RuleConfiguration> ruleConfigurations = new ArrayList<>();
    private final List<ConfigLoader> configLoaders = new ArrayList<>();
    private final List<CloudProvider<?, ?>> cloudProviders = new ArrayList<>();
    private final List<RuleBuilder<?, ?, ?>> ruleBuilders = new ArrayList<>();
    private final Map<String,CloudProvider<?, ?>> cloudProviderByConnectionKey = new HashMap<>();
    private final Map<String, CloudProviderConnection> cloudProviderConnectionMap = new HashMap<>();
    private boolean loaded = false;

    public ScannerCore(List<Plugin> plugins) {
        this.plugins = plugins;
    }

    private void load() {
        if (loaded) {
            return;
        }
        loaded = true;

        //region Plugins
        logger.info("Loading plugins...");
        for (Plugin plugin : plugins) {
            configLoaders.addAll(plugin.getConfigLoaders());
            cloudProviders.addAll(plugin.getCloudProviders());
            ruleBuilders.addAll(plugin.getSupportedRules());
        }
        logger.info("Plugins loaded.");
        //endregion

        //region Connection configuration
        logger.info("Loading configuration...");
        Map<String, ConnectionConfiguration> connectionConfigurations = new HashMap<>();
        for (ConfigLoader configLoader : configLoaders) {
            connectionConfigurations.putAll(configLoader.loadConnectionConfigurations());
            ruleConfigurations.addAll(configLoader.loadRuleConfigurations());
        }
        logger.info("Configuration loaded.");
        //endregion

        //region Cloud provider mapping
        logger.info("Configuring cloud providers...");
        for (String connectionKey : connectionConfigurations.keySet()) {
            ConnectionConfiguration connectionConfiguration = connectionConfigurations.get(connectionKey);

            Optional<CloudProvider<?, ?>> cloudProvider = cloudProviders.stream().filter(
                cp -> cp.getName().equalsIgnoreCase(connectionConfiguration.type)
            ).findFirst();

            if (!cloudProvider.isPresent()) {
                throw new RuntimeException("Cloud provider '" + connectionConfiguration.type + "' is not supported.");
            }
            cloudProviderByConnectionKey.put(connectionKey, cloudProvider.get());
            cloudProviderConnectionMap.put(
                connectionKey,
                cloudProviderConnectionFactory.create(
                    cloudProvider.get(),
                    connectionKey,
                    connectionConfigurations.get(connectionKey).options
                )
            );
        }
        logger.info("Cloud providers configured.");
        //endregion
    }

    public Stream<HostDiscoveryRecord> listIps() {
        load();
        logger.info("Listing IP addresses...");
        Stream<HostDiscoveryRecord> streams = Stream.empty();
        for (CloudProviderConnection connection : cloudProviderConnectionMap.values()) {
            if (connection instanceof HostDiscoveryCloudProviderConnection) {
                streams = Stream.concat(
                    streams,
                    ((HostDiscoveryCloudProviderConnection) connection).getHostDiscoveryClient().listIpAddresses()
                );
            }
        }
        logger.info("IP address list complete.");
        return streams;
    }

    public List<RuleResult> scan() {
        load();
        logger.info("Configuring and executing rules...");
        List<RuleResult> result = new ArrayList<>();
        for (RuleConfiguration ruleConfiguration : ruleConfigurations) {
            String ruleType = ruleConfiguration.type;
            List<String> ruleConnections = ruleConfiguration.connections;
            Map<String, Object> ruleOptions = ruleConfiguration.options;

            Optional<RuleBuilder<?, ?, ?>> ruleBuilder = ruleBuilders
                .stream()
                .filter(
                    rb ->
                        rb.getType().equalsIgnoreCase(ruleType) ||
                        rb.getTypeAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(ruleType))
                )
                .findFirst();

            if (!ruleBuilder.isPresent()) {
                throw new RuntimeException("Rule type '" + ruleType + "' not supported.");
            }

            if (ruleConnections.isEmpty()) {
                ruleConnections.addAll(cloudProviderByConnectionKey.keySet());
            }
            Rule rule = ruleFactory.create(ruleBuilder.get(), ruleOptions);

            for (String ruleConnection : ruleConnections) {
                if (!cloudProviderByConnectionKey.containsKey(ruleConnection)) {
                    throw new RuntimeException("No such connection: " + ruleConnection);
                }

                CloudProvider<?, ?> connection = cloudProviderByConnectionKey.get(ruleConnection);
                Class<?> connectionType = ruleBuilder.get().getConnectionType();
                if (!connectionType.isAssignableFrom(connection.getConnectionType())) {
                    //This connection does not support this type.
                    logger.debug("Connection type " + connection.getClass().getSimpleName() + " does not support " + connectionType.getSimpleName() + ", skipping.");
                    continue;
                }
                logger.info("Evaluating rule " + rule.getClass().getSimpleName() + " with connection " + ruleConnection + "...");
                //noinspection unchecked
                result.addAll(rule.evaluate(cloudProviderConnectionMap.get(ruleConnection)));
            }
        }
        logger.info("Rule execution complete.");

        return result;
    }

}
