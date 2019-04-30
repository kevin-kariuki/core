package com.opsbears.cscanner.tags;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public interface TaggedResourceClient {
    Stream<TaggedResource> getTaggedResources();
}
