package io.cscanner.core.engine;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class NullSupplier extends AbstractSupplier {
    @Override
    public Object get() {
        return null;
    }
}
