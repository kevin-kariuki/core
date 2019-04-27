package com.opsbears.cscanner.objectstorage;

import com.opsbears.cscanner.core.Plugin;
import com.opsbears.cscanner.core.RuleBuilder;

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
