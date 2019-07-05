package io.cscanner.core.rule.firewall;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface FirewallClient {
    List<FirewallGroup> listFirewallGroups();
}
