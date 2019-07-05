package io.cscanner.core.test.provider.digitalocean;

import io.cscanner.core.test.TestConfigurationLoader;
import io.cscanner.core.test.TestPlugin;
import io.cscanner.core.test.engine.*;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class DigitalOceanHostDiscoveryTestClientFactory implements HostDiscoveryTestClientFactory {
    @Nullable
    private final String apiToken;

    public DigitalOceanHostDiscoveryTestClientFactory(@Nullable String apiToken) {
        this.apiToken = apiToken;
    }

    @Override
    public ScannerCoreFactory getScannerCore() {
        return rules -> {
            Map<String, ConnectionConfiguration> connections = new HashMap<>();
            Map<String, Object> options = new HashMap<>();
            options.put("apiToken", apiToken);
            connections.put("do", new ConnectionConfiguration(
                "digitalocean",
                options
            ));

            List<Plugin> plugins = new ArrayList<>(Arrays.asList(
                new TestPlugin(
                    Arrays.asList(
                        new TestConfigurationLoader(
                            connections,
                            rules
                        )
                    )
                ),
                new DigitalOceanPlugin()
            ));
            return new ScannerCore(plugins);
        };
    }

    @Override
    public boolean isConfigured() {
        return apiToken != null;
    }
}
