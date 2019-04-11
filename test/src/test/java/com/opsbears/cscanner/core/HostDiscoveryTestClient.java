package com.opsbears.cscanner.core;

import com.opsbears.webcomponents.net.IPAddress;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface HostDiscoveryTestClient {
    Host createPublicHost(String name);
    void destroyPublicHost(Host host);

    class Host {
        public final String hostId;
        @Nullable
        public final IPAddress ipAddress;
        public final String name;

        public Host(
            String hostId,
            @Nullable
            IPAddress ipAddress,
            String name
        ) {
            this.hostId = hostId;
            this.ipAddress = ipAddress;
            this.name = name;
        }
    }
}
