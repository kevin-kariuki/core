package io.cscanner.core.engine;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface HostDiscoveryCloudProviderConnection extends CloudProviderConnection {
    HostDiscoveryClient getHostDiscoveryClient();
}
