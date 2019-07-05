package io.cscanner.core.test.engine;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface HostDiscoveryCloudProvider<CONFIGURATIONTYPE, CONNECTIONTYPE extends HostDiscoveryCloudProviderConnection> extends CloudProvider<CONFIGURATIONTYPE, CONNECTIONTYPE> {

}
