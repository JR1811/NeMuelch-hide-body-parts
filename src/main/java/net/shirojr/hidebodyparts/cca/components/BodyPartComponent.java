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

    /**
     * @return <code>null</code>, if the entity can't hide body parts
     */
    @Nullable
    static BodyPartComponent fromEntity(Entity entity) {
        if (!(entity instanceof PlayerEntity player)) return null;
        return HideBodyPartsComponents.ACCESSORIES.get(player);
    }

    @SuppressWarnings("unused")
    PlayerEntity getPlayer();

    HashSet<BodyPart> getHiddenBodyParts();

    void modifyHiddenBodyParts(Consumer<HashSet<BodyPart>> bodyPartsConsumer, boolean sync);

    @SuppressWarnings("unused")
    default boolean isHidden(BodyPart part) {
        return getHiddenBodyParts().contains(part);
    }
}
