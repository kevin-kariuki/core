---
title: Amazon Web Services
---

# Amazon Web Services

[Amazon Web Services](https://aws.amazon.com), or AWS, is a popular cloud provider and is supported by cscanner.
The configuration does as follows:

```yaml
connection-name:
  type: aws
  accessKeyId: ""
  secretAccessKey: ""
  sessionToken: ""
  profile: ""
``` 

Each of the parameters is optional. If they are not provided, the default is to fall back to the AWS client default
behavior, such as reading options from environment variables or credentials files.

## What access does Cscanner need?

Cscanner uses the AWS API to scan the account for policy violation. As such, each rule will require its own set of
permissions. The easiest way to make sure no rules are skipped due to missing permissions is to use the `ReadOnlyAccess`
policy for the account that is being used for cscanner.

## Creating credentials

To create API credentials on AWS please follow the following steps:

1. Log in to the [AWS console](https://console.aws.amazon.com)
2. Go to [Identity and Access Management](https://console.aws.amazon.com/iam/home?#/home)
3. To to [Users](https://console.aws.amazon.com/iam/home?#/users)
4. Click [Add User](https://console.aws.amazon.com/iam/home?#/users$new?step=details)
5. Create a user with *"Programmatic Access"* enabled
6. In Permissions select *"Attach existing policies directly"*
7. Select the `ReadOnlyAccess` policy
8. Copy the access key and secret key into your configuration as mentioned above. 

## Rules

The AWS provider currently supports the following rules:

- [Firewall](../rules/firewall.md)
- [Object Storage](../rules/objectstorage.md)