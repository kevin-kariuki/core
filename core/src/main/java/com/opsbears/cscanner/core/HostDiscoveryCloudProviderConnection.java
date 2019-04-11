package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface HostDiscoveryCloudProviderConnection extends CloudProviderConnection {
    HostDiscoveryClient getHostDiscoveryClient();
}
