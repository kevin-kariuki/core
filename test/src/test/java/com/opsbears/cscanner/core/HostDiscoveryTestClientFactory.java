package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public interface HostDiscoveryTestClientFactory {
    ScannerCoreFactory getScannerCore();
    boolean isConfigured();
}
