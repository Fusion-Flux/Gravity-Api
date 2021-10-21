# GravityChanger
A fabric mod and api that lets you change player gravity direction.

## Features
This mod adds 6 items that let you change your gravity to any of the 6 axis directions.  
These items are currently uncraftable and can be found in the tools tab of creative menu.  
This mod can also be used as an api to let other mods change player gravity.

## Required Dependencies
[Fabric Api]()  
[Cloth Config]()

## Optional Dependencies
[Mod Menu]()

## Importing
First clone this repo and run `gradlew build`.  
Copy the resulting dev jar from `build/libs/` into `libs/` inside your project.  
Then add the following to your project:
#### grable.properties
```properties
gravitychanger_version = 0.0.1-1.17.1
```
Replace 0.0.1-1.17.1 with the version of the jar you have in `libs/`.
#### build.gradle
```gradle
repositories {
    flatDir {
        dirs "libs"
    }
}

dependencies {
    modImplementation ":GravityChanger-${project.gravitychanger_version}-dev"
}
```
#### fabric.mod.json
```json
"depends": {
    "gravitychanger": "^0.0.1"
}
```
Replace 0.0.1 with the lowest version you need

Now you should be able to cast a `PlayerEntity` instance into `RotatableEntityAccessor` and use it's methods `gravitychanger$getGravityDirection` and `gravitychanger$setGravityDirection` to get and set gravity for that player.
