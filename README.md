# GravityChanger
A fabric mod and api that lets you change player gravity direction.

## Features
This mod adds 6 items that let you change your gravity to any of the 6 axis directions.  
These items are currently uncraftable and can be found in the tools tab of creative menu.  
This mod can also be used as an api to let other mods change player gravity.

## Required Dependencies
[Fabric Api](https://github.com/FabricMC/fabric)  
[Cloth Config](https://github.com/shedaniel/cloth-config)

## Optional Dependencies
[Mod Menu](https://github.com/TerraformersMC/ModMenu)

## Config
This mod has a config located in `.minecraft/config/gravitychanger.json`. You can edit it manually or in game using modmenu.  
The config has 2 categories: `Client` and `Server`.  
`Client` category contains options that only take effect on the client and need to be set on the client.  
`Server` category contains options that only take effect on the server and need to be set in the server config. If you are playing in singleplayer your server uses the same config as your client and you can change it from modmenu. If you are playing on a server the server config is not updated when you change it on your client, it can only be edited using the server config file.

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
