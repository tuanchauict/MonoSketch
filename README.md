# What is it?

Mono Sketch is a client-side only web based sketch tool for drawing *ASCII diagrams*. You can use
the app at [app.monosketch.io][app].

```
             ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─
              Edge Region 1                                           │
             │
                   ┌──────────────┐   send msg to ┌──────────────┐    │
 /\_/\       │     │┌─────────────┴┐   websocket  │┌─────────────┴┐
( o.o ) ◀══════════▶┤┌─────────────┴┐◀═══════════▶└┤┌─────────────┴┐  │
 > ^ <       │      └┤    Envoy     │ sub to this  └┤Gateway server│
                     └──────────────┘   channel     └───────▲──────┘  │
             │              ▲                               │
              ─ ─ ─ ─ ─ ─ ─ ║ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ │ ─ ─ ─ ─ ┘
 /\_/\                      ║                               │
( o.o ) ◀═══════════════════╝                               │ send msg to all GS subs
 > ^ <                                                      └───────────────────────┐
   │     ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─
   │      Main region                                                               │     │
   │     │                                                                          │
   │         ┌──────────────┐               ┌──────────────┐            ┌───────────┼──┐  │
   │     │   │┌─────────────┴┐     send     │┌─────────────┴┐           │┌──────────┼──┴┐
   └───── ──▶└┤┌─────────────┴┐ channel msg └┤┌─────────────┴┐          └┤┌─────────┴───┴┐│
         │    └┤    Webapp    ├───────────────▶ Admin Server ├───────────▶┤Channel Server│
               └───────┬──────┘               └──────────────┘  route to  └──────────────┘│
         │             │ store                                  channel
                       ▼ message                                server                    │
         │         ░░░░░░░░
                   ░Vitess░                                                               │
         │         ░░░░░░░░
                                                                                          │
         └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─
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

Editing:

- Infinity scroll, no limitation for 4 directions
- Auto save
- Multiple projects
- Copy / Cut / Paste / Duplicate
- Move and change shapes' order
- Dark mode

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

> Currently, Mono sketch provides only three tools: Rectangle, Text, Line. One tool which is also
> used frequently when drawing with ASCII is paint - draw with a specific character. This project
> also aims for providing richer options of Fill, Border, Line Start/End head

### Line snapping

> Make Line's heads able to snap to a shape and position is updated along with the shape

### Sharing
> Allow opening file from a url, share to gist, etc.

# Contributing

This project is fully written with [KotlinJS] and SASS
for CSS. There is no environment setup requires except for Java.

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

This is an alternative to `browserDevelopmentRun` for running the app for development (sometimes, the gradle does not reload when the code is updated).

Requirements: [Pipenv].
```bash
pipenv install
pipenv run dev
```
[app]: https://app.monosketch.io/
[KotlinJS]: https://kotlinlang.org/docs/js-overview.html
[Pipenv]: https://pipenv.pypa.io/en/latest/
