package me.logwet.marathon.commands.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

public interface ServerCommand {
    LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(boolean dedicated);
}
