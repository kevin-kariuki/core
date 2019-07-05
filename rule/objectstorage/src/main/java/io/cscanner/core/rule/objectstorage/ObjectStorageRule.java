package io.cscanner.core.rule.objectstorage;

import io.cscanner.core.engine.Rule;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ObjectStorageRule extends Rule<ObjectStorageConnection> {
    String RESOURCE_TYPE = "objectstorage";
}
