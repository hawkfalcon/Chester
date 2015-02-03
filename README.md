Chester
=================

Chester... who else?
=================


Chester is an IRC chat bot. It uses the jMegaHal library to generate responses based on everything users have said previously. 
This library uses a 4th order Markov chain to make almost-human sentences.

Compiling
=================

This project uses gradle, so all you need to compile to a jar is java.
Clone this project, change to this directory.
To compile, run the following command:


```bash
./gradlew shadowJar
```

This automatically downloads all necessary dependencies and builds a jar.
 
 
Running
=================

```bash
java -jar Chester.jar
```

Todo
=================
- Use a config
- Put jMegaHal on a repo