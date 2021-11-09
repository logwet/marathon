package me.logwet.marathon.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.logwet.marathon.Marathon;
import me.logwet.marathon.util.spawner.BaseSpawnerAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

public class SpawnerCommand {
    public static int run(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();

        BaseSpawner spawner = null;
        double dist = Double.MAX_VALUE;
        double newDist;

        for (BlockEntity potentialBlockEntity : player.level.tickableBlockEntities) {
            if (!potentialBlockEntity.isRemoved() && potentialBlockEntity.hasLevel()) {
                if (potentialBlockEntity.getType() == BlockEntityType.MOB_SPAWNER) {
                    BlockPos blockPos = potentialBlockEntity.getBlockPos();
                    if (player.level.getChunkSource().isTickingChunk(blockPos)
                            && player.level.getWorldBorder().isWithinBounds(blockPos)) {
                        if ((newDist = player.blockPosition().distSqr(blockPos)) <= 16 * 16) {
                            if (newDist < dist) {
                                dist = newDist;
                                spawner = ((SpawnerBlockEntity) potentialBlockEntity).getSpawner();
                            }
                        }
                    }
                }
            }
        }

        if (spawner != null) {
            context.getSource()
                    .sendSuccess(
                            new TextComponent("Forcing analysis of nearest spawner...")
                                    .withStyle(ChatFormatting.GOLD),
                            false);
            long runTime = ((BaseSpawnerAccessor) spawner).analyse();
            context.getSource()
                    .sendSuccess(
                            new TextComponent("Analysis finished in " + runTime + "ms")
                                    .withStyle(ChatFormatting.GOLD),
                            false);
            return 1;
        } else {
            context.getSource()
                    .sendFailure(
                            new TextComponent(
                                    "Unable to find spawner in range to force analysis of."));
            return -1;
        }
    }

    public static int toggleSpawning(CommandContext<CommandSourceStack> context) {
        boolean status = Marathon.toggleSpawnersEnabled();

        context.getSource()
                .sendSuccess(
                        new TextComponent("All spawners have been ")
                                .withStyle(ChatFormatting.LIGHT_PURPLE)
                                .append(
                                        new TextComponent(status ? "enabled" : "disabled")
                                                .withStyle(
                                                        status
                                                                ? ChatFormatting.GREEN
                                                                : ChatFormatting.RED)),
                        true);

        return 1;
    }

    public static int toggleAnalysis(CommandContext<CommandSourceStack> context) {
        boolean status = Marathon.toggleSpawnerAnalysis();

        context.getSource()
                .sendSuccess(
                        new TextComponent("Automatic periodic spawner analysis has been ")
                                .withStyle(ChatFormatting.LIGHT_PURPLE)
                                .append(
                                        new TextComponent(status ? "enabled" : "disabled")
                                                .withStyle(
                                                        status
                                                                ? ChatFormatting.GREEN
                                                                : ChatFormatting.RED)),
                        true);

        return 1;
    }
}
