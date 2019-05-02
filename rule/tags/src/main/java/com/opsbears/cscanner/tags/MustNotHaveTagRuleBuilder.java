package com.opsbears.cscanner.tags;

import com.opsbears.cscanner.core.RuleBuilder;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MustNotHaveTagRuleBuilder implements RuleBuilder<MustNotHaveTagRule, TaggedResourceConnection, MustNotHaveTagRule.Options> {
    @Override
    public String getType() {
        return MustNotHaveTagRule.MUST_NOT_HAVE_TAG;
    }

    @Override
    public Class<TaggedResourceConnection> getConnectionType() {
        return TaggedResourceConnection.class;
    }

    @Override
    public Class<MustNotHaveTagRule.Options> getConfigurationType() {
        return MustNotHaveTagRule.Options.class;
    }

    @Override
    public MustNotHaveTagRule create(MustNotHaveTagRule.Options options) {
        return new MustNotHaveTagRule(
            options
        );
    }
}
