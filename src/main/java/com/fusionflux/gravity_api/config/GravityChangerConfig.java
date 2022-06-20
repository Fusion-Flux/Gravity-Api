package com.fusionflux.gravity_api.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(
        name ="gravitychanger"
)
public class GravityChangerConfig implements ConfigData {
    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Excluded
    private boolean client;

    @ConfigEntry.Gui.Tooltip(
            count = 2
    )
    public boolean keepWorldLook = false;

    @ConfigEntry.Gui.Tooltip(
            count = 2
    )
    public int rotationTime = 500;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Excluded
    private boolean server;

    @ConfigEntry.Gui.Tooltip(
            count = 2
    )
    public boolean worldVelocity = false;

    @ConfigEntry.Gui.Tooltip(
            count = 2
    )
    public boolean resetGravityOnDimensionChange = true;

    @ConfigEntry.Gui.Tooltip(
            count = 2
    )
    public boolean resetGravityOnRespawn = true;

    @ConfigEntry.Gui.Tooltip(
            count = 2
    )
    public boolean voidDamageAboveWorld = false;
}
