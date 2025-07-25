/**
 * Copyright (C) 2015 by jabelar
 * <p>
 * This file is part of jabelar's Minecraft Forge modding examples; as such, you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * For a copy of the GNU General Public License see <http://www.gnu.org/licenses/>.
 */

package org.jurassicraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jurassicraft.JurassiCraft;

public class ForceAnimationCommand {

    public static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("animate")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("target", EntityArgument.entity())
                        .then(Commands.argument("animation", StringArgumentType.string())
                                .executes(ForceAnimationCommand::executeAnimation)));
    }

    private static int executeAnimation(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Entity target = EntityArgument.getEntity(context, "target");
        String animationName = StringArgumentType.getString(context, "animation");

        // TODO: Implement animation system for entities
        // This is a placeholder implementation
        JurassiCraft.getLogger().info("Attempting to play animation '{}' on entity {}", animationName, target.getName().getString());
        
        source.sendSuccess(() -> Component.literal("Animation command executed for " + target.getName().getString()), true);
        
        return 1;
    }
}
