package net.shirojr.hidebodyparts.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.shirojr.hidebodyparts.init.HideBodyPartsEnchantments;
import net.shirojr.hidebodyparts.init.HideBodyPartsTags;
import net.shirojr.hidebodyparts.util.HideBodyPartsNbtKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    private ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"), cancellable = true)
    private void preventInvisibleArmorRendering(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity,
                                                EquipmentSlot armorSlot, int light, A model, CallbackInfo ci, @Local(ordinal = 0) ItemStack equippedStack) {
        NbtCompound stackNbt = equippedStack.getNbt();
        if (stackNbt != null && stackNbt.contains(HideBodyPartsNbtKeys.INVISIBLE_ARMOR) && stackNbt.getBoolean(HideBodyPartsNbtKeys.INVISIBLE_ARMOR)) {
            ci.cancel();
            return;
        }
        if (EnchantmentHelper.get(equippedStack).containsKey(HideBodyPartsEnchantments.INVISIBLE_ARMOR)) {
            ci.cancel();
            return;
        }
        if (equippedStack.isIn(HideBodyPartsTags.Items.INVISIBLE_ARMOR)) {
            ci.cancel();
        }
    }
}
