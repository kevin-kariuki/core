package io.cscanner.core.test;

import io.cscanner.core.engine.ConfigLoader;
import io.cscanner.core.engine.Plugin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class TestPlugin implements Plugin {
    private final List<ConfigLoader> configLoaders;

    public TestPlugin(List<ConfigLoader> configLoaders) {
        this.configLoaders = configLoaders;
    }

    @Override
    public List<ConfigLoader> getConfigLoaders() {
        return configLoaders;
    }
}
