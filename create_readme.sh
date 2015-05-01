#!/bin/bash
set -e
echo '```java' > README.md
sed -e '1,/README_TEXT/d' -e '/README_TEXT/,$d' src/test/java/com/oneeyedmen/fakir/ExampleTest.java >> README.md
echo '```' >> README.md
