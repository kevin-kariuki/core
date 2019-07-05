package io.cscanner.core.provider.aws;

import io.cscanner.core.test.engine.CloudProviderConnection;
import io.cscanner.core.test.engine.HostDiscoveryClient;
import io.cscanner.core.test.engine.HostDiscoveryCloudProviderConnection;
import io.cscanner.core.rule.firewall.FirewallClient;
import io.cscanner.core.rule.firewall.FirewallConnection;
import io.cscanner.core.rule.objectstorage.ObjectStorageClient;
import io.cscanner.core.rule.objectstorage.ObjectStorageConnection;
import io.cscanner.core.rule.objectstorage.S3ObjectStorageClient;

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
