package io.cscanner.core.test.engine;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FalseSupplier extends AbstractSupplier {
    @Override
    public Object get() {
        return false;
    }
}
