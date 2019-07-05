package io.cscanner.core.rule.firewall;

import io.cscanner.core.engine.ScannerCoreFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public interface TestFirewallClientFactory extends Supplier<TestFirewallClient> {
    ScannerCoreFactory getScannerCore();
}
