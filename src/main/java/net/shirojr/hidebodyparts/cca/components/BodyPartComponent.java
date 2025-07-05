package net.shirojr.hidebodyparts.cca.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.shirojr.hidebodyparts.HideBodyParts;
import net.shirojr.hidebodyparts.cca.HideBodyPartsComponents;
import net.shirojr.hidebodyparts.util.BodyPart;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.function.Consumer;

public interface BodyPartComponent extends Component {
    Identifier KEY = HideBodyParts.getId("hidden_parts");

    @Nullable
    default BodyPartComponent fromEntity(Entity entity) {
        if (!(entity instanceof PlayerEntity player)) return null;
        return HideBodyPartsComponents.ACCESSORIES.get(player);
    }

    PlayerEntity getPlayer();

    HashSet<BodyPart> getHiddenBodyParts();

    void modifyHiddenBodyParts(Consumer<HashSet<BodyPart>>bodyPartsConsumer, boolean sync);

    default boolean isHidden(BodyPart part) {
        return getHiddenBodyParts().contains(part);
    }
}
