package com.opsbears.cscanner.objectstorage;

import com.opsbears.cscanner.core.CloudProvider;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ObjectStorageCloudProvider<CONFIGURATIONTYPE, CONNECTIONTYPE extends ObjectStorageConnection> extends CloudProvider<CONFIGURATIONTYPE, CONNECTIONTYPE> {
}
