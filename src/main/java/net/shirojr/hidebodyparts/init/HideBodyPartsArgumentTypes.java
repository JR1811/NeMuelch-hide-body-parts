package net.shirojr.hidebodyparts.init;

import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.shirojr.hidebodyparts.HideBodyParts;
import net.shirojr.hidebodyparts.command.argument.RequestStateArgumentType;

public class HideBodyPartsArgumentTypes {
    static {
        register("request_state", RequestStateArgumentType.class, ConstantArgumentSerializer.of(RequestStateArgumentType::requestState));
    }


    @SuppressWarnings("SameParameterValue")
    private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> void register(
            String name,
            Class<? extends A> clazz,
            ArgumentSerializer<A, T> serializer) {
        ArgumentTypeRegistry.registerArgumentType(HideBodyParts.getId(name), clazz, serializer);
    }

    public static void initialize() {
        // static initialisation
    }
}
