package net.shirojr.hidebodyparts.util;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

public enum BodyPart implements StringIdentifiable {

    // HAT("hat"),
    HEAD("head"),
    BODY("body"),
    RIGHT_ARM("r_arm"),
    LEFT_ARM("l_arm"),
    RIGHT_LEG("r_leg"),
    LEFT_LEG("l_leg");

    @SuppressWarnings("unused")
    public static final Codec<BodyPart> CODEC = Codec.STRING.xmap(BodyPart::valueOf, BodyPart::name);
    public static final PacketCodec<RegistryByteBuf, BodyPart> PACKET_CODEC = PacketCodec.of(
            (value, buf) -> buf.writeVarInt(value.ordinal()), buf -> BodyPart.values()[buf.readVarInt()]
    );

    private final String bodyPart;

    BodyPart(String bodyPart) {
        this.bodyPart = bodyPart;
    }

    @Override
    public String asString() {
        return this.bodyPart;
    }

    @Nullable
    public static BodyPart fromName(String name) {
        for (BodyPart entry : BodyPart.values()) {
            if (entry.asString().equals(name)) return entry;
        }
        return null;
    }
}
