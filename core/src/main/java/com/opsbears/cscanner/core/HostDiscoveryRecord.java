package com.opsbears.cscanner.core;

import com.opsbears.webcomponents.net.IPAddress;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
public class HostDiscoveryRecord {
    public final String connectionName;
    @Nullable
    public final String resourceRegion;
    public final IPAddress ipAddress;
    public final List<String> instanceIds;

    public HostDiscoveryRecord(
        String connectionName,
        @Nullable String resourceRegion,
        IPAddress ipAddress,
        List<String> instanceIds
    ) {
        this.connectionName = connectionName;
        this.resourceRegion = resourceRegion;
        this.ipAddress = ipAddress;
        this.instanceIds = Collections.unmodifiableList(instanceIds);
    }
}
