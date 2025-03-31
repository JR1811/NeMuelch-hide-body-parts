package net.shirojr.hidebodyparts.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.shirojr.hidebodyparts.api.BodyPartHider;
import net.shirojr.hidebodyparts.util.BodyPart;

import java.util.List;

public class HoldingDebugItem extends Item implements BodyPartHider {
    public HoldingDebugItem(Settings settings) {
        super(settings);
    }

    @Override
    public List<BodyPart> hideOnHolding(LivingEntity livingEntity, Hand hand) {
        return List.of(BodyPart.HEAD, BodyPart.LEFT_ARM);
    }
}
