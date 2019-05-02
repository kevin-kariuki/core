package com.opsbears.cscanner.exoscale;

import com.opsbears.cscanner.core.CloudProvider;
import com.opsbears.cscanner.core.HostDiscoveryCloudProvider;
import com.opsbears.cscanner.firewall.FirewallCloudProvider;
import com.opsbears.cscanner.objectstorage.ObjectStorageCloudProvider;
import com.opsbears.cscanner.tags.TaggedResourceCloudProvider;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExoscaleCloudProvider implements
    CloudProvider<ExoscaleConfiguration, ExoscaleConnection>,
    HostDiscoveryCloudProvider<ExoscaleConfiguration, ExoscaleConnection>,
    ObjectStorageCloudProvider<ExoscaleConfiguration, ExoscaleConnection>,
    FirewallCloudProvider<ExoscaleConfiguration, ExoscaleConnection>,
    TaggedResourceCloudProvider<ExoscaleConfiguration, ExoscaleConnection> {
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
