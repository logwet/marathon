package me.logwet.marathon;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.logwet.marathon.util.SpawnerInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class Marathon implements ModInitializer {
    public static final String MODID = "marathon";
    public static final String VERSION =
            FabricLoader.getInstance()
                    .getModContainer(MODID)
                    .get()
                    .getMetadata()
                    .getVersion()
                    .getFriendlyString();
    public static final boolean IS_CLIENT =
            FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    private static final Cache<Long, SpawnerInfo> spawnerInfoMap =
            CacheBuilder.newBuilder().maximumSize(64).concurrencyLevel(2).build();
    private static MinecraftServer MS;

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MODID + " v" + VERSION + "] " + message);
    }

    public static MinecraftServer getMS() {
        return MS;
    }

    private static void setMS(MinecraftServer MS) {
        Marathon.MS = MS;
    }

    public static boolean inSinglePlayer() {
        return Minecraft.getInstance().hasSingleplayerServer();
    }

    private static Cache<Long, SpawnerInfo> getSpawnerInfoMap() {
        return spawnerInfoMap;
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    public static SpawnerInfo getSpawnerInfo(BlockPos blockPos) {
        return getSpawnerInfoMap().getIfPresent(blockPos.asLong());
    }

    public static void addSpawnerInfo(BlockPos blockPos, SpawnerInfo spawnerInfo) {
        getSpawnerInfoMap().put(blockPos.asLong(), spawnerInfo);
    }

    public static void removeSpawnerInfo(BlockPos blockPos) {
        getSpawnerInfoMap().invalidate(blockPos.asLong());
    }

    public static void onServerInit(MinecraftServer ms) {
        setMS(ms);
        getSpawnerInfoMap().invalidateAll();
        getSpawnerInfoMap().cleanUp();
        log(Level.INFO, "Server object initialized!");
    }

    @Override
    public void onInitialize() {
        log(Level.INFO, "Main class initialized!");
    }
}
