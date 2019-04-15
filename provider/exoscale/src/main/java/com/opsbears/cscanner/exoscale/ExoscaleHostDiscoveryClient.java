package com.opsbears.cscanner.exoscale;

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackClient;
import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest;
import br.com.autonomiccs.apacheCloudStack.client.beans.ApacheCloudStackUser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.opsbears.cscanner.core.HostDiscoveryClient;
import com.opsbears.webcomponents.net.IPAddress;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class ExoscaleHostDiscoveryClient implements HostDiscoveryClient {
    private final String apiKey;
    private final String apiSecret;

    public ExoscaleHostDiscoveryClient(ExoscaleConfiguration configuration) {
        this.apiKey = configuration.key;
        this.apiSecret = configuration.secret;
    }

    @Override
    public Stream<IPAddress> listIpAddresses() {
        ApacheCloudStackUser apacheCloudStackUser = new ApacheCloudStackUser(apiSecret, apiKey);
        ApacheCloudStackClient apacheCloudStackClient = new ApacheCloudStackClient(
            "https://api.exoscale.ch/compute",
            apacheCloudStackUser
        );

        ApacheCloudStackRequest apacheCloudStackRequest = new ApacheCloudStackRequest("listPublicIpAddresses");
        String response = apacheCloudStackClient.executeRequest(apacheCloudStackRequest);
        JsonObject responseObject = new Gson().fromJson(response, JsonObject.class);
        JsonArray ipList = responseObject
            .get("listpublicipaddressesresponse")
            .getAsJsonObject()
            .get("publicipaddress")
            .getAsJsonArray();

        List<IPAddress> ipAddresses = new ArrayList<>();
        for (int i = 0; i < ipList.size(); i++) {
            ipAddresses.add(IPAddress.getFromString(ipList
                .get(i)
                .getAsJsonObject()
                .get("ipaddress")
                .getAsString()));
        }

        return Stream.of(ipAddresses.toArray(new IPAddress[0]));
    }
}
