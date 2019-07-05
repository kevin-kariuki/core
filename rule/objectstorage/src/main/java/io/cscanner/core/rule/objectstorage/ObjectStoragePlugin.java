package io.cscanner.core.rule.objectstorage;

import io.cscanner.core.test.engine.Plugin;
import io.cscanner.core.test.engine.RuleBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
public class ObjectStoragePlugin implements Plugin {
    @Override
    public List<RuleBuilder<?, ?, ?>> getSupportedRules() {
        //noinspection unchecked
        return Collections.singletonList(
                new ObjectStoragePublicReadProhibitedRuleBuilder()
        );
    }
}
