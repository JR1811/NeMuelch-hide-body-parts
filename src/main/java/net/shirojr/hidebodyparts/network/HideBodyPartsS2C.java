package net.shirojr.hidebodyparts.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.shirojr.hidebodyparts.network.packet.PlayerEntitySyncPacket;

public class HideBodyPartsS2C {
    static {
        ClientPlayNetworking.registerGlobalReceiver(PlayerEntitySyncPacket.IDENTIFIER, PlayerEntitySyncPacket::handlePacket);
    }


    public static void initialize() {
        // static initialisation
    }
}
