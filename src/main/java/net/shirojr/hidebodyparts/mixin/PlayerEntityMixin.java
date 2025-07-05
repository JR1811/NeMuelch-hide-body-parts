package net.shirojr.hidebodyparts.mixin;

import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.shirojr.hidebodyparts.cca.components.BodyPartComponent;
import net.shirojr.hidebodyparts.util.BodyPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow
    public abstract void remove(RemovalReason reason);

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "isPartVisible", at = @At("HEAD"), cancellable = true)
    private void isSecondLayerVisible(PlayerModelPart modelPart, CallbackInfoReturnable<Boolean> cir) {
        BodyPartComponent target = BodyPartComponent.fromEntity(this);
        if (target == null) return;
        for (BodyPart entry : target.getHiddenBodyParts()) {
            if (!entry.getSecondLayer().contains(modelPart)) continue;
            cir.setReturnValue(false);
            return;
        }
    }
}
