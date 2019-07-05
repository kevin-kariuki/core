package io.cscanner.core.test.engine;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public interface HostDiscoveryClient {
    Stream<HostDiscoveryRecord> listIpAddresses();
}
