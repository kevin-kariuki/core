package com.opsbears.cscanner.exoscale;

import com.opsbears.cscanner.core.CloudProviderConnection;
import com.opsbears.cscanner.core.HostDiscoveryClient;
import com.opsbears.cscanner.core.HostDiscoveryCloudProviderConnection;
import com.opsbears.cscanner.firewall.FirewallClient;
import com.opsbears.cscanner.firewall.FirewallConnection;
import com.opsbears.cscanner.objectstorage.ObjectStorageClient;
import com.opsbears.cscanner.objectstorage.ObjectStorageConnection;
import com.opsbears.cscanner.objectstorage.S3ObjectStorageClient;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExoscaleConnection implements CloudProviderConnection, HostDiscoveryCloudProviderConnection, ObjectStorageConnection, FirewallConnection {
    private final String name;
    private final ExoscaleConfiguration exoscaleConfiguration;
    private final ExoscaleFirewallClient exoscaleFirewallClient;

    public ExoscaleConnection(
        String name,
        ExoscaleConfiguration exoscaleConfiguration
    ) {
        this.name = name;

        this.exoscaleConfiguration = exoscaleConfiguration;
        //todo handle cloudstack config
        exoscaleFirewallClient = new ExoscaleFirewallClient(
            exoscaleConfiguration.key,
            exoscaleConfiguration.secret
        );
    }

    @Override
    public ObjectStorageClient getObjectStorageClient() {
        return new S3ObjectStorageClient(new ExoscaleS3ObjectStorageFactory(exoscaleConfiguration));
    }

    @Override
    public String getConnectionName() {
        return name;
    }

    @Override
    public FirewallClient getFirewallClient() {
        return exoscaleFirewallClient;
    }

    @Override
    public HostDiscoveryClient getHostDiscoveryClient() {
        return new ExoscaleHostDiscoveryClient(exoscaleConfiguration);
    }
}
