package com.opsbears.cscanner.tags;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
public class TaggedResource {
    public final String connectionName;
    public final TaggedResourceType resourceType;
    @Nullable
    public final String resourceRegion;
    public final String resourceName;
    public final Map<String, String> tags;

    public TaggedResource(
        String connectionName,
        TaggedResourceType resourceType,
        @Nullable String resourceRegion,
        String resourceName,
        Map<String, String> tags
    ) {
        this.connectionName = connectionName;
        this.resourceType = resourceType;
        this.resourceRegion = resourceRegion;
        this.resourceName = resourceName;
        this.tags = tags;
    }
}
