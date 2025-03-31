package net.shirojr.hidebodyparts.api;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.shirojr.hidebodyparts.util.BodyPart;

import java.util.List;

/**
 * Dynamic Body Part hiding interface for custom items.<br>
 * This only applies for {@link net.minecraft.entity.player.PlayerEntity PlayerEntities}.
 *
 * @see net.shirojr.hidebodyparts.mixin.PlayerEntityDataMixin PlayerEntityDataMixin
 */
@SuppressWarnings("unused")
public interface BodyPartHider {
    /**
     * Toggles visibility of {@link BodyPart BodyParts} based on if the item is in either the main hand or offhand.
     *
     * @param livingEntity entity which is holding the item
     * @param hand         hand, in which the item is being held in
     * @return list of body parts which will be toggled on holding this item
     */
    default List<BodyPart> hideOnHolding(LivingEntity livingEntity, Hand hand) {
        return List.of();
    }

    /**
     * Toggles visibility of {@link BodyPart BodyParts} based on if the item is in an Armor Slot.
     *
     * @param livingEntity entity which is wearing the item
     * @param slot         armor slot, in which this item is being worn
     * @return list of body parts which will be toggled on wearing this item
     */
    default List<BodyPart> hideOnWearing(LivingEntity livingEntity, EquipmentSlot slot) {
        return List.of();
    }
}
