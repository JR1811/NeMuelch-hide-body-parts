package net.shirojr.hidebodyparts.util.cast;

import net.shirojr.hidebodyparts.util.BodyPart;

import java.util.HashSet;
import java.util.function.Consumer;

public interface IBodyPartSaver {
    HashSet<BodyPart> hidebodyparts$getInvisibleParts();

    void hidebodyparts$modifyInvisibleParts(Consumer<HashSet<BodyPart>> invisibleBodyPartsConsumer);

    void hidebodyparts$modifyInvisiblePartsForNewEntity(int entityId, Consumer<HashSet<BodyPart>> invisibleBodyPartsConsumer);
}
