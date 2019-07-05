package io.cscanner.core.test;

import io.cscanner.core.test.engine.ConfigLoader;
import io.cscanner.core.test.engine.ConnectionConfiguration;
import io.cscanner.core.test.engine.RuleConfiguration;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public class TestConfigurationLoader implements ConfigLoader {
    private final Map<String, ConnectionConfiguration> connections;
    private final List<RuleConfiguration> rules;

    public TestConfigurationLoader(
        Map<String, ConnectionConfiguration> connections,
        List<RuleConfiguration> rules
    ) {
        this.connections = connections;
        this.rules = rules;
    }

    @Override
    public Map<String, ConnectionConfiguration> loadConnectionConfigurations() {
        return connections;
    }

    @Override
    public List<RuleConfiguration> loadRuleConfigurations() {
        return rules;
    }
}
