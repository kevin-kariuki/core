package com.opsbears.cscanner.tags;

import com.opsbears.cscanner.core.CloudProviderConnection;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface TaggedResourceConnection extends CloudProviderConnection {
    TaggedResourceClient getTaggedResourceClient();
}
