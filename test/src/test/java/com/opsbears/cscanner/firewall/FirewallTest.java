package com.opsbears.cscanner.firewall;

import com.opsbears.cscanner.IntegrationTest;
import com.opsbears.cscanner.aws.AWSTestFirewallClientFactory;
import com.opsbears.cscanner.core.RuleConfiguration;
import com.opsbears.cscanner.core.RuleResult;
import com.opsbears.cscanner.core.ScannerCore;
import com.opsbears.cscanner.core.ScannerCoreFactory;
import com.opsbears.cscanner.digitalocean.DigitalOceanTestFirewallClientFactory;
import com.opsbears.cscanner.exoscale.ExoscaleTestFirewallClientFactory;
import com.opsbears.cscanner.objectstorage.ObjectStorageTestClientSupplier;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This test suite tests the Exoscale firewall behavior. It requires a live Exoscale connection.
 */
@ParametersAreNonnullByDefault
public class FirewallTest extends IntegrationTest {
    @SuppressWarnings("unchecked")
    private static List<Class<TestFirewallClientFactory>> factories = Arrays.<Class<ObjectStorageTestClientSupplier>>asList(
        new Class[]{
            AWSTestFirewallClientFactory.class,
            DigitalOceanTestFirewallClientFactory.class,
            ExoscaleTestFirewallClientFactory.class,
        }
    );


    @DataProvider(name = "dataProvider")
    public Object[][] dataProvider() {
        String resourcePrefix;
        if (System.getenv("TEST_RESOURCE_PREFIX") == null || System.getenv("TEST_RESOURCE_PREFIX").equals("")) {
            resourcePrefix = "test-" + UUID.randomUUID().toString() + "-";
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
                factory -> factory.get() != null
            )
            .map(
                factory -> new Object[]{
                    resourcePrefix,
                    factory.get(),
                    factory.getScannerCore()
                }
            ).collect(Collectors.toList());

        return params.toArray(new Object[][]{});
    }


    @Test(dataProvider = "dataProvider")
    public void testCompliantSecurityGroup(
        String resourcePrefix,
        TestFirewallClient testClient,
        ScannerCoreFactory scannerCoreFactory
    ) {
        //Setup
        String sgName = resourcePrefix + "compliant";
        List<RuleConfiguration> rules = new ArrayList<>();
        Map<String, Object> options = new HashMap<>();
        options.put("protocol", "tcp");
        options.put("ports", Arrays.asList(22));
        rules.add(new RuleConfiguration(
            FirewallPublicServiceProhibitedRule.RULE,
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
            .filter(result -> result.resourceName.equalsIgnoreCase(sgName))
            .filter(result -> result.resourceType.equalsIgnoreCase(FirewallConnection.RESOURCE_TYPE))
            .collect(Collectors.toList());

        Assert.assertEquals(1, filteredResults.size());
        Assert.assertEquals(RuleResult.Compliancy.COMPLIANT, filteredResults.get(0).compliancy);
    }

    @Test(dataProvider = "dataProvider")
    public void testNonCompliantSecurityGroup(
        String resourcePrefix,
        TestFirewallClient testClient,
        ScannerCoreFactory scannerCoreFactory
    ) {
        //Setup
        String sgName = resourcePrefix + "noncompliant";
        List<RuleConfiguration> rules = new ArrayList<>();
        Map<String, Object> options = new HashMap<>();
        options.put("protocol", "tcp");
        options.put("ports", Arrays.asList(22));
        rules.add(new RuleConfiguration(
            FirewallPublicServiceProhibitedRule.RULE,
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
            .filter(result -> result.resourceName.equalsIgnoreCase(sgName))
            .filter(result -> result.resourceType.equalsIgnoreCase(FirewallConnection.RESOURCE_TYPE))
            .collect(Collectors.toList());

        Assert.assertEquals(1, filteredResults.size());
        Assert.assertEquals(RuleResult.Compliancy.NONCOMPLIANT, filteredResults.get(0).compliancy);
    }

    @Test(dataProvider = "dataProvider")
    public void testProtocolAll(
        String resourcePrefix,
        TestFirewallClient testClient,
        ScannerCoreFactory scannerCoreFactory
    ) {
        //Setup
        String sgName = resourcePrefix + "protocol-all";
        List<RuleConfiguration> rules = new ArrayList<>();
        Map<String, Object> options = new HashMap<>();
        options.put("protocol", "tcp");
        options.put("ports", Arrays.asList(22));
        rules.add(new RuleConfiguration(
            FirewallPublicServiceProhibitedRule.RULE,
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
            .filter(result -> result.resourceName.equalsIgnoreCase(sgName))
            .filter(result -> result.resourceType.equalsIgnoreCase(FirewallConnection.RESOURCE_TYPE))
            .collect(Collectors.toList());

        Assert.assertEquals(1, filteredResults.size());
        Assert.assertEquals(RuleResult.Compliancy.NONCOMPLIANT, filteredResults.get(0).compliancy);
    }
}
