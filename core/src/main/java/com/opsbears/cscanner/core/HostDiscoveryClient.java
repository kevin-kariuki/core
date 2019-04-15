package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public interface HostDiscoveryClient {
    Stream<HostDiscoveryRecord> listIpAddresses();
}
