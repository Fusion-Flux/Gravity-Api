package me.andrew.gravitychanger.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.LiteralText;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EnumArgumentType<T extends Enum<T>> implements ArgumentType<T> {
    private static final DynamicCommandExceptionType INVALID_ELEMENT_EXCEPTION = new DynamicCommandExceptionType((object) -> {
        return new LiteralText("Enumeration element not found: " + object.toString());
    });
    private final Map<String, T> values;

    private EnumArgumentType(Class<T> clazz) {
        T[] values = clazz.getEnumConstants();
        this.values = new HashMap<>(values.length * 2);
        for(T value : values) {
            this.values.put(value.name(), value);
        }
    }

    public static <T extends Enum<T>> EnumArgumentType<T> enumeration(Class<T> clazz) {
        return new EnumArgumentType<>(clazz);
    }

    public static <T extends Enum<T>> T getEnumeration(CommandContext<?> commandContext, String name, Class<T> clazz) {
        return commandContext.getArgument(name, clazz);
    }

    public T parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readString();
        T result = this.values.get(name);
        if (result != null) {
            return result;
        } else {
            throw INVALID_ELEMENT_EXCEPTION.create(name);
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(this.values.keySet(), builder);
    }
}

