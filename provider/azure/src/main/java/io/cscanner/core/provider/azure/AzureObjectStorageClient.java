package io.cscanner.core.provider.azure;

import com.microsoft.azure.management.Azure;
import io.cscanner.core.rule.objectstorage.ObjectStorageClient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class AzureObjectStorageClient implements ObjectStorageClient {
    private final String connectionName;
    private final Azure azure;

    public AzureObjectStorageClient(
        String connectionName,
        Azure azure
    ) {
        this.connectionName = connectionName;
        this.azure = azure;
    }

    @Override
    public Stream<Bucket> listBuckets() {
        //TODO: implement
        return Stream.empty();
    }

    @Override
    public Stream<Object> listObjects(Bucket bucket) {
        //TODO: implement
        return Stream.empty();
    }
}
