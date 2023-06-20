package com.fusionflux.gravity_api.command;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.Gravity;
import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

import java.util.Collection;
import java.util.Collections;

public class GravityCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalSet = literal("add");
        for (Direction direction : Direction.values()) {
            literalSet.then(
                    literal(direction.getName())
                            .then(argument("priority", IntegerArgumentType.integer())
                                    .then(argument("duration", IntegerArgumentType.integer())
                                            .executes(context -> executeSet(context.getSource(), direction,IntegerArgumentType.getInteger(context, "priority"), IntegerArgumentType.getInteger(context, "duration"), Collections.singleton(context.getSource().getPlayer())))
                                                .then(argument("entities", EntityArgumentType.entities())
                                                        .executes(context -> executeSet(context.getSource(), direction, IntegerArgumentType.getInteger(context, "priority"), IntegerArgumentType.getInteger(context, "duration"), EntityArgumentType.getEntities(context, "entities"))))))
            );
        }

        LiteralArgumentBuilder<ServerCommandSource> literalSetDefault = literal("set");
        for (Direction direction : Direction.values())
            literalSetDefault.then(literal(direction.getName())
                    .executes(context -> executeSetDefault(context.getSource(), direction, Collections.singleton(context.getSource().getPlayer())))
                    .then(argument("entities", EntityArgumentType.entities())
                            .executes(context -> executeSetDefault(context.getSource(), direction, EntityArgumentType.getEntities(context, "entities")))));

        LiteralArgumentBuilder<ServerCommandSource> literalRotate = literal("rotate");
        for (FacingDirection facingDirection : FacingDirection.values())
            literalRotate.then(literal(facingDirection.getName())
                    .executes(context -> executeRotate(context.getSource(), facingDirection, Collections.singleton(context.getSource().getPlayer())))
                    .then(argument("entities", EntityArgumentType.entities())
                            .executes(context -> executeRotate(context.getSource(), facingDirection, EntityArgumentType.getEntities(context, "entities")))));

        dispatcher.register(literal("gravity").requires(source -> source.hasPermissionLevel(2))
                .then(literal("get")
                        .executes(context -> executeGet(context.getSource(), context.getSource().getPlayer()))
                        .then(argument("entities", EntityArgumentType.entity())
                                .executes(context -> executeGet(context.getSource(), EntityArgumentType.getEntity(context, "entities")))))
                .then(literal("cleargravity")
                        .executes(context -> executeClearGravity(context.getSource(), Collections.singleton(context.getSource().getPlayer())))
                        .then(argument("entities", EntityArgumentType.entity())
                                .executes(context -> executeClearGravity(context.getSource(), EntityArgumentType.getEntities(context, "entities")))))
                .then(literal("setdefaultstrength")
                        .executes(context -> executeSetDefaultStrength(context.getSource(), DoubleArgumentType.getDouble(context, "double"), Collections.singleton(context.getSource().getPlayer())))
                        .then(argument("entities", EntityArgumentType.entity()).then(argument("double", DoubleArgumentType.doubleArg())
                                .executes(context -> executeSetDefaultStrength(context.getSource(), DoubleArgumentType.getDouble(context, "double"), Collections.singleton(EntityArgumentType.getEntity(context, "entities")))))))
                .then(literalSet).then(literalSetDefault).then(literalRotate).then(literal("randomise")
                        .executes(context -> executeRandomise(context.getSource(), Collections.singleton(context.getSource().getPlayer())))
                        .then(argument("entities", EntityArgumentType.entities())
                                .executes(context -> executeRandomise(context.getSource(), EntityArgumentType.getEntities(context, "entities"))))));
    }

    private static void getSendFeedback(ServerCommandSource source, Entity entity, Direction gravityDirection) {
        Text text = Text.translatable("direction." + gravityDirection.getName());
        if (source.getEntity() != null && source.getEntity() == entity) {
            source.sendFeedback(() -> Text.translatable("commands.gravity.get.self", text), true);
        } else {
            source.sendFeedback(() -> Text.translatable("commands.gravity.get.other", entity.getDisplayName(), text), true);
        }
    }

    private static void getStrengthSendFeedback(ServerCommandSource source, Entity entity, double strength) {
        Text text = Text.translatable("strength " + strength);
        if (source.getEntity() != null && source.getEntity() == entity) {
            source.sendFeedback(() -> Text.translatable("commands.gravity.get.self", text), true);
        } else {
            source.sendFeedback(() -> Text.translatable("commands.gravity.get.other", entity.getDisplayName(), text), true);
        }
    }

    private static int executeGet(ServerCommandSource source, Entity entity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        getSendFeedback(source, entity, gravityDirection);
        return gravityDirection.getId();
    }

    private static int executeSet(ServerCommandSource source, Direction gravityDirection,int priority, int durration, Collection<? extends Entity> entities) {
        int i = 0;
        for (Entity entity : entities) {
            //if (GravityChangerAPI.getGravityDirection(entity) != gravityDirection) {
                GravityChangerAPI.addGravity(entity,new Gravity(gravityDirection,priority,durration,"command"));
                //getSendFeedback(source, entity, gravityDirection);
                i++;
            //}
        }
        return i;
    }

    private static int executeSetDefault(ServerCommandSource source, Direction gravityDirection, Collection<? extends Entity> entities) {
        int i = 0;
        for (Entity entity : entities) {
            if (GravityChangerAPI.getDefaultGravityDirection(entity) != gravityDirection) {
                GravityChangerAPI.setDefaultGravityDirection(entity, gravityDirection, new RotationParameters());
                //GravityChangerAPI.updateGravity(entity);
                getSendFeedback(source, entity, gravityDirection);
                i++;
            }
        }
        return i;
    }

    private static int executeSetDefaultStrength(ServerCommandSource source, double gravityStrength, Collection<? extends Entity> entities) {
        int i = 0;
        for (Entity entity : entities) {
            if (GravityChangerAPI.getDefaultGravityStrength(entity) != gravityStrength) {
                GravityChangerAPI.setDefualtGravityStrength(entity, gravityStrength);
                getStrengthSendFeedback(source, entity, gravityStrength);
                i++;
            }
        }
        return i;
    }

    private static int executeRotate(ServerCommandSource source, FacingDirection relativeDirection, Collection<? extends Entity> entities) {
        int i = 0;
        for (Entity entity : entities) {
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
            Direction combinedRelativeDirection = switch(relativeDirection) {
                case DOWN -> Direction.DOWN;
                case UP -> Direction.UP;
                case FORWARD, BACKWARD, LEFT, RIGHT -> Direction.fromHorizontal(relativeDirection.getHorizontalOffset() + Direction.fromRotation(entity.getYaw()).getHorizontal());
            };
            Direction newGravityDirection = RotationUtil.dirPlayerToWorld(combinedRelativeDirection, gravityDirection);
            GravityChangerAPI.setDefaultGravityDirection(entity, newGravityDirection, new RotationParameters());
            //GravityChangerAPI.updateGravity(entity);
            getSendFeedback(source, entity, newGravityDirection);
            i++;
        }
        return i;
    }

    private static int executeRandomise(ServerCommandSource source, Collection<? extends Entity> entities) {
        int i = 0;
        for (Entity entity : entities) {
            Direction gravityDirection = Direction.random(source.getWorld().random);
            if (GravityChangerAPI.getGravityDirection(entity) != gravityDirection) {
                GravityChangerAPI.setDefaultGravityDirection(entity, gravityDirection, new RotationParameters());
                //GravityChangerAPI.updateGravity(entity);
                getSendFeedback(source, entity, gravityDirection);
                i++;
            }
        }
        return i;
    }

    private static int executeClearGravity(ServerCommandSource source, Collection<? extends Entity> entities) {
        int i = 0;
        for (Entity entity : entities) {
            GravityChangerAPI.clearGravity(entity, new RotationParameters());
            i++;
        }
        return i;
    }

    public enum FacingDirection {
        DOWN(-1, "down"),
        UP(-1, "up"),
        FORWARD(0, "forward"),
        BACKWARD(2, "backward"),
        LEFT(3, "left"),
        RIGHT(1, "right");

        private final int horizontalOffset;
        private final String name;

        FacingDirection(int horizontalOffset, String name) {
            this.horizontalOffset = horizontalOffset;
            this.name = name;
        }

        public int getHorizontalOffset() {
            return this.horizontalOffset;
        }

        public String getName() {
            return this.name;
        }
    }
}
