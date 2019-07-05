package io.cscanner.core.config.yaml;

import io.cscanner.core.engine.ConfigLoader;
import io.cscanner.core.engine.Plugin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
public class YamlPlugin implements Plugin {
    private final String filename;

    public YamlPlugin(String filename) {
        this.filename = filename;
    }

    @Override
    public List<ConfigLoader> getConfigLoaders() {
        return Collections.singletonList(
                new YamlConfigLoader(filename)
        );
    }
}
