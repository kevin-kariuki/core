package io.cscanner.core.rule.firewall;

import io.cscanner.core.test.engine.CloudProvider;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface FirewallCloudProvider<CONFIGURATIONTYPE, CONNECTIONTYPE extends FirewallConnection> extends CloudProvider<CONFIGURATIONTYPE, CONNECTIONTYPE> {
}
