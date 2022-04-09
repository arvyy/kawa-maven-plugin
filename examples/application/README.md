First make sure to run `mvn install` in `../library` directory.

Run `mvn kawa:run` to start. You should see a short a CLI application, asking for input for 3 numbers, and returning a number before exiting.

Run `mvn kawa:test` to run the tests.

Run `mvn package` to compile the application into a jar file. The jar will be output to `./target` directory.
Run `cd target; java -jar application-0.0.1.jar` to run the application.