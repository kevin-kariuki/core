package io.cscanner.core.test.engine;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public interface HostDiscoveryTestClientFactory {
    ScannerCoreFactory getScannerCore();
    boolean isConfigured();
}
