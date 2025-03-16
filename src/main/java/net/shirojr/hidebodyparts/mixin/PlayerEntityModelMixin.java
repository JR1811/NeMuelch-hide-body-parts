package net.shirojr.hidebodyparts.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.shirojr.hidebodyparts.util.BodyPart;
import net.shirojr.hidebodyparts.util.cast.IBodyPartSaver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityModelMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public PlayerEntityModelMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    public void hidebodyparts$render(AbstractClientPlayerEntity clientPlayer, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        HashSet<BodyPart> invisibleParts = ((IBodyPartSaver) clientPlayer).hidebodyparts$getInvisibleParts();
        setVisible(invisibleParts);
    }

    @Unique
    protected void setVisible(HashSet<BodyPart> invisibleParts) {
        this.model.head.visible = true;
        this.model.hat.visible = true;
        this.model.body.visible = true;
        this.model.rightArm.visible = true;
        this.model.leftArm.visible = true;
        this.model.rightLeg.visible = true;
        this.model.leftLeg.visible = true;

        if (invisibleParts.contains(BodyPart.HEAD)) {
            this.model.head.visible = false;
            this.model.hat.visible = false;
        }
        if (invisibleParts.contains(BodyPart.BODY)) {
            this.model.body.visible = false;
        }
        if (invisibleParts.contains(BodyPart.RIGHT_ARM)) {
            this.model.rightArm.visible = false;
        }
        if (invisibleParts.contains(BodyPart.LEFT_ARM)) {
            this.model.leftArm.visible = false;
        }
        if (invisibleParts.contains(BodyPart.RIGHT_LEG)) {
            this.model.rightLeg.visible = false;
        }
        if (invisibleParts.contains(BodyPart.LEFT_LEG)) {
            this.model.leftLeg.visible = false;
        }
    }
}
