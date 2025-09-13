package net.shirojr.hidebodyparts.enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class InvisibleArmorEnchantment extends Enchantment {
    public InvisibleArmorEnchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot... slots) {
        super(weight, target, slots);
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }
}

