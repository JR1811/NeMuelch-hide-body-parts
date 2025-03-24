package net.shirojr.hidebodyparts.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.shirojr.hidebodyparts.util.BodyPart;
import net.shirojr.hidebodyparts.util.cast.IBodyPartSaver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "renderArm", at = @At("HEAD"), cancellable = true)
    private void disableFirstPersonArmRendering(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Arm arm, CallbackInfo ci) {
        if (disabledEntry(arm)) ci.cancel();
    }

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", ordinal = 0),
            cancellable = true
    )
    private void disableFirstPersonItemRendering(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers,
                                                 ClientPlayerEntity player, int light, CallbackInfo ci, @Local Hand hand) {
        Arm arm = hand == Hand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
        if (disabledEntry(arm)) ci.cancel();
    }

    @Unique
    private boolean disabledEntry(Arm arm) {
        ClientPlayerEntity clientPlayer = client.player;
        if (clientPlayer == null) return false;
        HashSet<BodyPart> invisibleParts = ((IBodyPartSaver) clientPlayer).hidebodyparts$getInvisibleParts();

        if (arm.equals(Arm.LEFT) && invisibleParts.contains(BodyPart.LEFT_ARM)) {
            return true;
        }
        return arm.equals(Arm.RIGHT) && invisibleParts.contains(BodyPart.RIGHT_ARM);
    }
}
