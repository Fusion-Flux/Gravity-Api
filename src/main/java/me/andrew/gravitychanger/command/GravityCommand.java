package me.andrew.gravitychanger.command;

import com.mojang.brigadier.CommandDispatcher;
import me.andrew.gravitychanger.accessor.RotatableEntityAccessor;
import me.andrew.gravitychanger.command.argument.EnumArgumentType;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;

import java.util.Collection;
import java.util.Collections;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class GravityCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("gravity").requires(source -> source.hasPermissionLevel(2))
                .then(literal("get")
                        .executes(context -> {
                            return executeGet(context.getSource(), context.getSource().getPlayer());
                        }).then(argument("player", EntityArgumentType.player())
                                .executes(context -> {
                                    return executeGet(context.getSource(), EntityArgumentType.getPlayer(context, "player"));
                                })
                        )
                ).then(literal("set")
                        .then(argument("direction", EnumArgumentType.enumeration(Direction.class))
                                .executes(context -> {
                                    return executeSet(context.getSource(), EnumArgumentType.getEnumeration(context, "direction", Direction.class), Collections.singleton(context.getSource().getPlayer()));
                                }).then(argument("players", EntityArgumentType.players())
                                        .executes(context -> {
                                            return executeSet(context.getSource(), EnumArgumentType.getEnumeration(context, "direction", Direction.class), EntityArgumentType.getPlayers(context, "players"));
                                        })
                                )
                        )
                ).then(literal("rotate")
                        .then(argument("direction", EnumArgumentType.enumeration(FacingDirection.class))
                                .executes(context -> {
                                    return executeRotate(context.getSource(), EnumArgumentType.getEnumeration(context, "direction", FacingDirection.class).direction, Collections.singleton(context.getSource().getPlayer()));
                                }).then(argument("players", EntityArgumentType.players())
                                        .executes(context -> {
                                            return executeRotate(context.getSource(), EnumArgumentType.getEnumeration(context, "direction", FacingDirection.class).direction, EntityArgumentType.getPlayers(context, "players"));
                                        })
                                )
                        )
                ).then(literal("randomise")
                        .executes(context -> {
                            return executeRandomise(context.getSource(), Collections.singleton(context.getSource().getPlayer()));
                        }).then(argument("players", EntityArgumentType.players())
                                .executes(context -> {
                                    return executeRandomise(context.getSource(), EntityArgumentType.getPlayers(context, "players"));
                                })
                        )
                )
        );
    }

    private static int executeGet(ServerCommandSource source, ServerPlayerEntity player) {
        Direction gravityDirection = ((RotatableEntityAccessor) player).gravitychanger$getGravityDirection();
        getSendFeedback(source, player, gravityDirection);

        return gravityDirection.getId();
    }

    private static void getSendFeedback(ServerCommandSource source, ServerPlayerEntity player, Direction gravityDirection) {
        Text text = new TranslatableText("direction." + gravityDirection.getName());
        if (source.getEntity() == player) {
            source.sendFeedback(new TranslatableText("commands.gravity.get.self", text), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.gravity.get.other", player.getDisplayName(), text), true);
        }
    }

    private static int executeSet(ServerCommandSource source, Direction gravityDirection, Collection<ServerPlayerEntity> players) {
        int i = 0;

        for(ServerPlayerEntity player : players) {
            RotatableEntityAccessor rotatableEntityAccessor = (RotatableEntityAccessor) player;
            if(rotatableEntityAccessor.gravitychanger$getGravityDirection() != gravityDirection) {
                rotatableEntityAccessor.gravitychanger$setGravityDirection(gravityDirection, false);
                setSendFeedback(source, player, gravityDirection);
                i++;
            }
        }

        return i;
    }

    private static void setSendFeedback(ServerCommandSource source, ServerPlayerEntity player, Direction gravityDirection) {
        Text text = new TranslatableText("direction." + gravityDirection.getName());
        if (source.getEntity() == player) {
            source.sendFeedback(new TranslatableText("commands.gravity.set.self", text), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.gravity.set.other", player.getDisplayName(), text), true);
        }
    }

    private static int executeRotate(ServerCommandSource source, Direction relativeDirection, Collection<ServerPlayerEntity> players) {
        int i = 0;

        for(ServerPlayerEntity player : players) {
            RotatableEntityAccessor rotatableEntityAccessor = (RotatableEntityAccessor) player;
            Direction gravityDirection = rotatableEntityAccessor.gravitychanger$getGravityDirection();
            Direction combinedRelativeDirection = switch(relativeDirection) {
                case DOWN, UP -> relativeDirection;
                case NORTH, SOUTH, WEST, EAST -> Direction.fromHorizontal(relativeDirection.getHorizontal() + Direction.fromRotation(player.getYaw()).getHorizontal());
            };
            Direction newGravityDirection = RotationUtil.dirPlayerToWorld(combinedRelativeDirection, gravityDirection);
            rotatableEntityAccessor.gravitychanger$setGravityDirection(newGravityDirection, false);
            setSendFeedback(source, player, newGravityDirection);
            i++;
        }

        return i;
    }

    private static int executeRandomise(ServerCommandSource source, Collection<ServerPlayerEntity> players) {
        int i = 0;

        for(ServerPlayerEntity player : players) {
            RotatableEntityAccessor rotatableEntityAccessor = (RotatableEntityAccessor) player;
            Direction gravityDirection = Direction.random(source.getWorld().random);
            if(rotatableEntityAccessor.gravitychanger$getGravityDirection() != gravityDirection) {
                rotatableEntityAccessor.gravitychanger$setGravityDirection(gravityDirection, false);
                setSendFeedback(source, player, gravityDirection);
                i++;
            }
        }

        return i;
    }

    public enum FacingDirection {
        DOWN(Direction.DOWN),
        UP(Direction.UP),
        FORWARD(Direction.SOUTH),
        BACKWARD(Direction.NORTH),
        LEFT(Direction.EAST),
        RIGHT(Direction.WEST);

        public final Direction direction;

        FacingDirection(Direction direction) {
            this.direction = direction;
        }
    }
}
