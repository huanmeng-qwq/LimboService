package cn.ycraft.limbo.command;

import com.loohp.limbo.Limbo;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleCompleter {

    private SimpleCompleter() {
        throw new IllegalStateException("Utility class");
    }

    public static @NotNull List<String> none() {
        return Collections.emptyList();
    }

    public static @NotNull List<String> objects(@NotNull String input, Collection<?> objects) {
        return objects(input, objects.size(), objects);
    }

    public static @NotNull List<String> objects(@NotNull String input, int limit, Collection<?> objects) {
        return objects(input, limit, objects.stream());
    }

    public static @NotNull List<String> objects(@NotNull String input, Stream<?> stream) {
        return objects(input, 20, stream);
    }

    public static @NotNull List<String> objects(@NotNull String input, int limit, Stream<?> stream) {
        return stream.filter(Objects::nonNull).map(Object::toString)
                .filter(s -> CommandHandler.startsWithIgnoreCase(s, input))
                .limit(Math.max(0, limit)).collect(Collectors.toList());
    }

    public static @NotNull List<String> text(@NotNull String input, String... texts) {
        return text(input, texts.length, texts);
    }

    public static @NotNull List<String> text(@NotNull String input, int limit, String... texts) {
        return text(input, limit, Arrays.asList(texts));
    }

    public static @NotNull List<String> text(@NotNull String input, Collection<String> texts) {
        return text(input, texts.size(), texts);
    }

    public static @NotNull List<String> text(@NotNull String input, int limit, Collection<String> texts) {
        return objects(input, limit, texts);
    }


    public static @NotNull List<String> players(@NotNull String input) {
        return players(input, 10);
    }

    public static @NotNull List<String> players(@NotNull String input, int limit) {
        return objects(input, limit, Limbo.getInstance().getPlayers());
    }

}
