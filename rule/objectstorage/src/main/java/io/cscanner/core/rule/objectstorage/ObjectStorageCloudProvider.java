package io.cscanner.core.rule.objectstorage;

import io.cscanner.core.engine.CloudProvider;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ObjectStorageCloudProvider<CONFIGURATIONTYPE, CONNECTIONTYPE extends ObjectStorageConnection> extends CloudProvider<CONFIGURATIONTYPE, CONNECTIONTYPE> {
}
