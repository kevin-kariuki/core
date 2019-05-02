package com.opsbears.cscanner.digitalocean;

import com.opsbears.cscanner.tags.S3TaggedResourceClient;
import com.opsbears.cscanner.tags.TaggedResource;
import com.opsbears.cscanner.tags.TaggedResourceClient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class DigitalOceanTaggedResourceClient implements TaggedResourceClient {
    private final String connectionName;
    private final DigitalOceanS3ClientSupplier digitalOceanS3ClientSupplier;

    public DigitalOceanTaggedResourceClient(
        String connectionName,
        DigitalOceanS3ClientSupplier digitalOceanS3ClientSupplier
    ) {

        this.connectionName = connectionName;
        this.digitalOceanS3ClientSupplier = digitalOceanS3ClientSupplier;
    }

    @Override
    public Stream<TaggedResource> getTaggedResources() {
        return getBuckets();
    }

    private Stream<TaggedResource> getBuckets() {
        return new S3TaggedResourceClient(
            connectionName,
            digitalOceanS3ClientSupplier
        ).getTaggedResources();
    }
}
