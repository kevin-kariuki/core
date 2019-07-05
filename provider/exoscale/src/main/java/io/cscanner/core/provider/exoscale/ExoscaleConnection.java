package io.cscanner.core.provider.exoscale;

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
        return new ExoscaleHostDiscoveryClient(name, exoscaleConfiguration);
    }
}
