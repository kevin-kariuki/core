package io.cscanner.core.rule.objectstorage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.SetBucketAclRequest;
import io.cscanner.core.provider.aws.AWSS3TestClientFactory;
import io.cscanner.core.engine.RuleConfiguration;
import io.cscanner.core.engine.RuleResult;
import io.cscanner.core.engine.ScannerCore;
import io.cscanner.core.engine.ScannerCoreFactory;
import io.cscanner.core.provider.digitalocean.DigitalOceanS3TestClientFactory;
import io.cscanner.core.provider.exoscale.ExoscaleS3TestClientFactory;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

@ParametersAreNonnullByDefault
public class ObjectStorageTest {
    @SuppressWarnings("unchecked")
    private static List<Class<ObjectStorageTestClientSupplier>> factories = Arrays.<Class<ObjectStorageTestClientSupplier>>asList(
        new Class[]{ExoscaleS3TestClientFactory.class, AWSS3TestClientFactory.class, DigitalOceanS3TestClientFactory.class}
    );

    @DataProvider(name = "dataProvider")
    public static Object[][] dataProvider() {
        String resourcePrefix;
        if (System.getenv("TEST_RESOURCE_PREFIX") == null || System.getenv("TEST_RESOURCE_PREFIX").equals("")) {
            resourcePrefix = "test-" + UUID.randomUUID().toString();
        } else {
            resourcePrefix = System.getenv("TEST_RESOURCE_PREFIX");
        }

        List<Object[]> params = factories.stream()
            .map(
                factory -> {
                    try {
                        return factory.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        return null;
                    }
                }
            )
            .filter(Objects::nonNull)
            .filter(
                ObjectStorageTestClientSupplier::isConfigured
            )
            .map(
                factory -> new Object[]{
                    resourcePrefix,
                    factory,
                    factory.getScannerCore()
                }
            ).collect(Collectors.toList());

        return params.toArray(new Object[][]{});
    }

    @Test(dataProvider = "dataProvider")
    public void testCompliantBucket(
        String resourcePrefix,
        ObjectStorageTestClientSupplier testClientSupplier,
        ScannerCoreFactory scannerCoreFactory
    ) {
        //Setup
        String bucketName = resourcePrefix + "compliant-bucket";
        AmazonS3 client = testClientSupplier.get(testClientSupplier.getDefaultZone());
        client.createBucket(bucketName);
        try {
            client.setBucketAcl(new SetBucketAclRequest(
                bucketName,
                CannedAccessControlList.Private
            ));
            List<RuleConfiguration> rules = new ArrayList<>();
            Map<String, Object> options = new HashMap<>();
            rules.add(new RuleConfiguration(
                ObjectStoragePublicReadProhibitedRule.RULE,
                new ArrayList<>(),
                options
            ));

            ScannerCore scannerCore = scannerCoreFactory.create(
                rules
            );

            //Execute
            List<RuleResult> results = scannerCore.scan();

            //Assert
            List<RuleResult> filteredResults = results
                .stream()
                .filter(result -> result.resourceName.equalsIgnoreCase(bucketName))
                .filter(result -> result.resourceType.equalsIgnoreCase(ObjectStorageRule.RESOURCE_TYPE))
                .collect(Collectors.toList());

            assertEquals(1, filteredResults.size());
            assertEquals(RuleResult.Compliancy.COMPLIANT, filteredResults.get(0).compliancy);
        } catch (Exception e) {
            LoggerFactory.getLogger(this.getClass()).warn(e.getMessage(), e);
            throw e;
        } finally {
            //Cleanup
            client.deleteBucket(bucketName);
        }
    }

    private void assertNonCompliantFilePublicAcl(
        String resourcePrefix,
        ObjectStorageTestClientSupplier testClientSupplier,
        ScannerCoreFactory scannerCoreFactory,
        CannedAccessControlList bucketAcl,
        CannedAccessControlList fileAcl,
        boolean scanContents,
        RuleResult.Compliancy expectedResult
    ) {
        //Setup
        String bucketName = resourcePrefix + "-bucket";
        AmazonS3 client = testClientSupplier.get(testClientSupplier.getDefaultZone());
        client.createBucket(bucketName);
        try {
            client.setBucketAcl(bucketName, bucketAcl);
            byte[] data = "Hello world!".getBytes();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(data.length);
            metadata.setContentType("application/octet-stream");
            client.putObject(
                bucketName,
                "test.txt",
                new ByteArrayInputStream(data),
                metadata
            );
            client.setObjectAcl(bucketName, "test.txt", fileAcl);
            List<RuleConfiguration> rules = new ArrayList<>();
            Map<String, Object> options = new HashMap<>();
            options.put("scanContents", scanContents);
            rules.add(new RuleConfiguration(
                ObjectStoragePublicReadProhibitedRule.RULE,
                new ArrayList<>(),
                options
            ));

            ScannerCore scannerCore = scannerCoreFactory.create(
                rules
            );

            //Execute
            List<RuleResult> results = scannerCore.scan();

            //Assert
            List<RuleResult> filteredResults = results
                .stream()
                .filter(result -> result.resourceName.equalsIgnoreCase(bucketName))
                .filter(result -> result.resourceType.equalsIgnoreCase(ObjectStorageRule.RESOURCE_TYPE))
                .collect(Collectors.toList());

            assertEquals(1, filteredResults.size());
            assertEquals(
                expectedResult,
                filteredResults.get(0).compliancy
            );
        } catch (Exception e) {
            LoggerFactory.getLogger(this.getClass()).warn(e.getMessage(), e);
            throw e;
        } finally {
            //Cleanup
            client.deleteObject(new DeleteObjectRequest(bucketName, "test.txt"));
            client.deleteBucket(bucketName);
        }
    }

    @Test(dataProvider = "dataProvider")
    public void testNonCompliantBucketAcl(
        String resourcePrefix,
        ObjectStorageTestClientSupplier testClientSupplier,
        ScannerCoreFactory scannerCoreFactory
    ) {
        assertNonCompliantFilePublicAcl(
            resourcePrefix,
            testClientSupplier,
            scannerCoreFactory,
            CannedAccessControlList.PublicRead,
            CannedAccessControlList.Private,
            false,
            RuleResult.Compliancy.NONCOMPLIANT
        );
    }


    @Test(dataProvider = "dataProvider")
    public void testNonCompliantFilePublicAclNoScanContents(
        String resourcePrefix,
        ObjectStorageTestClientSupplier testClientSupplier,
        ScannerCoreFactory scannerCoreFactory
    ) {
        assertNonCompliantFilePublicAcl(
            resourcePrefix,
            testClientSupplier,
            scannerCoreFactory,
            CannedAccessControlList.Private,
            CannedAccessControlList.PublicRead,
            false,
            RuleResult.Compliancy.COMPLIANT
        );
    }

    @Test(dataProvider = "dataProvider")
    public void testNonCompliantFilePublicAclScanContents(
        String resourcePrefix,
        ObjectStorageTestClientSupplier testClientSupplier,
        ScannerCoreFactory scannerCoreFactory
    ) {
        assertNonCompliantFilePublicAcl(
            resourcePrefix,
            testClientSupplier,
            scannerCoreFactory,
            CannedAccessControlList.Private,
            CannedAccessControlList.PublicRead,
            true,
            RuleResult.Compliancy.NONCOMPLIANT
        );
    }
}
