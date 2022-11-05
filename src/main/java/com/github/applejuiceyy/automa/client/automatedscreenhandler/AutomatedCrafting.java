package com.github.applejuiceyy.automa.client.automatedscreenhandler;

import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.DynamicSlotReference;
import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.InventoryAccess;
import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import com.github.applejuiceyy.automa.client.lua.annotation.IsIndex;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.annotation.Property;
import com.github.applejuiceyy.automa.mixin.screenhandler.CraftingScreenHandlerAccessor;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;

@LuaConvertible
public class AutomatedCrafting extends AutomatedScreenHandler<CraftingScreenHandler> {
    AutomatedCrafting(LuaExecution executor, ScreenHandler handler) {
        super(executor, (CraftingScreenHandler) handler);
    }

    @Property
    public InventoryAccess input() {
        return new InventoryAccess(this, ((CraftingScreenHandlerAccessor) handler).getInput());
    }

    @Property
    public DynamicSlotReference result() {
        return new DynamicSlotReference(this, ((CraftingScreenHandlerAccessor) handler).getResult(), 0);
    }
}
