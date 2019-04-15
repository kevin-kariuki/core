package com.opsbears.cscanner.digitalocean;

import com.opsbears.cscanner.core.*;
import com.opsbears.cscanner.exoscale.ExoscaleHostDiscoveryTestClient;
import com.opsbears.cscanner.exoscale.ExoscalePlugin;
import com.opsbears.cscanner.firewall.FirewallPlugin;
import com.opsbears.cscanner.test.TestConfigurationLoader;
import com.opsbears.cscanner.test.TestPlugin;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class DigitalOceanHostDiscoveryTestClientFactory implements HostDiscoveryTestClientFactory {
    @Nullable
    private static final String apiToken;

    static {
        apiToken = System.getenv("DIGITALOCEAN_TOKEN");
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

    @Override
    public HostDiscoveryTestClient get() {
        return new DigitalOceanHostDiscoveryTestClient(apiToken);
    }
}
