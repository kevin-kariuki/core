package io.cscanner.core.rule.objectstorage;

import io.cscanner.core.engine.CScannerParameter;
import io.cscanner.core.engine.EmptyListSupplier;
import io.cscanner.core.engine.FalseSupplier;
import io.cscanner.core.engine.RuleResult;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class ObjectStoragePublicReadProhibitedRule implements ObjectStorageRule {
    public final static String RULE = "OS_PUBLIC_READ_PROHIBITED";
    public final static String LEGACY_RULE = "S3_PUBLIC_READ_PROHIBITED";
    private final boolean scanContents;
    private final List<Pattern> include;
    private final List<Pattern> exclude;

    public ObjectStoragePublicReadProhibitedRule(
        Options options
    ) {
        this.scanContents = options.scanContents;
        this.include = options.include;
        this.exclude = options.exclude;
    }

    @Override
    public List<RuleResult> evaluate(ObjectStorageConnection connection) {
        ObjectStorageClient client = connection.getObjectStorageClient();

        return client
            .listBuckets()
            .filter(bucket -> {
                if (include == null || include.isEmpty()) {
                    return true;
                } else {
                    for (Pattern includePattern : include) {
                        if (includePattern.matcher(bucket.bucketName).matches()) {
                            return true;
                        }
                    }
                }
                return false;
            })
            .filter(bucket -> {
                if (exclude != null && !exclude.isEmpty()) {
                    for (Pattern excludePattern : exclude) {
                        if (excludePattern.matcher(bucket.bucketName).matches()) {
                            return false;
                        }
                    }
                }
                return true;
            })
            .map(bucket -> {
                List<RuleResult.Violation> violations = new ArrayList<>();
                if (bucket.hasPublicReadAcl) {
                    violations.add(new RuleResult.Violation(null, "Bucket has a public-read ACL"));
                    return new RuleResult(
                        connection.getConnectionName(),
                        RESOURCE_TYPE,
                        bucket.region,
                        bucket.bucketName,
                        RuleResult.Compliancy.NONCOMPLIANT,
                        violations
                    );
                }
                if (bucket.blocksContentPublicReadAcl) {
                    return new RuleResult(
                        connection.getConnectionName(),
                        RESOURCE_TYPE,
                        bucket.region,
                        bucket.bucketName,
                        RuleResult.Compliancy.COMPLIANT,
                        violations
                    );
                }
                if (scanContents) {
                    long nonCompliantCount = client
                        .listObjects(bucket)
                        .filter(object -> object.hasPublicReadAcl)
                        .peek(object -> violations.add(new RuleResult.Violation(
                            object.key,
                            "Object has a public-read ACL"
                        )))
                        .count();
                    if (nonCompliantCount > 0) {
                        return new RuleResult(
                            connection.getConnectionName(),
                            RESOURCE_TYPE,
                            bucket.region,
                            bucket.bucketName,
                            RuleResult.Compliancy.NONCOMPLIANT,
                            violations
                        );
                    }
                }
                return new RuleResult(
                    connection.getConnectionName(),
                    RESOURCE_TYPE,
                    bucket.region,
                    bucket.bucketName,
                    RuleResult.Compliancy.COMPLIANT,
                    violations
                );
            })
            .collect(Collectors.toList());
    }

    public static class Options {
        @Nullable
        public final boolean scanContents;
        @Nullable
        public final List<Pattern> include;
        @Nullable
        public final List<Pattern> exclude;

        public Options(
            @CScannerParameter(
                value = "scanContents",
                description = "Can the contents of S3 buckets for ACL violations. Keep in mind that this can take a long time.",
                defaultSupplier = FalseSupplier.class
            )
                boolean scanContents,
            @CScannerParameter(
                value = "include",
                description = "A list of bucket regexps to include.",
                defaultSupplier = EmptyListSupplier.class
            )
                List<Pattern> include,
            @CScannerParameter(
                value = "exclude",
                description = "A list of bucket regexps to exclude. Exclude takes precedence over include.",
                defaultSupplier = EmptyListSupplier.class
            )
                List<Pattern> exclude
        ) {
            this.scanContents = scanContents;
            this.include = include;
            this.exclude = exclude;
        }
    }
}
