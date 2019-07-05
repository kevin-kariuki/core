package io.cscanner.core.provider.aws;

import io.cscanner.core.test.TestConfigurationLoader;
import io.cscanner.core.test.TestPlugin;
import io.cscanner.core.engine.*;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class AWSHostDiscoveryTestClientFactory implements HostDiscoveryTestClientFactory {
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
    public HostDiscoveryTestClient get() {
        if (apiKey != null && apiSecret != null) {
            return new AWSHostDiscoveryTestClient(
                apiKey,
                apiSecret
            );
        } else {
            return null;
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
            return new ScannerCore(plugins);
        };
    }

    @Override
    public boolean isConfigured() {
        return apiKey != null && apiSecret != null;
    }
}
