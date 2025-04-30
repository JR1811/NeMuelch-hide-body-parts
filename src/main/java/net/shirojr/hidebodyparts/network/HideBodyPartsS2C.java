package net.shirojr.hidebodyparts.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.shirojr.hidebodyparts.util.BodyPart;
import net.shirojr.hidebodyparts.util.cast.BodyPartSaver;

import java.util.HashSet;

public class HideBodyPartsS2C {
    static {
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifier.PLAYER_SYNC_PACKET, HideBodyPartsS2C::handlePlayerEntitySync);
    }

    private static void handlePlayerEntitySync(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        int entityId = buf.readVarInt();
        HashSet<BodyPart> parts = BodyPart.fromPacketByteBuf(buf);

        client.execute(() -> {
            ClientWorld world = client.world;
            if (world == null || !(world.getEntityById(entityId) instanceof BodyPartSaver player)) return;
            player.hidebodyparts$modifyInvisibleParts(bodyParts -> {
                bodyParts.clear();
                bodyParts.addAll(parts);
            });
        });
    }


    public static void initialize() {
        // static initialisation
    }
}
