package net.shirojr.hidebodyparts.request;

import net.minecraft.util.StringIdentifiable;

import java.util.Locale;
import java.util.NoSuchElementException;

public enum RequestState implements StringIdentifiable {
    REQUESTED,
    APPROVED,
    DENIED;

    @SuppressWarnings("deprecation")
    public static final Codec<RequestState> CODEC = StringIdentifiable.createCodec(RequestState::values);

    @Override
    public String asString() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public static RequestState get(String name) {
        for (RequestState entry : RequestState.values()) {
            if (entry.asString().equals(name)) return entry;
        }
        throw new NoSuchElementException("No such RequestState: %s".formatted(name));
    }
}
