package net.shirojr.hidebodyparts.init;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.shirojr.hidebodyparts.HideBodyParts;
import net.shirojr.hidebodyparts.enchant.InvisibleArmorEnchantment;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public interface HideBodyPartsEnchantments {
    List<Enchantment> ALL_ENCHANTMENTS = new ArrayList<>();

    InvisibleArmorEnchantment INVISIBLE_ARMOR = register(
            "invisible_armor",
            new InvisibleArmorEnchantment(
                    Enchantment.Rarity.VERY_RARE,
                    EnchantmentTarget.ARMOR,
                    EquipmentSlot.HEAD,
                    EquipmentSlot.CHEST,
                    EquipmentSlot.LEGS,
                    EquipmentSlot.FEET
            )
    );


    @SuppressWarnings("SameParameterValue")
    private static <T extends Enchantment> T register(String name, T entry) {
        T registeredEntry = Registry.register(Registries.ENCHANTMENT, HideBodyParts.getId(name), entry);
        ALL_ENCHANTMENTS.add(registeredEntry);
        return registeredEntry;
    }

    static void initialize() {
        // static initialisation
    }
}
