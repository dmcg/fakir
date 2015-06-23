#!/bin/bash
set -e

function write_file_contents {
    echo '```java'
    sed -e '1,/README_TEXT/d' -e '/README_TEXT/,$d' $1
    echo '```'
}

echo "
Fakir - The Ascetic Wonder-Worker
=================================

Fake difficult-to-build objects with default property values and custom overrides.

[ExampleTest](src/test/java/com/oneeyedmen/okeydoke/examples/ApprovalsRuleTest.java)
"  > README.md

write_file_contents src/test/java/com/oneeyedmen/fakir/ExampleTest.java >> README.md

echo "
Fakir builds and runs under JDK6, but now has some [Java8'y goodness](java8/src/test/java/com/oneeyedmen/fakir/java8/Java8ExampleTest.java)
for easy testing of legacy code.
"  >> README.md

write_file_contents java8/src/test/java/com/oneeyedmen/fakir/java8/Java8ExampleTest.java >> README.md

