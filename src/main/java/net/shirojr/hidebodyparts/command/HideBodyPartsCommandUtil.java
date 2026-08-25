package net.shirojr.hidebodyparts.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class HideBodyPartsCommandUtil {
    public static CommandNode<ServerCommandSource> getOrCreateNode(CommandDispatcher<ServerCommandSource> dispatcher) {
        CommandNode<ServerCommandSource> hideNode = dispatcher.getRoot().getChild("hide");
        if (hideNode == null) {
            hideNode = dispatcher.register(literal("hide"));
        }
        CommandNode<ServerCommandSource> bodyPartNode = hideNode.getChild("bodyPart");
        if (bodyPartNode == null) {
            hideNode.addChild(literal("bodyPart").build());
            bodyPartNode = hideNode.getChild("bodyPart");
        }
        return bodyPartNode;
    }
}
