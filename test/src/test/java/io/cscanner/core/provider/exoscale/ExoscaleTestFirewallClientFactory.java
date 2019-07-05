package io.cscanner.core.provider.exoscale;

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
public class ExoscaleTestFirewallClientFactory implements TestFirewallClientFactory {
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
            plugins.add(new FirewallPlugin());
            return new ScannerCore(plugins);
        };
    }

    @Override
    public TestFirewallClient get() {
        if (apiKey == null || apiSecret == null) {
            return null;
        }
        return new ExoscaleTestFirewallClient(
            apiKey,
            apiSecret
        );
    }
}
