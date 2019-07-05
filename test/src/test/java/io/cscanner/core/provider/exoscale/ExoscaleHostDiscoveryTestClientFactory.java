package io.cscanner.core.provider.exoscale;

import io.cscanner.core.engine.*;
import io.cscanner.core.test.TestConfigurationLoader;
import io.cscanner.core.test.TestPlugin;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class ExoscaleHostDiscoveryTestClientFactory implements HostDiscoveryTestClientFactory {
    @Nullable
    private static final String apiKey;
    @Nullable
    private static final String apiSecret;

    static {
        apiKey = System.getenv("EXOSCALE_KEY");
        apiSecret = System.getenv("EXOSCALE_SECRET");
    }

    @Override
    public ScannerCoreFactory getScannerCore() {
        return rules -> {
            Map<String, ConnectionConfiguration> connections = new HashMap<>();
            Map<String, Object> options = new HashMap<>();
            options.put("key", apiKey);
            options.put("secret", apiSecret);
            connections.put("exo", new ConnectionConfiguration(
                "exoscale",
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
                new ExoscalePlugin()
            ));
            return new ScannerCore(plugins);
        };
    }

    @Override
    public boolean isConfigured() {
        return apiKey != null && apiSecret != null;
    }

    @Override
    public HostDiscoveryTestClient get() {
        return new ExoscaleHostDiscoveryTestClient(apiKey, apiSecret);
    }
}
