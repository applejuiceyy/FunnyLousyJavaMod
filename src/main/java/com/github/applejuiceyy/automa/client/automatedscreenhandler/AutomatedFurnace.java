package com.github.applejuiceyy.automa.client.automatedscreenhandler;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.mixin.screenhandler.AbstractFurnaceScreenHandlerAccessor;
import com.github.applejuiceyy.automa.mixin.screenhandler.ForgingScreenHandlerAccessor;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;

@LuaConvertible
public class AutomatedFurnace extends AutomatedScreenHandler<AbstractFurnaceScreenHandler> {
    AutomatedFurnace(LuaExecutionFacade executor, ScreenHandler handler) {
        super(executor, (AbstractFurnaceScreenHandler) handler);
    }

    @LuaConvertible
    public DynamicSlotReference input() {
        return new DynamicSlotReference(((AbstractFurnaceScreenHandlerAccessor) handler).getInventory(), 0);
    }
    @LuaConvertible
    public DynamicSlotReference fuel() {
        return new DynamicSlotReference(((AbstractFurnaceScreenHandlerAccessor) handler).getInventory(), 1);
    }
    @LuaConvertible
    public DynamicSlotReference output() {
        return new DynamicSlotReference(((AbstractFurnaceScreenHandlerAccessor) handler).getInventory(), 2);
    }
}
