package net.shirojr.hidebodyparts.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.shirojr.hidebodyparts.cca.components.BodyPartComponent;
import net.shirojr.hidebodyparts.cca.implementation.HiddenBodyPartsImpl;

public class HideBodyPartsComponents implements EntityComponentInitializer {
    public static final ComponentKey<BodyPartComponent> ACCESSORIES =
            ComponentRegistry.getOrCreate(BodyPartComponent.KEY, BodyPartComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(ACCESSORIES, HiddenBodyPartsImpl::new, HiddenBodyPartsImpl::onRespawn);
    }
}
