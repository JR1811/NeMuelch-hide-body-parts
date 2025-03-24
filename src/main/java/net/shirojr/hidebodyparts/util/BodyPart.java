package net.shirojr.hidebodyparts.util;

import com.mojang.serialization.Codec;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public enum BodyPart implements StringIdentifiable {
    HEAD("head", PlayerModelPart.HAT),
    BODY("body", PlayerModelPart.CAPE, PlayerModelPart.JACKET),
    RIGHT_ARM("r_arm", PlayerModelPart.RIGHT_SLEEVE),
    LEFT_ARM("l_arm", PlayerModelPart.LEFT_SLEEVE),
    RIGHT_LEG("r_leg", PlayerModelPart.RIGHT_PANTS_LEG),
    LEFT_LEG("l_leg", PlayerModelPart.LEFT_PANTS_LEG);

    @SuppressWarnings("unused")
    public static final Codec<BodyPart> CODEC = Codec.STRING.xmap(BodyPart::valueOf, BodyPart::name);
    public static final PacketCodec<RegistryByteBuf, BodyPart> PACKET_CODEC = PacketCodec.of(
            (value, buf) -> buf.writeVarInt(value.ordinal()), buf -> BodyPart.values()[buf.readVarInt()]
    );

    private final String bodyPart;
    private final List<PlayerModelPart> secondLayer;

    BodyPart(String bodyPart, PlayerModelPart... secondLayer) {
        this.bodyPart = bodyPart;
        this.secondLayer = List.of(secondLayer);
    }

    @Override
    public String asString() {
        return this.bodyPart;
    }

    public List<PlayerModelPart> getSecondLayer() {
        return secondLayer;
    }

    @Nullable
    public static BodyPart fromName(String name) {
        for (BodyPart entry : BodyPart.values()) {
            if (entry.asString().equals(name)) return entry;
        }
        return null;
    }
}
