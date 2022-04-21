package me.andrew.gravitychanger.item;

import me.andrew.gravitychanger.GravityChangerMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public abstract class ModItems {
    public static final Item GRAVITY_CHANGER_DOWN = new GravityChangerItem(new Item.Settings().group(ItemGroup.TOOLS).maxCount(1), Direction.DOWN);
    public static final Item GRAVITY_CHANGER_UP = new GravityChangerItem(new Item.Settings().group(ItemGroup.TOOLS).maxCount(1), Direction.UP);
    public static final Item GRAVITY_CHANGER_NORTH = new GravityChangerItem(new Item.Settings().group(ItemGroup.TOOLS).maxCount(1), Direction.NORTH);
    public static final Item GRAVITY_CHANGER_SOUTH = new GravityChangerItem(new Item.Settings().group(ItemGroup.TOOLS).maxCount(1), Direction.SOUTH);
    public static final Item GRAVITY_CHANGER_WEST = new GravityChangerItem(new Item.Settings().group(ItemGroup.TOOLS).maxCount(1), Direction.WEST);
    public static final Item GRAVITY_CHANGER_EAST = new GravityChangerItem(new Item.Settings().group(ItemGroup.TOOLS).maxCount(1), Direction.EAST);

    public static void init() {
        Registry.register(Registry.ITEM, new Identifier(GravityChangerMod.MOD_ID, "gravity_changer_down"), GRAVITY_CHANGER_DOWN);
        Registry.register(Registry.ITEM, new Identifier(GravityChangerMod.MOD_ID, "gravity_changer_up"), GRAVITY_CHANGER_UP);
        Registry.register(Registry.ITEM, new Identifier(GravityChangerMod.MOD_ID, "gravity_changer_north"), GRAVITY_CHANGER_NORTH);
        Registry.register(Registry.ITEM, new Identifier(GravityChangerMod.MOD_ID, "gravity_changer_south"), GRAVITY_CHANGER_SOUTH);
        Registry.register(Registry.ITEM, new Identifier(GravityChangerMod.MOD_ID, "gravity_changer_west"), GRAVITY_CHANGER_WEST);
        Registry.register(Registry.ITEM, new Identifier(GravityChangerMod.MOD_ID, "gravity_changer_east"), GRAVITY_CHANGER_EAST);
    }
}
