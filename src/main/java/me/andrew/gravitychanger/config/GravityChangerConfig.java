package me.andrew.gravitychanger.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(
        name ="gravitychanger"
)
public class GravityChangerConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip(
            count = 2
    )
    public boolean keepWorldLook = false;
}
