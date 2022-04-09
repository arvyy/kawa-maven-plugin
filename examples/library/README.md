Run `mvn kawa:repl` to start repl. Enter into repl
```
(import (library normal-distribution))
(cumulative-normal-probability 0 1 2)
```
And you should see numeric output of ~0.977

Run `mvn kawa:test` to run the tests.

Run `mvn install` to install the library in local repository, and make it available to be used by other projects (`../application`)