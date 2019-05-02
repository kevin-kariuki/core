package com.opsbears.cscanner.aws;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.opsbears.cscanner.tags.S3TaggedResourceClient;
import com.opsbears.cscanner.tags.TaggedResource;
import com.opsbears.cscanner.tags.TaggedResourceClient;
import com.opsbears.cscanner.tags.TaggedResourceType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class AWSTaggedResourceClient implements TaggedResourceClient {
    private final String connectionName;
    private final AWSConfiguration awsConfiguration;
    private final AWSS3ClientSupplier awss3ClientSupplier;

    public AWSTaggedResourceClient(
        String connectionName,
        AWSConfiguration awsConfiguration,
        AWSS3ClientSupplier awss3ClientSupplier
    ) {
        this.connectionName = connectionName;
        this.awsConfiguration = awsConfiguration;
        this.awss3ClientSupplier = awss3ClientSupplier;
    }

    @Override
    public Stream<TaggedResource> getTaggedResources() {
        return Stream.concat(Stream.concat(
            getSecurityGroups(),
            getInstances()
        ),getBuckets());
    }

    private Stream<TaggedResource> getSecurityGroups() {
        Stream.Builder<TaggedResource> streamBuilder = Stream.builder();
        awsConfiguration.validateCredentials();
        for (Regions region : Regions.values()) {
            try {
                AmazonEC2ClientBuilder builder = AmazonEC2ClientBuilder.standard();
                builder.withCredentials(awsConfiguration.getCredentialsProvider());
                builder.withRegion(region);
                AmazonEC2 client = builder.build();

                String nextToken = null;
                do {
                    DescribeSecurityGroupsResult describeSecurityGroups = client.describeSecurityGroups(
                        new DescribeSecurityGroupsRequest()
                            .withNextToken(nextToken)
                    );

                    describeSecurityGroups
                        .getSecurityGroups()
                        .stream()
                        .map(sg -> new TaggedResource(
                            connectionName,
                            TaggedResourceType.SECURITY_GROUP,
                            region.getName(),
                            sg.getGroupName(),
                            sg
                                .getTags()
                                .stream()
                                .collect(Collectors.toMap(
                                    Tag::getKey,
                                    Tag::getValue
                                ))
                        ))
                        .forEach(streamBuilder::add);
                    ;
                    nextToken = describeSecurityGroups.getNextToken();
                } while (nextToken != null);
            } catch (AmazonEC2Exception e) {
                //ignore error, region probably not available.
            }
        }
        return streamBuilder.build();
    }

    private Stream<TaggedResource> getInstances() {
        Stream.Builder<TaggedResource> streamBuilder = Stream.builder();
        awsConfiguration.validateCredentials();
        for (Regions region : Regions.values()) {
            try {
                AmazonEC2ClientBuilder builder = AmazonEC2ClientBuilder.standard();
                builder.withCredentials(awsConfiguration.getCredentialsProvider());
                builder.withRegion(region);
                AmazonEC2 client = builder.build();

                String nextToken = null;
                do {
                    DescribeInstancesResult describeInstancesResult = client.describeInstances(
                        new DescribeInstancesRequest()
                            .withNextToken(nextToken)
                    );

                    describeInstancesResult
                        .getReservations()
                        .forEach((Reservation reservation) ->
                            reservation.getInstances()
                                .stream()
                                .map(sg -> new TaggedResource(
                                    connectionName,
                                    TaggedResourceType.VM,
                                    region.getName(),
                                    sg.getInstanceId(),
                                    sg
                                        .getTags()
                                        .stream()
                                        .collect(Collectors.toMap(
                                            Tag::getKey,
                                            Tag::getValue
                                        ))
                                ))
                                .forEach(streamBuilder::add)
                        )
                    ;
                    nextToken = describeInstancesResult.getNextToken();
                } while (nextToken != null);
            } catch (AmazonEC2Exception e) {
                //Ignore error, region probably not available.
            }
        }
        return streamBuilder.build();
    }


    private Stream<TaggedResource> getBuckets() {
        return new S3TaggedResourceClient(
            connectionName,
            awss3ClientSupplier
        ).getTaggedResources();
    }
}
