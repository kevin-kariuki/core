package io.cscanner.core.rule.objectstorage;

import com.amazonaws.services.s3.AmazonS3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface S3Factory {
    AmazonS3 get(@Nullable String region);
}
