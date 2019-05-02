package com.opsbears.cscanner.tags;

import com.opsbears.cscanner.core.ScannerCoreFactory;
import com.opsbears.cscanner.firewall.TestFirewallClient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public interface TaggedResourceTestClientFactory extends Supplier<TestFirewallClient> {
    ScannerCoreFactory getScannerCore();
}
