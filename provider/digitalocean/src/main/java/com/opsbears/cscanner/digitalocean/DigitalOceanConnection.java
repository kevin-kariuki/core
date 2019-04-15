package com.opsbears.cscanner.digitalocean;

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
public class DigitalOceanConnection implements CloudProviderConnection, HostDiscoveryCloudProviderConnection, ObjectStorageConnection, FirewallConnection {
    private final String connectionName;
    private final DigitalOceanConfiguration configuration;

    public DigitalOceanConnection(String connectionName, DigitalOceanConfiguration configuration) {
        this.connectionName = connectionName;
        this.configuration = configuration;
    }

    @Override
    public ObjectStorageClient getObjectStorageClient() {
        return new S3ObjectStorageClient(new DigitalOceanS3ClientSupplier(configuration));
    }

    @Override
    public String getConnectionName() {
        return "digitalocean";
    }

    @Override
    public FirewallClient getFirewallClient() {
        return new DigitalOceanFirewallClient(
            configuration
        );
    }

    @Override
    public HostDiscoveryClient getHostDiscoveryClient() {
        return new DigitalOceanHostDiscoveryClient(
            connectionName,
            configuration
        );
    }
}
