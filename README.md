[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)][apache2.0]
[![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=flat&logo=kotlin&logoColor=white)][KotlinJS] 
[![SASS](https://img.shields.io/badge/SASS-hotpink.svg?style=flat&logo=SASS&logoColor=white)][sass]
[![GitHub release (with filter)](https://img.shields.io/github/v/release/tuanchauict/monosketch)](https://github.com/tuanchauict/MonoSketch/releases)
[![Twitter Follow](https://img.shields.io/twitter/follow/MonoSketchApp)](https://twitter.com/MonoSketchApp)



# What is it?

Mono Sketch is a client-side-only web-based sketch tool for drawing *ASCII diagrams*. You can use
the app at [app.monosketch.io][app].

```
        +10-15V                0,047R                                        
       ●─────────○───────○─░░░░░─○─○─────────○────○─────╮                    
    +  │         │       │       │ │         │    │     │                    
    ─═════─      │       │       │ │         │    │     │                    
    ─═════─    ──┼──     │       │╭┴╮        │    │     │                    
    ─═════─     ─┼─      │       ││ │ 2k2    │    │     │                    
    -  │      470│ +     │       ││ │        │    │     │                    
       │       uF│       ╰──╮    │╰┬╯       ╭┴╮   │     │                    
       └─────────│          │    │ │     1k │ │   │     ▽ LED                
                 │         6│   7│ │8       │ │   │     ┬                    
              ───┴───    ╭──┴────┴─┴─╮      ╰┬╯   │     │                    
               ─═══─     │           │1      │  │ / BC  │                    
                 ─       │           ├───────○──┤/  547 │                    
                GND      │           │       │  │ ▶     │                    
                         │           │      ╭┴╮   │     │                    
               ╭─────────┤           │  220R│ │   ○───┤├┘  IRF9Z34           
               │         │           │      │ │   │   │├─▶                   
               │         │  MC34063  │      ╰┬╯   │   │├─┐ BYV29       -12V6 
               │         │           │       │    │      ○──┤◀─○────○───X OUT
             - │ +       │           │2      ╰────╯      │     │    │        
6000 micro ────┴────     │           ├──○                C│    │   ─── 470   
Farad, 40V ─ ─ ┬ ─ ─     │           │ GND               C│    │   ███  uF   
Capacitor      │         │           │3                  C│    │    │\       
               │         │           ├────────┤├╮        │     │   GND       
               │         ╰─────┬───┬─╯          │       GND    │             
               │              5│  4│            │              │             
               │               │   ╰────────────○──────────────│             
               │               │                               │             
               ╰───────────────●─────/\/\/─────────○────░░░░░──╯             
                                     2k            │         1k0             
                                                  ╭┴╮                        
                                                  │ │5k6   3k3               
                                                  │ │in Serie                
                                                  ╰┬╯                        
                                                   │                         
                                                  GND                        
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
- Rounded corner

Editing:

- Infinity scroll, no limitation for 4 directions
- Autosave
- Multiple projects
- Copy / Cut / Paste / Duplicate
- Move and change shapes' order
- Dark mode
- Line snapping: connect a line to a shape

Exporting:

- Export selected shapes
- Copy as text (`cmd + shift + C` or `ctrl + shift + C`)

## Future features

### Grouping

> Group is added as a kind of shape but until now, there are no features that are applying Group
> except for rendering. Besides, the shape tool does not work with groups or multiple selected
> shapes. This project aims to make the tool able to work with Group and also add a Shape tree on
> the left of the tool.

### Paint tool

> Currently, Mono Sketch provides only three tools: Rectangle, Text, and Line. One tool that is also
> used frequently when drawing with ASCII is paint - draw with a specific character. This project
> also aims to provide richer options of Fill, Border, Line Start/End head

### Sharing

> Allow opening files from a url, share to gist, etc.

# Contributing

This project is fully written with [KotlinJS] and SASS
for CSS. There is no environment setup required except for Java.

To run debug:

```bash
./gradlew browserDevelopmentRun --continuous -Dorg.gradle.parallel=false
```

Or with production configuration

```bash
./gradlew browserProductionRun --continuous -Dorg.gradle.parallel=false
```

* `-Dorg.gradle.parallel=false` is a workaround for a bug on KotlinJS build with `--continuous`.

**Run with Python**

This is an alternative to `browserDevelopmentRun` for running the app for development (sometimes,
the Gradle does not reload when the code is updated).

Requirements: [Pipenv].

```bash
pipenv install
pipenv run dev
```

[apache2.0]: https://opensource.org/licenses/Apache-2.0

[app]: https://app.monosketch.io/

[KotlinJS]: https://kotlinlang.org/docs/js-overview.html

[Pipenv]: https://pipenv.pypa.io/en/latest/

[sass]: https://sass-lang.com/
