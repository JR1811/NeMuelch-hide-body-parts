package net.shirojr.hidebodyparts.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.shirojr.hidebodyparts.event.custom.HideBodyPartsPlayerEventCopyFrom;

public class HideBodyPartsEvents {
    public static void registerEvents() {
        ServerPlayerEvents.COPY_FROM.register(new HideBodyPartsPlayerEventCopyFrom());
    }
}
