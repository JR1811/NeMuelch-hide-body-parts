package net.shirojr.hidebodyparts.cca;

import net.shirojr.hidebodyparts.cca.components.BodyPartComponent;
import net.shirojr.hidebodyparts.cca.implementation.HiddenBodyPartsImpl;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

public class HideBodyPartsComponents implements EntityComponentInitializer {
    public static final ComponentKey<BodyPartComponent> ACCESSORIES =
            ComponentRegistry.getOrCreate(BodyPartComponent.KEY, BodyPartComponent.class);


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(ACCESSORIES, HiddenBodyPartsImpl::new, HiddenBodyPartsImpl::onRespawn);
    }
}
