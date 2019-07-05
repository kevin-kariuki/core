package io.cscanner.core.rule.firewall;

import io.cscanner.core.test.engine.CloudProviderConnection;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface FirewallConnection extends CloudProviderConnection {
    String RESOURCE_TYPE = "firewallGroup";

    FirewallClient getFirewallClient();
}
