package com.github.applejuiceyy.automa.client.lua.api.world;

import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.api.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;

@LuaConvertible
public record BlockWrap(Block block) implements Wrapper<Block> {
    public BlockState getDefaultBlockState() {
        return block.getDefaultState();
    }

    public String id() {
        return Registry.BLOCK.getId(this.block).toString();
    }

    @Override
    public Block getWrapped() {
        return block;
    }
}
