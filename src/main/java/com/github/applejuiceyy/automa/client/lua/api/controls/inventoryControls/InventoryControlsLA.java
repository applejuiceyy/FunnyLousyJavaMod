package com.github.applejuiceyy.automa.client.lua.api.controls.inventoryControls;

import com.github.applejuiceyy.automa.client.InventoryScreenHelper;
import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.api.controls.MissionCriticalLuaInterface;
import com.github.applejuiceyy.automa.client.lua.api.world.ItemStackWrap;
import com.github.applejuiceyy.automa.mixin.SlotAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.luaj.vm2.LuaError;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getInteractionManager;
import static com.github.applejuiceyy.automa.client.lua.api.Getter.getPlayer;

@LuaConvertible
public class InventoryControlsLA extends MissionCriticalLuaInterface<InventoryControls> {
    public InventoryControlsLA(LuaExecutionFacade f, InventoryControls obj) { super(f, obj); }

    public void useItem() {
        this.owner.usingItem = true;
    }

    public void stopUsingItem() {
        this.owner.usingItem = false;
    }

    public void attackItem() {
        this.owner.attackingItem = true;
    }

    public void stopAttackingItem() {
        this.owner.attackingItem = false;
    }

    // TODO: merge with AutomatedScreenHandlers
    public ItemStackWrap getStack(int number) {
        return new ItemStackWrap(getPlayer().getInventory().getStack(number));
    }

    public void swapStacks(int from, int to) {
        checkControl();
        InventoryScreenHelper.moveStacks(getPlayer().playerScreenHandler, getPlayer().getInventory(), from, getPlayer().getInventory(), to);
    }


    public void dropSelectedStack() {
        checkControl();
        getPlayer().dropSelectedItem(false);
    }

    public void scroll(int idx) {
        checkControl();
        getPlayer().getInventory().selectedSlot = normaliseSelectedSlot(getPlayer().getInventory().selectedSlot + idx);
    }

    public void setSlot(int idx) {
        checkControl();
        getPlayer().getInventory().selectedSlot = normaliseSelectedSlot(idx);
    }

    public ItemStack getSelectedStack() {
        return getPlayer().getInventory().getMainHandStack();
    }

    public int getSelectedSlot() {
        return getPlayer().getInventory().selectedSlot;
    }

    int normaliseSelectedSlot(int number) {
        while (number < 0) number += 9;
        while (number > 8) number -= 9;
        return number;
    }
}
