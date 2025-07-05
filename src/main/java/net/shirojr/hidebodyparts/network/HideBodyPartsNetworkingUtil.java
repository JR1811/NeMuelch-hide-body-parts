package net.shirojr.hidebodyparts.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.hidebodyparts.network.packet.BodyPartS2CSync;
import net.shirojr.hidebodyparts.util.BodyPart;
import net.shirojr.hidebodyparts.util.cast.BodyPartSaver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HideBodyPartsNetworkingUtil {
    public static void sendPlayerEntitySync(ServerPlayerEntity player, HashSet<BodyPart> parts, boolean syncSelf) {
        if (!(player instanceof BodyPartSaver)) return;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(player.getId());
        BodyPart.toPacketByteBuf(parts, buf);
        if (player.getServer() != null) {
            List<PlayerEntity> players = new ArrayList<>(PlayerLookup.all(player.getServer()));
            if (syncSelf) {
                players.add(player);
            }
            ServerPlayNetworking.send(player, new BodyPartS2CSync(players));
        }
    }
}
