package io.cscanner.core.test.engine;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public interface RuleBuilder<RULETYPE extends Rule<? extends CloudProviderConnection>, CONNECTION extends CloudProviderConnection, CONFIGURATIONTYPE> {
    String getType();

    default List<String> getTypeAliases() {
        return new ArrayList<>();
    }

    Class<CONNECTION> getConnectionType();

    Class<CONFIGURATIONTYPE> getConfigurationType();

    /**
     * @param options the configuration options for this type.
     * @return a certain type
     */
    RULETYPE create(
        CONFIGURATIONTYPE options
    );
}
