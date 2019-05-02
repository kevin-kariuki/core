package com.opsbears.cscanner.tags;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
public interface TaggedResourceTestClient {
    String ensureTaggedResourceExists(Map<String,String> tags);
    void ensureTaggedResourceAbsent(String resourceId);
}
