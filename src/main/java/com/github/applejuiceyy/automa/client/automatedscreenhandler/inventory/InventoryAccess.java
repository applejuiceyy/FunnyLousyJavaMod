package com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory;

import com.github.applejuiceyy.automa.client.automatedscreenhandler.AutomatedScreenHandler;
import com.github.applejuiceyy.automa.client.lua.annotation.IsIndex;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.registry.Registry;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.util.function.Predicate;

@LuaConvertible
public record InventoryAccess(AutomatedScreenHandler<?> owner, Inventory inventory) implements ContentQuery {
    @LuaConvertible
    @IsIndex
    public int contains(String id) {
        return contains(i -> Registry.ITEM.getId(i.getItem()).toString().equals(id));
    }

    @LuaConvertible
    @IsIndex
    public int contains(LuaFunction func) {
        return contains(i -> func.invoke(owner.executor.boundary.J2L(i)).arg1().toboolean());
    }

    int contains(Predicate<ItemStack> predicate) {
        for(int i = 0; i < inventory.size(); i++) {
            if (predicate.test(inventory.getStack(i))) {
                return i;
            }
        }

        return -1;
    }

    @LuaConvertible
    public int count(String id) {
        return count(i -> Registry.ITEM.getId(i.getItem()).toString().equals(id));
    }

    @LuaConvertible
    public int count(LuaFunction func) {
        return count(i -> func.invoke(owner.executor.boundary.J2L(i)).arg1().toboolean());
    }

    int count(Predicate<ItemStack> predicate) {
        int c = 0;
        for(int i = 0; i < inventory.size(); i++) {
            if (predicate.test(inventory.getStack(i))) {
                c += inventory.getStack(i).getCount();
            }
        }

        return c;
    }

    @LuaConvertible
    public DynamicSlotReference at(@IsIndex int slot) {
        return new DynamicSlotReference(owner, inventory, slot);
    }

    @Override
    @LuaConvertible
    public int size() {
        return inventory.size();
    }
}
