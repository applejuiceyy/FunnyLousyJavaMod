package com.github.applejuiceyy.automa.client.automatedscreenhandler;

import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.DynamicSlotAction;
import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.DynamicSlotReference;
import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.InventoryAccess;
import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import com.github.applejuiceyy.automa.client.lua.annotation.IsIndex;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.annotation.Property;
import com.github.applejuiceyy.automa.mixin.screenhandler.BrewingStandScreenHandlerAccessor;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.ScreenHandler;

@LuaConvertible
public class AutomatedBrewingStand extends AutomatedScreenHandler<BrewingStandScreenHandler> {
    AutomatedBrewingStand(LuaExecution executor, ScreenHandler handler) {
        super(executor, (BrewingStandScreenHandler) handler);
    }

    @Property
    public DynamicSlotReference left() {
        return new DynamicSlotReference(this, ((BrewingStandScreenHandlerAccessor) handler).getInventory(), 0);
    }

    @Property
    public DynamicSlotReference center() {
        return new DynamicSlotReference(this, ((BrewingStandScreenHandlerAccessor) handler).getInventory(), 1);
    }

    @Property
    public DynamicSlotReference right() {
        return new DynamicSlotReference(this, ((BrewingStandScreenHandlerAccessor) handler).getInventory(), 2);
    }


    @Property
    public InventoryAccess potion() {
        return new InventoryAccess(this, ((BrewingStandScreenHandlerAccessor) handler).getInventory());
    }

    @Property
    public DynamicSlotReference ingredient() {
        return new DynamicSlotReference(this, ((BrewingStandScreenHandlerAccessor) handler).getInventory(), 3);
    }

    @Property
    public DynamicSlotReference fuel() {
        return new DynamicSlotReference(this, ((BrewingStandScreenHandlerAccessor) handler).getInventory(), 4);
    }


    @LuaConvertible
    public float brewingPercentage() {
        return 1 - handler.getBrewTime() / 400f;
    }

    @LuaConvertible
    public float fuelPercentage() {
        return handler.getFuel() / 20f;
    }
}