package com.github.applejuiceyy.automa.client.lua.api.world;

import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.api.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

@LuaConvertible
public record BlockWrap(Block block) implements Wrapper<Block> {
    public BlockState getDefaultBlockState() {
        return block.getDefaultState();
    }

    @Override
    public Block getWrapped() {
        return block;
    }
}
