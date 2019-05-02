package com.opsbears.cscanner.tags;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketTaggingConfiguration;
import com.amazonaws.services.s3.model.TagSet;
import com.opsbears.cscanner.objectstorage.S3Factory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class S3TaggedResourceClient implements TaggedResourceClient {
    private final String connectionName;
    private final S3Factory s3Factory;

    public S3TaggedResourceClient(
        String connectionName,
        S3Factory s3Factory
    ) {
        this.connectionName = connectionName;
        this.s3Factory = s3Factory;
    }

    @Override
    public Stream<TaggedResource> getTaggedResources() {
        List<Bucket> buckets = s3Factory.get(null).listBuckets();
        return buckets
            .stream()
            .map(bucket -> {
                String region = s3Factory.get(null).getBucketLocation(bucket.getName());
                AmazonS3 s3Client = s3Factory.get(region);

                return new BucketWithTags(
                    region,
                    bucket,
                    s3Client.getBucketTaggingConfiguration(bucket.getName())
                );
            })
            .filter(bucketWithTags -> bucketWithTags.bucketTaggingConfiguration != null)
            .map(bucketWithTags -> {
                Bucket bucket = bucketWithTags.bucket;
                String region = bucketWithTags.region;
                BucketTaggingConfiguration taggingConfiguration = bucketWithTags.bucketTaggingConfiguration;
                Map<String, String> tags = new HashMap<>();
                taggingConfiguration.getAllTagSets().stream()
                    .map(TagSet::getAllTags)
                    .forEach(
                        map -> map.keySet().forEach(
                            tag -> tags.put(tag, map.get(tag))
                        )
                    );

                return new TaggedResource(
                    connectionName,
                    TaggedResourceType.BUCKET,
                    region,
                    bucket.getName(),
                    tags
                );
            });
    }

    static class BucketWithTags {
        final String region;
        final Bucket bucket;
        @Nullable
        final BucketTaggingConfiguration bucketTaggingConfiguration;

        BucketWithTags(
            String region,
            Bucket bucket,
            @Nullable BucketTaggingConfiguration bucketTaggingConfiguration
        ) {
            this.region = region;
            this.bucket = bucket;
            this.bucketTaggingConfiguration = bucketTaggingConfiguration;
        }
    }
}
