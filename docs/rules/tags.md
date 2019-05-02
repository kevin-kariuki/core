---
title: Tag rules
---

# Tag rules

These rules enforce certain tagging requirements on cloud providers that support key-value tags.
All rules come with the same set of standard configuration options:

- `onlyTypes`: a list of resource types to consider. These can be `VM`, `BUCKET` and `SECURITY_GROUP`. If empty, all 
  taggable resources are taken into account.
- `include`: a list of patterns which resources to include. If left empty all resources are considered.
- `exclude`:a list of patterns which resources to exclude. Exclude takes precedence over include.

## Resource must have tags

To apply this rule use `type: MUST_HAVE_TAG`. Additionally, you will have to provide the `tagName` option which tag
you want to enforce.