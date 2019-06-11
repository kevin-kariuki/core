package com.opsbears.cscanner.azure;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.*;
import com.opsbears.cscanner.firewall.FirewallClient;
import com.opsbears.cscanner.firewall.FirewallGroup;
import com.opsbears.cscanner.firewall.FirewallRule;
import com.opsbears.cscanner.firewall.Protocols;
import com.opsbears.webcomponents.immutable.ImmutableTuple2;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@ParametersAreNonnullByDefault
public class AzureFirewallClient implements FirewallClient {
    private final String connectionName;
    private final Azure azure;

    public AzureFirewallClient(
        String connectionName,
        Azure azure
    ) {
        this.connectionName = connectionName;
        this.azure = azure;
    }

    private static Integer convertFromAzureProtocol(SecurityRuleProtocol protocol) {
        if (protocol.equals(SecurityRuleProtocol.ASTERISK)) {
            return null;
        } else {
            return Protocols.getInstance().getProtocolIdByName(protocol.toString());
        }
    }

    private static List<ImmutableTuple2<Integer, Integer>> convertPorts(String portDescription) {
        List<ImmutableTuple2<Integer, Integer>> result = new ArrayList<>();
        String[] portParts = portDescription.split(",");
        for (String portPart : portParts) {
            if (portPart.trim().equalsIgnoreCase("*") || portPart.equalsIgnoreCase("-1")) {
                result.add(new ImmutableTuple2<>(0, 65535));
            } else if (portPart.trim().matches("\\A[0-9]+\\Z")) {
                int port = Integer.parseInt(portPart.trim());
                result.add(new ImmutableTuple2<>(port, port));
            } else if (portPart.trim().matches("\\A[0-9]+-[0-9]+\\Z")) {
                String[] portSubParts = portPart.split("-");
                result.add(new ImmutableTuple2<>(
                    Integer.parseInt(portSubParts[0].trim()),
                    Integer.parseInt(portSubParts[1].trim())
                ));
            } else {
                throw new RuntimeException("Unsupported port format: " + portDescription);
            }
        }
        return result;
    }

    private static List<FirewallRule> convertRules(NetworkSecurityGroup sg, NetworkSecurityRule sr) {
        List<FirewallRule> rules = new ArrayList<>();
        String ipRange = sr
            .direction()
            .equals(SecurityRuleDirection.INBOUND) ? sr.sourceAddressPrefix() : sr.destinationAddressPrefix();
        Set<String> sgs = sr.direction().equals(SecurityRuleDirection.INBOUND)?sr.sourceApplicationSecurityGroupIds():sr.destinationApplicationSecurityGroupIds();
        List<ImmutableTuple2<Integer, Integer>> ports = convertPorts(sr
            .direction()
            .equals(SecurityRuleDirection.INBOUND) ? sr.destinationPortRange() : sr.sourcePortRange());
        List<String> cidrs = new ArrayList<>();
        if (ipRange.equalsIgnoreCase("*")) {
            cidrs.add("0.0.0.0/0");
            cidrs.add("::/0");
        } else {
            cidrs.add(ipRange);
        }
        for (String cidr : cidrs) {
            for (ImmutableTuple2<Integer, Integer> portRange : ports) {
                rules.add(new FirewallRule(
                    sg.id() + "/" + sr.name(),
                    convertFromAzureProtocol(sr.protocol()),
                    cidr,
                    null,
                    portRange.getA(),
                    portRange.getB(),
                    null,
                    null,
                    sr.direction() == SecurityRuleDirection.INBOUND?FirewallRule.Direction.INGRESS:FirewallRule.Direction.EGRESS,
                    sr.access() == SecurityRuleAccess.ALLOW?FirewallRule.Rule.ALLOW:FirewallRule.Rule.DENY
                ));
            }
        }
        for (String sgId : sgs) {
            for (ImmutableTuple2<Integer, Integer> portRange : ports) {
                rules.add(new FirewallRule(
                    sg.id() + "/" + sr.name(),
                    convertFromAzureProtocol(sr.protocol()),
                    null,
                    sgId,

                    portRange.getA(),
                    portRange.getB(),
                    null,
                    null,
                    sr.direction() == SecurityRuleDirection.INBOUND?FirewallRule.Direction.INGRESS:FirewallRule.Direction.EGRESS,
                    sr.access() == SecurityRuleAccess.ALLOW?FirewallRule.Rule.ALLOW:FirewallRule.Rule.DENY
                ));
            }
        }
        return rules;
    }

    @Override
    public List<FirewallGroup> listFirewallGroups() {
        List<FirewallGroup> result = new ArrayList<>();

        PagedList<NetworkSecurityGroup> securityGroups = azure.networkSecurityGroups().list();
        boolean finished = false;
        do {
            for (NetworkSecurityGroup sg : securityGroups) {
                List<FirewallRule> firewallRules = new ArrayList<>();

                sg
                    .securityRules()
                    .values()
                    .stream()
                    .sorted(Comparator.comparingInt(NetworkSecurityRule::priority))
                    .forEach(
                        sr -> firewallRules.addAll(convertRules(sg, sr))
                    );
                sg
                    .defaultSecurityRules()
                    .values()
                    .stream()
                    .sorted(Comparator.comparingInt(NetworkSecurityRule::priority))
                    .forEach(
                        sr -> firewallRules.addAll(convertRules(sg, sr))
                    );

                result.add(new FirewallGroup(
                    sg.name(),
                    sg.regionName(),
                    firewallRules
                ));
            }
            if (securityGroups.hasNextPage()) {
                securityGroups.loadNextPage();
            } else {
                finished = true;
            }
        } while (!finished);

        return result;
    }
}
