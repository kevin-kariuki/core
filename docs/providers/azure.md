---
title: Azure
---

# Azure {{ since("0.0.8") }}

[Azure](https://azure.com) is a cloud service operated by Microsoft. The configuration looks as follows:

```yaml
connection-name:
  type: azure
     type: azure
     appId: ""
     subscriptionId: ""
     tenantId: ""
     key: ""
```

Alternatively certificates can also be used:

```yaml
connection-name:
  type: azure
     type: azure
     appId: ""
     subscriptionId: ""
     tenantId: ""
     certificate: ""
     certificatePassword: ""
```


## Creating credentials

In order to use cscanner with Azure you will need to create a Service Principal. The easiest way to do this is using the
[Azure CLI 2.0](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli?view=azure-cli-latest).

```bash
az ad sp create-for-rbac --name USER-NAME-HERE 
az role assignment create --assignee APP-ID-HERE --role Reader
```

## Rules

The Azure provider currently supports the following rules:

- [Firewall](../rules/firewall.md)

## Firewall quirks

Azure has a much more sophisticated firewall configuration than other cloud providers allowing for filtering,
for example, for both source and destination IP addresses, etc. While unlikely, it is possible that this may cause
some false positives. If you encounter any [please report an issue](https://github.com/janoszen/cscanner/issues/new) 