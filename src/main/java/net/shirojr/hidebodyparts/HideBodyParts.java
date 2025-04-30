package net.shirojr.hidebodyparts;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.shirojr.hidebodyparts.event.HideBodyPartsEvents;
import net.shirojr.hidebodyparts.init.HideBodyPartsItemGroups;
import net.shirojr.hidebodyparts.init.HideBodyPartsItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HideBodyParts implements ModInitializer {
    public static final String MOD_ID = "hide-body-parts";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    @Override
    public void onInitialize() {
        HideBodyPartsItems.initialize();
        HideBodyPartsItemGroups.initialize();
        HideBodyPartsEvents.registerEvents();

        LOGGER.info("Ooops, there goes my spleen...");
    }

    public static Identifier getId(String path) {
        return Identifier.of(MOD_ID, path);
    }
}