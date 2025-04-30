package net.shirojr.hidebodyparts.event.custom;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.hidebodyparts.network.HideBodyPartsNetworkingUtil;
import net.shirojr.hidebodyparts.util.cast.BodyPartSaver;

@SuppressWarnings("unused")
public class PlayerUpdateEvents {
    public static void registerCopyData(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        if (!(oldPlayer instanceof BodyPartSaver oldParts) || !(newPlayer instanceof BodyPartSaver newParts)) return;
        newParts.hidebodyparts$modifyInvisibleParts(bodyParts -> {
            bodyParts.clear();
            bodyParts.addAll(oldParts.hidebodyparts$getInvisibleParts());
        });
    }

    public static void registerPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.getPlayer();
        if (!(player instanceof BodyPartSaver bodyPartSaver)) return;
        HideBodyPartsNetworkingUtil.sendPlayerEntitySyncToTracking(player, bodyPartSaver.hidebodyparts$getInvisibleParts());
        player.getArmorItems();
    }
}
