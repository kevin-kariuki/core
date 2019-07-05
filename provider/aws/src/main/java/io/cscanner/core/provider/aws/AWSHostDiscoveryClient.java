package io.cscanner.core.provider.aws;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import io.cscanner.core.test.engine.HostDiscoveryClient;
import io.cscanner.core.test.engine.HostDiscoveryRecord;
import com.opsbears.webcomponents.net.IPAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@ParametersAreNonnullByDefault
public class AWSHostDiscoveryClient implements HostDiscoveryClient {
    private final String connectionName;
    private final AWSConfiguration awsConfiguration;

    public AWSHostDiscoveryClient(String connectionName, AWSConfiguration awsConfiguration) {
        this.connectionName = connectionName;
        this.awsConfiguration = awsConfiguration;
    }

    @Override
    public Stream<HostDiscoveryRecord> listIpAddresses() {
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(new IpAddressIterator(
                awsConfiguration
            ), Spliterator.ORDERED),
            false);

    }

    private class IpAddressIterator implements Iterator<HostDiscoveryRecord> {
        private final Logger logger = LoggerFactory.getLogger(IpAddressIterator.class);
        private String nextToken = null;
        private Regions currentRegion = null;
        private AmazonEC2 currentClient = null;
        private final Map<Regions, AmazonEC2> clients = new HashMap<>();
        private final Queue<Regions> regionQueue = new LinkedBlockingQueue<>();
        private final Queue<HostDiscoveryRecord> ipAddresses = new LinkedBlockingQueue<>();

        private IpAddressIterator(AWSConfiguration awsConfiguration) {
            for (Regions region : Regions.values()) {
                AmazonEC2 client = AmazonEC2ClientBuilder
                    .standard()
                    .withRegion(region.getName())
                    .withCredentials(awsConfiguration.getCredentialsProvider())
                    .build();
                try {
                    logger.info("Checking connection in region " + region.getName() + "...");
                    client.describeInstances();
                    clients.put(region, client);
                    regionQueue.offer(region);
                } catch (AmazonEC2Exception e) {
                    logger.warn("Received exception while describing instances in region " + region.getName() + ", skipping region. " + e.getMessage());
                }
            }
        }

        @Override
        public boolean hasNext() {
            if (ipAddresses.isEmpty()) {
                if (nextToken != null) {
                    //Fetch next batch of data
                    DescribeInstancesResult instancesResult =
                        currentClient.describeInstances(
                            new DescribeInstancesRequest()
                                .withNextToken(nextToken)
                        );
                    for (Reservation reservation : instancesResult.getReservations()) {
                        for (Instance instance : reservation.getInstances()) {
                            ipAddresses.offer(
                                new HostDiscoveryRecord(
                                    connectionName,
                                    currentRegion.getName(),
                                    IPAddress.getFromString(instance.getPublicIpAddress()),
                                    Collections.singletonList(instance.getInstanceId())
                                )
                            );
                        }
                    }
                } else if (!regionQueue.isEmpty()) {
                    do {
                        //Fetch next region
                        currentRegion = regionQueue.poll();
                        logger.info("Fetching IP addresses for region " + currentRegion.getName() + "...");
                        currentClient = clients.get(currentRegion);
                        DescribeInstancesResult instancesResult =
                            currentClient.describeInstances(new DescribeInstancesRequest());
                        nextToken = instancesResult.getNextToken();
                        for (Reservation reservation : instancesResult.getReservations()) {
                            for (Instance instance : reservation.getInstances()) {
                                if (instance.getPublicIpAddress() != null) {
                                    ipAddresses.offer(
                                        new HostDiscoveryRecord(
                                            connectionName,
                                            currentRegion.getName(),
                                            IPAddress.getFromString(instance.getPublicIpAddress()),
                                            Collections.singletonList(instance.getInstanceId())
                                        )
                                    );
                                }
                            }
                        }
                    } while (ipAddresses.isEmpty() && !regionQueue.isEmpty());
                } else {
                    return false;
                }
            }

            return
                !regionQueue.isEmpty() ||
                nextToken != null ||
                !ipAddresses.isEmpty()
            ;
        }

        @Override
        public HostDiscoveryRecord next() {
            return ipAddresses.poll();
        }
    }
}
