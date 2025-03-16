package net.shirojr.hidebodyparts.event.custom;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.hidebodyparts.network.packet.PlayerEntitySyncPacket;
import net.shirojr.hidebodyparts.util.cast.IBodyPartSaver;

public class PlayerUpdateEvents {
    public static void registerCopyData(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        IBodyPartSaver oldPartsOfPlayer = ((IBodyPartSaver) oldPlayer);
        IBodyPartSaver newPartsOfPlayer = ((IBodyPartSaver) newPlayer);
        newPartsOfPlayer.hidebodyparts$modifyInvisibleParts(bodyParts -> {
            bodyParts.clear();
            bodyParts.addAll(oldPartsOfPlayer.hidebodyparts$getInvisibleParts());
        });
    }

    public static void registerPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.getPlayer();
        if (!(player instanceof IBodyPartSaver parts)) return;
        new PlayerEntitySyncPacket(player.getId(), parts.hidebodyparts$getInvisibleParts()).sendPacket(player, PlayerLookup.tracking(player));
    }
}
