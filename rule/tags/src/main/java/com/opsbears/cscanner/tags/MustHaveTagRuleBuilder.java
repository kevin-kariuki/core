package com.opsbears.cscanner.tags;

import com.opsbears.cscanner.core.RuleBuilder;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MustHaveTagRuleBuilder implements RuleBuilder<MustHaveTagRule, TaggedResourceConnection, MustHaveTagRule.Options> {
    @Override
    public String getType() {
        return MustHaveTagRule.MUST_HAVE_TAG;
    }

    @Override
    public Class<TaggedResourceConnection> getConnectionType() {
        return TaggedResourceConnection.class;
    }

    @Override
    public Class<MustHaveTagRule.Options> getConfigurationType() {
        return MustHaveTagRule.Options.class;
    }

    @Override
    public MustHaveTagRule create(MustHaveTagRule.Options options) {
        return new MustHaveTagRule(
            options
        );
    }
}
