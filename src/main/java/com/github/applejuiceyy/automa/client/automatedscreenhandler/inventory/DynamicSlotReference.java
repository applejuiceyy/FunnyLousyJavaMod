package com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory;

import com.github.applejuiceyy.automa.client.automatedscreenhandler.AutomatedScreenHandler;
import com.github.applejuiceyy.automa.client.lua.annotation.IsIndex;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

@LuaConvertible
public record DynamicSlotReference(AutomatedScreenHandler<?> owner, Inventory inventory, int slot) implements ContentQuery {
    public DynamicSlotReference(AutomatedScreenHandler<?> owner, Inventory inventory, int slot) {
        this.inventory = inventory;
        this.slot = slot;
        this.owner = owner;

        assertBounds(slot, inventory.size());
    }

    @LuaConvertible
    public ItemStack get() {
        return inventory.getStack(slot);
    }

    public static void assertBounds(int slot, int size) {
        if (slot < 0 || slot >= size) {
            throw new LuaError("position is outside the bounds of inventory");
        }
    }

    @Override
    @IsIndex
    @LuaConvertible
    public int contains(String id) {
        return Registry.ITEM.getId(get().getItem()).toString().equals(id) ? 0 : -1;
    }

    @Override
    @IsIndex
    @LuaConvertible
    public int contains(LuaFunction func) {
        return func.invoke(owner.executor.boundary.J2L(get())).arg1().toboolean() ? 0 : -1;
    }

    @Override
    @LuaConvertible
    public int count(String id) {
        return Registry.ITEM.getId(get().getItem()).toString().equals(id) ? get().getCount() : 0;
    }

    @Override
    @LuaConvertible
    public int count(LuaFunction func) {
        return func.invoke(owner.executor.boundary.J2L(get())).arg1().toboolean() ? get().getCount() : 0;
    }

    @Override
    @LuaConvertible
    public DynamicSlotReference at(int slot) {
        return slot == 0 ? this : null;
    }

    @Override
    @LuaConvertible
    public int size() {
        return 1;
    }
}
