package io.cscanner.core.engine;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public interface HostDiscoveryTestClientFactory extends Supplier<HostDiscoveryTestClient> {
    ScannerCoreFactory getScannerCore();
    boolean isConfigured();
}
