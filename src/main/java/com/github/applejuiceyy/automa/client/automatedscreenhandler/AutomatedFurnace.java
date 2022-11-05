package com.github.applejuiceyy.automa.client.automatedscreenhandler;

import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.DynamicSlotReference;
import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.annotation.Property;
import com.github.applejuiceyy.automa.mixin.screenhandler.AbstractFurnaceScreenHandlerAccessor;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;

@LuaConvertible
public class AutomatedFurnace extends AutomatedScreenHandler<AbstractFurnaceScreenHandler> {
    AutomatedFurnace(LuaExecution executor, ScreenHandler handler) {
        super(executor, (AbstractFurnaceScreenHandler) handler);
    }

    @Property
    public DynamicSlotReference input() {
        return new DynamicSlotReference(this, ((AbstractFurnaceScreenHandlerAccessor) handler).getInventory(), 0);
    }
    @Property
    public DynamicSlotReference fuel() {
        return new DynamicSlotReference(this, ((AbstractFurnaceScreenHandlerAccessor) handler).getInventory(), 1);
    }
    @Property
    public DynamicSlotReference output() {
        return new DynamicSlotReference(this, ((AbstractFurnaceScreenHandlerAccessor) handler).getInventory(), 2);
    }
}
