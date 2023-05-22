package net.shirojr.hidebodyparts.event.custom;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.hidebodyparts.util.BodyParts;
import net.shirojr.hidebodyparts.util.cast.IBodyPartSaver;

public class HideBodyPartsPlayerEventCopyFrom implements ServerPlayerEvents.CopyFrom {
    @Override
    public void copyFromPlayer(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        ((IBodyPartSaver)oldPlayer).editPersistentData(nbtCompound ->
                ((IBodyPartSaver)newPlayer).editPersistentData(player -> {
                    for (var entry : BodyParts.values()) {
                        if (nbtCompound.contains(entry.getBodyPartName())) {
                            player.putString(entry.getBodyPartName(),
                                    nbtCompound.getString(entry.getBodyPartName()));
                        }
                    }
                    return true;
                })
        );
    }
}
