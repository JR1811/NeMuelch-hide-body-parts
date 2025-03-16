package net.shirojr.hidebodyparts;

import net.fabricmc.api.ClientModInitializer;
import net.shirojr.hidebodyparts.network.HideBodyPartsS2C;

public class HideBodyPartsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HideBodyPartsS2C.initialize();
    }
}
