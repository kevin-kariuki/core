package com.opsbears.cscanner.core;

import com.opsbears.webcomponents.net.IPAddress;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public interface HostDiscoveryClient {
    Stream<IPAddress> listIpAddresses();
}
