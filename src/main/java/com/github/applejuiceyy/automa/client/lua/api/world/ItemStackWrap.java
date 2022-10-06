package com.github.applejuiceyy.automa.client.lua.api.world;

import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.api.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

@LuaConvertible
public record ItemStackWrap(ItemStack stack) implements Wrapper<ItemStack> {

    public String id() {
        return Registry.ITEM.getId(this.stack.getItem()).toString();
    }

    public Block asBlock() {
        if (this.stack.getItem() instanceof BlockItem blockItem) {
            return blockItem.getBlock();
        }

        return null;
    }

    public Item asItem() {
        return this.stack.getItem();
    }

    @Override
    public ItemStack getWrapped() {
        return stack;
    }
}