package com.opsbears.cscanner.aws;

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
public class AWSConnection implements CloudProviderConnection, ObjectStorageConnection, FirewallConnection, HostDiscoveryCloudProviderConnection {
    private final String name;
    private final AWSConfiguration awsConfiguration;
    private final AWSFirewallClient awsFirewallClient;

    public AWSConnection(
        String name,
        AWSConfiguration awsConfiguration
    ) {
        this.name = name;
        this.awsConfiguration = awsConfiguration;

        //Put here for caching
        awsFirewallClient = new AWSFirewallClient(awsConfiguration);
    }

    @Override
    public ObjectStorageClient getObjectStorageClient() {
        return new S3ObjectStorageClient(new AWSS3ClientSupplier(awsConfiguration));
    }

    @Override
    public String getConnectionName() {
        return name;
    }

    @Override
    public FirewallClient getFirewallClient() {
        return awsFirewallClient;
    }

    @Override
    public HostDiscoveryClient getHostDiscoveryClient() {
        return new AWSHostDiscoveryClient(name, awsConfiguration);
    }
}
