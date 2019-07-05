package io.cscanner.core.provider.exoscale;

import io.cscanner.core.test.engine.CScannerParameter;
import io.cscanner.core.test.engine.NullSupplier;

import javax.annotation.Nullable;

public class ExoscaleConfiguration {
    @Nullable
    public final String key;
    @Nullable
    public final String secret;
    @Nullable
    public final String cloudStackConfig;
    @Nullable
    public final String profile;

    public ExoscaleConfiguration(
        @CScannerParameter(value = "key", description = "API access key", defaultSupplier = NullSupplier.class)
        @Nullable String key,
        @CScannerParameter(value = "secret", description = "API secret", defaultSupplier = NullSupplier.class)
        @Nullable String secret,
        @CScannerParameter(value = "cloudStackConfig", description = "The CloudStack configuration file.", defaultSupplier = NullSupplier.class)
        @Nullable String cloudStackConfig,
        @CScannerParameter(value = "profile", description = "The CloudStack configuration file section to use.", defaultSupplier = NullSupplier.class)
        @Nullable String profile
    ) {
        this.key = key;
        this.secret = secret;
        this.cloudStackConfig = cloudStackConfig;
        this.profile = profile;
    }
}
