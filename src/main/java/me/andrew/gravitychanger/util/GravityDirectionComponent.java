package me.andrew.gravitychanger.util;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;

class GravityDirectionComponent implements GravityComponent{

    Direction gravityDirection = Direction.DOWN;
    Direction defaultGravityDirection = Direction.DOWN;
    Direction prevGravityDirection = Direction.DOWN;

    @Override
    public void onGravityChanged(Direction prevGravityDirection, boolean initialGravity) {

    }

    @Override
    public Direction getTrackedGravityDirection() {
        return gravityDirection;
    }

    @Override
    public Direction getDefaultTrackedGravityDirection() {
        return defaultGravityDirection;
    }

    @Override
    public void setTrackedGravityDirection(Direction gravityDirection) {
        if (this.prevGravityDirection != gravityDirection) {
            this.onGravityChanged(this.prevGravityDirection, false);
            this.prevGravityDirection = gravityDirection;
        }
            this.gravityDirection = gravityDirection;
    }

    @Override
    public void setDefaultTrackedGravityDirection(Direction gravityDirection) {
            this.defaultGravityDirection = gravityDirection;
    }

    @Override
    public void onTrackedData(TrackedData<?> data) {

    }

    @Override
    public void readFromNbt(NbtCompound tag) {

    }

    @Override
    public void writeToNbt(NbtCompound tag) {

    }
}
