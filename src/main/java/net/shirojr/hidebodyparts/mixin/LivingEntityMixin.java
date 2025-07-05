package net.shirojr.hidebodyparts.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.shirojr.hidebodyparts.api.BodyPartHider;
import net.shirojr.hidebodyparts.cca.components.BodyPartComponent;
import net.shirojr.hidebodyparts.util.BodyPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Map;
import java.util.function.Predicate;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract boolean areItemsDifferent(ItemStack stack, ItemStack stack2);

    @Inject(method = "getEquipmentChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;areItemsDifferent(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
    private void adjustHiddenPartsFromEquipmentStack(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir,
                                                     @Local EquipmentSlot equipmentSlot,
                                                     @Local(ordinal = 0) ItemStack oldStack, @Local(ordinal = 1) ItemStack newStack) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.getWorld().isClient()) return;
        BodyPartComponent target = BodyPartComponent.fromEntity(entity);
        if (target == null) return;
        if (!areItemsDifferent(oldStack, newStack)) return;
        HashSet<BodyPart> hiddenParts = target.getHiddenBodyParts();

        if (oldStack.getItem() instanceof BodyPartHider hider) {
            if (equipmentSlot.getType().equals(EquipmentSlot.Type.ARMOR)) {
                Predicate<BodyPart> wearingPartsPredicate = bodyPart -> hider.hideOnWearing(entity, equipmentSlot).contains(bodyPart);
                if (hiddenParts.stream().anyMatch(wearingPartsPredicate)) {
                    target.modifyHiddenBodyParts(parts -> parts.removeAll(hider.hideOnWearing(entity, equipmentSlot)), true);
                }
            } else if (equipmentSlot.getType().equals(EquipmentSlot.Type.HAND)) {
                Predicate<BodyPart> holdingPartsPredicate = bodyPart -> hider.hideOnHolding(entity, getHand(equipmentSlot)).contains(bodyPart);
                if (hiddenParts.stream().anyMatch(holdingPartsPredicate)) {
                    target.modifyHiddenBodyParts(parts -> parts.removeAll(hider.hideOnHolding(entity, getHand(equipmentSlot))), true);
                }
            }
        }

        if (newStack.getItem() instanceof BodyPartHider hider) {
            if (equipmentSlot.getType().equals(EquipmentSlot.Type.ARMOR)) {
                if (!hiddenParts.containsAll(hider.hideOnWearing(entity, equipmentSlot))) {
                    target.modifyHiddenBodyParts(parts -> parts.addAll(hider.hideOnWearing(entity, equipmentSlot)), true);
                }
            } else if (equipmentSlot.getType().equals(EquipmentSlot.Type.HAND)) {
                if (!hiddenParts.containsAll(hider.hideOnHolding(entity, getHand(equipmentSlot)))) {
                    target.modifyHiddenBodyParts(parts -> parts.addAll(hider.hideOnHolding(entity, getHand(equipmentSlot))), true);
                }
            }
        }
    }

    @Unique
    private Hand getHand(EquipmentSlot slot) {
        return slot.equals(EquipmentSlot.OFFHAND) ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }
}
