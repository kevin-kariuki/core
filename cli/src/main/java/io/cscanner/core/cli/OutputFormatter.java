package io.cscanner.core.cli;

import io.cscanner.core.test.engine.RuleResult;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface OutputFormatter {
    String getType();
    String format(List<RuleResult> results);
}
