package com.github.applejuiceyy.automa.client.lua.api.world;

import com.github.applejuiceyy.automa.client.lua.api.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import org.jetbrains.annotations.NotNull;

public record ItemWrap(Item item) implements Wrapper<Item> {
    public boolean isSuitable(@NotNull BlockState state) {
        if (item instanceof MiningToolItem toolItem) {
            return toolItem.isSuitableFor(state);
        }
        return false;
    }

    public Block asBlock() {
        if (item instanceof BlockItem blockItem) {
            return blockItem.getBlock();
        }

        return null;
    }

    @Override
    public Item getWrapped() {
        return item;
    }
}