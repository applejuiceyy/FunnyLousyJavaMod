package com.github.applejuiceyy.automa.client.lua.api.controls.inventoryControls;

import com.github.applejuiceyy.automa.client.InventoryScreenHelper;
import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.api.controls.MissionCriticalLuaInterface;
import com.github.applejuiceyy.automa.client.lua.api.wrappers.ItemStackWrap;
import com.github.applejuiceyy.automa.mixin.acessors.ClientPlayerInteractionManagerAccessor;
import net.minecraft.item.ItemStack;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getInteractionManager;
import static com.github.applejuiceyy.automa.client.lua.api.Getter.getPlayer;

@LuaConvertible
public class InventoryControlsLA extends MissionCriticalLuaInterface<InventoryControls> {
    public InventoryControlsLA(LuaExecution f, InventoryControls obj) { super(f, obj); }
    @LuaConvertible
    public void useItem() {
        this.owner.usingItem = true;
        this.owner.timesUsingItem += 1;
    }
    @LuaConvertible
    public void stopUsingItem() {
        this.owner.usingItem = false;
    }
    @LuaConvertible
    public void attackItem() {
        this.owner.attackingItem = true;
        this.owner.timesAttackingItem += 1;
    }
    @LuaConvertible
    public void stopAttackingItem() {
        this.owner.attackingItem = false;
    }

    // TODO: merge with AutomatedScreenHandlers
    @LuaConvertible
    public ItemStackWrap getStack(int number) {
        return new ItemStackWrap(getPlayer().getInventory().getStack(number));
    }
    @LuaConvertible
    public void swapStacks(int from, int to) {
        checkControl();
        InventoryScreenHelper.moveStacks(getPlayer().playerScreenHandler, getPlayer().getInventory(), from, getPlayer().getInventory(), to);
    }

    @LuaConvertible
    public void dropSelectedStack() {
        checkControl();
        ((ClientPlayerInteractionManagerAccessor) getInteractionManager()).invokeSyncSelectedSlot();
        getPlayer().dropSelectedItem(false);
    }
    @LuaConvertible
    public void scroll(int idx) {
        checkControl();
        getPlayer().getInventory().selectedSlot = normaliseSelectedSlot(getPlayer().getInventory().selectedSlot + idx);
    }
    @LuaConvertible
    public void setSlot(int idx) {
        checkControl();
        getPlayer().getInventory().selectedSlot = normaliseSelectedSlot(idx);
    }
    @LuaConvertible
    public ItemStack getSelectedStack() {
        return getPlayer().getInventory().getMainHandStack();
    }
    @LuaConvertible
    public int getSelectedSlot() {
        return getPlayer().getInventory().selectedSlot;
    }

    int normaliseSelectedSlot(int number) {
        while (number < 0) number += 9;
        while (number > 8) number -= 9;
        return number;
    }
}
