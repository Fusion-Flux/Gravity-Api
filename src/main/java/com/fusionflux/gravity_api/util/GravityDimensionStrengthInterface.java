package com.fusionflux.gravity_api.util;

import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public interface GravityDimensionStrengthInterface extends Component, AutoSyncedComponent {
    double getDimensionGravityStrength();
    void setDimensionGravityStrength(double strength);
}
