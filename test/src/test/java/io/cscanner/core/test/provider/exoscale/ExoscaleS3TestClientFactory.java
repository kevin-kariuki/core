package io.cscanner.core.test.provider.exoscale;

import com.amazonaws.services.s3.AmazonS3;
import io.cscanner.core.provider.exoscale.ExoscaleConfiguration;
import io.cscanner.core.provider.exoscale.ExoscalePlugin;
import io.cscanner.core.provider.exoscale.ExoscaleS3ObjectStorageFactory;
import io.cscanner.core.test.engine.ConnectionConfiguration;
import io.cscanner.core.test.engine.Plugin;
import io.cscanner.core.test.engine.ScannerCore;
import io.cscanner.core.test.engine.ScannerCoreFactory;
import io.cscanner.core.rule.objectstorage.ObjectStoragePlugin;
import io.cscanner.core.test.rule.objectstorage.ObjectStorageTestClientSupplier;
import io.cscanner.core.test.TestConfigurationLoader;
import io.cscanner.core.test.TestPlugin;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class ExoscaleS3TestClientFactory implements ObjectStorageTestClientSupplier {
    private final ExoscaleConfiguration exoscaleConfiguration;

    public ExoscaleS3TestClientFactory(ExoscaleConfiguration exoscaleConfiguration) {
        this.exoscaleConfiguration = exoscaleConfiguration;
    }


    @Override
    public boolean isConfigured() {
        return exoscaleConfiguration.key != null;
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
            plugins.add(new ObjectStoragePlugin());
            return new ScannerCore(plugins);
        };
    }
}
