package com.opsbears.cscanner.objectstorage;

import com.opsbears.cscanner.core.Rule;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ObjectStorageRule extends Rule<ObjectStorageConnection> {
    String RESOURCE_TYPE = "objectstorage";
}
