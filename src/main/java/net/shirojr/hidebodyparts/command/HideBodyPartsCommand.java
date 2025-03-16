package net.shirojr.hidebodyparts.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.shirojr.hidebodyparts.util.BodyPart;
import net.shirojr.hidebodyparts.util.cast.IBodyPartSaver;

import java.util.HashSet;
import java.util.Set;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HideBodyPartsCommand {

    private static final SimpleCommandExceptionType INVALID_PART =
            new SimpleCommandExceptionType(Text.literal("Body Part not found"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(literal("hide").requires(source -> source.hasPermissionLevel(2))
                .then(literal("bodyPart")
                        .then(literal("changeEntry")
                                .then(argument("bodyPartName", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            for (BodyPart entry : BodyPart.values()) {
                                                builder.suggest(entry.asString());
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(argument("target", EntityArgumentType.player())
                                                .executes(HideBodyPartsCommand::run))))
                        .then(literal("removeAllEntries")
                                .then(argument("target", EntityArgumentType.player())
                                        .executes(HideBodyPartsCommand::runRemoveAllEntries)))
                        .then(literal("addAllEntries")
                                .then(argument("target", EntityArgumentType.player())
                                        .executes(HideBodyPartsCommand::runEnableAllEntries))))
        );
    }

    private static int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String bodyPartInput = StringArgumentType.getString(context, "bodyPartName");
        BodyPart selectedPart = BodyPart.fromName(bodyPartInput);
        if (selectedPart == null) throw INVALID_PART.create();
        IBodyPartSaver targetPlayer = (IBodyPartSaver) EntityArgumentType.getPlayer(context, "target");

        targetPlayer.hidebodyparts$modifyInvisibleParts(invisibleParts -> {
            if (!invisibleParts.remove(selectedPart)) {
                invisibleParts.add(selectedPart);
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    private static int runRemoveAllEntries(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        IBodyPartSaver targetPlayer = (IBodyPartSaver) EntityArgumentType.getPlayer(context, "target");
        targetPlayer.hidebodyparts$modifyInvisibleParts(HashSet::clear);
        context.getSource().sendFeedback(() -> Text.translatable("feedback.bodypart.removed.all"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int runEnableAllEntries(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        IBodyPartSaver targetPlayer = (IBodyPartSaver) EntityArgumentType.getPlayer(context, "target");
        targetPlayer.hidebodyparts$modifyInvisibleParts(bodyParts -> bodyParts.addAll(Set.of(BodyPart.values())));
        context.getSource().sendFeedback(() -> Text.translatable("feedback.bodypart.added.all"), true);
        return Command.SINGLE_SUCCESS;
    }
}
