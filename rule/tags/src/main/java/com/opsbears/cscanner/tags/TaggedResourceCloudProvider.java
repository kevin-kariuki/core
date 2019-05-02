package com.opsbears.cscanner.tags;

import com.opsbears.cscanner.core.CloudProvider;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface TaggedResourceCloudProvider<CONFIGURATIONTYPE, CONNECTIONTYPE extends TaggedResourceConnection> extends CloudProvider<CONFIGURATIONTYPE, CONNECTIONTYPE> {
}
