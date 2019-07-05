package io.cscanner.core.test.engine;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@ParametersAreNonnullByDefault
public class EmptyListSupplier extends AbstractSupplier {
    @Override
    public Object get() {
        return new ArrayList<>();
    }
}
