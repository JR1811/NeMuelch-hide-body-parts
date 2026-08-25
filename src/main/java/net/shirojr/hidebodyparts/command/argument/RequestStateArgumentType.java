package net.shirojr.hidebodyparts.command.argument;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.shirojr.hidebodyparts.request.RequestState;

public class RequestStateArgumentType extends EnumArgumentType<RequestState> {
    private RequestStateArgumentType() {
        super(RequestState.CODEC, RequestState::values);
    }

    public static EnumArgumentType<RequestState> requestState() {
        return new RequestStateArgumentType();
    }

    public static RequestState getRequestState(CommandContext<ServerCommandSource> context, String id) {
        return context.getArgument(id, RequestState.class);
    }
}
