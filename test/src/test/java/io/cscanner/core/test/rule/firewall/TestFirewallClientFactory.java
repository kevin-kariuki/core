package io.cscanner.core.test.rule.firewall;

import io.cscanner.core.test.engine.ScannerCoreFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public interface TestFirewallClientFactory extends Supplier<TestFirewallClient> {
    ScannerCoreFactory getScannerCore();
}
