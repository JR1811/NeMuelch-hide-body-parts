package net.shirojr.hidebodyparts.util.cast;

import net.minecraft.nbt.NbtCompound;

import java.util.function.Function;

public interface IBodyPartSaver {
    NbtCompound getPersistentData();

    <T> T editPersistentData(Function<NbtCompound, T> action);

    record Wrapper(NbtCompound nbt) {}
}
