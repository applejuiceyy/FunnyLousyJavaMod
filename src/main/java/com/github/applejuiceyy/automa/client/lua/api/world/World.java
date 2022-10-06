package com.github.applejuiceyy.automa.client.lua.api.world;

import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

@LuaConvertible
public class World {
    MinecraftClient getClient() {
        return MinecraftClient.getInstance();
    }

    public BlockState getBlockState(int x, int y, int z) {
        return Objects.requireNonNull(getClient().world).getBlockState(new BlockPos(x, y, z));
    }
}
