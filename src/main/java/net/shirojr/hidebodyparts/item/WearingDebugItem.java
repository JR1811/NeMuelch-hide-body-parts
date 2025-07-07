package net.shirojr.hidebodyparts.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.shirojr.hidebodyparts.api.BodyPartHider;
import net.shirojr.hidebodyparts.util.BodyPart;

import java.util.List;

public class WearingDebugItem extends ArmorItem implements BodyPartHider {
    public WearingDebugItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public List<BodyPart> hideOnWearing(LivingEntity livingEntity, EquipmentSlot slot) {
        return List.of(BodyPart.LEFT_LEG, BodyPart.RIGHT_LEG);
    }
}
