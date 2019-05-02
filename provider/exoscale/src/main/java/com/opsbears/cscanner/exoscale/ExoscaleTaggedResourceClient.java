package com.opsbears.cscanner.exoscale;

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackClient;
import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest;
import br.com.autonomiccs.apacheCloudStack.client.beans.ApacheCloudStackUser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.opsbears.cscanner.tags.TaggedResource;
import com.opsbears.cscanner.tags.TaggedResourceClient;
import com.opsbears.cscanner.tags.TaggedResourceType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class ExoscaleTaggedResourceClient implements TaggedResourceClient {
    private final String name;
    private final ExoscaleConfiguration exoscaleConfiguration;

    public ExoscaleTaggedResourceClient(
        String name,
        ExoscaleConfiguration exoscaleConfiguration
    ) {

        this.name = name;
        this.exoscaleConfiguration = exoscaleConfiguration;
    }

    @Override
    public Stream<TaggedResource> getTaggedResources() {
        ApacheCloudStackUser apacheCloudStackUser = new ApacheCloudStackUser(exoscaleConfiguration.secret, exoscaleConfiguration.key);
        ApacheCloudStackClient apacheCloudStackClient = new ApacheCloudStackClient("https://api.exoscale.ch/compute", apacheCloudStackUser);

        ApacheCloudStackRequest apacheCloudStackRequest = new ApacheCloudStackRequest("listVirtualMachines");
        String response = apacheCloudStackClient.executeRequest(apacheCloudStackRequest);
        JsonObject responseObject = new Gson().fromJson(response, JsonObject.class);
        JsonArray vmList = responseObject
            .get("listvirtualmachinesresponse")
            .getAsJsonObject()
            .get("virtualmachine")
            .getAsJsonArray();

        Stream.Builder<TaggedResource> builder = Stream.builder();
        for (int i = 0; i < vmList.size(); i++) {
            JsonObject vm = vmList.get(i).getAsJsonObject();
            JsonArray tagArray = vm.get("tags").getAsJsonArray();
            Map<String, String> tags = new HashMap<>();
            for (int j = 0; j < tagArray.size(); j++) {
                tags.put(
                    tagArray.get(j).getAsJsonObject().get("key").getAsString(),
                    tagArray.get(j).getAsJsonObject().get("value").getAsString()
                );
            }
            builder.add(new TaggedResource(
                name,
                TaggedResourceType.VM,
                vm.get("zonename").getAsString(),
                vm.get("displayname").getAsString(),
                tags
            ));
        }
        return builder.build();
    }
}
