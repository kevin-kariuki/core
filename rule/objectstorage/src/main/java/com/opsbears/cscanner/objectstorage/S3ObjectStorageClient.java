package com.opsbears.cscanner.objectstorage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@ParametersAreNonnullByDefault
public class S3ObjectStorageClient implements ObjectStorageClient {
    private final S3Factory s3Factory;
    private final ExecutorService executorService =
        new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    public S3ObjectStorageClient(S3Factory s3Factory) {
        this.s3Factory = s3Factory;
    }

    private boolean hasPublicAccess(List<Grant> grants) {
        for (Grant grant : grants) {
            if (
                (
                    grant.getPermission().equals(Permission.Read) ||
                        grant.getPermission().equals(Permission.FullControl)
                ) &&
                    grant
                        .getGrantee()
                        .getIdentifier()
                        .equalsIgnoreCase("http://acs.amazonaws.com/groups/global/AllUsers")
            ) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Stream<Bucket> listBuckets() {
        List<com.amazonaws.services.s3.model.Bucket> buckets = s3Factory.get(null).listBuckets();

        return buckets.stream().map(bucket -> {
            AmazonS3 s3Client = s3Factory.get(s3Factory.get(null).getBucketLocation(bucket.getName()));
            List<Grant> grants = s3Client
                .getBucketAcl(
                    new GetBucketAclRequest(
                        bucket.getName()
                    )
                )
                .getGrantsAsList();
            Boolean blockPublicAccess;
            try {
                blockPublicAccess = s3Client
                    .getPublicAccessBlock(new GetPublicAccessBlockRequest().withBucketName(bucket.getName()))
                    .getPublicAccessBlockConfiguration()
                    .getBlockPublicAcls();
                if (blockPublicAccess == null) {
                    blockPublicAccess = false;
                }
            } catch (AmazonS3Exception e) {
                if (e.getErrorCode().equalsIgnoreCase("NoSuchPublicAccessBlockConfiguration")) {
                    blockPublicAccess = false;
                } else {
                    throw e;
                }
            }
            return new Bucket(
                bucket.getName(),
                s3Client.getBucketLocation(bucket.getName()),
                blockPublicAccess,
                hasPublicAccess(grants)
            );
        });
    }

    @Override
    public Stream<Object> listObjects(Bucket bucket) {
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(new ObjectIterator(
                bucket,
                s3Factory
            ), Spliterator.ORDERED),
            false);
    }

    private class ObjectIterator implements Iterator<Object> {
        private final Bucket bucket;
        private final S3Factory s3Factory;
        private final AmazonS3 s3Client;
        private final Queue<Object> queue = new LinkedBlockingQueue<>();
        private String lastToken = null;
        private boolean finished = false;

        ObjectIterator(
            Bucket bucket,
            S3Factory s3Factory
        ) {
            this.bucket = bucket;
            this.s3Factory = s3Factory;
            this.s3Client = s3Factory.get(bucket.region);
        }

        private void refillQueue() {
            ListObjectsV2Request req = new ListObjectsV2Request()
                .withBucketName(bucket.bucketName)
                ;
            if (lastToken != null) {
                req = req.withContinuationToken(lastToken);
            }
            ListObjectsV2Result result;

            result = s3Client.listObjectsV2(req);

            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                AccessControlList acls = s3Client.getObjectAcl(
                    bucket.bucketName,
                    objectSummary.getKey()
                );
                queue.offer(new Object(
                    bucket.bucketName,
                    objectSummary.getKey(),
                    hasPublicAccess(acls.getGrantsAsList())
                ));
            }
            // If there are more than maxKeys keys in the bucket, get a continuation token
            // and list the next objects.
            lastToken = result.getNextContinuationToken();
            finished = !result.isTruncated();
        }

        @Override
        public boolean hasNext() {
            if (finished) {
                return false;
            }
            if (queue.isEmpty()) {
                refillQueue();
            }
            if (!queue.isEmpty()) {
                finished = true;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Object next() {
            if (finished && queue.isEmpty()) {
                throw new IllegalStateException();
            }
            if (queue.isEmpty()) {
                refillQueue();
            }
            if (!queue.isEmpty()) {
                finished = true;
                return queue.poll();
            } else {
                throw new IllegalStateException();
            }
        }
    }
}
