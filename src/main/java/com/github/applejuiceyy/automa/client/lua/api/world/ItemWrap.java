package com.github.applejuiceyy.automa.client.lua.api.world;

import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.api.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

public record ItemWrap(Item item) implements Wrapper<Item> {
    @LuaConvertible
    public boolean isSuitable(@NotNull BlockState state) {
        if (item instanceof MiningToolItem toolItem) {
            return toolItem.isSuitableFor(state);
        }
        return false;
    }
    @LuaConvertible
    public Block asBlock() {
        if (item instanceof BlockItem blockItem) {
            return blockItem.getBlock();
        }

        return null;
    }
    @LuaConvertible
    public String id() {
        return Registry.ITEM.getId(this.item).toString();
    }

    @Override
    public Item getWrapped() {
        return item;
    }
}
