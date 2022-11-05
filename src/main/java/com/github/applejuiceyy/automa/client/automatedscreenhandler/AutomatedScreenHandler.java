package com.github.applejuiceyy.automa.client.automatedscreenhandler;

import com.github.applejuiceyy.automa.client.InventoryScreenHelper;
import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.DynamicSlotAction;
import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.DynamicSlotReference;
import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.InventoryAccess;
import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import com.github.applejuiceyy.automa.client.lua.annotation.IsIndex;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.annotation.Property;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getClient;
import static com.github.applejuiceyy.automa.client.lua.api.Getter.getPlayer;

@LuaConvertible
public class AutomatedScreenHandler<T extends ScreenHandler> {
    public final T handler;
    public final LuaExecution executor;

    AutomatedScreenHandler(LuaExecution executor, T handler) {
        this.handler = handler;
        this.executor = executor;
    }
    @LuaConvertible
    public void move(DynamicSlotReference from, DynamicSlotReference to) {
        InventoryScreenHelper.moveStacks(this.handler, from.inventory(), from.slot(), to.inventory(), to.slot());
    }
    @LuaConvertible
    public DynamicSlotAction takeAll(DynamicSlotReference from) {
        return new DynamicSlotAction(this, from, InventoryScreenHelper.ActionAmount.ALL);
    }
    @LuaConvertible
    public DynamicSlotAction takeHalf(DynamicSlotReference from) {
        return new DynamicSlotAction(this, from, InventoryScreenHelper.ActionAmount.HALF);
    }
    @LuaConvertible
    public DynamicSlotAction takeOne(DynamicSlotReference from) {
        return new DynamicSlotAction(this, from, InventoryScreenHelper.ActionAmount.ONE);
    }

    @Property
    public InventoryAccess player() {
        return new InventoryAccess(this, getPlayer().getInventory());
    }

    @LuaConvertible
    public ItemStack getAt(DynamicSlotReference slot) {
        return slot.get();
    }

    @LuaConvertible
    public void close() {
        executor.executeInMain(() -> getPlayer().closeHandledScreen());
    }
}
