package io.cscanner.core.provider.azure;

import io.cscanner.core.engine.CloudProvider;
import io.cscanner.core.engine.Plugin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
public class AzurePlugin implements Plugin {
    @Override
    public List<CloudProvider<?, ?>> getCloudProviders() {
        return Collections.singletonList(
            new AzureCloudProvider()
        );
    }
}
