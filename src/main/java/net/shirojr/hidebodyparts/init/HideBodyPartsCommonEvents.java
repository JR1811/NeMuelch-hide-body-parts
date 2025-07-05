package net.shirojr.hidebodyparts.init;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.shirojr.hidebodyparts.command.HideBodyPartsCommand;

public class HideBodyPartsCommonEvents {
    static {
        CommandRegistrationCallback.EVENT.register(HideBodyPartsCommand::register);
    }

    public static void initialize() {
        // static initialisation
    }
}
