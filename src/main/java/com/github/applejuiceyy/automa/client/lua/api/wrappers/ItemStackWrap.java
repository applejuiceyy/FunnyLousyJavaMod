package com.github.applejuiceyy.automa.client.lua.api.wrappers;

import com.github.applejuiceyy.automa.client.lua.NBTConverter;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.api.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.registry.Registry;
import org.luaj.vm2.LuaValue;

@LuaConvertible
public record ItemStackWrap(ItemStack stack) implements Wrapper<ItemStack> {
    @LuaConvertible
    public String id() {
        return Registry.ITEM.getId(this.stack.getItem()).toString();
    }
    @LuaConvertible
    public int count() {return this.stack.getCount(); }
    @LuaConvertible
    public Block asBlock() {
        if (this.stack.getItem() instanceof BlockItem blockItem) {
            return blockItem.getBlock();
        }

        return null;
    }
    @LuaConvertible
    public Item asItem() {
        return this.stack.getItem();
    }

    @LuaConvertible
    public LuaValue nbt() {
        return NBTConverter.convert(stack.getNbt());
    }

    @Override
    public ItemStack getWrapped() {
        return stack;
    }
}
