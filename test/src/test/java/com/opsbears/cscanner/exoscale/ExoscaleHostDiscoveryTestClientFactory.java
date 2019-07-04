package com.opsbears.cscanner.exoscale;

import com.opsbears.cscanner.core.*;
import com.opsbears.cscanner.test.TestConfigurationLoader;
import com.opsbears.cscanner.test.TestPlugin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class ExoscaleHostDiscoveryTestClientFactory implements HostDiscoveryTestClientFactory {
    private final ExoscaleConfiguration exoscaleConfiguration;

    public ExoscaleHostDiscoveryTestClientFactory(ExoscaleConfiguration exoscaleConfiguration) {
        this.exoscaleConfiguration = exoscaleConfiguration;
    }

    @Override
    public ScannerCoreFactory getScannerCore() {
        return rules -> {
            Map<String, ConnectionConfiguration> connections = new HashMap<>();
            Map<String, Object> options = new HashMap<>();
            options.put("key", exoscaleConfiguration.key);
            options.put("secret", exoscaleConfiguration.secret);
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
        return exoscaleConfiguration.key != null;
    }
}
