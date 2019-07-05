package io.cscanner.core.provider.exoscale;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.cscanner.core.rule.objectstorage.S3Factory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExoscaleS3ObjectStorageFactory implements S3Factory {
    private final ExoscaleConfiguration configuration;

    public ExoscaleS3ObjectStorageFactory(ExoscaleConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public AmazonS3 get(@Nullable String region) {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
        if (configuration.key != null && configuration.secret != null) {
            builder.withCredentials(new AWSCredentialsProvider() {
                @Override
                public AWSCredentials getCredentials() {
                    return new AWSCredentials() {
                        @Override
                        public String getAWSAccessKeyId() {
                            return configuration.key;
                        }

                        @Override
                        public String getAWSSecretKey() {
                            return configuration.secret;
                        }
                    };
                }

                @Override
                public void refresh() {}
            });
        } else {
            //todo read CloudStack configuration
        }

        if (region != null) {
            builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                "https://sos-" + region + ".exo.io",
                region
            ));

        } else {
            builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                "https://sos-ch-dk-2.exo.io",
                "ch-dk-2"
            ));
        }

        return builder.build();
    }
}
