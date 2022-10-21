package com.github.applejuiceyy.automa.client;

import com.github.applejuiceyy.automa.client.command.Commands;
import com.github.applejuiceyy.automa.client.lua.LuaExecutionContainer;
import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.api.controls.MissionCritical;
import com.github.applejuiceyy.automa.client.lua.api.controls.inventoryControls.InventoryControls;
import com.github.applejuiceyy.automa.client.lua.api.controls.lookControls.LookControls;
import com.github.applejuiceyy.automa.client.lua.api.controls.movementControls.MovementControls;
import com.github.applejuiceyy.automa.client.lua.api.listener.Future;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
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

    public static MovementControls movementControls = new MovementControls();
    public static LookControls lookControls = new LookControls();
    public static InventoryControls inventoryControls = new InventoryControls();

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
            LuaExecutionFacade executor = LuaExecutionContainer.getExecutor();
            if (executor != null) {
                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                TextRenderer renderer = minecraftClient.textRenderer;
                String text = "Running " + executor.getName();
                float y = minecraftClient.getWindow().getScaledHeight() - renderer.fontHeight;
                renderer.draw(
                        matrix,
                        text,
                        minecraftClient.getWindow().getScaledWidth() - renderer.getWidth(text),
                        y,
                        0xffffff
                );

                y -= renderer.fontHeight + 10;

                MissionCritical[] toCheck = new MissionCritical[]{
                        movementControls, lookControls, inventoryControls
                };

                for(MissionCritical s: toCheck) {
                    if(s.requested()) {
                        text = s.getRequester() + " requested critical " + s.getControllingAspect() + " control";
                        renderer.draw(
                                matrix,
                                text,
                                minecraftClient.getWindow().getScaledWidth() - renderer.getWidth(text),
                                y,
                                0xffffff
                        );

                        y -= renderer.fontHeight;
                    }
                }
            }
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
