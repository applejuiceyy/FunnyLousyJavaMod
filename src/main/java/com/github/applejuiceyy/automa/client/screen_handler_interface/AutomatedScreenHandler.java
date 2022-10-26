package com.github.applejuiceyy.automa.client.screen_handler_interface;

import com.github.applejuiceyy.automa.client.InventoryScreenHelper;
import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getPlayer;

@LuaConvertible
public class AutomatedScreenHandler<T extends ScreenHandler> {
    public final T handler;
    protected final LuaExecutionFacade executor;

    AutomatedScreenHandler(LuaExecutionFacade executor, T handler) {
        this.handler = handler;
        this.executor = executor;
    }

    public void move(DynamicSlotReference from, DynamicSlotReference to) {
        InventoryScreenHelper.moveStacks(this.handler, from.inventory, from.slot, to.inventory, to.slot);
    }

    public DynamicSlotAction takeAll(DynamicSlotReference from) {
        return new DynamicSlotAction(this, from, InventoryScreenHelper.ActionAmount.ALL);
    }

    public DynamicSlotAction takeHalf(DynamicSlotReference from) {
        return new DynamicSlotAction(this, from, InventoryScreenHelper.ActionAmount.HALF);
    }

    public DynamicSlotAction takeOne(DynamicSlotReference from) {
        return new DynamicSlotAction(this, from, InventoryScreenHelper.ActionAmount.ONE);
    }

    public DynamicSlotReference player(int slot) {
        return new DynamicSlotReference(getPlayer().getInventory(), slot);
    }

    public ItemStack getAt(DynamicSlotReference slot) {
        return slot.inventory.getStack(slot.slot);
    }

    @LuaConvertible
    public record DynamicSlotReference(Inventory inventory, int slot) {
        public ItemStack get() {
            return inventory.getStack(slot);
        }
    }
    @LuaConvertible
    public record DynamicSlotAction(AutomatedScreenHandler<?> owner, DynamicSlotReference reference, InventoryScreenHelper.ActionAmount action) {
        public void to(DynamicSlotReference to) {
            InventoryScreenHelper.movePickup(owner.handler, reference.inventory, reference.slot, to.inventory, to.slot, action);
        }
    }
}
