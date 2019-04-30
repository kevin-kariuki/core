package com.opsbears.cscanner.aws;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.Tag;
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

    public AWSTaggedResourceClient(
        String connectionName,
        AWSConfiguration awsConfiguration
    ) {
        this.connectionName = connectionName;
        this.awsConfiguration = awsConfiguration;
    }

    @Override
    public Stream<TaggedResource> getTaggedResources() {
        return getSecurityGroups();
    }

    private Stream<TaggedResource> getSecurityGroups() {
        Stream.Builder<TaggedResource> streamBuilder = Stream.builder();
        for (Regions region : Regions.values()) {
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
        }
        return streamBuilder.build();
    }
}
