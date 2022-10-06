package com.github.applejuiceyy.automa.client.lua.api;

import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.api.world.ItemStackWrap;
import com.github.applejuiceyy.automa.mixin.SlotAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.luaj.vm2.LuaError;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getInteractionManager;
import static com.github.applejuiceyy.automa.client.lua.api.Getter.getPlayer;

@LuaConvertible
public class Inventory {

    public ItemStackWrap getStack(int number) {
        return new ItemStackWrap(getPlayer().getInventory().getStack(number));
    }

    public void swapStacks(int from, int to) {
        PlayerScreenHandler screen = getPlayer().playerScreenHandler;

        int fromSlot = -1;
        int toSlot = -1;

        for (Slot slot: screen.slots) {
            if(slot.inventory == getPlayer().getInventory()) {
                int index = ((SlotAccessor) slot).getIndex();

                if(index == from) {
                    fromSlot = slot.id;
                }
                if(index == to) {
                    toSlot = slot.id;
                }
            }
        }

        if (fromSlot == -1 || toSlot == -1) {
            throw new LuaError("Cannot find inventory slots");
        }

        getInteractionManager().clickSlot(screen.syncId, fromSlot, toSlot, SlotActionType.SWAP, getPlayer());
    }


    public void dropSelectedStack() {
        getPlayer().dropSelectedItem(false);
    }

    public void scroll(int idx) {
        getPlayer().getInventory().selectedSlot = normaliseSelectedSlot(getPlayer().getInventory().selectedSlot + idx);
    }

    public void setSlot(int idx) {
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
