# MeteorLite ![](https://i.imgur.com/Y6ghBw3.png) 

## openosrs-injector

MeteorLite injects directly into a deobfuscated client. This differs from the traditional
approach of obfuscated gamepack injection used by other 3rd party RS clients like OPRS and RuneLite.

The deobfuscated client is a transformed version of an official (obfuscated) gamepack downloaded from RS.
The deobfuscation process performs static analysis and subsequently applies a sequence of code transforms
to strip out dead or unnecessary code and renaming the Java class names, fields, and methods.

Reflection in rs-client has been modified to still support jagex reflection checks, you won't get input denied ever.
The various injectors/raws have all been fixed to accommodate the new injection target.

I maintained the RL API where I could. Due to injecting rs-api, there are a very small handful of name clashes after
injection. if an rs-api method is "missing", it likely has a `$api` suffex (i.e. `model.draw$api`)

## meteor-client

MeteorLite uses a mixture of Swing and [JavaFX](https://openjfx.io).

The UI is controlled via JavaFX and FXML/CSS and supports the eventbus/Guice injection.

The overlays are rendered via Swing but you can use JavaFX here if you want too.

UIs can be built rapidly using [SceneBuilder for JavaFX](https://www.oracle.com/java/technologies/javase/javafxscenebuilder-info.html)

It should be noted that the client is mostly setup for a high end PC.

The following defaults have been used:

```
GPU: enabled
drawDistance: 100
antiAliasing: 8x
filtering: 16x
Stretched: enabled
```

It maintains solid performance with these settings on my RTX 2060M


## Building
MeteorLite is built using JDK 16 and JavaFX. You will need to add JavaFX in your programfiles/java folder prior to building.

It is built using the gradle task: `clean build run`

## Credits  

OpenOSRS staff :  https://github.com/open-osrs/runelite  
RuneLite staff :  https://github.com/runelite/runelite  
RuneScape staff :  https://oldschool.runescape.com/  
Jfoenix :  https://github.com/sshahine/JFoenix  
