package org.jurassicraft.server.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jurassicraft.JurassiCraft;

import java.util.concurrent.CompletableFuture;

public class SpawnStructureCommand {

    private static final SuggestionProvider<CommandSourceStack> STRUCTURE_SUGGESTIONS = (context, builder) -> {
        return SharedSuggestionProvider.suggest(new String[]{"visitor_center", "raptor_paddock"}, builder);
    };

    public static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("spawnjc")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("structure", StringArgumentType.string())
                        .suggests(STRUCTURE_SUGGESTIONS)
                        .executes(SpawnStructureCommand::executeStructureAtSender)
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(SpawnStructureCommand::executeStructureAtPos)));
    }

    private static int executeStructureAtSender(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String structureName = StringArgumentType.getString(context, "structure");
        BlockPos pos = BlockPos.containing(source.getPosition());
        
        return executeStructure(source, structureName, pos);
    }

    private static int executeStructureAtPos(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String structureName = StringArgumentType.getString(context, "structure");
        BlockPos pos = BlockPosArgument.getBlockPos(context, "pos");
        
        return executeStructure(source, structureName, pos);
    }

    private static int executeStructure(CommandSourceStack source, String structureName, BlockPos pos) {
        // TODO: Implement structure generation system
        // This is a placeholder implementation
        JurassiCraft.getLogger().info("Attempting to spawn structure '{}' at {}", structureName, pos);
        
        switch (structureName.toLowerCase()) {
            case "visitor_center":
                source.sendSuccess(() -> Component.literal("Spawning Visitor Center at " + pos.toShortString()), true);
                // TODO: Generate visitor center structure
                break;
            case "raptor_paddock":
                source.sendSuccess(() -> Component.literal("Spawning Raptor Paddock at " + pos.toShortString()), true);
                // TODO: Generate raptor paddock structure
                break;
            default:
                source.sendFailure(Component.literal("Unknown structure: " + structureName));
                return 0;
        }
        
        return 1;
    }
}
