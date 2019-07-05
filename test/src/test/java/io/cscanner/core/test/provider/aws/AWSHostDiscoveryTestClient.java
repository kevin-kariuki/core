package io.cscanner.core.test.provider.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
<<<<<<< HEAD:test/src/test/java/io/cscanner/core/provider/aws/AWSHostDiscoveryTestClient.java
import io.cscanner.core.test.engine.HostDiscoveryTestClient;
=======
>>>>>>> a57a28e509d66f16a62523fb1b21fabe8753ca00:test/src/test/java/com/opsbears/cscanner/aws/AWSHostDiscoveryTestClient.java
import com.opsbears.webcomponents.net.IPAddress;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@ParametersAreNonnullByDefault
public class AWSHostDiscoveryTestClient implements HostDiscoveryTestClient {
    private final String apiKey;
    private final String apiSecret;
    private final AmazonEC2 client;

    public AWSHostDiscoveryTestClient(
        String apiKey,
        String apiSecret
    ) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;

        this.client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {
                return new AWSCredentials() {
                    @Override
                    public String getAWSAccessKeyId() {
                        return apiKey;
                    }

                    @Override
                    public String getAWSSecretKey() {
                        return apiSecret;
                    }
                };
            }

            @Override
            public void refresh() {

            }
        })
            .withRegion(Regions.DEFAULT_REGION)
            .build();
    }


    @Override
    public Host createPublicHost(String name) {
        CreateVpcResult vpc = client.createVpc(new CreateVpcRequest("10.0.0.0/24"));
        try {
            client.createTags(
                new CreateTagsRequest()
                    .withTags(new Tag().withKey("Name").withValue(name))
                    .withResources(vpc.getVpc().getVpcId())
            );
            CreateSecurityGroupResult sg = client.createSecurityGroup(
                new CreateSecurityGroupRequest()
                    .withDescription("CScanner Test")
                    .withGroupName(name)
                    .withVpcId(vpc.getVpc().getVpcId())
            );
            client.createTags(
                new CreateTagsRequest()
                    .withTags(new Tag().withKey("Name").withValue(name))
                    .withResources(sg.getGroupId())
            );
            try {
                CreateSubnetResult subnetResponse = client.createSubnet(
                    new CreateSubnetRequest()
                        .withAvailabilityZone(Regions.DEFAULT_REGION.getName() + "a")
                        .withCidrBlock("10.0.0.0/24")
                        .withVpcId(vpc.getVpc().getVpcId())
                );

                try {
                    String amiId = client
                        .describeImages(
                            new DescribeImagesRequest()
                                //Canonical
                                .withOwners("099720109477")
                                .withFilters(
                                    new Filter()
                                        .withName("virtualization-type")
                                        .withValues("hvm"),
                                    new Filter()
                                        .withName("name")
                                        .withValues("ubuntu/images/hvm-ssd/ubuntu-xenial-16.04-amd64-server-*")
                                )
                        )
                        .getImages()
                        .get(0)
                        .getImageId();

                    RunInstancesRequest request = new RunInstancesRequest()
                        .withImageId(amiId)
                        .withInstanceType(InstanceType.T3Micro)
                        .withMinCount(1)
                        .withMaxCount(1)
                        .withNetworkInterfaces(
                            new InstanceNetworkInterfaceSpecification()
                                .withAssociatePublicIpAddress(true)
                                .withDeleteOnTermination(true)
                                .withDeviceIndex(0)
                                .withSubnetId(subnetResponse.getSubnet().getSubnetId())
                                .withGroups(sg.getGroupId()))
                        .withTagSpecifications(
                            new TagSpecification()
                                .withTags(new Tag("Name", name))
                                .withResourceType(ResourceType.Instance)
                        )
                    ;

                    RunInstancesResult result = client.runInstances(request);

                    InstanceStatus status;
                    do {
                        DescribeInstanceStatusResult response = client.describeInstanceStatus(new DescribeInstanceStatusRequest().withInstanceIds(
                            result.getReservation().getInstances().get(0).getInstanceId()
                        ).withIncludeAllInstances(true));
                        status = response.getInstanceStatuses().get(0);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } while (status.getInstanceState().getCode() != 16);

                    DescribeInstancesResult instance = client.describeInstances(new DescribeInstancesRequest().withInstanceIds(
                        result.getReservation().getInstances().get(0).getInstanceId()));

                    String publicIp = instance.getReservations().get(0).getInstances().get(0).getPublicIpAddress();

                    return new Host(
                        result.getReservation().getInstances().get(0).getInstanceId(),
                        publicIp != null ? IPAddress.getFromString(publicIp):null,
                        name
                    );
                } catch (Exception e) {
                    client.deleteSubnet(new DeleteSubnetRequest().withSubnetId(subnetResponse.getSubnet().getSubnetId()));
                    throw e;
                }
            } catch (Exception e) {
                client.deleteSecurityGroup(new DeleteSecurityGroupRequest().withGroupId(sg.getGroupId()));
                throw e;
            }
        } catch (Exception e) {
            client.deleteVpc(new DeleteVpcRequest(vpc.getVpc().getVpcId()));
            throw e;
        }
    }

    @Override
    public void destroyPublicHost(Host host) {
        InstanceStateChange terminatingInstance = client
            .terminateInstances(new TerminateInstancesRequest().withInstanceIds(host.hostId))
            .getTerminatingInstances()
            .get(0);

        InstanceStatus status;
        do {
            DescribeInstanceStatusResult response = client.describeInstanceStatus(new DescribeInstanceStatusRequest().withInstanceIds(
                host.hostId
            ).withIncludeAllInstances(true));
            status = response.getInstanceStatuses().get(0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } while (status.getInstanceState().getCode() != 48);

        String nextToken = null;
        do {
            DescribeVpcsResult describeVpcs = client.describeVpcs(
                new DescribeVpcsRequest()
                    .withFilters(
                        Arrays.asList(new Filter("tag:Name", Arrays.asList(host.name)))
                    )
                    .withNextToken(nextToken)
            );
            for (Vpc vpc : describeVpcs.getVpcs()) {
                String sgNextToken = null;
                do {
                    DescribeSecurityGroupsResult securityGroups = client
                        .describeSecurityGroups(new DescribeSecurityGroupsRequest()
                            .withFilters(
                                new Filter("vpc-id", Arrays.asList(vpc.getVpcId())),
                                new Filter("tag:Name", Arrays.asList(host.name))
                            )
                        )
                        .withNextToken(sgNextToken);
                    for (SecurityGroup securityGroup : securityGroups.getSecurityGroups()) {
                        client.deleteSecurityGroup(
                            new DeleteSecurityGroupRequest().withGroupId(securityGroup.getGroupId())
                        );
                    }
                    sgNextToken = securityGroups.getNextToken();
                } while (sgNextToken != null);

                String subnetNextToken = null;
                DescribeSubnetsResult subnets = client
                    .describeSubnets(new DescribeSubnetsRequest()
                        .withFilters(
                            new Filter("vpc-id", Arrays.asList(vpc.getVpcId()))
                        )
                    );
                for (Subnet subnet : subnets.getSubnets()) {
                    client.deleteSubnet(
                        new DeleteSubnetRequest().withSubnetId(subnet.getSubnetId())
                    );
                }
                client.deleteVpc(new DeleteVpcRequest().withVpcId(vpc.getVpcId()));
            }
            nextToken = describeVpcs.getNextToken();
        } while (nextToken != null);
    }
}
