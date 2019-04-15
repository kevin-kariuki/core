---
title: Installation
---

# Installation

In order to run cscanner you will need at least Java 8 (OpenJDK will work). Simply [download the JAR file](downloads.md)
and run it:

```
java -jar cscanner.jar
```

That's it!

## For development

For development purposes you will need [Maven](https://maven.org) as a package manager. It is also strongly recommended
to have a modern IDE to help you with code completion.

To build the documentation you will need mkdocs 1.0+ and install the Python packages in
`requirements.txt`. This is best done with pip: `pip install -r requirements.txt`. You can then run
`mkdocs serve` to start up a development webserver.

For details please see [the development section](development/index.md).
