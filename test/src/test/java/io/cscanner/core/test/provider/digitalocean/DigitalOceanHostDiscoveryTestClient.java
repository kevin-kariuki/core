package io.cscanner.core.test.provider.digitalocean;

import com.myjeeva.digitalocean.DigitalOcean;
import com.myjeeva.digitalocean.common.ActionType;
import com.myjeeva.digitalocean.common.DropletStatus;
import com.myjeeva.digitalocean.exception.DigitalOceanException;
import com.myjeeva.digitalocean.exception.RequestUnsuccessfulException;
import com.myjeeva.digitalocean.impl.DigitalOceanClient;
import com.myjeeva.digitalocean.pojo.Droplet;
import com.myjeeva.digitalocean.pojo.Image;
import com.myjeeva.digitalocean.pojo.Region;
import io.cscanner.core.test.engine.HostDiscoveryTestClient;
import com.opsbears.webcomponents.net.IPAddress;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DigitalOceanHostDiscoveryTestClient implements HostDiscoveryTestClient {
    private final DigitalOcean apiClient;

    public DigitalOceanHostDiscoveryTestClient(String apiToken) {
        apiClient = new DigitalOceanClient(apiToken);
    }

    @Override
    public Host createPublicHost(String name) {
        Image image;
        try {
            image = apiClient
                .getAvailableImages(0, 100, ActionType.DISTRIBUTION)
                .getImages()
                .stream()
                //We know that Ubuntu images have passwords, no SSH key needed.
                .filter(img -> img.getSlug().startsWith("ubuntu"))
                .findFirst()
                .get();
        } catch (DigitalOceanException | RequestUnsuccessfulException e) {
            throw new RuntimeException(e);
        }

        Droplet droplet = new Droplet();
        droplet.setDiskSize(10);
        droplet.setName(name);
        droplet.setSize("512mb");
        droplet.setRegion(new Region("ams3"));
        droplet.setDiskSize(image.getMinDiskSize());
        droplet.setImage(image);
        try {
            droplet = apiClient.createDroplet(
                droplet
            );
        } catch (DigitalOceanException | RequestUnsuccessfulException e) {
            throw new RuntimeException(e);
        }

        do {
            try {
                droplet = apiClient.getDropletInfo(droplet.getId());
            } catch (DigitalOceanException | RequestUnsuccessfulException e) {
                throw new RuntimeException(e);
            }
        } while (droplet.getStatus() != DropletStatus.ACTIVE);

        return new Host(
            droplet.getId().toString(),
            IPAddress.getFromString(droplet.getNetworks().getVersion4Networks().get(0).getIpAddress()),
            name
        );
    }

    @Override
    public void destroyPublicHost(Host host) {
        try {
            apiClient.deleteDroplet(Integer.valueOf(host.hostId));
        } catch (DigitalOceanException | RequestUnsuccessfulException e) {
            throw new RuntimeException(e);
        }
    }
}
