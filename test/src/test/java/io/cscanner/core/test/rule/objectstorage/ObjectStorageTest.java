package io.cscanner.core.test.rule.objectstorage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.SetBucketAclRequest;
<<<<<<< HEAD:test/src/test/java/io/cscanner/core/rule/objectstorage/ObjectStorageTest.java
import io.cscanner.core.rule.objectstorage.ObjectStoragePublicReadProhibitedRule;
import io.cscanner.core.rule.objectstorage.ObjectStorageRule;
import io.cscanner.core.test.provider.aws.AWSS3TestClientFactory;
import io.cscanner.core.test.engine.RuleConfiguration;
import io.cscanner.core.test.engine.RuleResult;
import io.cscanner.core.test.engine.ScannerCore;
import io.cscanner.core.test.engine.ScannerCoreFactory;
import io.cscanner.core.test.provider.digitalocean.DigitalOceanS3TestClientFactory;
import io.cscanner.core.test.provider.exoscale.ExoscaleS3TestClientFactory;
=======
import io.cscanner.core.test.IntegrationTest;
import com.opsbears.cscanner.aws.AWSS3TestClientFactory;
import com.opsbears.cscanner.core.RuleConfiguration;
import com.opsbears.cscanner.core.RuleResult;
import com.opsbears.cscanner.core.ScannerCore;
import com.opsbears.cscanner.core.ScannerCoreFactory;
import com.opsbears.cscanner.digitalocean.DigitalOceanS3TestClientFactory;
import com.opsbears.cscanner.exoscale.ExoscaleS3TestClientFactory;
>>>>>>> a57a28e509d66f16a62523fb1b21fabe8753ca00:test/src/test/java/com/opsbears/cscanner/objectstorage/ObjectStorageTest.java
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

@ParametersAreNonnullByDefault
public class ObjectStorageTest extends IntegrationTest {
    @SuppressWarnings("unchecked")
    private static List<Class<ObjectStorageTestClientSupplier>> factories = Arrays.<Class<ObjectStorageTestClientSupplier>>asList(
            new Class[]{ExoscaleS3TestClientFactory.class, AWSS3TestClientFactory.class, DigitalOceanS3TestClientFactory.class}
    );

    @DataProvider(name = "dataProvider")
    public Object[][] dataProvider() {
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
            String bucketName,
            ScannerCoreFactory scannerCoreFactory,
            boolean scanContents,
            RuleResult.Compliancy expectedResult
    ) {
        //Setup
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
    }

    @Test(dataProvider = "dataProvider")
    public void testNonCompliantBucketAcl(
            String resourcePrefix,
            ScannerCoreFactory scannerCoreFactory
    ) {
        assertNonCompliantFilePublicAcl(
                resourcePrefix + "-noncompliant-bucket",
                scannerCoreFactory,
                false,
                RuleResult.Compliancy.NONCOMPLIANT
        );
    }


    @Test(dataProvider = "dataProvider")
    public void testNonCompliantFilePublicAclNoScanContents(
            String resourcePrefix,
            ScannerCoreFactory scannerCoreFactory
    ) {
        assertNonCompliantFilePublicAcl(
                resourcePrefix + "noncompliant-file",
                scannerCoreFactory,
                false,
                RuleResult.Compliancy.COMPLIANT
        );
    }

    @Test(dataProvider = "dataProvider")
    public void testNonCompliantFilePublicAclScanContents(
            String resourcePrefix,
            ScannerCoreFactory scannerCoreFactory
    ) {
        assertNonCompliantFilePublicAcl(
                resourcePrefix + "noncompliant-file",
                scannerCoreFactory,
                true,
                RuleResult.Compliancy.NONCOMPLIANT
        );
    }
}
