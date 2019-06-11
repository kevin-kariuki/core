package com.opsbears.cscanner.azure;

import com.opsbears.cscanner.core.CScannerParameter;
import com.opsbears.cscanner.core.NoDefaultSupplier;
import com.opsbears.cscanner.core.NullSupplier;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AzureConfiguration {
    final String subscriptionId;
    final String tenantId;
    final String appId;
    @Nullable
    final String certificate;
    @Nullable
    final String key;
    @Nullable
    final String certificatePassword;

    public AzureConfiguration(
        @Nullable
        @CScannerParameter(
            value = "subscriptionId",
            description = "The Azure subscription ID",
            defaultSupplier = NullSupplier.class
        )
            String subscriptionId,
        @CScannerParameter(
            value = "tenantId",
            description = "The Azure tenant ID",
            defaultSupplier = NoDefaultSupplier.class
        )
            String tenantId,
        @CScannerParameter(
            value = "appId",
            description = "The Azure client ID",
            defaultSupplier = NoDefaultSupplier.class
        )
        String appId,
        @Nullable
        @CScannerParameter(
            value = "key",
            description = "The Azure key",
            defaultSupplier = NullSupplier.class
        )
        String key,
        @Nullable
        @CScannerParameter(
            value = "certificate",
            description = "The Azure certificate",
            defaultSupplier = NullSupplier.class
        )
        String certificate,
        @Nullable
        @CScannerParameter(
            value = "certificatePassword",
            description = "The Azure certificate password",
            defaultSupplier = NullSupplier.class
        )
            String certificatePassword
    ) {
        this.subscriptionId = subscriptionId;
        this.tenantId = tenantId;
        this.appId = appId;
        this.certificate = certificate;
        this.key = key;
        this.certificatePassword = certificatePassword;

        if (certificate == null && key == null) {
            throw new RuntimeException("Either certificate or key must be supplied for Azure connections.");
        }
    }
}
