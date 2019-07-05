package io.cscanner.core.provider.digitalocean;

import io.cscanner.core.engine.ConnectionConfiguration;
import io.cscanner.core.engine.Plugin;
import io.cscanner.core.engine.ScannerCore;
import io.cscanner.core.engine.ScannerCoreFactory;
import io.cscanner.core.rule.firewall.FirewallPlugin;
import io.cscanner.core.rule.firewall.TestFirewallClient;
import io.cscanner.core.rule.firewall.TestFirewallClientFactory;
import io.cscanner.core.test.TestConfigurationLoader;
import io.cscanner.core.test.TestPlugin;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class DigitalOceanTestFirewallClientFactory implements TestFirewallClientFactory {
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
            plugins.add(new FirewallPlugin());
            return new ScannerCore(plugins);
        };
    }

    @Override
    public TestFirewallClient get() {
        if (apiToken == null) {
            return null;
        }
        return new DigitalOceanTestFirewallClient(
            apiToken
        );
    }
}
