package net.shirojr.hidebodyparts.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.shirojr.hidebodyparts.cca.components.BodyPartComponent;
import net.shirojr.hidebodyparts.cca.implementation.RequestComponent;
import net.shirojr.hidebodyparts.command.argument.RequestStateArgumentType;
import net.shirojr.hidebodyparts.request.RequestEntry;
import net.shirojr.hidebodyparts.request.RequestState;
import net.shirojr.hidebodyparts.util.BodyPart;
import net.shirojr.hidebodyparts.util.EntityUtil;

import java.util.*;
import java.util.function.Predicate;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HideBodyPartsCommand {

    private static final SimpleCommandExceptionType INVALID_PART =
            new SimpleCommandExceptionType(Text.literal("Body Part not found"));
    private static final SimpleCommandExceptionType INVALID_HOLDER =
            new SimpleCommandExceptionType(Text.literal("Entity can't hide Body Parts"));
    private static final SimpleCommandExceptionType MISSING_REQUESTER =
            new SimpleCommandExceptionType(Text.literal("No requester found"));
    private static final SimpleCommandExceptionType NO_SUCH_ENTRY =
            new SimpleCommandExceptionType(Text.literal("Entry not found"));
    private static final Dynamic2CommandExceptionType ALREADY_HANDLED = new Dynamic2CommandExceptionType((oName, oState) -> {
        if (!(oName instanceof String name)) return Text.literal("Request already handled");
        if (!(oState instanceof RequestState requestState)) return Text.literal("Request already handled");
        return Text.literal("Request already handled by %s (%s)".formatted(name, requestState.asString()));
    });
    private static final SimpleCommandExceptionType NO_ENTRIES =
            new SimpleCommandExceptionType(Text.literal("No entries present"));
    private static final SimpleCommandExceptionType NO_HANDLER =
            new SimpleCommandExceptionType(Text.literal("No Request Handler found"));
    private static final SimpleCommandExceptionType REQUEST_APPLY_ATTEMPT_FAILED =
            new SimpleCommandExceptionType(Text.literal("Attempt to apply request failed"));

    private static final Predicate<ServerCommandSource> HIGH_PERMISSION_LEVEL = source -> source.hasPermissionLevel(2);
    private static final SuggestionProvider<ServerCommandSource> BODY_PART_SUGGESTIONS = (context, builder) -> {
        for (BodyPart entry : BodyPart.values()) {
            builder.suggest(entry.asString());
        }
        return builder.buildFuture();
    };

    @SuppressWarnings("unused")
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {

        LiteralCommandNode<ServerCommandSource> changeEntrySubCommand = literal("changeEntry").requires(HIGH_PERMISSION_LEVEL)
                .then(argument("bodyPartName", StringArgumentType.word())
                        .suggests(BODY_PART_SUGGESTIONS)
                        .then(argument("target", EntityArgumentType.player())
                                .then(argument("hidden", BoolArgumentType.bool())
                                        .executes(HideBodyPartsCommand::run))
                        )
                )
                .build();
        LiteralCommandNode<ServerCommandSource> removeAllCommand = literal("removeAllEntries").requires(HIGH_PERMISSION_LEVEL)
                .then(argument("target", EntityArgumentType.player())
                        .executes(HideBodyPartsCommand::runRemoveAllEntries))
                .build();

        LiteralCommandNode<ServerCommandSource> addAllCommand = literal("addAllEntries").requires(HIGH_PERMISSION_LEVEL)
                .then(argument("target", EntityArgumentType.player())
                        .executes(HideBodyPartsCommand::runEnableAllEntries))
                .build();

        LiteralCommandNode<ServerCommandSource> requestCommand = literal("request")
                .then(literal("create")
                        .then(argument("bodyPartName", StringArgumentType.word())
                                .suggests(BODY_PART_SUGGESTIONS)
                                .then(argument("hidden", BoolArgumentType.bool())
                                        .executes(HideBodyPartsCommand::runRequest)
                                )
                        )
                )
                .then(literal("handle").requires(HIGH_PERMISSION_LEVEL)
                        .then(argument("entry", UuidArgumentType.uuid())
                                .then(argument("requestState", RequestStateArgumentType.requestState())
                                        .executes(HideBodyPartsCommand::handleRequest)
                                )
                        )
                )
                .then(literal("list").requires(HIGH_PERMISSION_LEVEL)
                        .then(argument("amount", IntegerArgumentType.integer(1))
                                .executes(context -> HideBodyPartsCommand.listRequests(context, false))
                                .then(argument("pendingOnly", BoolArgumentType.bool())
                                        .executes(context -> HideBodyPartsCommand.listRequests(context, BoolArgumentType.getBool(context, "pendingOnly")))
                                )
                        )
                )
                .then(literal("print").requires(HIGH_PERMISSION_LEVEL)
                        .then(argument("entry", UuidArgumentType.uuid())
                                .executes(HideBodyPartsCommand::printRequest)
                        )
                )
                .then(literal("clear").requires(HIGH_PERMISSION_LEVEL)
                        .then(literal("all")
                                .executes(context -> HideBodyPartsCommand.clearRequests(context, false))
                        )
                        .then(literal("handled")
                                .executes(context -> HideBodyPartsCommand.clearRequests(context, true))
                        )
                        .then(literal("entry")
                                .then(argument("uuid", UuidArgumentType.uuid())
                                        .executes(HideBodyPartsCommand::clearRequestEntry)
                                )
                        )
                )
                .build();

        HideBodyPartsCommandUtil.getOrCreateNode(dispatcher).addChild(changeEntrySubCommand);
        HideBodyPartsCommandUtil.getOrCreateNode(dispatcher).addChild(removeAllCommand);
        HideBodyPartsCommandUtil.getOrCreateNode(dispatcher).addChild(addAllCommand);
        HideBodyPartsCommandUtil.getOrCreateNode(dispatcher).addChild(requestCommand);
    }

    private static int clearRequestEntry(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getServer();
        RequestComponent component = RequestComponent.get(server);
        UUID entryUuid = UuidArgumentType.getUuid(context, "uuid");
        if (!component.remove(entryUuid)) {
            throw NO_SUCH_ENTRY.create();
        }
        context.getSource().sendFeedback(() -> Text.literal("Request entry cleared"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int clearRequests(CommandContext<ServerCommandSource> context, boolean handledOnly) {
        MinecraftServer server = context.getSource().getServer();
        RequestComponent component = RequestComponent.get(server);
        if (handledOnly) component.clearHandled();
        else component.clear();
        context.getSource().sendFeedback(() -> Text.literal("Cleared%s requests".formatted(handledOnly ? " finished" : "")), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int printRequest(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getServer();
        UUID entryUuid = UuidArgumentType.getUuid(context, "entry");
        RequestComponent component = RequestComponent.get(server);
        if (!component.contains(entryUuid)) throw NO_SUCH_ENTRY.create();
        sendEntryFeedback(context.getSource(), server, entryUuid);
        return Command.SINGLE_SUCCESS;
    }

    private static int listRequests(CommandContext<ServerCommandSource> context, boolean pendingOnly) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getServer();
        RequestComponent component = RequestComponent.get(server);
        int amount = Math.min(IntegerArgumentType.getInteger(context, "amount"), component.size(pendingOnly));
        if (amount <= 0) {
            throw NO_ENTRIES.create();
        }
        int currentCount = 0;
        for (Map.Entry<UUID, RequestEntry> entry : component.getEntries(pendingOnly).entrySet()) {
            if (currentCount++ >= amount) break;
            sendEntryFeedback(context.getSource(), server, entry.getKey());
        }
        return Command.SINGLE_SUCCESS;
    }

    private static void sendEntryFeedback(ServerCommandSource source, MinecraftServer server, UUID entryUuid) {
        RequestComponent component = RequestComponent.get(server);
        List<Text> lines = component.printEntry(entryUuid);
        if (lines == null) return;
        lines.forEach(text -> source.sendFeedback(() -> text, true));
    }

    private static int handleRequest(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            throw NO_HANDLER.create();
        }
        MinecraftServer server = context.getSource().getServer();
        UUID entryUuid = UuidArgumentType.getUuid(context, "entry");
        RequestComponent component = RequestComponent.get(server);
        Optional<RequestEntry> selectedEntry = component.get(entryUuid);
        if (selectedEntry.isEmpty()) throw NO_SUCH_ENTRY.create();
        if (selectedEntry.get().state() != RequestState.REQUESTED) {
            throw ALREADY_HANDLED.create(
                    Optional.ofNullable(selectedEntry.get().handler())
                            .map(uuid -> EntityUtil.getCachedPlayerNameOrUuid(server, uuid))
                            .orElse(null),
                    selectedEntry.get().state()
            );
        }
        RequestState requestState = RequestStateArgumentType.getRequestState(context, "requestState");
        boolean appliedRequest = component.resolve(entryUuid, requestState, player.getUuid());
        if (!appliedRequest) {
            throw REQUEST_APPLY_ATTEMPT_FAILED.create();
        }
        MutableText feedback = Text.literal("Set request %s to %s".formatted(entryUuid, requestState.asString()))
                .styled(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, entryUuid.toString()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Copy Entry UUID"))));
        context.getSource().sendFeedback(() -> feedback, true);
        return Command.SINGLE_SUCCESS;
    }

    private static int runRequest(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String bodyPartInput = StringArgumentType.getString(context, "bodyPartName");
        BodyPart part = BodyPart.fromName(bodyPartInput);
        if (part == null) throw INVALID_PART.create();
        HashSet<BodyPart> requestedParts = new HashSet<>();
        requestedParts.add(part);
        boolean hidden = BoolArgumentType.getBool(context, "hidden");
        ServerPlayerEntity requester = context.getSource().getPlayer();
        if (requester == null) {
            throw MISSING_REQUESTER.create();
        }

        RequestComponent component = RequestComponent.get(context.getSource().getServer());

        component.add(new RequestEntry(requester.getUuid(), requestedParts, hidden));
        context.getSource().sendFeedback(() -> Text.literal("Request sent"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String bodyPartInput = StringArgumentType.getString(context, "bodyPartName");
        BodyPart selectedPart = BodyPart.fromName(bodyPartInput);
        if (selectedPart == null) throw INVALID_PART.create();
        BodyPartComponent target = BodyPartComponent.fromEntity(EntityArgumentType.getPlayer(context, "target"));
        if (target == null) {
            throw INVALID_HOLDER.create();
        }
        boolean hidden = BoolArgumentType.getBool(context, "hidden");

        target.modifyHiddenBodyParts(invisibleParts -> {
            if (hidden) invisibleParts.add(selectedPart);
            else invisibleParts.remove(selectedPart);
        }, true);
        return Command.SINGLE_SUCCESS;
    }

    private static int runRemoveAllEntries(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        BodyPartComponent target = BodyPartComponent.fromEntity(EntityArgumentType.getPlayer(context, "target"));
        if (target == null) {
            throw INVALID_HOLDER.create();
        }
        target.modifyHiddenBodyParts(HashSet::clear, true);
        context.getSource().sendFeedback(() -> Text.translatable("feedback.bodypart.removed.all"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int runEnableAllEntries(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        BodyPartComponent target = BodyPartComponent.fromEntity(EntityArgumentType.getPlayer(context, "target"));
        if (target == null) {
            throw INVALID_HOLDER.create();
        }
        target.modifyHiddenBodyParts(bodyParts -> bodyParts.addAll(Set.of(BodyPart.values())), true);
        context.getSource().sendFeedback(() -> Text.translatable("feedback.bodypart.added.all"), true);
        return Command.SINGLE_SUCCESS;
    }
}
