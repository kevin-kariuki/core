package io.cscanner.core.engine;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FalseSupplier extends AbstractSupplier {
    @Override
    public Object get() {
        return false;
    }
}
