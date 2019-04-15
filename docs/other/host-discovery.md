---
title: Host discovery
---

# Host discovery {{ since("0.0.7") }}

CScanner includes a host discovery component that lists all IP addresses in all cloud accounts. This is used internally,
but can also be useful to feed other scanner tools such as [OpenVAS](http://openvas.org/).

To stop cscanner at the host discovery phase, simply use this command line option:

```
java -jar cscanner.jar --list-ips config-file-name.yaml
```

This will list all IP addresses and their associated hosts.