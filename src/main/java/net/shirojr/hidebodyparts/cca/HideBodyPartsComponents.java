package net.shirojr.hidebodyparts.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import net.shirojr.hidebodyparts.cca.components.BodyPartComponent;
import net.shirojr.hidebodyparts.cca.implementation.HiddenBodyPartsImpl;
import net.shirojr.hidebodyparts.cca.implementation.RequestComponent;

public class HideBodyPartsComponents implements EntityComponentInitializer, ScoreboardComponentInitializer {
    public static final ComponentKey<BodyPartComponent> ACCESSORIES =
            ComponentRegistry.getOrCreate(BodyPartComponent.KEY, BodyPartComponent.class);
    public static final ComponentKey<RequestComponent> REQUEST =
            ComponentRegistry.getOrCreate(RequestComponent.KEY, RequestComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(ACCESSORIES, HiddenBodyPartsImpl::new, HiddenBodyPartsImpl::onRespawn);
    }

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(REQUEST, RequestComponent::new);
    }
}
