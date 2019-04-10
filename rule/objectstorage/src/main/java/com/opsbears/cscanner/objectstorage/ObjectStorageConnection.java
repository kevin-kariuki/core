package com.opsbears.cscanner.objectstorage;

import com.opsbears.cscanner.core.CloudProviderConnection;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ObjectStorageConnection extends CloudProviderConnection {
    ObjectStorageClient getObjectStorageClient();
}
