package net.shirojr.hidebodyparts.mixin;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.world.World;
import net.shirojr.hidebodyparts.network.packet.PlayerEntitySyncPacket;
import net.shirojr.hidebodyparts.util.BodyPart;
import net.shirojr.hidebodyparts.util.cast.IBodyPartSaver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.function.Consumer;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityDataMixin extends LivingEntity implements IBodyPartSaver {
    // private NbtCompound persistentData;

    @Shadow
    public abstract void remove(Entity.RemovalReason reason);

    @Unique
    private final HashSet<BodyPart> invisibleParts = new HashSet<>();

    protected PlayerEntityDataMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public HashSet<BodyPart> hidebodyparts$getInvisibleParts() {
        return new HashSet<>(this.invisibleParts);
    }

    @Override
    public void hidebodyparts$modifyInvisibleParts(Consumer<HashSet<BodyPart>> consumer) {
        consumer.accept(this.invisibleParts);
        if (this.getWorld().isClient()) return;
        PlayerEntity player = (PlayerEntity) (Object) this;
        new PlayerEntitySyncPacket(player.getId(), this.invisibleParts).sendPacket(player, PlayerLookup.tracking(player));
    }

    @Override
    public void hidebodyparts$modifyInvisiblePartsForNewEntity(int entityId, Consumer<HashSet<BodyPart>> consumer) {
        consumer.accept(this.invisibleParts);
        if (this.getWorld().isClient()) return;
        PlayerEntity player = (PlayerEntity) (Object) this;
        new PlayerEntitySyncPacket(player.getId(), this.invisibleParts).sendPacket(entityId, player, PlayerLookup.tracking(player));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    protected void hidebodyparts$injectCustomWriteNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtList bodyPartList = new NbtList();
        for (BodyPart part : this.hidebodyparts$getInvisibleParts()) {
            bodyPartList.add(NbtString.of(part.asString()));
        }
        nbt.put("invisibleParts", bodyPartList);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    protected void hidebodyparts$injectCustomReadNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtList bodyPartList = nbt.getList("invisibleParts", NbtElement.STRING_TYPE);
        HashSet<BodyPart> set = new HashSet<>();
        bodyPartList.forEach(nbtElement -> {
            BodyPart part = BodyPart.fromName(nbtElement.asString());
            if (part != null) set.add(part);
        });
        hidebodyparts$modifyInvisibleParts(bodyParts -> {
            bodyParts.clear();
            bodyParts.addAll(set);
        });
    }
}