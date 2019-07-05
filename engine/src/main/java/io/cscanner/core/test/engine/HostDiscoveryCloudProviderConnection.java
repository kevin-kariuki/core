package io.cscanner.core.test.engine;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface HostDiscoveryCloudProviderConnection extends CloudProviderConnection {
    HostDiscoveryClient getHostDiscoveryClient();
}
