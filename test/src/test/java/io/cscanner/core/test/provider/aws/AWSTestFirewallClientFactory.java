package io.cscanner.core.test.provider.aws;

import io.cscanner.core.provider.aws.AWSConfiguration;
import io.cscanner.core.provider.aws.AWSPlugin;
import io.cscanner.core.test.engine.ConnectionConfiguration;
import io.cscanner.core.test.engine.Plugin;
import io.cscanner.core.test.engine.ScannerCore;
import io.cscanner.core.test.engine.ScannerCoreFactory;
import io.cscanner.core.rule.firewall.FirewallPlugin;
import io.cscanner.core.test.rule.firewall.TestFirewallClient;
import io.cscanner.core.test.rule.firewall.TestFirewallClientFactory;
import io.cscanner.core.test.TestConfigurationLoader;
import io.cscanner.core.test.TestPlugin;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class AWSTestFirewallClientFactory implements TestFirewallClientFactory {
    @Nullable
    private static final String apiKey;
    @Nullable
    private static final String apiSecret;
    @Nullable
    private static final AWSConfiguration awsConfiguration;

    static {
        apiKey = System.getenv("AWS_ACCESS_KEY_ID");
        apiSecret = System.getenv("AWS_SECRET_ACCESS_KEY");
        if (apiKey != null && apiSecret != null) {
            awsConfiguration =
                new AWSConfiguration(
                    apiKey,
                    apiSecret,
                    null,
                    null
                );
        } else {
            awsConfiguration = null;
        }
    }

    @Override
    public ScannerCoreFactory getScannerCore() {
        return rules -> {
            Map<String, ConnectionConfiguration> connections = new HashMap<>();
            Map<String, Object> options = new HashMap<>();
            options.put("accessKeyId", apiKey);
            options.put("secretAccessKey", apiSecret);
            connections.put("aws", new ConnectionConfiguration(
                "aws",
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
                new AWSPlugin()
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
        return new AWSTestFirewallClient(
            apiKey,
            apiSecret
        );
    }
}
