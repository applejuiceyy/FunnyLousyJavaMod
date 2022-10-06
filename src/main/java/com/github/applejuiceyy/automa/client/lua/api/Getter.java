package com.github.applejuiceyy.automa.client.lua.api;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;

public class Getter {
    static MinecraftClient getClient() {
        return MinecraftClient.getInstance();
    }
    static ClientPlayerEntity getPlayer() {
        return getClient().player;
    }
    static ClientPlayerInteractionManager getInteractionManager() {
        return getClient().interactionManager;
    }
    static ClientWorld getWorld() {
        return getClient().world;
    }
}