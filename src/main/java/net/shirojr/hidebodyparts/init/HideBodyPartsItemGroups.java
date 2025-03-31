package net.shirojr.hidebodyparts.init;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shirojr.hidebodyparts.HideBodyParts;

public interface HideBodyPartsItemGroups {
    RegistryKey<ItemGroup> HIDE_BODY_PARTS_ITEM_GROUP = registerItemGroup("hide_body_parts",
            Text.translatable("itemgroup.hide_body_parts.hide_body_parts"), new ItemStack(HideBodyPartsItems.HOLDING_DEBUG));

    private static RegistryKey<ItemGroup> registerItemGroup(String name, Text displayName, ItemStack displayItemStack) {
        ItemGroup group = FabricItemGroup.builder().icon(() -> displayItemStack).displayName(displayName).build();
        Identifier groupIdentifier = HideBodyParts.getId(name);
        Registry.register(Registries.ITEM_GROUP, groupIdentifier, group);
        return RegistryKey.of(RegistryKeys.ITEM_GROUP, groupIdentifier);
    }

    private static void initializeItemGroups() {
        ItemGroupEvents.modifyEntriesEvent(HIDE_BODY_PARTS_ITEM_GROUP).register(boatismEntries -> boatismEntries.addAll(HideBodyPartsItems.ALL_ITEMS));
    }

    static void initialize() {
        // static initialisation
        initializeItemGroups();
    }
}
