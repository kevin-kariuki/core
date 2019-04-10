---
title: Object Storage rules
---

# Object Storage Rules

This section details the rules relevant to object storages, such as Amazon S3.

## Do not permit an object storage bucket to be public

This rule checks object storage bucket ACLs so that they are not public. The configuration goes like this:

```yaml
- type: S3_PUBLIC_READ_PROHIBITED
  include:
    - some.*regexp
  exclude:
    - some.*regexp  
```


### include

This option accepts a regular expression to match against the bucket name. If include is specified, only
the security groups with the specified name will be considered.

### exclude

This option lets you exclude certain buckets from checking, similar to include above. The
exclude rule takes precedence over include.

### scanContents

This is a true/false flag indicating if the contents of the buckets should be scanned for ACL violations.
If true all files in the bucket will be checked, which may take a very long time. If false only the bucket ACL will
be evaluated.
