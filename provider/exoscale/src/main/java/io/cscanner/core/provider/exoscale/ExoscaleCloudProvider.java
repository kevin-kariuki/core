package io.cscanner.core.provider.exoscale;

import io.cscanner.core.test.engine.CloudProvider;
import io.cscanner.core.test.engine.HostDiscoveryCloudProvider;
import io.cscanner.core.rule.firewall.FirewallCloudProvider;
import io.cscanner.core.rule.objectstorage.ObjectStorageCloudProvider;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExoscaleCloudProvider implements
    CloudProvider<ExoscaleConfiguration, ExoscaleConnection>,
    HostDiscoveryCloudProvider<ExoscaleConfiguration, ExoscaleConnection>,
    ObjectStorageCloudProvider<ExoscaleConfiguration, ExoscaleConnection>,
    FirewallCloudProvider<ExoscaleConfiguration, ExoscaleConnection> {
    @Override
    public String getName() {
        return "exoscale";
    }

    @Override
    public Class<ExoscaleConfiguration> getConfigurationType() {
        return ExoscaleConfiguration.class;
    }

    @Override
    public Class<ExoscaleConnection> getConnectionType() {
        return ExoscaleConnection.class;
    }

    @Override
    public ExoscaleConnection getConnection(
        String name,
        ExoscaleConfiguration configuration
    ) {
        return new ExoscaleConnection(name, configuration);
    }
}
