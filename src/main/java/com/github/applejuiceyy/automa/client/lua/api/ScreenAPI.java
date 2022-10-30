package com.github.applejuiceyy.automa.client.lua.api;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.automatedscreenhandler.AutomatedScreenHandler;
import com.github.applejuiceyy.automa.client.automatedscreenhandler.AutomatedScreenHandlers;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.ScreenHandler;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getClient;
import static com.github.applejuiceyy.automa.client.lua.api.Getter.getPlayer;

@LuaConvertible
public class ScreenAPI {
    LuaExecutionFacade executor;
    AutomatedScreenHandler<?> handler = null;
    ScreenHandler cache = null;

    public ScreenAPI(LuaExecutionFacade executor) {
        this.executor = executor;
    }
    @LuaConvertible
    public boolean isScreenOpen() {
        return getClient().currentScreen != null;
    }
    @LuaConvertible
    public boolean isHandledScreenOpen() {
        // the field currentScreenHandler has meaningful null checks
        // but the field is never set to null
        // I can bet this is old behaviour when closing a screenHandler
        // causes currentScreenHandler was actually set to null
        // instead of being set to the always-active playerScreenHandler
        // we actually want to check if there's a screenHandler opened instead of a cop-out,
        // so we treat playerScreenHandler as the null empty value
        return getPlayer().currentScreenHandler != getPlayer().playerScreenHandler;
    }
    @LuaConvertible
    public boolean hasAutomatedHandledScreen() {
        if (!isHandledScreenOpen()) {
            return false;
        }
        updateASH();
        return handler != null;
    }
    @LuaConvertible
    public AutomatedScreenHandler<?> getAutomatedHandledScreen() {
        updateASH();
        return handler;
    }

    private void updateASH() {
        ClientPlayerEntity player = getPlayer();

        if (cache == player.currentScreenHandler) {
            return;
        }
        cache = player.currentScreenHandler;
        handler = null;

        if (isHandledScreenOpen()) {
            if (AutomatedScreenHandlers.map.containsKey(player.currentScreenHandler.getClass())) {
                handler = AutomatedScreenHandlers.map.get(player.currentScreenHandler.getClass())
                        .create(executor, player.currentScreenHandler);
            }
        }
    }
}
