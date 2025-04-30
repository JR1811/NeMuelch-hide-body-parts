package net.shirojr.hidebodyparts.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.shirojr.hidebodyparts.util.BodyPart;

import java.util.HashSet;
import java.util.List;

public class PlayerModelPartHandler {
    public static void setSecondLayerState(HashSet<BodyPart> invisibleParts) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        GameOptions options = client.options;

        for (BodyPart entry : invisibleParts) {
            for (PlayerModelPart secondLayerEntry : entry.getSecondLayer()) {
                if (!options.isPlayerModelPartEnabled(secondLayerEntry)) continue;
                options.togglePlayerModelPart(secondLayerEntry, false);
            }
        }

        List<PlayerModelPart> playerModelParts = invisibleParts.stream().map(BodyPart::getSecondLayer).flatMap(List::stream).toList();
        for (PlayerModelPart entry : PlayerModelPart.values()) {
            if (playerModelParts.contains(entry)) continue;
            if (options.isPlayerModelPartEnabled(entry)) continue;
            options.setPlayerModelPart(entry, true);
        }
        options.sendClientSettings();
    }
}
