---
title: Exoscale
---

# Exoscale

[Exoscale](https://exoscale.com) is a European/Swiss IaaS cloud provider and is supported by cscanner. Under the hood
it implements a CloudStack-like API.

The configuration goes like this:

```yaml
  exoscale-test:
    type: exoscale
    key: ""
    secret: ""
```

Note, that `cloudstack.ini` is currently not supported.

## Creating credentials

Exoscale does not support non-personalized users, therefore it is recommended to create a pseudo-user, such as 
`cscanner@example.com` and use the API keys for Cscanner. This only works if you selected "For Teams" when
creating the account.

To invite a user on Exoscale go through the following steps:

1. In the top right corner select the correct organization.
2. Go to Account &rarr; Users and go through the invitation process

To get the API key for that account, go through the following steps:

1. Log in to your pseudo-account
2. Go to Account &rarr; Profile &rarr; API keys
3. Copy the API key and secret into the configuration as indicated above.

## Rules

The Exoscale provider supports the following rule sets:

- [Firewall](../rules/firewall.md)
- [Object Storage](../rules/objectstorage.md)
