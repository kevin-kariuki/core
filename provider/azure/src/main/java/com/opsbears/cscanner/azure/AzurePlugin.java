package com.opsbears.cscanner.azure;

import com.opsbears.cscanner.core.CloudProvider;
import com.opsbears.cscanner.core.Plugin;

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
