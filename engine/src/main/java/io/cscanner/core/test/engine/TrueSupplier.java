package io.cscanner.core.test.engine;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TrueSupplier extends AbstractSupplier {
    @Override
    public Object get() {
        return true;
    }
}
