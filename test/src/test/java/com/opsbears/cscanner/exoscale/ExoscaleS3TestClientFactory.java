package com.opsbears.cscanner.exoscale;

import com.amazonaws.services.s3.AmazonS3;
import com.opsbears.cscanner.core.ConnectionConfiguration;
import com.opsbears.cscanner.core.Plugin;
import com.opsbears.cscanner.core.ScannerCore;
import com.opsbears.cscanner.core.ScannerCoreFactory;
import com.opsbears.cscanner.objectstorage.ObjectStoragePlugin;
import com.opsbears.cscanner.objectstorage.ObjectStorageTestClientSupplier;
import com.opsbears.cscanner.test.TestConfigurationLoader;
import com.opsbears.cscanner.test.TestPlugin;

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
