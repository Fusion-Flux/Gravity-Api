package com.fusionflux.gravity_api.item;

import com.fusionflux.gravity_api.GravityChangerMod;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;


public abstract class ModItems {
    public static final Item GRAVITY_CHANGER_DOWN = new GravityChangerItem(new Item.Settings().group(GravityChangerMod.GravityChangerGroup).maxCount(1), Direction.DOWN);
    public static final Item GRAVITY_CHANGER_UP = new GravityChangerItem(new Item.Settings().group(GravityChangerMod.GravityChangerGroup).maxCount(1), Direction.UP);
    public static final Item GRAVITY_CHANGER_NORTH = new GravityChangerItem(new Item.Settings().group(GravityChangerMod.GravityChangerGroup).maxCount(1), Direction.NORTH);
    public static final Item GRAVITY_CHANGER_SOUTH = new GravityChangerItem(new Item.Settings().group(GravityChangerMod.GravityChangerGroup).maxCount(1), Direction.SOUTH);
    public static final Item GRAVITY_CHANGER_WEST = new GravityChangerItem(new Item.Settings().group(GravityChangerMod.GravityChangerGroup).maxCount(1), Direction.WEST);
    public static final Item GRAVITY_CHANGER_EAST = new GravityChangerItem(new Item.Settings().group(GravityChangerMod.GravityChangerGroup).maxCount(1), Direction.EAST);

    public static final Item GRAVITY_CHANGER_DOWN_AOE = new GravityChangerItemAOE(new Item.Settings().group(GravityChangerMod.GravityChangerGroup).maxCount(1), Direction.DOWN);
    public static final Item GRAVITY_CHANGER_UP_AOE = new GravityChangerItemAOE(new Item.Settings().group(GravityChangerMod.GravityChangerGroup).maxCount(1), Direction.UP);
    public static final Item GRAVITY_CHANGER_NORTH_AOE = new GravityChangerItemAOE(new Item.Settings().group(GravityChangerMod.GravityChangerGroup).maxCount(1), Direction.NORTH);
    public static final Item GRAVITY_CHANGER_SOUTH_AOE = new GravityChangerItemAOE(new Item.Settings().group(GravityChangerMod.GravityChangerGroup).maxCount(1), Direction.SOUTH);
    public static final Item GRAVITY_CHANGER_WEST_AOE = new GravityChangerItemAOE(new Item.Settings().group(GravityChangerMod.GravityChangerGroup).maxCount(1), Direction.WEST);
    public static final Item GRAVITY_CHANGER_EAST_AOE = new GravityChangerItemAOE(new Item.Settings().group(GravityChangerMod.GravityChangerGroup).maxCount(1), Direction.EAST);

    public static void init() {
        Registry.register(Registry.ITEM, new Identifier(GravityChangerMod.MOD_ID, "gravity_changer_down"), GRAVITY_CHANGER_DOWN);
        Registry.register(Registry.ITEM, new Identifier(GravityChangerMod.MOD_ID, "gravity_changer_up"), GRAVITY_CHANGER_UP);
        Registry.register(Registry.ITEM, new Identifier(GravityChangerMod.MOD_ID, "gravity_changer_north"), GRAVITY_CHANGER_NORTH);
        Registry.register(Registry.ITEM, new Identifier(GravityChangerMod.MOD_ID, "gravity_changer_south"), GRAVITY_CHANGER_SOUTH);
        Registry.register(Registry.ITEM, new Identifier(GravityChangerMod.MOD_ID, "gravity_changer_west"), GRAVITY_CHANGER_WEST);
        Registry.register(Registry.ITEM, new Identifier(GravityChangerMod.MOD_ID, "gravity_changer_east"), GRAVITY_CHANGER_EAST);


        Registry.register(Registry.ITEM, new Identifier(GravityChangerMod.MOD_ID, "gravity_changer_down_aoe"), GRAVITY_CHANGER_DOWN_AOE);
        Registry.register(Registry.ITEM, new Identifier(GravityChangerMod.MOD_ID, "gravity_changer_up_aoe"), GRAVITY_CHANGER_UP_AOE);
        Registry.register(Registry.ITEM, new Identifier(GravityChangerMod.MOD_ID, "gravity_changer_north_aoe"), GRAVITY_CHANGER_NORTH_AOE);
        Registry.register(Registry.ITEM, new Identifier(GravityChangerMod.MOD_ID, "gravity_changer_south_aoe"), GRAVITY_CHANGER_SOUTH_AOE);
        Registry.register(Registry.ITEM, new Identifier(GravityChangerMod.MOD_ID, "gravity_changer_west_aoe"), GRAVITY_CHANGER_WEST_AOE);
        Registry.register(Registry.ITEM, new Identifier(GravityChangerMod.MOD_ID, "gravity_changer_east_aoe"), GRAVITY_CHANGER_EAST_AOE);
    }
}
