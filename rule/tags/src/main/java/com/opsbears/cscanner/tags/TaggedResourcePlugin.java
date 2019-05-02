package com.opsbears.cscanner.tags;

import com.opsbears.cscanner.core.Plugin;
import com.opsbears.cscanner.core.RuleBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
public class TaggedResourcePlugin implements Plugin {
    @Override
    public List<RuleBuilder<?, ?, ?>> getSupportedRules() {
        //noinspection unchecked
        return Arrays.asList(
            new MustHaveTagRuleBuilder(),
            new MustNotHaveTagRuleBuilder()
        );
    }
}
