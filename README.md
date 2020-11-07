# Kawa maven plugin

## About

A simple maven plugin for working with kawa, intended to give similar workflow as when working with java.
Currently intermixing java in same project is a non-goal for this plugin.

For example use of this plugin, see https://github.com/arvyy/kawa-maven-plugin-example/

## Kawa runtime

Note, that this plugin expects kawa runtime available on classpath, and it doesn't bring it in by itself.
One way to go about it, is to include kawa redistribution dependency (as is done in example project) from central

```
<dependency>
    <groupId>com.github.arvyy</groupId>
    <artifactId>kawa</artifactId>
    <version>3.1.1</version>
</dependency>
```

Alternatively, you can install any kawa.jar locally (with your own thought up groupId and artifactId), which you'd then add as dependency. See https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html for details.

## Available maven goals

Plugin exposes following goals. Since plugin follows standard maven naming convention, you can invoke using `mvn kawa:<goal>`, for example `mvn kawa:compile`. The word parameter refers to maven parameter concept; see section "Parameters" on how to change them.

* `compile` -- compiles kawa sources into classes. Kawa sources are looked at directory defined by parameter `schemeRoot`, which defaults to /src/main/scheme. First, it checks whether or not a main file is present (defined by parameter `schemeMain`, defaults to `main.scm`). If yes, only it is compiled, and the other files it depends on are pulled transitively. If main file is not present, next it checks for `schemeCompileTargets` parameter. If the parameter (a list of files, relative to schemeRoot directory) is defined, those are compiled. If it's not defined, all .sld files under schemeRoot are compiled. During compilation phase, compile-time maven dependencies are included (which can be both java projects, and kawa projects made with this very plugin). This goal should be attached to maven's compile phase (see "Recommended configuration").

* `test` -- runs the root test file defined by parameter schemeTestMain (defaults to main-test.scm) in test root directory defined by parameter schemeTestRoot (defaults to /src/test/scheme). Kawa sources are looked at compile-time directory (see `compile` target), as well in schemeTestRoot. At this stage, maven compilation doesn't fail on test failures alone -- it's imperative the scheme test code exists with status that signals failure (ie `(exit #f)`). See example project for how to setup a testrunner that invokes `(exit #f)` at the end of test suite if there were failures. This goal should be attached to maven's test phase (see "Recommended configuration").

* `repl` -- runs REPL. Includes maven dependencies on path, as well as kawa source paths defined in `compile` target. The goal doesn't invoke compilation; if running repl you're unable to import libraries from this project, run a compile goal first.

* `run` -- runs main file defined by parameter `schemeMain` (defaults to `main.scm`). Includes maven dependencies on path, as well as kawa source paths defined in `compile` target. Note, to pass command line parameters, use `schemeMainArgs` parameter

## Parameters

See https://maven.apache.org/guides/mini/guide-configuring-plugins.html for details. In short, to override parameter, either add -D option on command line (for example, `mvn kawa:compile -DschemeMain=main2.scm`), or add it to configuration block under plugin, for example

```
<plugin>
    <groupId>com.github.arvyy</groupId>
    <artifactId>kawa-maven-plugin</artifactId>
    <configuration>
        <schemeMain>main2.scm</schemeMain>
    </configuration>
</plugin>
```

Complete list of available parameters is as follows:

* `schemeRoot` -- root folder for scheme files. Defaults to `./src/main/scheme/`
* `schemeTestRoot` -- root folder for scheme test files. Defaults to `./src/test/scheme/`
* `schemeMain` -- main file, relative from schemeRoot, for running and compiling to fatjar. Defaults to `main.scm`
* `schemeTestMain` -- main file, relative from schemeTestRoot, for running unit test. Defaults to `main-test.scm`
* `schemeMainArgs` -- list of arguments to pass to main, when running with `run` goal
* `schemeCompileTargets` -- list of files, relative to scheme root, to compile

Following warning parameters are transparantly passed to kawa; for details see kawa manual. Default values of each of those are set to `true`

* `warnUndefinedVariable`
* `warnUnknownMember`
* `warnInvokeUnknownMethod`
* `warnUnused`
* `warnUninitialized`
* `warnUnreachable`
* `warnVoidUsed`
* `warnAsError`

## Recommended configuration

It's recommended to attach `kawa:test` goal to maven's `test` phase, and `kawa:compile` goal to maven's `compile` phase.
This way, when invoking higher level maven phases (for example `mvn package`), all appropriate kawa plugin goals will be executed as well. As such, configuration should look like this:

```
<plugin>
    <groupId>com.github.arvyy</groupId>
    <artifactId>kawa-maven-plugin</artifactId>
    <version>0.0.5</version>
    <executions>
        <execution>
            <id>test</id>
            <goals>
                <goal>test</goal>
            </goals>
        </execution>
        <execution>
            <id>compile</id>
            <goals>
                <goal>compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Additionally, *if* you're writting a runnable application (as opposed to library), you'll probably want to distribute it as just a single jar file. In such case, include a shading plugin, which will make sure to include kawa runtime (as well as any other dependencies you have in your pom.xml) and also mark `main` as the entry point.

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.2.4</version>
    <configuration>
        <transformers>
            <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                <mainClass>main</mainClass>
            </transformer>
        </transformers>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
