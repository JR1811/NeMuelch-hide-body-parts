package net.shirojr.hidebodyparts.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.shirojr.hidebodyparts.HideBodyParts;
import net.shirojr.hidebodyparts.util.cast.IBodyPartSaver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityDataMixin extends LivingEntity implements IBodyPartSaver {
	// private NbtCompound persistentData;

	@Shadow
	public abstract void remove(Entity.RemovalReason reason);

	@Unique
	@SuppressWarnings("WrongEntityDataParameterClass")
	private static final TrackedData<NbtCompound> HIDDEN_BODYPARTS = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);

	protected PlayerEntityDataMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "initDataTracker", at = @At("TAIL"))
	protected void hidebodyparts$initDataTracker(CallbackInfo ci) {
		this.dataTracker.startTracking(HIDDEN_BODYPARTS, new NbtCompound());
	}

	@Override
	public NbtCompound hidebodyparts$getPersistentData() {
		return getDataTracker().get(HIDDEN_BODYPARTS);
	}

	@Override
	public <T> T hidebodyparts$editPersistentData(Function<NbtCompound, T> action) {
		var wrapper = this.hidebodyparts$getPersistentData().copy();

		T result = action.apply(wrapper);
		this.dataTracker.set(HIDDEN_BODYPARTS, wrapper);
		return result;
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
	protected void hidebodyparts$injectCustomWriteNbt(NbtCompound nbt, CallbackInfo ci) {
		NbtCompound hiddenParts = this.dataTracker.get(HIDDEN_BODYPARTS);

		if (!hiddenParts.isEmpty()) {
			nbt.put(HideBodyParts.NBT_KEY, hiddenParts);
		}
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
	protected void hidebodyparts$injectCustomReadNbt(NbtCompound nbt, CallbackInfo ci) {
		if (nbt.contains(HideBodyParts.NBT_KEY)) {
			this.dataTracker.set(HIDDEN_BODYPARTS, nbt.getCompound(HideBodyParts.NBT_KEY));
		}
	}
}