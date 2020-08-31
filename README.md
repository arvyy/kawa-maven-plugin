# Kawa maven plugin

## About

A simple maven plugin for working with kawa scheme, intended to give similar workflow as when working with java.
Currently intermixing java in same project is a non-goal for this plugin. The plugin expects kawa jar file to be included as a dependency, which you can do using `install:install-file` maven command.

## Available targets

* `compile` -- compiles kawa sources into classes. Kawa sources are looked at directory defined by parameter `schemeRoot`, which defaults to /src/main/scheme. It checks whether or not a main file is present (defined by parameter schemeMain, defaults to `main.scm`). If yes, only it is compiled, and the other files it depends on are pulled transitively. If it's not present, all .scm files under schemeRoot are compiled. During compilation phase, compile-time maven dependencies are included (which can be both java projects, and kawa projects made with this very plugin). Recommended to attach to maven's compile phase.

* `test` -- runs the root test file defined by parameter schemeTestMain (defaults to test-main.scm) in test root directory defined by parameter schemeTestRoot (defaults to /src/test/scheme). Kawa sources are looked at compile-time directory (see `compile` target), as well in schemeTestRoot. At this stage, maven compilation doesn't fail on test failures alone -- it's imperative the scheme test code exists with status that signals failure (ie `(exit #f)`). This is why currently there is only one file that is being run. For splitting testing code into multiple files, it's recommended to use `include`. Recommended to attach to maven's test phase.

* `repl` -- runs REPL. Includes maven dependencies on path, as well as kawa source paths defined in `compile` target.

* `run` -- runs main file defined by parameter schemeMain (defaults to main.scm). Includes maven dependencies on path, as well as kawa source paths defined in `compile` target.
