package io.cscanner.core.test.engine;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface CloudProviderConnection {
    String getConnectionName();
}
