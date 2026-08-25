package net.shirojr.hidebodyparts.init;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.shirojr.hidebodyparts.command.HideBodyPartsCommand;
import net.shirojr.hidebodyparts.event.custom.BodyPartCallbacks;
import net.shirojr.hidebodyparts.event.handler.BodyPartEvents;

public class HideBodyPartsCommonEvents {
    private static final BodyPartEvents BODY_PART_EVENTS = new BodyPartEvents();

    static {
        CommandRegistrationCallback.EVENT.register(HideBodyPartsCommand::register);
        BodyPartCallbacks.BODY_PART_ADDED.register(BODY_PART_EVENTS);
        BodyPartCallbacks.BODY_PART_REMOVED.register(BODY_PART_EVENTS);
        BodyPartCallbacks.REQUEST_CREATED.register(BODY_PART_EVENTS);
        BodyPartCallbacks.REQUEST_STATE_CHANGED.register(BODY_PART_EVENTS);
    }

    public static void initialize() {
        // static initialisation
    }
}
