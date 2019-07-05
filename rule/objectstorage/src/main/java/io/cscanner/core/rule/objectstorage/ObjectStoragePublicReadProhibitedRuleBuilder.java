package io.cscanner.core.rule.objectstorage;

import io.cscanner.core.engine.RuleBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
public class ObjectStoragePublicReadProhibitedRuleBuilder implements RuleBuilder<ObjectStoragePublicReadProhibitedRule, ObjectStorageConnection, ObjectStoragePublicReadProhibitedRule.Options> {
    @Override
    public String getType() {
        return ObjectStoragePublicReadProhibitedRule.RULE;
    }

    @Override
    public List<String> getTypeAliases() {
        return Collections.singletonList(
                ObjectStoragePublicReadProhibitedRule.LEGACY_RULE
        );
    }

    @Override
    public Class<ObjectStorageConnection> getConnectionType() {
        return ObjectStorageConnection.class;
    }

    @Override
    public Class<ObjectStoragePublicReadProhibitedRule.Options> getConfigurationType() {
        return ObjectStoragePublicReadProhibitedRule.Options.class;
    }

    @Override
    public ObjectStoragePublicReadProhibitedRule create(ObjectStoragePublicReadProhibitedRule.Options options) {
        return new ObjectStoragePublicReadProhibitedRule(
            options
        );
    }
}
