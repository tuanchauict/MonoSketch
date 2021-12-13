# README.md

# What is it?

Mono Sketch is a client-side only web based sketch tool for drawing *ASCII diagrams*. You can use the app at [app.monosketch.io](https://app.monosketch.io/). 

```bash
Upgrade app:                        Event                     Event       
Schedule resource                   start                     stop        
downloading worker                  time                      time        
      │                               │                        │          
      │              before event     │                        │          
   ●──┴─────────░░░░░░░░░░░░░░░░░░░░░░████████████████○████████┴───┬─────▶
                     ■────────────────────■           │            │      
                  Download           Resources is     │            │      
                  resource           downloaded       │            │      
                  Retry if           succeeded        │            │      
                  failed                          1st open      Delete    
                                                               resources
```

# Features

## Supporting features

Draw tools:

- Rectangle
- Text
- Line

Shape formats:

- Fill
- Border
- Line start/end heads

Editting:

- Infinity scroll, no limitation for 4 directions
- Auto save
- Copy / Cut / Paste / Duplicate
- Move and change shapes' order

Exporting:

- Export selected shapes
- Copy as text (`cmd + shift + C` or `ctrl + shift + C`)

## Future features

### Version 1.1 - Grouping

> Group is added as a kind of shape but until now, there are no features that are applying Group except for rendering. Besides, the shape tool does not work with groups or multiple selected shapes. 
This project aims to make the tool able to work with Group and also add a Shape tree on the left of the tool.
> 

### Version 1.2 - Paint tool

> Currently, Mono sketch provides only three tools: Rectangle, Text, Line. One tool which is also used frequently when drawing with ASCII is paint - draw with a specific character.
This project also aims for providing richer options of Fill, Border, Line Start/End head
> 

### Version 1.3 - Line snapping

> Make Line's heads able to snap to a shape and position is updated along with the shape
> 

# Contributing

This project is fully written with [KotlinJS](https://kotlinlang.org/docs/js-overview.html) and SASS for CSS. There is no environment setup requires except for Java.

To run debug:

```bash
./gradlew browserDevelopmentRun --continuous -Dorg.gradle.parallel=false
```

Or with production configuration

```bash
./gradlew browserProductionRun --continuous -Dorg.gradle.parallel=false
```

* `-Dorg.gradle.parallel=false` is a workaround for a bug on KotlinJS build with `--continuous`.

There is no needs to setting up the environment except for Java.

## Note

An inconvenience requirement for SASS: I haven't configured the Gradle for generating CSS from SASS, therefore, if you update the style, please compile the SASS yourself with

```
sass src/main/resources/main.sass src/main/resources/main.css
```
