package net.shirojr.hidebodyparts.cca.implementation;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.shirojr.hidebodyparts.cca.HideBodyPartsComponents;
import net.shirojr.hidebodyparts.cca.components.BodyPartComponent;
import net.shirojr.hidebodyparts.event.custom.BodyPartCallbacks;
import net.shirojr.hidebodyparts.util.BodyPart;

import java.util.HashSet;
import java.util.function.Consumer;

public class HiddenBodyPartsImpl implements BodyPartComponent, AutoSyncedComponent {
    private final PlayerEntity player;
    private final HashSet<BodyPart> hiddenParts;

    public HiddenBodyPartsImpl(PlayerEntity player) {
        this.player = player;
        this.hiddenParts = new HashSet<>();
    }

    @Override
    public PlayerEntity getPlayer() {
        return player;
    }

    @Override
    public HashSet<BodyPart> getHiddenBodyParts() {
        return new HashSet<>(hiddenParts);
    }

    @Override
    public boolean modifyHiddenBodyParts(Consumer<HashSet<BodyPart>> bodyPartsConsumer, boolean sync) {
        HashSet<BodyPart> oldSet = new HashSet<>(this.hiddenParts);
        bodyPartsConsumer.accept(this.hiddenParts);
        if (oldSet.equals(getHiddenBodyParts())) return false;

        HashSet<BodyPart> addedParts = new HashSet<>(this.hiddenParts);
        addedParts.removeAll(oldSet);
        BodyPartCallbacks.BODY_PART_ADDED.invoker().onBodyPartAdded(this.player, addedParts);

        HashSet<BodyPart> removedParts = new HashSet<>(oldSet);
        removedParts.removeAll(this.hiddenParts);
        BodyPartCallbacks.BODY_PART_REMOVED.invoker().onBodyPartRemoved(this.player, removedParts);

        if (sync) {
            HideBodyPartsComponents.ACCESSORIES.sync(this.player);
        }
        return true;
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        modifyHiddenBodyParts(parts -> {
            parts.clear();
            parts.addAll(BodyPart.fromNbt(nbt));
        }, true);
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        BodyPart.toNbt(this.hiddenParts, nbt);
    }

    @SuppressWarnings("unused")
    public static void onRespawn(HiddenBodyPartsImpl from, HiddenBodyPartsImpl to, boolean lossless, boolean keepInventory, boolean sameCharacter) {
        to.modifyHiddenBodyParts(parts -> {
            parts.clear();
            parts.addAll(from.hiddenParts);
        }, true);
    }
}
