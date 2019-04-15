package com.opsbears.cscanner.exoscale;

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackClient;
import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest;
import br.com.autonomiccs.apacheCloudStack.client.beans.ApacheCloudStackUser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.opsbears.cscanner.core.HostDiscoveryTestClient;
import com.opsbears.webcomponents.net.IPAddress;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExoscaleHostDiscoveryTestClient implements HostDiscoveryTestClient {
    private final String apiKey;
    private final String apiSecret;
    private final ApacheCloudStackUser apacheCloudStackUser;
    private final ApacheCloudStackClient apacheCloudStackClient;

    public ExoscaleHostDiscoveryTestClient(
        String apiKey,
        String apiSecret
    ) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;

        apacheCloudStackUser = new ApacheCloudStackUser(apiSecret, apiKey);
        apacheCloudStackClient = new ApacheCloudStackClient("https://api.exoscale.ch/compute", apacheCloudStackUser);
    }


    @Override
    public Host createPublicHost(String name) {
        String listZonesResponse = apacheCloudStackClient.executeRequest(
            new ApacheCloudStackRequest(
                "listZones"
            )
        );
        JsonArray zoneList = new Gson().fromJson(listZonesResponse, JsonObject.class)
            .get("listzonesresponse")
            .getAsJsonObject()
            .get("zone")
            .getAsJsonArray();

        String zoneId = zoneList.get(0).getAsJsonObject().get("id").getAsString();

        String serviceOfferingsResponse = apacheCloudStackClient.executeRequest(
            new ApacheCloudStackRequest(
                "listServiceOfferings"
            )
        );

        JsonArray soList = new Gson().fromJson(serviceOfferingsResponse, JsonObject.class)
            .get("listserviceofferingsresponse")
            .getAsJsonObject()
            .get("serviceoffering")
            .getAsJsonArray();

        String serviceOfferingId = soList.get(0).getAsJsonObject().get("id").getAsString();

        String listTemplatesResponse = apacheCloudStackClient.executeRequest(
            new ApacheCloudStackRequest(
                "listTemplates"

            )
                .addParameter("templatefilter", "featured")
            .addParameter("zoneid", zoneId)
        );

        JsonArray templateList = new Gson().fromJson(listTemplatesResponse, JsonObject.class)
            .get("listtemplatesresponse")
            .getAsJsonObject()
            .get("template")
            .getAsJsonArray();

        String templateId = templateList.get(0).getAsJsonObject().get("id").getAsString();

        String deployVirtualMachineResponse = apacheCloudStackClient.executeRequest(
            new ApacheCloudStackRequest(
                "deployVirtualMachine"

            )
                .addParameter("serviceOfferingId", serviceOfferingId)
                .addParameter("templateId", templateId)
                .addParameter("zoneId", zoneId)
        );

        JsonObject instanceJob = new Gson().fromJson(deployVirtualMachineResponse, JsonObject.class)
            .get("deployvirtualmachineresponse")
            .getAsJsonObject();

        String jobId = instanceJob.get("jobid").getAsString();

        String deployVirtualMachineJobResponse;
        int jobStatus;
        JsonObject responseObject;
        do {
            deployVirtualMachineJobResponse = apacheCloudStackClient.executeRequest(
                new ApacheCloudStackRequest(
                    "queryAsyncJobResult"

                )
                    .addParameter("jobId", jobId)
            );

            responseObject =  new Gson().fromJson(deployVirtualMachineJobResponse, JsonObject.class)
                .get("queryasyncjobresultresponse")
                .getAsJsonObject();

            jobStatus = responseObject
                .get("jobstatus")
                .getAsInt();
        } while (jobStatus == 0);

        JsonObject virtualMachine = responseObject
            .get("jobresult")
            .getAsJsonObject()
            .get("virtualmachine")
            .getAsJsonObject();

        String hostId = virtualMachine.get("id").getAsString();
        IPAddress ipAddress = IPAddress.getFromString(virtualMachine
            .get("nic")
            .getAsJsonArray()
            .get(0)
            .getAsJsonObject()
            .get("ipaddress")
            .getAsString());

        return new Host(
            hostId,
            ipAddress,
            name
        );
    }

    @Override
    public void destroyPublicHost(Host host) {
        apacheCloudStackClient.executeRequest(
            new ApacheCloudStackRequest(
                "destroyVirtualMachine"

            )
                .addParameter("id", host.hostId)
        );
    }
}
