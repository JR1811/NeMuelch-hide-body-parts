package net.shirojr.hidebodyparts.network.packet;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;
import net.shirojr.hidebodyparts.HideBodyParts;
import net.shirojr.hidebodyparts.util.BodyPart;
import net.shirojr.hidebodyparts.util.cast.BodyPartSaver;

import java.util.*;

public final class BodyPartS2CSync implements FabricPacket {
    public static final PacketType<BodyPartS2CSync> TYPE = PacketType.create(HideBodyParts.getId("sync_parts_s2c"), BodyPartS2CSync::new);

    private final Map<Integer, Set<BodyPart>> players;

    public BodyPartS2CSync(PacketByteBuf buf) {
        int playerSize = buf.readVarInt();
        this.players = new HashMap<>(playerSize);
        for (int i = 0; i < playerSize; i++) {
            int playerId = buf.readVarInt();
            int bodyPartsSize = buf.readVarInt();
            HashSet<BodyPart> parts = new HashSet<>(bodyPartsSize);
            for (int j = 0; j < bodyPartsSize; j++) {
                parts.add(BodyPart.values()[buf.readByte()]);
            }
            players.put(playerId, parts);
        }
    }

    public BodyPartS2CSync(Collection<PlayerEntity> players) {
        this.players = new HashMap<>();
        for (PlayerEntity player : players) {
            if (!(player instanceof BodyPartSaver holder)) continue;
            this.players.put(player.getId(), holder.hidebodyparts$getInvisibleParts());
        }
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(players.size());
        for (int playerId : players.keySet()) {
            buf.writeVarInt(playerId);
            Set<BodyPart> bodyParts = players.get(playerId);
            buf.writeVarInt(bodyParts.size());
            for (BodyPart part : bodyParts) {
                buf.writeByte(part.ordinal());
            }
        }
    }


    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public void handle(ClientPlayerEntity player, PacketSender sender) {
        World world = player.getWorld();
        if (world == null) return;
        for (var target : this.players.entrySet()) {
            if (!(world.getEntityById(target.getKey()) instanceof BodyPartSaver holder)) continue;
            holder.hidebodyparts$modifyInvisibleParts(parts -> {
                parts.clear();
                parts.addAll(target.getValue());
            }, false);
        }
    }
}
