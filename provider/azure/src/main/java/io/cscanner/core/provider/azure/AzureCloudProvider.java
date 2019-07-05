package io.cscanner.core.provider.azure;

import io.cscanner.core.engine.CloudProvider;
import io.cscanner.core.engine.HostDiscoveryCloudProvider;
import io.cscanner.core.rule.firewall.FirewallCloudProvider;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AzureCloudProvider implements
    CloudProvider<AzureConfiguration, AzureConnection>,
    HostDiscoveryCloudProvider<AzureConfiguration, AzureConnection>,
    FirewallCloudProvider<AzureConfiguration, AzureConnection> {

    @Override
    public String getName() {
        return "azure";
    }

    @Override
    public Class<AzureConfiguration> getConfigurationType() {
        return AzureConfiguration.class;
    }

    @Override
    public Class<AzureConnection> getConnectionType() {
        return AzureConnection.class;
    }

    @Override
    public AzureConnection getConnection(
        String name,
        AzureConfiguration configuration
    ) {
        return new AzureConnection(
            name,
            configuration
        );
    }
}
