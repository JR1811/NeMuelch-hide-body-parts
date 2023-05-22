package net.shirojr.hidebodyparts;

import net.fabricmc.api.ModInitializer;

import net.shirojr.hidebodyparts.event.HideBodyPartsEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HideBodyParts implements ModInitializer {
    public static final String MOD_ID = "hide-body-parts";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        HideBodyPartsEvents.registerEvents();
    }
}