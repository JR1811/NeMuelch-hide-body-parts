package net.shirojr.hidebodyparts.event.custom;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.shirojr.hidebodyparts.cca.implementation.RequestComponent;
import net.shirojr.hidebodyparts.request.RequestEntry;
import net.shirojr.hidebodyparts.request.RequestState;
import net.shirojr.hidebodyparts.util.BodyPart;

import java.util.HashSet;
import java.util.UUID;

public class BodyPartCallbacks {
    public static Event<PartAdded> BODY_PART_ADDED = EventFactory.createArrayBacked(PartAdded.class,
            listeners -> (entity, parts) -> {
                for (PartAdded listener : listeners) {
                    listener.onBodyPartAdded(entity, parts);
                }
            }
    );

    public static Event<PartRemoved> BODY_PART_REMOVED = EventFactory.createArrayBacked(PartRemoved.class,
            listeners -> (entity, parts) -> {
                for (PartRemoved listener : listeners) {
                    listener.onBodyPartRemoved(entity, parts);
                }
            }
    );

    public static Event<RequestCreated> REQUEST_CREATED = EventFactory.createArrayBacked(RequestCreated.class,
            listeners -> (component, entryUuid, entry) -> {
                for (RequestCreated listener : listeners) {
                    listener.onRequestCreated(component, entryUuid, entry);
                }
            }
    );

    public static Event<RequestStateChange> REQUEST_STATE_CHANGED = EventFactory.createArrayBacked(RequestStateChange.class,
            listeners -> (component, entryUuid, oldState, newState) -> {
                for (RequestStateChange listener : listeners) {
                    listener.onRequestStateChanged(component, entryUuid, oldState, newState);
                }
            }
    );

    @FunctionalInterface
    public interface PartAdded {
        void onBodyPartAdded(Entity entity, HashSet<BodyPart> parts);
    }

    @FunctionalInterface
    public interface PartRemoved {
        void onBodyPartRemoved(Entity entity, HashSet<BodyPart> parts);
    }

    @FunctionalInterface
    public interface RequestCreated {
        void onRequestCreated(RequestComponent component, UUID entryUuid, RequestEntry entry);
    }

    @FunctionalInterface
    public interface RequestStateChange {
        void onRequestStateChanged(RequestComponent component, UUID entryUuid, RequestState oldState, RequestState newState);
    }
}
