package io.cscanner.core.rule.objectstorage;

import io.cscanner.core.engine.ScannerCoreFactory;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ObjectStorageTestClientSupplier extends S3Factory {
    boolean isConfigured();
    String getDefaultZone();
    ScannerCoreFactory getScannerCore();
}
