package net.shirojr.hidebodyparts.event.handler;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.shirojr.hidebodyparts.cca.implementation.RequestComponent;
import net.shirojr.hidebodyparts.event.custom.BodyPartCallbacks;
import net.shirojr.hidebodyparts.request.RequestEntry;
import net.shirojr.hidebodyparts.request.RequestState;
import net.shirojr.hidebodyparts.util.BodyPart;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class BodyPartEvents implements BodyPartCallbacks.PartAdded, BodyPartCallbacks.PartRemoved, BodyPartCallbacks.RequestCreated, BodyPartCallbacks.RequestStateChange {
    @Override
    public void onBodyPartAdded(Entity entity, HashSet<BodyPart> parts) {

    }

    @Override
    public void onBodyPartRemoved(Entity entity, HashSet<BodyPart> parts) {

    }

    @Override
    public void onRequestCreated(RequestComponent component, UUID entryUuid, RequestEntry entry) {
        MinecraftServer server = component.getServer();
        if (server == null) return;
        List<Text> lines = component.printEntry(entryUuid);
        if (lines == null) return;
        lines.add(0, Text.literal("--- New BodyPart Change Request ---"));

        for (ServerPlayerEntity target : PlayerLookup.all(server)) {
            if (!target.hasPermissionLevel(2)) continue;
            lines.forEach(line -> target.sendMessage(line, false));
        }
    }

    @Override
    public void onRequestStateChanged(RequestComponent component, UUID entryUuid, RequestState oldState, RequestState newState) {
        MinecraftServer server = component.getServer();
        if (server == null) return;
        List<Text> lines = component.printEntry(entryUuid);
        if (lines == null) return;
        lines.add(0, Text.literal("--- BodyPart Changed Request State ---"));
        lines.add(1, Text.literal(" (%s --> %s)".formatted(oldState.asString(), newState.asString())));

        for (ServerPlayerEntity target : PlayerLookup.all(server)) {
            if (!target.hasPermissionLevel(2)) continue;
            lines.forEach(line -> target.sendMessage(line, false));
        }
    }
}
