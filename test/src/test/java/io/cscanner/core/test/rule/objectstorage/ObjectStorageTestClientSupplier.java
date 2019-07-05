package io.cscanner.core.test.rule.objectstorage;

import io.cscanner.core.rule.objectstorage.S3Factory;
import io.cscanner.core.test.engine.ScannerCoreFactory;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ObjectStorageTestClientSupplier extends S3Factory {
    boolean isConfigured();
    String getDefaultZone();
    ScannerCoreFactory getScannerCore();
}
