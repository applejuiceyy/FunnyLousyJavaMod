package com.github.applejuiceyy.automa.client.automatedscreenhandler;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.IsIndex;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.mixin.screenhandler.BrewingStandScreenHandlerAccessor;
import com.github.applejuiceyy.automa.mixin.screenhandler.CraftingScreenHandlerAccessor;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;

@LuaConvertible
public class AutomatedCrafting extends AutomatedScreenHandler<CraftingScreenHandler> {
    AutomatedCrafting(LuaExecutionFacade executor, ScreenHandler handler) {
        super(executor, (CraftingScreenHandler) handler);
    }

    @LuaConvertible
    public DynamicSlotReference input(@IsIndex int idx) {
        return new DynamicSlotReference(((CraftingScreenHandlerAccessor) handler).getInput(), idx);
    }

    @LuaConvertible
    public DynamicSlotReference result() {
        return new DynamicSlotReference(((CraftingScreenHandlerAccessor) handler).getResult(), 0);
    }
}
