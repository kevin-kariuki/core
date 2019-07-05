package io.cscanner.core.test.provider.aws;

import com.amazonaws.services.s3.AmazonS3;
import io.cscanner.core.provider.aws.AWSConfiguration;
import io.cscanner.core.provider.aws.AWSPlugin;
import io.cscanner.core.provider.aws.AWSS3ClientSupplier;
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
public class AWSS3TestClientFactory implements ObjectStorageTestClientSupplier {
    @Nullable
    private static final String apiKey;
    @Nullable
    private static final String apiSecret;
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
    public boolean isConfigured() {
        return apiKey != null && apiSecret != null;
    }

    @Override
    public String getDefaultZone() {
        return "us-east-1";
    }

    @Override
    public AmazonS3 get(@Nullable String region) {
        return new AWSS3ClientSupplier(awsConfiguration).get(region);
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
            plugins.add(new ObjectStoragePlugin());
            return new ScannerCore(plugins);
        };
    }
}
