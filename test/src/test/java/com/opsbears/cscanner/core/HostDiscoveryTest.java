package com.opsbears.cscanner.core;

import com.opsbears.cscanner.aws.AWSHostDiscoveryTestClientFactory;
import com.opsbears.cscanner.digitalocean.DigitalOceanHostDiscoveryTestClientFactory;
import com.opsbears.cscanner.exoscale.ExoscaleHostDiscoveryTestClientFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

import static org.testng.Assert.assertTrue;

@ParametersAreNonnullByDefault
public class HostDiscoveryTest {

    @SuppressWarnings("unchecked")
    private static List<Class<HostDiscoveryTestClientFactory>> factories = Arrays.asList(
        new Class[]{
            AWSHostDiscoveryTestClientFactory.class,
            DigitalOceanHostDiscoveryTestClientFactory.class,
            ExoscaleHostDiscoveryTestClientFactory.class,
        }
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
                HostDiscoveryTestClientFactory::isConfigured
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
    public void testHostIp(String resourcePrefix, HostDiscoveryTestClientFactory factory, ScannerCoreFactory scannerCoreFactory) {
        //Setup
        HostDiscoveryTestClient testClient = factory.get();
        HostDiscoveryTestClient.Host host = testClient.createPublicHost(resourcePrefix + "ip");
        ScannerCore scannerCore = scannerCoreFactory.create(new ArrayList<>());
        Optional<String> result;

        try {
            //Execute
            result = scannerCore
                .listIps()
                .map(ip -> ip.ipAddress.toString())
                .filter(ip -> ip.equalsIgnoreCase(host.ipAddress.toString()))
                .findAny();

            //Assert
            assertTrue(result.isPresent());

        } finally {
            //Cleanup
            testClient.destroyPublicHost(host);
        }
    }
}
