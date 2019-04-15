---
title: Configuration
---

# Configuration

The cloud scanner is configured using YAML files. The yaml files have two sections: `connections` and `rules`. The
layout looks roughly like this:

```yaml
---
connections:
  # Cloud provider connections go here
rules:
  # Compliance rules go here
```

In `connections` you configure your various cloud accounts, such as this:

```yaml
exoscale-test:
  type: exoscale
  key: ""
  secret: ""
```

The respective options for each cloud provider are documented in their documentation. The connection name can then 
referenced with the rules.

The next section is the `rules` section, which you can specify your rules in:

```yaml
- type: FIREWALL_PUBLIC_SERVICE_PROHIBITED
  protocol: tcp
  ports:
    - 22
```

Each rule has two universal parameters: `type` to specify the rule type and `connections` which you can use to
limit the rule to only certain connections. The default is to use all connections.

If a certain cloud provider doesn't support a specific functionality, that cloud provider will be simply skipped for
the specified rule.

## Includes {{ since("0.0.5") }}

The configuration supports includes. These includes can either be local files, or http/https URLs. Include files can
include other files.

For connections they work as follows:

```yaml
---
connections:
  include:
    - config-file-1.yaml
    - https://example.com/config-file-2.yaml
  exoscale-test:
    type: exoscale
    key: ""
    secret: ""
rules:
  # ...
```

In other words you have one include key and you need to list all files you want to load.

For rules it works a little different:

```yaml
connections:
  # ...
rules:
  - include: config-file-1.yaml
  - include: https://example.com/config-file-2.yaml
  - type: FIREWALL_PUBLIC_SERVICE_PROHIBITED
    protocol: tcp
    ports:
      - 22
```

Note that both for connections and rules the target config file only needs to have a list of connections / rules, NOT 
the `connections` or `rules` key.

!!! note
    At this time there is no way to restrict included rules to a certain resource name as the resource filtering is done
    on a per-rule basis.
