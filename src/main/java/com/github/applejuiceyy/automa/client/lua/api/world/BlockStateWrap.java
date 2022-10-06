package com.github.applejuiceyy.automa.client.lua.api.world;

import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.api.Wrapper;
import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;

@LuaConvertible
public record BlockStateWrap(BlockState blockState) implements Wrapper<BlockState> {
    public String getPreferredTool() {
        if (blockState.isIn(BlockTags.PICKAXE_MINEABLE)) {
            return "PICKAXE";
        }
        if (blockState.isIn(BlockTags.AXE_MINEABLE)) {
            return "AXE";
        }
        if (blockState.isIn(BlockTags.SHOVEL_MINEABLE)) {
            return "SHOVEL";
        }
        if (blockState.isIn(BlockTags.HOE_MINEABLE)) {
            return "HOE";
        }

        return null;
    }

    public int getPreferredLevel() {
        return MiningLevelManager.getRequiredMiningLevel(blockState);
    }

    @Override
    public BlockState getWrapped() {
        return blockState;
    }
}