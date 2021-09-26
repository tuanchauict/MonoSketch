# README.md

Mono Sketch is a client-side only web based sketch tool for drawing ASCII diagrams. You can use the app at [app.monosketch.io](https://app.monosketch.io/).

```
Upgrade app:                        Event                     Event       
Schedule resource                   start                     stop        
downloading worker                  time                      time        
      │                               │                        │          
      │              before event     │                        │          
   ●──┴─────────░░░░░░░░░░░░░░░░░░░░░░████████████████○████████┴───┬─────▶
                     ■────────────────────■           │            │      
                  Download           Resources is     │            │      
                  resource           downloaded       │            │      
                  Retry if           successfully     │            │      
                  failed                          1st open      Delete    
                                                               resources
```

# Contributing

Although this is a web based application, it is written fully with [KotlinJS](https://kotlinlang.org/docs/js-overview.html). The project is compiled with Gradle.

To run debugging:

```bash
./gradlew browserDevelopmentRun --continuous -Dorg.gradle.parallel=false
```

Or with production configuration:

```bash
./gradlew browserProductionRun --continuous -Dorg.gradle.parallel=false
```

(Note that `-Dorg.gradle.parallel=false` is a workaround for a bug on KotlinJS build with `--continuous`.)

To generate deployable bundle:
```bash
./gradlew assemble
```

There is no needs to setting up the environment except for Java.

### Note:
This project uses [SASS](https://sass-lang.com/) for CSS, however, I haven't configed the gradle for compiling SASS, therefore, you need to compile yourself if you change the style.
```bash
sass src/main/resources/main.sass src/main/resources/main.css
```
