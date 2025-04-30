package net.shirojr.hidebodyparts.util;

import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

public enum BodyPart implements StringIdentifiable {
    HEAD("head", PlayerModelPart.HAT),
    BODY("body", PlayerModelPart.CAPE, PlayerModelPart.JACKET),
    RIGHT_ARM("r_arm", PlayerModelPart.RIGHT_SLEEVE),
    LEFT_ARM("l_arm", PlayerModelPart.LEFT_SLEEVE),
    RIGHT_LEG("r_leg", PlayerModelPart.RIGHT_PANTS_LEG),
    LEFT_LEG("l_leg", PlayerModelPart.LEFT_PANTS_LEG);

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

    public static HashSet<BodyPart> fromNbt(NbtCompound nbt) {
        HashSet<BodyPart> set = new HashSet<>();
        NbtList partsNbt = nbt.getList("bodyParts", NbtElement.STRING_TYPE);
        for (NbtElement entry : partsNbt) {
            String key = entry.asString();
            BodyPart part = fromName(key);
            if (part == null) continue;
            set.add(part);
        }
        return set;
    }

    public static void toNbt(HashSet<BodyPart> parts, NbtCompound nbt) {
        NbtList list = new NbtList();
        for (BodyPart part : parts) {
            list.add(NbtString.of(part.asString()));
        }
        nbt.put("bodyParts", list);
    }

    public static HashSet<BodyPart> fromPacketByteBuf(PacketByteBuf buf) {
        HashSet<BodyPart> set = new HashSet<>();
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            BodyPart part = fromName(buf.readString());
            if (part == null) continue;
            set.add(part);
        }
        return set;
    }

    public static void toPacketByteBuf(HashSet<BodyPart> parts, PacketByteBuf buf) {
        buf.writeVarInt(parts.size());
        for (BodyPart part : parts) {
            buf.writeString(part.asString());
        }
    }
}
