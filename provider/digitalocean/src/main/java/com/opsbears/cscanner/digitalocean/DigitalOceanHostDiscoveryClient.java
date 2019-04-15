package com.opsbears.cscanner.digitalocean;

import com.myjeeva.digitalocean.DigitalOcean;
import com.myjeeva.digitalocean.exception.DigitalOceanException;
import com.myjeeva.digitalocean.exception.RequestUnsuccessfulException;
import com.myjeeva.digitalocean.impl.DigitalOceanClient;
import com.myjeeva.digitalocean.pojo.*;
import com.opsbears.cscanner.core.HostDiscoveryClient;
import com.opsbears.webcomponents.net.IPAddress;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class DigitalOceanHostDiscoveryClient implements HostDiscoveryClient {
    private final String token;
    private final DigitalOcean apiClient;

    public DigitalOceanHostDiscoveryClient(DigitalOceanConfiguration configuration) {
        token = configuration.apiToken;
        apiClient = new DigitalOceanClient(token);
    }

    private List<IPAddress> getFloatingIps() {
        List<IPAddress> ipAddresses = new ArrayList<>();

        Pages pages;
        int page = 0;
        int perPage = 100;
        do {
            try {
                FloatingIPs floatingIps = apiClient.getAvailableFloatingIPs(
                    page,
                    perPage
                );
                ipAddresses.addAll(floatingIps
                    .getFloatingIPs()
                    .stream()
                    .map(FloatingIP::getIp)
                    .map(IPAddress::getFromString)
                    .collect(Collectors.toList()));
                if (floatingIps.getLinks() != null && floatingIps.getLinks().getPages() != null && floatingIps.getLinks().getPages().getNext() != null) {
                    pages = floatingIps.getLinks().getPages();
                    page++;
                } else {
                    pages = null;
                }
            } catch (DigitalOceanException | RequestUnsuccessfulException e) {
                throw new RuntimeException(e);
            }
        } while (pages != null);

        return ipAddresses;
    }

    private List<IPAddress> getDropletIps() {
        List<IPAddress> ipAddresses = new ArrayList<>();

        Pages pages;
        int page = 0;
        int perPage = 100;
        do {
            try {
                Droplets droplets = apiClient.getAvailableDroplets(
                    page,
                    perPage
                );
                droplets
                    .getDroplets()
                    .forEach(droplet -> {
                        ipAddresses.addAll(
                            droplet
                                .getNetworks()
                                .getVersion4Networks()
                                .stream()
                                .map(Network::getIpAddress)
                                .map(IPAddress::getFromString)
                                .collect(Collectors.toList()));
                        ipAddresses.addAll(
                            droplet
                                .getNetworks()
                                .getVersion6Networks()
                                .stream()
                                .map(Network::getIpAddress)
                                .map(IPAddress::getFromString)
                                .collect(Collectors.toList()));
                    });
                if (droplets.getLinks() != null && droplets.getLinks().getPages() != null && droplets.getLinks().getPages().getNext() != null) {
                    pages = droplets.getLinks().getPages();
                    page++;
                } else {
                    pages = null;
                }
            } catch (DigitalOceanException | RequestUnsuccessfulException e) {
                throw new RuntimeException(e);
            }
        } while (pages != null);

        return ipAddresses;

    }

    @Override
    public Stream<IPAddress> listIpAddresses() {
        List<IPAddress> ipAddresses = new ArrayList<>();

        ipAddresses.addAll(getDropletIps());
        ipAddresses.addAll(getFloatingIps());

        return ipAddresses.stream();
    }
}
