package com.github.applejuiceyy.automa.client;

import com.github.applejuiceyy.automa.mixin.SlotAccessor;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.luaj.vm2.LuaError;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getInteractionManager;
import static com.github.applejuiceyy.automa.client.lua.api.Getter.getPlayer;

public class InventoryScreenHelper {
    private static void moveStacksWithinPlayerInventory(ScreenHandler handler, Inventory fromInventory, int fromPos, Inventory toInventory, int toPos) {

        int fromSlot = -1;
        int toSlot = -1;

        for (Slot slot: handler.slots) {
            int index = ((SlotAccessor) slot).getIndex();
            if (index == fromPos) {
                if (slot.inventory == fromInventory) {
                    fromSlot = slot.id;
                }
            }
            if (index == toPos) {
                if(slot.inventory == toInventory) {
                    toSlot = slot.id;
                }
            }

        }

        if (fromSlot == -1 || toSlot == -1) {
            throw new LuaError("Cannot find inventory slots");
        }

        getInteractionManager().clickSlot(handler.syncId, fromSlot, toSlot, SlotActionType.SWAP, getPlayer());
    }

    public static void moveStacks(ScreenHandler handler, Inventory fromInventory, int fromPos, Inventory toInventory, int toPos) {
        if (fromInventory == toInventory && toInventory instanceof PlayerInventory) {
            moveStacksWithinPlayerInventory(handler, fromInventory, fromPos, toInventory, toPos);
            return;
        }
        if (fromInventory == toInventory && fromPos == toPos) {
            return;
        }
        int fromSlot = -1;
        int toSlot = -1;

        for (Slot slot: handler.slots) {
            int index = ((SlotAccessor) slot).getIndex();
            if (index == fromPos) {
                if (slot.inventory == fromInventory) {
                    fromSlot = slot.id;
                }
            }
            if (index == toPos) {
                if(slot.inventory == toInventory) {
                    toSlot = slot.id;
                }
            }

        }

        if (fromSlot == -1 || toSlot == -1) {
            throw new LuaError("Cannot find inventory slots");
        }

        getInteractionManager().clickSlot(handler.syncId, fromSlot, 0, SlotActionType.SWAP, getPlayer());
        getInteractionManager().clickSlot(handler.syncId, toSlot, 0, SlotActionType.SWAP, getPlayer());
        getInteractionManager().clickSlot(handler.syncId, fromSlot, 0, SlotActionType.SWAP, getPlayer());
    }

    public static void movePickup(ScreenHandler handler, Inventory fromInventory, int fromPos, Inventory toInventory, int toPos, ActionAmount amount) {
        if (fromInventory == toInventory && fromPos == toPos) {
            return;
        }

        int fromSlot = -1;
        int toSlot = -1;

        for (Slot slot: handler.slots) {
            int index = ((SlotAccessor) slot).getIndex();
            if (index == fromPos) {
                if (slot.inventory == fromInventory) {
                    fromSlot = slot.id;
                }
            }
            if (index == toPos) {
                if(slot.inventory == toInventory) {
                    toSlot = slot.id;
                }
            }

        }

        if (fromSlot == -1 || toSlot == -1) {
            throw new LuaError("Cannot find inventory slots");
        }

        getInteractionManager().clickSlot(handler.syncId, fromSlot, amount == ActionAmount.HALF ? 1 : 0, SlotActionType.PICKUP, getPlayer());
        getInteractionManager().clickSlot(handler.syncId, toSlot, amount == ActionAmount.ONE ? 1 : 0, SlotActionType.PICKUP, getPlayer());

        if (!handler.getCursorStack().isEmpty()) {
            getInteractionManager().clickSlot(handler.syncId, fromSlot, 0, SlotActionType.PICKUP, getPlayer());
        }
    }

    public enum ActionAmount {
        ALL, HALF, ONE
    }
}
