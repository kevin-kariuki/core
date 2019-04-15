package com.opsbears.cscanner.aws;

import com.opsbears.cscanner.core.CloudProvider;
import com.opsbears.cscanner.core.HostDiscoveryCloudProvider;
import com.opsbears.cscanner.firewall.FirewallCloudProvider;
import com.opsbears.cscanner.objectstorage.ObjectStorageCloudProvider;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AWSCloudProvider implements
    CloudProvider<AWSConfiguration, AWSConnection>,
    HostDiscoveryCloudProvider<AWSConfiguration, AWSConnection>,
    ObjectStorageCloudProvider<AWSConfiguration, AWSConnection>,
    FirewallCloudProvider<AWSConfiguration, AWSConnection> {
    @Override
    public String getName() {
        return "aws";
    }

    @Override
    public Class<AWSConfiguration> getConfigurationType() {
        return AWSConfiguration.class;
    }

    @Override
    public Class<AWSConnection> getConnectionType() {
        return AWSConnection.class;
    }

    @Override
    public AWSConnection getConnection(
        String name,
        AWSConfiguration configuration
    ) {
        configuration.validateCredentials();

        return new AWSConnection(name, configuration);
    }
}
