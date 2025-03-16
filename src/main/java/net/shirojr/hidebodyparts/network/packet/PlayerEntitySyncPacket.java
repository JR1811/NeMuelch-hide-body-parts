package net.shirojr.hidebodyparts.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.hidebodyparts.HideBodyParts;
import net.shirojr.hidebodyparts.util.BodyPart;
import net.shirojr.hidebodyparts.util.cast.IBodyPartSaver;

import java.util.Collection;
import java.util.HashSet;

public record PlayerEntitySyncPacket(int entityId, HashSet<BodyPart> parts) implements CustomPayload {
    public static final CustomPayload.Id<PlayerEntitySyncPacket> IDENTIFIER =
            new CustomPayload.Id<>(HideBodyParts.getId("player_entity_sync"));

    public static final PacketCodec<RegistryByteBuf, PlayerEntitySyncPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, PlayerEntitySyncPacket::entityId,
            BodyPart.PACKET_CODEC.collect(PacketCodecs.toCollection(HashSet::new)), PlayerEntitySyncPacket::parts,
            PlayerEntitySyncPacket::new
    );

    public PlayerEntitySyncPacket withNewId(int newEntityId) {
        return new PlayerEntitySyncPacket(newEntityId, parts);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void sendPacket(PlayerEntity sender, Collection<ServerPlayerEntity> targets) {
        targets.forEach(target -> ServerPlayNetworking.send(target, this));
        if (sender instanceof ServerPlayerEntity serverPlayer && serverPlayer.networkHandler != null) {
            ServerPlayNetworking.send(serverPlayer, this);
        }
    }

    public void sendPacket(int entityId, PlayerEntity sender, Collection<ServerPlayerEntity> targets) {
        targets.forEach(target -> ServerPlayNetworking.send(target, this));
        if (sender.getWorld().getEntityById(entityId) instanceof ServerPlayerEntity serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, this.withNewId(entityId));
        }
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        ClientWorld world = context.player().clientWorld;
        if (world == null || !(world.getEntityById(entityId) instanceof IBodyPartSaver player)) return;
        player.hidebodyparts$modifyInvisibleParts(bodyParts -> {
            bodyParts.clear();
            bodyParts.addAll(parts);
        });
    }
}
