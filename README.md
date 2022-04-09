# Kawa maven plugin

## About

A maven plugin for working with kawa, the scheme implementation on jvm. 

## Kawa runtime

This plugin expects kawa runtime available on classpath, and it (intentionally) doesn't bring it in by itself.
Kawa is redistributed on central through

```
<dependency>
    <groupId>com.github.arvyy</groupId>
    <artifactId>kawa</artifactId>
    <version>3.1.1</version>
</dependency>
```

Alternatively, install any kawa.jar locally (with your own thought up groupId and artifactId),
and add that as a dependency instead. See https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html for details.

## Maven goals

Plugin exposes following goals. Since plugin follows standard maven naming convention, 
you can invoke using them `mvn kawa:<goal>`, for example `mvn kawa:compile`. 

You'll notice process builder parameter mentioned below -- this is a plugin parameter, a list of strings, which forms full command line
used to launch appropriate feature. When running, simple textual substitutions are performed:

`@KAWAIMPORT` - gets replaced by directory, containing extraced library code. Meant to be used with kawa's `-Dkawa.import.path`.
`@SEPARATOR` - gets replaced by path separator character; `;` on windows, `:` on unix.
`@PROJECTROOT` - absolute path of maven project. 

As an example, default compile behavior is equivalent to following explicit configuration

```
<groupId>com.github.arvyy</groupId>
<artifactId>kawa-maven-plugin</artifactId>
<version>VERSION</version>
<configuration>
    <compile-command>
        <str>java</str>
        <str>-Dkawa.import.path=@KAWAIMPORT@SEPARATOR@PROJECTROOT/src/main/scheme</str>
        <str>kawa.repl</str>
        <str>-d</str>
        <str>@PROJECTROOT/target/classes</str>
        <str>--main</str>
        <str>-C</str>
        <str>@PROJECTROOT/src/main/scheme/main.scm</str>
    </compile-command>
</configuration>
```

### `compile`

Compiles the project, more specifically src/main/scheme/main.scm, with all other modules being transitively included as imported from main. 

Process builder parameter: `compile-command`.

### `test`

Runs tests - it expects to find a module `(main-test)` at src/test/scheme/main-test.scm, which must export a no arg method `run-tests`. Such concession is unfortunately
needed to properly signal exit code to maven on test failures, so it can cancel the build.

Process builder parameter: `test-command`.
Skip tests parameter: `skipTests`. Most often useful to be setup from command line.

### `repl`

Starts repl, including src/main/scheme as root path, as well as including all maven dependencies.

Process builder parameter: `repl-command`.

### `run`

Runs src/main/scheme/main.scm, without compiling.

Process builder parameter: `run-command`.

## Library and custom packaging

You might have noticed, above steps mostly concern main program. What about libraries? 
The issue with libraries, is that they cannot be precompiled. If they are, they're only binary compatible
with other compilation units of same parameterization. The solution to this is using custom packaging, and managing them in source form.
If you create a project to be used as a kawa library, define `<packaging>` as `kawalib`, which won't compile, but instead zip
source code. When compiling ultimately main application, this plugin finds all dependencies with `kawalib` packaging,
extracts them, and includes in kawa import path. Note, that for this to work, the plugin declaration 
must define `<extensions>true</extensions>`.

## Examples

See `./examples` subdirectory for some minimal sample projects, which you can use as scaffolding to start your own.