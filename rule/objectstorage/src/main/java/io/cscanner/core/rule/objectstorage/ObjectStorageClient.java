package io.cscanner.core.rule.objectstorage;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public interface ObjectStorageClient {
    Stream<Bucket> listBuckets();
    Stream<Object> listObjects(Bucket bucket);

    class Bucket {
        public final String bucketName;
        public final String region;
        public final boolean blocksContentPublicReadAcl;
        public final boolean hasPublicReadAcl;

        public Bucket(
            String bucketName,
            String region,
            boolean blocksContentPublicReadAcl,
            boolean hasPublicReadAcl
        ) {
            this.bucketName = bucketName;
            this.region = region;
            this.blocksContentPublicReadAcl = blocksContentPublicReadAcl;
            this.hasPublicReadAcl = hasPublicReadAcl;
        }
    }

    class Object {
        public final String bucketName;
        public final String key;
        public final boolean hasPublicReadAcl;

        public Object(
            String bucketName,
            String key,
            boolean hasPublicReadAcl
        ) {
            this.bucketName = bucketName;
            this.key = key;
            this.hasPublicReadAcl = hasPublicReadAcl;
        }
    }
}
