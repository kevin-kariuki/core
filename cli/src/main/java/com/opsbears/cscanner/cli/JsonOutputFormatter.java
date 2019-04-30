package com.opsbears.cscanner.cli;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.opsbears.cscanner.core.RuleResult;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class JsonOutputFormatter implements OutputFormatter {
    @Override
    public String getType() {
        return "json";
    }

    @Override
    public String format(List<RuleResult> results) {
        JsonArray jsonArray = new JsonArray();
        for (RuleResult result : results) {
            JsonObject resultObject = new JsonObject();
            resultObject.addProperty("connectionName", result.connectionName);
            resultObject.addProperty("resourceType", result.resourceType);
            resultObject.addProperty("resourceRegion", result.resourceRegion);
            resultObject.addProperty("resourceName", result.resourceName);
            resultObject.addProperty("ruleType", result.ruleType);
            resultObject.addProperty("compliancy", result.compliancy.toString());


            JsonArray violations = new JsonArray();
            if (result.compliancy == RuleResult.Compliancy.NONCOMPLIANT) {
                for (RuleResult.Violation violation : result.violations) {
                    JsonObject violationObject = new JsonObject();
                    violationObject.addProperty("subresource", violation.subresource);
                    violationObject.addProperty("description", violation.description);
                    violations.add(violationObject);
                }
            }
            resultObject.add("violations", violations);
            jsonArray.add(resultObject);
        }
        return jsonArray.toString();
    }
}
