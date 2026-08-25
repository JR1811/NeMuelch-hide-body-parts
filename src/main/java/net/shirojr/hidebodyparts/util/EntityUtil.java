package net.shirojr.hidebodyparts.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class EntityUtil {
    @Nullable
    public static String getCachedPlayerNameOrUuid(MinecraftServer server, @Nullable UUID uuid) {
        if (uuid == null) return null;
        return Optional.ofNullable(server.getUserCache())
                .flatMap(userCache -> userCache.getByUuid(uuid).map(GameProfile::getName))
                .orElse(uuid.toString());
    }

    @Nullable
    public static Entity get(MinecraftServer server, UUID targetUuid) {
        for (ServerWorld world : server.getWorlds()) {
            Entity entity = world.getEntity(targetUuid);
            if (entity != null) return entity;
        }
        return null;
    }
}
