package net.shirojr.hidebodyparts.init;

import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.shirojr.hidebodyparts.HideBodyParts;
import net.shirojr.hidebodyparts.item.HoldingDebugItem;
import net.shirojr.hidebodyparts.item.WearingDebugItem;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public interface HideBodyPartsItems {
    List<ItemStack> ALL_ITEMS = new ArrayList<>();

    HoldingDebugItem HOLDING_DEBUG = register("holding_debug", new HoldingDebugItem(new Item.Settings()));
    WearingDebugItem WEARING_DEBUG = register("wearing_debug", new WearingDebugItem(ArmorMaterials.IRON, ArmorItem.Type.HELMET, new Item.Settings().maxCount(1)));

    private static <T extends Item> T register(String name, T entry) {
        T registeredEntry = Registry.register(Registries.ITEM, HideBodyParts.getId(name), entry);
        ALL_ITEMS.add(registeredEntry.getDefaultStack());
        return registeredEntry;
    }

    static void initialize() {
        // static initialisation
    }
}
