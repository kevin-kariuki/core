package com.opsbears.cscanner.exoscale;

import com.opsbears.cscanner.core.CloudProvider;
import com.opsbears.cscanner.firewall.FirewallCloudProvider;
import com.opsbears.cscanner.objectstorage.ObjectStorageCloudProvider;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExoscaleCloudProvider implements CloudProvider<ExoscaleConfiguration, ExoscaleConnection>, ObjectStorageCloudProvider<ExoscaleConfiguration, ExoscaleConnection>, FirewallCloudProvider<ExoscaleConfiguration, ExoscaleConnection> {
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
