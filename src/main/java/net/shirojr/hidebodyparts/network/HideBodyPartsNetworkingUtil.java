package net.shirojr.hidebodyparts.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.hidebodyparts.util.BodyPart;
import net.shirojr.hidebodyparts.util.cast.BodyPartSaver;

import java.util.HashSet;

public class HideBodyPartsNetworkingUtil {
    public static void sendPlayerEntitySyncToTracking(ServerPlayerEntity player, HashSet<BodyPart> parts) {
        if (!(player instanceof BodyPartSaver)) return;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(player.getId());
        BodyPart.toPacketByteBuf(parts, buf);

        for (ServerPlayerEntity target : PlayerLookup.tracking(player)) {
            ServerPlayNetworking.send(target, PacketIdentifier.PLAYER_SYNC_PACKET, buf);
        }
        ServerPlayNetworking.send(player, PacketIdentifier.PLAYER_SYNC_PACKET, buf);
    }
}
