package com.github.applejuiceyy.automa.client;

import com.github.applejuiceyy.automa.client.command.Commands;
import com.github.applejuiceyy.automa.client.lua.LuaExecutionContainer;
import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.luaj.vm2.Lua;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

@Environment(EnvType.CLIENT)
public class AutomaClient implements ClientModInitializer {
    public static String MOD_ID = "automa";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(Commands::registerCommands);
        ClientPlayConnectionEvents.DISCONNECT.register((a, b) -> {
            LuaExecutionContainer.stopExecutor();
        });
        ClientTickEvents.START_CLIENT_TICK.register((a) -> {
            LuaExecutionFacade executor = LuaExecutionContainer.getExecutor();
            if (executor != null && !a.isPaused()) {
                executor.tick();
            }
        });
        HudRenderCallback.EVENT.register((matrix, f) -> {

        });
    }

    public static Path getScriptsPath() {
        Path p = Path.of("automation");
        try {
            Files.createDirectories(p);
        } catch (FileAlreadyExistsException ignored) {
        } catch (IOException err) {
            throw new RuntimeException("Cannot create directory");
        }

        return p;
    }
}
