package net.shirojr.hidebodyparts.event;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.shirojr.hidebodyparts.command.HideBodyPartsCommand;
import net.shirojr.hidebodyparts.event.custom.PlayerUpdateEvents;

public class HideBodyPartsEvents {
    public static void registerEvents() {
        ServerPlayerEvents.AFTER_RESPAWN.register(PlayerUpdateEvents::registerCopyData);
        ServerPlayConnectionEvents.JOIN.register(PlayerUpdateEvents::registerPlayerJoin);
        CommandRegistrationCallback.EVENT.register(HideBodyPartsCommand::register);
    }
}
