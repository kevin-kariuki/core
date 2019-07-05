package io.cscanner.core.provider.aws;

import io.cscanner.core.test.engine.CloudProvider;
import io.cscanner.core.test.engine.Plugin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
public class AWSPlugin implements Plugin {
    @Override
    public List<CloudProvider<?, ?>> getCloudProviders() {
        return Collections.singletonList(
                new AWSCloudProvider()
        );
    }
}
