package com.opsbears.cscanner.exoscale;

import com.opsbears.cscanner.core.CloudProvider;
import com.opsbears.cscanner.core.Plugin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
public class ExoscalePlugin implements Plugin {
    @Override
    public List<CloudProvider<?, ?>> getCloudProviders() {
        return Collections.singletonList(
                new ExoscaleCloudProvider()
        );
    }
}
