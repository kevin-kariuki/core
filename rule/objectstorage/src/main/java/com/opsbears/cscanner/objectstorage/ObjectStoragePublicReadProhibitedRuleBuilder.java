package com.opsbears.cscanner.objectstorage;

import com.opsbears.cscanner.core.RuleBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
public class ObjectStoragePublicReadProhibitedRuleBuilder implements RuleBuilder<ObjectStoragePublicReadProhibitedRule, ObjectStorageConnection, ObjectStoragePublicReadProhibitedRule.Options> {
    @Override
    public String getType() {
        return ObjectStoragePublicReadProhibitedRule.RULE;
    }

    @Override
    public List<String> getTypeAliases() {
        return Arrays.asList(
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
