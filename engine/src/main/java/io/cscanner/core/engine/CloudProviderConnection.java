package io.cscanner.core.engine;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface CloudProviderConnection {
    String getConnectionName();
}
