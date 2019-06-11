package com.opsbears.cscanner.azure;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.opsbears.cscanner.core.HostDiscoveryClient;
import com.opsbears.cscanner.core.HostDiscoveryRecord;
import com.opsbears.webcomponents.net.IPAddress;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class AzureHostDiscoveryClient implements HostDiscoveryClient {
    private final String connectionName;
    private final Azure azure;

    public AzureHostDiscoveryClient(
        String connectionName,
        Azure azure
    ) {
        this.connectionName = connectionName;
        this.azure = azure;
    }

    @Override
    public Stream<HostDiscoveryRecord> listIpAddresses() {
        PagedList<VirtualMachine> virtualMachines = azure
            .virtualMachines()
            .list();

        Stream.Builder<HostDiscoveryRecord> result = Stream.builder();

        boolean finished = false;
        do {
            for (VirtualMachine vm : virtualMachines) {
                result.add(new HostDiscoveryRecord(
                    connectionName,
                    vm.regionName(),
                    IPAddress.getFromString(vm.getPrimaryPublicIPAddress().ipAddress()),
                    Collections.singletonList(
                        vm.id()
                    )
                ));
            }
            if (virtualMachines.hasNextPage()) {
                virtualMachines.loadNextPage();
            } else {
                finished = true;
            }
        } while(!finished);

        return result.build();
    }
}
