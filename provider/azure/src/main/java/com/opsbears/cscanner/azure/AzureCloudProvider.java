package com.opsbears.cscanner.azure;

import com.opsbears.cscanner.core.CloudProvider;
import com.opsbears.cscanner.core.HostDiscoveryCloudProvider;
import com.opsbears.cscanner.firewall.FirewallCloudProvider;

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
