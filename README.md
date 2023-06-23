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

## Commands
`/gravity get <player>` - gets player's gravity, returns direction index(0-5)  
`/gravity set <direction> <players>` - sets gravity for selected players  
`/gravity rotate <direction> <players>` - rotates gravity for selected players relative to their look direction  
`/gravity randomise <players>` - randomises gravity for selected players

## Importing
To import the mod you can use modrinth maven.  
Add the following to your project:
#### grable.properties
```properties
gravity_api_version = 1.0.6
```
Replace 0.2.0 with the version you want to use from [here](https://modrinth.com/mod/gravitychanger/versions).
#### build.gradle
```gradle
repositories {
    maven {
		name = "Modrinth"
		url = "https://api.modrinth.com/maven"
		content {
			includeGroup "maven.modrinth"
		}
	}
}

dependencies {
    modImplementation include ("maven.modrinth:gravity-api:${project.gravity_api_version}"){
	    exclude group: "net.fabricmc.fabric-api"
	    exclude group: "net.fabricmc"
	}
}
```
#### fabric.mod.json
```json
"depends": [
  {
    "id": "com.fusionflux:gravity_api",
    "versions": ">=1.0.6"
  }
]
```
Replace 1.0.6 with the lowest version of the mod your mod works with

Now you should be able to use methods in `me.andrew.gravitychanger.api.GravityChangerAPI` class to manipulate player gravity.
