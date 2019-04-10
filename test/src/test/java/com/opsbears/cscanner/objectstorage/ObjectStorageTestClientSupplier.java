package com.opsbears.cscanner.objectstorage;

import com.opsbears.cscanner.core.ScannerCoreFactory;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ObjectStorageTestClientSupplier extends S3Factory {
    boolean isConfigured();
    String getDefaultZone();
    ScannerCoreFactory getScannerCore();
}
