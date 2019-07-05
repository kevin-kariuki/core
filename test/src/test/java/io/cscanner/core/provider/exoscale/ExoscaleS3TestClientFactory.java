package io.cscanner.core.provider.exoscale;

import com.amazonaws.services.s3.AmazonS3;
import io.cscanner.core.engine.ConnectionConfiguration;
import io.cscanner.core.engine.Plugin;
import io.cscanner.core.engine.ScannerCore;
import io.cscanner.core.engine.ScannerCoreFactory;
import io.cscanner.core.rule.objectstorage.ObjectStoragePlugin;
import io.cscanner.core.rule.objectstorage.ObjectStorageTestClientSupplier;
import io.cscanner.core.test.TestConfigurationLoader;
import io.cscanner.core.test.TestPlugin;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class ExoscaleS3TestClientFactory implements ObjectStorageTestClientSupplier {
    @Nullable
    private static final String apiKey;
    @Nullable
    private static final String apiSecret;
    private static final ExoscaleConfiguration exoscaleConfiguration;

    static {
        apiKey = System.getenv("EXOSCALE_KEY");
        apiSecret = System.getenv("EXOSCALE_SECRET");
        if (apiKey != null && apiSecret != null) {
            exoscaleConfiguration =
                new ExoscaleConfiguration(
                    apiKey,
                    apiSecret,
                    null,
                    null
            );
        } else {
            exoscaleConfiguration = null;
        }
    }

    @Override
    public boolean isConfigured() {
        return apiKey != null && apiSecret != null;
    }

    @Override
    public String getDefaultZone() {
        return "at-vie-1";
    }

    @Override
    public AmazonS3 get(@Nullable String region) {
        return new ExoscaleS3ObjectStorageFactory(exoscaleConfiguration).get(region);
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
            plugins.add(new ObjectStoragePlugin());
            return new ScannerCore(plugins);
        };
    }
}
