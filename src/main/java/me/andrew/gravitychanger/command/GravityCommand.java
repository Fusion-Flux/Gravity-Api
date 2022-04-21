package me.andrew.gravitychanger.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.andrew.gravitychanger.accessor.RotatableEntityAccessor;
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
        LiteralArgumentBuilder<ServerCommandSource> literalSet = literal("set");
        for(Direction direction : Direction.values()) {
            literalSet.then(literal(direction.getName())
                    .executes(context -> {
                        return executeSet(context.getSource(), direction, Collections.singleton(context.getSource().getPlayer()));
                    }).then(argument("players", EntityArgumentType.players())
                            .executes(context -> {
                                return executeSet(context.getSource(), direction, EntityArgumentType.getPlayers(context, "players"));
                            })
                    )
            );
        }

        LiteralArgumentBuilder<ServerCommandSource> literalSetDefault = literal("setdefualt");
        for(Direction direction : Direction.values()) {
            literalSetDefault.then(literal(direction.getName())
                    .executes(context -> {
                        return executeSetDefault(context.getSource(), direction, Collections.singleton(context.getSource().getPlayer()));
                    }).then(argument("players", EntityArgumentType.players())
                            .executes(context -> {
                                return executeSetDefault(context.getSource(), direction, EntityArgumentType.getPlayers(context, "players"));
                            })
                    )
            );
        }

        LiteralArgumentBuilder<ServerCommandSource> literalRotate = literal("rotate");
        for(FacingDirection facingDirection : FacingDirection.values()) {
            literalRotate.then(literal(facingDirection.getName())
                    .executes(context -> {
                        return executeRotate(context.getSource(), facingDirection, Collections.singleton(context.getSource().getPlayer()));
                    }).then(argument("players", EntityArgumentType.players())
                            .executes(context -> {
                                return executeRotate(context.getSource(), facingDirection, EntityArgumentType.getPlayers(context, "players"));
                            })
                    )
            );
        }

        dispatcher.register(literal("gravity").requires(source -> source.hasPermissionLevel(2))
                .then(literal("get")
                        .executes(context -> {
                            return executeGet(context.getSource(), context.getSource().getPlayer());
                        }).then(argument("player", EntityArgumentType.player())
                                .executes(context -> {
                                    return executeGet(context.getSource(), EntityArgumentType.getPlayer(context, "player"));
                                })
                        )
                ).then(literalSet)
                .then(literalSetDefault)
                .then(literalRotate)
                .then(literal("randomise")
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

    private static int executeSetDefault(ServerCommandSource source, Direction gravityDirection, Collection<ServerPlayerEntity> players) {
        int i = 0;

        for(ServerPlayerEntity player : players) {
            RotatableEntityAccessor rotatableEntityAccessor = (RotatableEntityAccessor) player;
            if(rotatableEntityAccessor.gravitychanger$getDefaultGravityDirection() != gravityDirection) {
                rotatableEntityAccessor.gravitychanger$setGravityDirection(gravityDirection, false);
                rotatableEntityAccessor.gravitychanger$setDefaultGravityDirection(gravityDirection, false);
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

    private static int executeRotate(ServerCommandSource source, FacingDirection relativeDirection, Collection<ServerPlayerEntity> players) {
        int i = 0;

        for(ServerPlayerEntity player : players) {
            RotatableEntityAccessor rotatableEntityAccessor = (RotatableEntityAccessor) player;
            Direction gravityDirection = rotatableEntityAccessor.gravitychanger$getGravityDirection();
            Direction combinedRelativeDirection = switch(relativeDirection) {
                case DOWN -> Direction.DOWN;
                case UP -> Direction.UP;
                case FORWARD, BACKWARD, LEFT, RIGHT -> Direction.fromHorizontal(relativeDirection.getHorizontalOffset() + Direction.fromRotation(player.getYaw()).getHorizontal());
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
